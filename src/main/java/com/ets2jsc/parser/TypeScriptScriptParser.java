package com.ets2jsc.parser;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.exception.ParserException;
import com.ets2jsc.exception.ParserInitializationException;

import com.ets2jsc.parser.internal.ConversionContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeScriptScriptParser.class);

    // Constants for resource paths and protocols
    private static final String PROTOCOL_FILE = "file";
    private static final String RESOURCE_PATH_PREFIX = "typescript-parser/";
    private static final String SCRIPT_RESOURCE_PATH = "typescript-parser/index.js";
    private static final String TEMP_DIR_PREFIX = "typescript-parser-";
    private static final String TEMP_SOURCE_PREFIX = "ets-source-";
    private static final String TEMP_AST_PREFIX = "ets-ast-";

    private final String scriptPath;
    private final ObjectMapper objectMapper;
    private final ConversionContext conversionContext;

    public TypeScriptScriptParser() {
        this.objectMapper = new ObjectMapper();
        this.conversionContext = new ConversionContext(objectMapper);

        // Try to use the classpath location directly (where node_modules is also available)
        URL scriptUrl = getClass().getClassLoader().getResource(SCRIPT_RESOURCE_PATH);

        if (scriptUrl != null && PROTOCOL_FILE.equals(scriptUrl.getProtocol())) {
            // Running from classpath on filesystem (e.g., target/classes)
            this.scriptPath = new File(scriptUrl.getFile()).getAbsolutePath();
        } else {
            // Running from JAR - extract entire typescript-parser directory to temp
            try {
                Path tempDir = Files.createTempDirectory(TEMP_DIR_PREFIX);
                tempDir.toFile().deleteOnExit();

                // Extract all resources from typescript-parser directory
                extractResourceDirectory(RESOURCE_PATH_PREFIX, tempDir);

                this.scriptPath = tempDir.resolve("index.js").toAbsolutePath().toString();
            } catch (Exception e) {
                throw new ParserInitializationException("Failed to initialize TypeScript parser script", e);
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
            Path tempSourceFile = Files.createTempFile(TEMP_SOURCE_PREFIX, ".ets");
            Files.writeString(tempSourceFile, sourceCode);

            // Create temp file for AST JSON output
            Path tempAstFile = Files.createTempFile(TEMP_AST_PREFIX, ".json");

            try {
                // Run Node.js TypeScript parser
                JsonNode astJson = runTypeScriptParser(tempSourceFile, tempAstFile);

                // Convert JSON to AST
                return convertJsonToAst(fileName, sourceCode, astJson);

            } finally {
                // Clean up temp files
                Files.deleteIfExists(tempSourceFile);
                Files.deleteIfExists(tempAstFile);
            }

        } catch (Exception e) {
            throw new ParserException("Failed to parse TypeScript file: " + fileName, e);
        }
    }

    /**
     * Run the Node.js TypeScript parser script.
     * Uses try-with-resources for automatic resource management.
     * CC: 4 (null check + exit code check + try-with-resources + process cleanup)
     */
    private JsonNode runTypeScriptParser(Path sourceFile, Path outputFile) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("node");
        command.add(scriptPath);
        command.add(sourceFile.toAbsolutePath().toString());
        command.add(outputFile.toAbsolutePath().toString());

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        // Use try-with-resources for automatic BufferedReader closure
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            StringBuilder errorOutput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new ParserException("TypeScript parser failed:\n" + errorOutput.toString());
            }

            // Read and parse JSON output
            String jsonContent = Files.readString(outputFile);
            return objectMapper.readTree(jsonContent);
        } finally {
            // Ensure the process is destroyed to prevent resource leaks
            process.destroyForcibly();
        }
    }

    /**
     * Convert JSON AST to our AST model.
     */
    private SourceFile convertJsonToAst(String fileName, String sourceCode, JsonNode astJson) {
        SourceFile sourceFile = new SourceFile(fileName, sourceCode);

        JsonNode statements = astJson.get("statements");
        if (statements != null && statements.isArray()) {
            ArrayNode statementsArray = (ArrayNode) statements;
            for (JsonNode stmtElement : statementsArray) {
                AstNode node = convertJsonNode(stmtElement);
                if (node != null) {
                    sourceFile.addStatement(node);
                }
            }
        }

        return sourceFile;
    }

    /**
     * Convert JSON node to AST node using the new converter architecture.
     * CC: 2 (switch + try-catch)
     */
    private AstNode convertJsonNode(JsonNode json) {
        // Guard Clause: validate kindName exists
        JsonNode kindNameNode = json.get("kindName");
        if (kindNameNode == null || kindNameNode.isNull()) {
            throw new ParserException("JSON node missing required 'kindName' field");
        }
        String kindName = kindNameNode.asText();

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
            case "EmptyStatement":
                // Empty statement - just a semicolon
                return new ExpressionStatement(";");
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
            case "SuperKeyword":
                // These are expression literals - convert to expression statement
                return new ExpressionStatement(convertExpressionToString(json));
            default:
                // Use the new converter architecture for all other cases
                try {
                    return conversionContext.convertStatement(json);
                } catch (Exception e) {
                    // Log the exception for debugging
                    LOGGER.warn("Failed to convert {} using new converter, trying fallback: {}", kindName, e.getMessage());
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
    private AstNode convertJsonNodeLegacy(JsonNode json, String kindName) {
        // Simplified fallback - just handle expression statements as strings
        if (kindName.endsWith("Expression")) {
            return new ExpressionStatement(convertExpressionToString(json));
        }
        return null;
    }

    private SourceFile convertSourceFile(JsonNode json) {
        return new SourceFile(json.get("fileName").asText());
    }

    private Decorator convertDecorator(JsonNode json) {
        String name = json.get("name").asText();
        return new Decorator(name);
    }

    /**
     * Safely gets the kindName from a JSON object.
     * Returns empty string if kindName is not present.
     */
    private String getKindName(JsonNode json) {
        return json.has("kindName") ? json.get("kindName").asText() : "";
    }

    /**
     * Converts a JSON expression to a JavaScript string representation.
     * Now delegates to the new converter architecture.
     */
    private String convertExpressionToString(JsonNode exprJson) {
        // Use the new converter architecture
        try {
            return conversionContext.convertExpression(exprJson);
        } catch (Exception e) {
            // Fallback: return the text if available
            String fallbackText = exprJson.has("text") ? exprJson.get("text").asText() : "";
            if (!fallbackText.isEmpty()) {
                return fallbackText.trim();
            }
            // Last resort: return the JSON as string
            return exprJson.toString();
        }
    }
}
