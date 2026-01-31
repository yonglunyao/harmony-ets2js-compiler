package com.ets2jsc.parser;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.ast.SourceFile;

import com.ets2jsc.parser.internal.ConversionContext;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * TypeScript/ETS parser using Node.js and TypeScript Compiler API.
 * Provides full TypeScript syntax parsing capabilities.
 * <p>
 * This parser now uses a modular converter architecture with:
 * - ConversionContext for shared state and utilities
 * - ExpressionConverterRegistry for expression conversion
 * - StatementConverterRegistry for statement conversion
 */
public class TypeScriptScriptParser {

    private final String scriptPath;
    private final Gson gson = new Gson();
    private final ConversionContext conversionContext;

    public TypeScriptScriptParser() {
        this.conversionContext = new ConversionContext(gson);

        // Try to use the classpath location directly (where node_modules is also available)
        URL scriptUrl = getClass().getClassLoader().getResource("typescript-parser/parse-ets.js");

        if (scriptUrl != null && "file".equals(scriptUrl.getProtocol())) {
            // Running from classpath on filesystem (e.g., target/classes)
            this.scriptPath = new File(scriptUrl.getFile()).getAbsolutePath();
        } else {
            // Running from JAR - extract entire typescript-parser directory to temp
            try {
                Path tempDir = Files.createTempDirectory("typescript-parser-");
                tempDir.toFile().deleteOnExit();

                // Extract all resources from typescript-parser directory
                extractResourceDirectory("typescript-parser/", tempDir);

                this.scriptPath = tempDir.resolve("parse-ets.js").toAbsolutePath().toString();
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize TypeScript parser script", e);
            }
        }
    }

    /**
     * Extract all files from a resource directory in the JAR to a temp directory.
     */
    private void extractResourceDirectory(String resourcePath, Path targetDir) throws Exception {
        // Get the path to the JAR file
        String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

        // Handle Windows paths with spaces
        if (jarPath.startsWith("/") && jarPath.contains(":")) {
            jarPath = jarPath.substring(1);
        }

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(resourcePath)) {
                    String relativePath = entryName.substring(resourcePath.length());
                    Path targetPath = targetDir.resolve(relativePath.replace('/', File.separatorChar));

                    if (entry.isDirectory()) {
                        Files.createDirectories(targetPath);
                    } else {
                        Files.createDirectories(targetPath.getParent());
                        try (InputStream is = jarFile.getInputStream(entry)) {
                            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        }
    }

    /**
     * Parse ETS/TypeScript source file into a SourceFile AST node.
     */
    public SourceFile parse(String fileName, String sourceCode) {
        try {
            // Create temporary file for source code
            Path tempSourceFile = Files.createTempFile("ets-source-", ".ets");
            Files.writeString(tempSourceFile, sourceCode);

            // Create temp file for AST JSON output
            Path tempAstFile = Files.createTempFile("ets-ast-", ".json");

            try {
                // Run Node.js TypeScript parser
                JsonObject astJson = runTypeScriptParser(tempSourceFile, tempAstFile);

                // Convert JSON to AST
                return convertJsonToAst(fileName, sourceCode, astJson);

            } finally {
                // Clean up temp files
                Files.deleteIfExists(tempSourceFile);
                Files.deleteIfExists(tempAstFile);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse TypeScript file: " + fileName, e);
        }
    }

    /**
     * Run the Node.js TypeScript parser script.
     */
    private JsonObject runTypeScriptParser(Path sourceFile, Path outputFile) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("node");
        command.add(scriptPath);
        command.add(sourceFile.toAbsolutePath().toString());
        command.add(outputFile.toAbsolutePath().toString());

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        // Read output (for debugging)
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder errorOutput = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("TypeScript parser failed:\n" + errorOutput.toString());
        }

        // Read and parse JSON output
        String jsonContent = Files.readString(outputFile);
        return gson.fromJson(jsonContent, JsonObject.class);
    }

    /**
     * Convert JSON AST to our AST model.
     */
    private SourceFile convertJsonToAst(String fileName, String sourceCode, JsonObject astJson) {
        SourceFile sourceFile = new SourceFile(fileName, sourceCode);

        JsonArray statements = astJson.getAsJsonArray("statements");
        if (statements != null) {
            for (JsonElement stmtElement : statements) {
                JsonObject stmtObj = stmtElement.getAsJsonObject();
                AstNode node = convertJsonNode(stmtObj);
                if (node != null) {
                    sourceFile.addStatement(node);
                }
            }
        }

        return sourceFile;
    }

    /**
     * Convert JSON node to AST node using the new converter architecture.
     */
    private AstNode convertJsonNode(JsonObject json) {
        String kindName = json.get("kindName").getAsString();

        // Special cases that don't go through converters
        switch (kindName) {
            case "SourceFile":
                return convertSourceFile(json);
            case "Decorator":
                return convertDecorator(json);
            case "BreakStatement":
                return new ExpressionStatement("break;");
            case "ContinueStatement":
                return new ExpressionStatement("continue;");
            case "InterfaceDeclaration":
                // Interface declarations don't generate runtime code
                return null;
            case "StringLiteral":
            case "NumericLiteral":
            case "TrueLiteral":
            case "FalseLiteral":
            case "NullLiteral":
            case "UndefinedLiteral":
            case "ThisKeyword":
                // These are expression literals - convert to expression statement
                return new ExpressionStatement(convertExpressionToString(json));
            default:
                // Use the new converter architecture for all other cases
                try {
                    return conversionContext.convertStatement(json);
                } catch (Exception e) {
                    // Fallback to legacy methods if converter fails
                    return convertJsonNodeLegacy(json, kindName);
                }
        }
    }

    /**
     * Legacy fallback for JSON node conversion.
     * Used only if the new converter architecture fails.
     */
    @Deprecated
    private AstNode convertJsonNodeLegacy(JsonObject json, String kindName) {
        // Simplified fallback - just handle expression statements as strings
        if (kindName.endsWith("Expression")) {
            return new ExpressionStatement(convertExpressionToString(json));
        }
        return null;
    }

    private SourceFile convertSourceFile(JsonObject json) {
        return new SourceFile(json.get("fileName").getAsString());
    }

    private Decorator convertDecorator(JsonObject json) {
        String name = json.get("name").getAsString();
        return new Decorator(name);
    }

    /**
     * Safely gets the kindName from a JSON object.
     * Returns empty string if kindName is not present.
     */
    private String getKindName(JsonObject json) {
        return json.has("kindName") ? json.get("kindName").getAsString() : "";
    }

    /**
     * Converts a JSON expression to a JavaScript string representation.
     * Now delegates to the new converter architecture.
     */
    private String convertExpressionToString(JsonObject exprJson) {
        // Use the new converter architecture
        try {
            return conversionContext.convertExpression(exprJson);
        } catch (Exception e) {
            // Fallback: return the text if available
            String fallbackText = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
            if (!fallbackText.isEmpty()) {
                return fallbackText.trim();
            }
            // Last resort: return the JSON as string
            return exprJson.toString();
        }
    }
}
