package com.ets2jsc.parser;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.AstVisitor;
import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.CallExpression;
import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.ComponentExpression;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.ast.SourceFile;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * TypeScript/ETS parser using Node.js and TypeScript Compiler API.
 * Provides full TypeScript syntax parsing capabilities.
 */
public class TypeScriptScriptParser {

    private final String scriptPath;
    private final Gson gson = new Gson();

    public TypeScriptScriptParser() {
        // Locate the parse-ets.js script
        String resourcePath = "src/main/resources/typescript-parser/parse-ets.js";
        Path scriptFile = Path.of(resourcePath).toAbsolutePath();

        if (!Files.exists(scriptFile)) {
            throw new RuntimeException("TypeScript parser script not found: " + scriptFile);
        }

        this.scriptPath = scriptFile.toString();
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
     * Convert JSON node to AST node.
     */
    private AstNode convertJsonNode(JsonObject json) {
        String kindName = json.get("kindName").getAsString();

        switch (kindName) {
            case "SourceFile":
                return convertSourceFile(json);
            case "ClassDeclaration":
                return convertClassDeclaration(json);
            case "MethodDeclaration":
                return convertMethodDeclaration(json);
            case "PropertyDeclaration":
                return convertPropertyDeclaration(json);
            case "Decorator":
                return convertDecorator(json);
            case "Block":
                return convertBlock(json);
            case "ExpressionStatement":
                return convertExpressionStatement(json);
            case "CallExpression":
                return convertCallExpression(json);
            case "Identifier":
                return convertIdentifier(json);
            case "ReturnStatement":
                return convertReturnStatement(json);
            case "AwaitExpression":
                return convertAwaitExpression(json);
            case "ForOfStatement":
                return convertForOfStatement(json);
            case "VariableStatement":
            case "FirstStatement":
                return convertVariableStatement(json);
            default:
                return null;
        }
    }

    private SourceFile convertSourceFile(JsonObject json) {
        return new SourceFile(json.get("fileName").getAsString());
    }

    private ClassDeclaration convertClassDeclaration(JsonObject json) {
        String name = json.get("name").getAsString();
        ClassDeclaration classDecl = new ClassDeclaration(name);

        // Convert decorators
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        if (decoratorsArray != null) {
            for (JsonElement decElement : decoratorsArray) {
                JsonObject decObj = decElement.getAsJsonObject();
                String decName = decObj.get("name").getAsString();
                classDecl.addDecorator(new Decorator(decName));
            }
        }

        // Convert members
        JsonArray membersArray = json.getAsJsonArray("members");
        if (membersArray != null) {
            for (JsonElement memberElement : membersArray) {
                JsonObject memberObj = memberElement.getAsJsonObject();
                AstNode member = convertJsonNode(memberObj);
                if (member instanceof PropertyDeclaration) {
                    classDecl.addMember((PropertyDeclaration) member);
                } else if (member instanceof MethodDeclaration) {
                    classDecl.addMember((MethodDeclaration) member);
                }
            }
        }

        return classDecl;
    }

    private MethodDeclaration convertMethodDeclaration(JsonObject json) {
        String name = json.get("name").getAsString();
        MethodDeclaration methodDecl = new MethodDeclaration(name);

        // Convert decorators
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        if (decoratorsArray != null) {
            for (JsonElement decElement : decoratorsArray) {
                JsonObject decObj = decElement.getAsJsonObject();
                String decName = decObj.get("name").getAsString();
                methodDecl.addDecorator(new Decorator(decName));
            }
        }

        // Convert modifiers (static, etc.)
        JsonArray modifiersArray = json.getAsJsonArray("modifiers");
        if (modifiersArray != null) {
            for (JsonElement modElement : modifiersArray) {
                JsonObject modObj = modElement.getAsJsonObject();
                String modKindName = modObj.has("kindName") ? modObj.get("kindName").getAsString() : "";
                // Check for static keyword
                if ("StaticKeyword".equals(modKindName) || "static".equals(modKindName)) {
                    methodDecl.setStatic(true);
                }
                // Check for async keyword
                if ("AsyncKeyword".equals(modKindName) || "async".equals(modKindName)) {
                    methodDecl.setAsync(true);
                }
            }
        }

        // Convert parameters
        JsonArray paramsArray = json.getAsJsonArray("parameters");
        if (paramsArray != null) {
            for (JsonElement paramElement : paramsArray) {
                JsonObject paramObj = paramElement.getAsJsonObject();
                String paramName = paramObj.get("name").getAsString();
                String paramType = paramObj.get("type").getAsString();
                MethodDeclaration.Parameter param = new MethodDeclaration.Parameter(paramName, paramType);
                methodDecl.addParameter(param);
            }
        }

        // Convert body
        JsonObject bodyObj = json.getAsJsonObject("body");
        if (bodyObj != null) {
            AstNode body = convertJsonNode(bodyObj);
            methodDecl.setBody(body);
        }

        return methodDecl;
    }

    private PropertyDeclaration convertPropertyDeclaration(JsonObject json) {
        String name = json.get("name").getAsString();
        PropertyDeclaration propDecl = new PropertyDeclaration(name);

        String type = json.get("type").getAsString();
        if (type != null && !type.isEmpty()) {
            propDecl.setTypeAnnotation(type);
        }

        String initializer = json.get("initializer").getAsString();
        if (initializer != null && !initializer.isEmpty()) {
            propDecl.setInitializer(initializer);
        }

        // Convert decorators
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        if (decoratorsArray != null) {
            for (JsonElement decElement : decoratorsArray) {
                JsonObject decObj = decElement.getAsJsonObject();
                String decName = decObj.get("name").getAsString();
                propDecl.addDecorator(new Decorator(decName));
            }
        }

        return propDecl;
    }

    private Decorator convertDecorator(JsonObject json) {
        String name = json.get("name").getAsString();
        return new Decorator(name);
    }

    private Block convertBlock(JsonObject json) {
        Block block = new Block();
        JsonArray statementsArray = json.getAsJsonArray("statements");
        if (statementsArray != null) {
            for (JsonElement stmtElement : statementsArray) {
                JsonObject stmtObj = stmtElement.getAsJsonObject();
                AstNode stmt = convertJsonNode(stmtObj);
                if (stmt != null) {
                    block.addStatement(stmt);
                }
            }
        }
        return block;
    }

    private ExpressionStatement convertExpressionStatement(JsonObject json) {
        JsonObject exprObj = json.getAsJsonObject("expression");
        String expression = convertExpressionToString(exprObj);
        return new ExpressionStatement(expression);
    }

    private CallExpression convertCallExpression(JsonObject json) {
        JsonObject exprObj = json.getAsJsonObject("expression");
        String expression = convertExpressionToString(exprObj);
        return new CallExpression(expression);
    }

    /**
     * Converts a JSON expression to a JavaScript string representation.
     */
    private String convertExpressionToString(JsonObject exprJson) {
        String kindName = exprJson.has("kindName") ? exprJson.get("kindName").getAsString() : "";

        switch (kindName) {
            case "CallExpression":
                String callResult = convertCallExpressionToString(exprJson);
                return callResult.trim();  // Remove leading/trailing whitespace
            case "AwaitExpression":
                JsonObject awaitExpr = exprJson.getAsJsonObject("expression");
                if (awaitExpr != null) {
                    String awaitResult = convertExpressionToString(awaitExpr);
                    return "await " + awaitResult.trim();
                }
                return "await";
            case "Identifier":
                String text = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
                return text.trim();
            case "PropertyAccessExpression":
                String propResult = convertPropertyAccessToString(exprJson);
                return propResult.trim();  // Remove leading/trailing whitespace
            case "StringLiteral":
            case "NumericLiteral":
                String litText = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
                return litText.trim();
            default:
                // Fallback: return the text if available
                String fallbackText = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
                return fallbackText.isEmpty() ? exprJson.toString() : fallbackText.trim();
        }
    }

    /**
     * Converts a CallExpression to JavaScript string.
     */
    private String convertCallExpressionToString(JsonObject json) {
        JsonObject expression = json.getAsJsonObject("expression");
        String base = convertExpressionToString(expression);

        JsonArray argsArray = json.getAsJsonArray("arguments");
        StringBuilder args = new StringBuilder();
        if (argsArray != null) {
            for (int i = 0; i < argsArray.size(); i++) {
                if (i > 0) args.append(", ");
                String arg = argsArray.get(i).getAsString();
                args.append(arg != null ? arg.trim() : "");
            }
        }

        return base + "(" + args + ")";
    }

    /**
     * Converts a PropertyAccessExpression to JavaScript string.
     */
    private String convertPropertyAccessToString(JsonObject json) {
        JsonObject expression = json.getAsJsonObject("expression");
        String base = convertExpressionToString(expression);
        String property = json.has("name") ? json.get("name").getAsString().trim() : "";

        // Check if this is a chained call (expression is CallExpression with arguments)
        JsonArray argsArray = json.getAsJsonArray("arguments");
        if (argsArray != null && argsArray.size() > 0) {
            StringBuilder args = new StringBuilder();
            for (int i = 0; i < argsArray.size(); i++) {
                if (i > 0) args.append(", ");
                String arg = argsArray.get(i).getAsString();
                args.append(arg != null ? arg.trim() : "");
            }
            return base + "." + property + "(" + args + ")";
        }

        return base + "." + property;
    }

    private Identifier convertIdentifier(JsonObject json) {
        String name = json.get("name").getAsString();
        Identifier identifier = new Identifier(name);
        String text = json.get("text").getAsString();
        if (text != null) {
            identifier.setText(text);
        }
        return identifier;
    }

    private AstNode convertReturnStatement(JsonObject json) {
        JsonObject exprObj = json.getAsJsonObject("expression");
        if (exprObj != null) {
            String expression = convertExpressionToString(exprObj);
            return new ExpressionStatement("return " + expression);
        }
        return new ExpressionStatement("return");
    }

    private AstNode convertAwaitExpression(JsonObject json) {
        JsonObject exprObj = json.getAsJsonObject("expression");
        if (exprObj != null) {
            // Convert the expression being awaited
            String expression = convertExpressionToString(exprObj);
            return new ExpressionStatement("await " + expression);
        }
        return new ExpressionStatement("await");
    }

    private AstNode convertForOfStatement(JsonObject json) {
        // For simplicity, generate a placeholder comment
        // A full implementation would parse the initializer, expression, and statement
        return new ExpressionStatement("// for...of loop");
    }

    private AstNode convertVariableStatement(JsonObject json) {
        JsonObject declarationList = json.getAsJsonObject("declarationList");
        if (declarationList != null) {
            JsonArray declarations = declarationList.getAsJsonArray("declarations");
            if (declarations != null && declarations.size() > 0) {
                JsonObject decl = declarations.get(0).getAsJsonObject();
                String name = decl.has("name") ? decl.get("name").getAsString() : "";
                String type = decl.has("type") ? decl.get("type").getAsString() : "";
                JsonObject initializer = decl.getAsJsonObject("initializer");
                String init = "";
                if (initializer != null) {
                    init = convertExpressionToString(initializer);
                }

                StringBuilder sb = new StringBuilder();
                sb.append("const ").append(name);
                if (type != null && !type.isEmpty()) {
                    sb.append(": ").append(type);
                }
                if (!init.isEmpty()) {
                    sb.append(" = ").append(init);
                }
                return new ExpressionStatement(sb.toString());
            }
        }
        return new ExpressionStatement("// variable declaration");
    }
}

/**
 * Simple Identifier node for tracking.
 */
class Identifier implements AstNode {
    private final String name;
    private String text;

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text != null ? text : name;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getType() {
        return "Identifier";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return null;
    }
}
