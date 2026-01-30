package com.ets2jsc.parser;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.AstVisitor;
import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.CallExpression;
import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.ComponentExpression;
import com.ets2jsc.ast.ComponentRegistry;
import com.ets2jsc.ast.ComponentStatement;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.ast.ForeachStatement;
import com.ets2jsc.ast.IfStatement;
import com.ets2jsc.ast.ImportStatement;
import com.ets2jsc.ast.ExportStatement;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.transformer.ComponentExpressionTransformer;
import com.ets2jsc.generator.CodeGenerator;
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
 */
public class TypeScriptScriptParser {

    private final String scriptPath;
    private final Gson gson = new Gson();

    public TypeScriptScriptParser() {
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
            case "ForInStatement":
                return convertForInStatement(json);
            case "WhileStatement":
                return convertWhileStatement(json);
            case "DoStatement":
                return convertDoStatement(json);
            case "ForStatement":
                return convertForStatement(json);
            case "SwitchStatement":
                return convertSwitchStatement(json);
            case "TryStatement":
                return convertTryStatement(json);
            case "BreakStatement":
                return new ExpressionStatement("break;");
            case "ContinueStatement":
                return new ExpressionStatement("continue;");
            case "VariableStatement":
            case "FirstStatement":
                return convertVariableStatement(json);
            case "ImportDeclaration":
                return convertImportDeclaration(json);
            case "ExportDeclaration":
                return convertExportDeclaration(json);
            case "BinaryExpression":
                return convertBinaryExpression(json);
            case "ConditionalExpression":
                return convertConditionalExpression(json);
            case "ArrayLiteralExpression":
                return convertArrayLiteralExpression(json);
            case "ObjectLiteralExpression":
                return convertObjectLiteralExpression(json);
            case "PropertyAssignment":
                return convertPropertyAssignment(json);
            case "ShorthandPropertyAssignment":
                return convertShorthandPropertyAssignment(json);
            case "TemplateExpression":
            case "NoSubstitutionTemplateLiteral":
                // Template expressions are handled via convertExpressionToString
                return new ExpressionStatement(convertExpressionToString(json));
            case "ImportExpression":
                // Dynamic imports are handled via convertExpressionToString
                return new ExpressionStatement(convertExpressionToString(json));
            case "InterfaceDeclaration":
                // Interface declarations don't generate runtime code
                return null;
            case "FunctionDeclaration":
                return convertFunctionDeclaration(json);
            case "IfStatement":
                return convertIfStatement(json);
            case "TrueLiteral":
            case "FalseLiteral":
            case "NullLiteral":
            case "UndefinedLiteral":
                return convertLiteralExpression(json);
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

        // Convert decorators first
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        boolean hasEntryDecorator = false;
        if (decoratorsArray != null) {
            for (JsonElement decElement : decoratorsArray) {
                JsonObject decObj = decElement.getAsJsonObject();
                String decName = decObj.get("name").getAsString();
                classDecl.addDecorator(new Decorator(decName));
                // Check for @Entry decorator
                if ("Entry".equals(decName)) {
                    hasEntryDecorator = true;
                }
            }
        }

        // Set export flag
        boolean isExported = json.has("isExport") && json.get("isExport").getAsBoolean();
        if (isExported) {
            classDecl.setExport(true);
        } else if (hasEntryDecorator) {
            // @Entry components must be exported - automatically add export
            classDecl.setExport(true);
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
        JsonElement bodyElem = json.get("body");
        if (bodyElem != null && !bodyElem.isJsonNull()) {
            AstNode body = convertJsonNode(bodyElem.getAsJsonObject());
            methodDecl.setBody(body);
        }

        return methodDecl;
    }

    /**
     * Convert a standalone function declaration (not a class method).
     * Standalone functions are converted to ExpressionStatement with function syntax.
     */
    private AstNode convertFunctionDeclaration(JsonObject json) {
        String name = json.get("name").getAsString();

        // Check for async modifier
        boolean isAsync = false;
        JsonArray modifiersArray = json.getAsJsonArray("modifiers");
        if (modifiersArray != null) {
            for (JsonElement modElement : modifiersArray) {
                JsonObject modObj = modElement.getAsJsonObject();
                String modKindName = modObj.has("kindName") ? modObj.get("kindName").getAsString() : "";
                if ("AsyncKeyword".equals(modKindName) || "async".equals(modKindName)) {
                    isAsync = true;
                    break;
                }
            }
        }

        // Convert parameters
        JsonArray paramsArray = json.getAsJsonArray("parameters");
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.size() > 0) {
            for (int i = 0; i < paramsArray.size(); i++) {
                if (i > 0) params.append(", ");
                JsonObject paramObj = paramsArray.get(i).getAsJsonObject();
                String paramName = paramObj.get("name").getAsString();
                params.append(paramName);
            }
        }

        // Convert body
        JsonElement bodyElem = json.get("body");
        StringBuilder body = new StringBuilder();
        if (bodyElem != null && !bodyElem.isJsonNull()) {
            JsonObject bodyObj = bodyElem.getAsJsonObject();
            JsonArray statementsArray = bodyObj.getAsJsonArray("statements");
            if (statementsArray != null) {
                for (JsonElement stmtElem : statementsArray) {
                    JsonObject stmtObj = stmtElem.getAsJsonObject();
                    AstNode stmt = convertJsonNode(stmtObj);
                    if (stmt != null) {
                        String stmtCode = stmt.accept(new CodeGenerator());
                        body.append("  ").append(stmtCode);
                    }
                }
            }
        }

        // Build function declaration string
        StringBuilder sb = new StringBuilder();
        if (isAsync) {
            sb.append("async ");
        }
        sb.append("function ").append(name).append("(").append(params).append(") {\n");
        sb.append(body);
        sb.append("}\n");

        return new ExpressionStatement(sb.toString());
    }

    private PropertyDeclaration convertPropertyDeclaration(JsonObject json) {
        String name = json.get("name").getAsString();
        PropertyDeclaration propDecl = new PropertyDeclaration(name);

        JsonElement typeElem = json.get("type");
        if (typeElem != null && !typeElem.isJsonNull()) {
            String type = typeElem.getAsString();
            if (type != null && !type.isEmpty()) {
                propDecl.setTypeAnnotation(type);
            }
        }

        JsonElement initElem = json.get("initializer");
        if (initElem != null && !initElem.isJsonNull()) {
            String initializer = null;
            // First check if there's a pre-processed initializerText (priority)
            if (json.has("initializerText")) {
                initializer = json.get("initializerText").getAsString();
            } else if (initElem.isJsonObject()) {
                // Complex expression - convert it to strip TypeScript syntax
                // Also check if the JSON object has a 'text' property (generated in parse-ets.js)
                JsonObject initObj = initElem.getAsJsonObject();
                if (initObj.has("text")) {
                    initializer = initObj.get("text").getAsString();
                } else {
                    initializer = convertExpressionToString(initObj);
                }
            } else if (initElem.isJsonPrimitive() && initElem.getAsJsonPrimitive().isString()) {
                // Simple string value
                initializer = initElem.getAsString();
            }

            if (initializer != null && !initializer.isEmpty()) {
                propDecl.setInitializer(initializer);
            }
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
            int i = 0;
            while (i < statementsArray.size()) {
                JsonObject stmtObj = statementsArray.get(i).getAsJsonObject();
                String stmtKindName = stmtObj.has("kindName") ? stmtObj.get("kindName").getAsString() : "";

                // Check if this is an ExpressionStatement with a component CallExpression followed by a Block
                if ("ExpressionStatement".equals(stmtKindName) && i + 1 < statementsArray.size()) {
                    JsonObject exprObj = stmtObj.getAsJsonObject("expression");
                    String exprKindName = exprObj.has("kindName") ? exprObj.get("kindName").getAsString() : "";

                    if ("CallExpression".equals(exprKindName)) {
                        JsonObject idObj = exprObj.getAsJsonObject("expression");
                        if (idObj != null && "Identifier".equals(idObj.get("kindName").getAsString())) {
                            String componentName = idObj.get("name").getAsString();
                            // Check if this is a built-in container component (Column, Row, Stack, etc.)
                            if (ComponentRegistry.isContainerComponent(componentName)) {
                                JsonObject nextStmtObj = statementsArray.get(i + 1).getAsJsonObject();
                                String nextKindName = nextStmtObj.has("kindName") ? nextStmtObj.get("kindName").getAsString() : "";

                                if ("Block".equals(nextKindName)) {
                                    // This is a component with children block like Column() { ... }
                                    // Convert the component call
                                    AstNode componentStmt = convertJsonNode(stmtObj);
                                    // Convert the children block
                                    Block childrenBlock = (Block) convertJsonNode(nextStmtObj);

                                    // Associate the children with the component
                                    if (componentStmt instanceof ComponentStatement) {
                                        ((ComponentStatement) componentStmt).setChildren(childrenBlock);
                                    } else if (componentStmt instanceof ExpressionStatement) {
                                        // Transform ExpressionStatement to ComponentStatement with children
                                        ExpressionStatement exprStmt = (ExpressionStatement) componentStmt;
                                        String expr = exprStmt.getExpression();
                                        // Extract component name from expression like "Column()"
                                        ComponentStatement compStmt = (ComponentStatement) ComponentExpressionTransformer.transform(expr);
                                        if (compStmt != null) {
                                            compStmt.setChildren(childrenBlock);
                                            block.addStatement(compStmt);
                                        } else {
                                            block.addStatement(componentStmt);
                                        }
                                    } else {
                                        block.addStatement(componentStmt);
                                    }
                                    // Skip the next statement (the children block) as we've already processed it
                                    i += 2;
                                    continue;
                                }
                            }
                        }
                    }
                }

                AstNode stmt = convertJsonNode(stmtObj);
                if (stmt != null) {
                    block.addStatement(stmt);
                }
                i++;
            }
        }
        return block;
    }

    private AstNode convertExpressionStatement(JsonObject json) {
        JsonObject exprObj = json.getAsJsonObject("expression");
        String kindName = exprObj.has("kindName") ? exprObj.get("kindName").getAsString() : "";

        // Check for special components: ForEach
        if ("CallExpression".equals(kindName)) {
            String componentName = exprObj.has("componentName") ? exprObj.get("componentName").getAsString() : "";
            if ("ForEach".equals(componentName)) {
                return convertForEachExpression(exprObj);
            }
        }

        String expression = convertExpressionToString(exprObj);
        return new ExpressionStatement(expression);
    }

    private ForeachStatement convertForEachExpression(JsonObject json) {
        // ForEach has 3 arguments: array, itemGenerator, keyGenerator
        JsonArray argsArray = json.getAsJsonArray("arguments");

        String arrayExpr = "";
        String itemGenExpr = "";
        String keyGenExpr = "";

        if (argsArray != null && argsArray.size() >= 2) {
            arrayExpr = convertExpressionToString(argsArray.get(0).getAsJsonObject());
            itemGenExpr = convertExpressionToString(argsArray.get(1).getAsJsonObject());

            if (argsArray.size() >= 3) {
                keyGenExpr = convertExpressionToString(argsArray.get(2).getAsJsonObject());
            }
        }

        return new ForeachStatement(arrayExpr, itemGenExpr, keyGenExpr);
    }

    private IfStatement convertIfStatement(JsonObject json) {
        // IfStatement has: expression (condition), thenStatement, elseStatement
        JsonObject exprObj = json.getAsJsonObject("expression");
        String condition = convertExpressionToString(exprObj);

        // Convert then block
        JsonElement thenElem = json.get("thenStatement");
        Block thenBlock = new Block();
        if (thenElem != null && !thenElem.isJsonNull()) {
            AstNode thenNode = convertJsonNode(thenElem.getAsJsonObject());
            if (thenNode instanceof Block) {
                thenBlock = (Block) thenNode;
            } else if (thenNode != null) {
                // Single statement, wrap it in a block
                thenBlock = new Block();
                thenBlock.addStatement(thenNode);
            }
        }

        // Convert else block (if exists)
        JsonElement elseElem = json.get("elseStatement");
        Block elseBlock = null;
        if (elseElem != null && !elseElem.isJsonNull()) {
            AstNode elseNode = convertJsonNode(elseElem.getAsJsonObject());
            if (elseNode instanceof Block) {
                elseBlock = (Block) elseNode;
            } else if (elseNode != null) {
                // Single statement, wrap it in a block
                elseBlock = new Block();
                elseBlock.addStatement(elseNode);
            }
        }

        return new IfStatement(condition, thenBlock, elseBlock);
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
            case "ImportKeyword":
                // ImportKeyword is the "import" keyword, treat it like an identifier
                String text = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
                return text.trim();
            case "PropertyAccessExpression":
                String propResult = convertPropertyAccessToString(exprJson);
                return propResult.trim();  // Remove leading/trailing whitespace
            case "StringLiteral":
                // String literals may already have quotes from parse-ets.js
                String strText = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
                if (strText.startsWith("\"") || strText.startsWith("'")) {
                    return strText;
                }
                return "\"" + strText + "\"";
            case "NumericLiteral":
                String numText = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
                // Check for computed value (for octal conversion)
                if (exprJson.has("value")) {
                    Number value = exprJson.get("value").getAsNumber();
                    return String.valueOf(value.intValue());
                }
                // Manual octal conversion if needed
                if (numText.startsWith("0o") || numText.startsWith("0O")) {
                    String octalStr = numText.substring(2);
                    try {
                        int decimalValue = Integer.parseInt(octalStr, 8);
                        return String.valueOf(decimalValue);
                    } catch (NumberFormatException e) {
                        return numText;
                    }
                }
                return numText.trim();
            case "BinaryExpression":
                return convertBinaryExpressionToString(exprJson);
            case "ConditionalExpression":
                return convertConditionalExpressionToString(exprJson);
            case "ArrayLiteralExpression":
                return convertArrayLiteralToString(exprJson);
            case "ObjectLiteralExpression":
                return convertObjectLiteralToString(exprJson);
            case "TrueLiteral":
                return "true";
            case "FalseLiteral":
                return "false";
            case "NullLiteral":
                return "null";
            case "UndefinedLiteral":
                return "undefined";
            case "ThisKeyword":
                return "this";
            case "ArrowFunction":
                // Arrow functions are already handled in parse-ets.js
                String arrowText = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
                return arrowText.trim();
            case "PropertyAssignment": {
                String propName = exprJson.has("name") ? exprJson.get("name").getAsString() : "";
                JsonObject propValue = exprJson.getAsJsonObject("value");
                String valueStr = propValue != null ? convertExpressionToString(propValue) : "";
                return propName + ": " + valueStr;
            }
            case "ShorthandPropertyAssignment": {
                String shortName = exprJson.has("name") ? exprJson.get("name").getAsString() : "";
                return shortName;
            }
            case "NewExpression": {
                // Handle new Constructor() expressions
                JsonObject newExpr = exprJson.getAsJsonObject("expression");
                String exprStr = newExpr != null ? convertExpressionToString(newExpr) : "";
                StringBuilder sb = new StringBuilder();
                sb.append("new ").append(exprStr).append("(");
                JsonArray arguments = exprJson.getAsJsonArray("arguments");
                if (arguments != null && arguments.size() > 0) {
                    List<String> argStrings = new ArrayList<>();
                    for (JsonElement arg : arguments) {
                        String argStr = convertExpressionToString(arg.getAsJsonObject());
                        argStrings.add(argStr);
                    }
                    sb.append(String.join(", ", argStrings));
                }
                sb.append(")");
                return sb.toString();
            }
            case "ElementAccessExpression": {
                // Handle array[index] or object[property] expressions
                JsonObject elementObj = exprJson.getAsJsonObject("expression");
                String elementStr = elementObj != null ? convertExpressionToString(elementObj) : "";
                JsonObject argumentExpr = exprJson.getAsJsonObject("argumentExpression");
                String argStr = argumentExpr != null ? convertExpressionToString(argumentExpr) : "";
                return elementStr + "[" + argStr + "]";
            }
            case "ParenthesizedExpression": {
                // Handle (expression)
                JsonObject parenExpr = exprJson.getAsJsonObject("expression");
                String exprStr = parenExpr != null ? convertExpressionToString(parenExpr) : "";
                return "(" + exprStr + ")";
            }
            case "TypeOfExpression": {
                // Handle typeof expression
                JsonObject typeOfExpr = exprJson.getAsJsonObject("expression");
                String exprStr = typeOfExpr != null ? convertExpressionToString(typeOfExpr) : "";
                return "typeof " + exprStr;
            }
            case "PrefixUnaryExpression": {
                // Handle prefix unary expressions like -1, !true, etc.
                String operator = exprJson.has("operator") ? exprJson.get("operator").getAsString() : "";
                JsonObject operand = exprJson.getAsJsonObject("operand");
                String operandStr = operand != null ? convertExpressionToString(operand) : "";
                return operator + operandStr;
            }
            case "PostfixUnaryExpression": {
                // Handle postfix unary expressions like i++, i--
                String operator = exprJson.has("operator") ? exprJson.get("operator").getAsString() : "";
                JsonObject operand = exprJson.getAsJsonObject("operand");
                String operandStr = operand != null ? convertExpressionToString(operand) : "";
                return operandStr + operator;
            }
            case "NonNullExpression": {
                // Non-null assertion ! has no runtime effect, just output the expression
                JsonObject expr = exprJson.getAsJsonObject("expression");
                return expr != null ? convertExpressionToString(expr) : "";
            }
            case "AsExpression":
            case "TypeAssertion": {
                // Type assertion has no runtime effect, just output the expression
                // First check if there's a pre-generated text (without the type assertion)
                if (exprJson.has("text")) {
                    return exprJson.get("text").getAsString();
                }
                JsonObject expr = exprJson.getAsJsonObject("expression");
                return expr != null ? convertExpressionToString(expr) : "";
            }
            case "SpreadElement": {
                JsonObject expr = exprJson.getAsJsonObject("expression");
                String exprStr = expr != null ? convertExpressionToString(expr) : "";
                return "..." + exprStr;
            }
            case "SpreadAssignment": {
                JsonObject expr = exprJson.getAsJsonObject("expression");
                String exprStr = expr != null ? convertExpressionToString(expr) : "";
                return "..." + exprStr;
            }
            case "TemplateExpression": {
                // Handle template strings with interpolation: `hello ${name}`
                JsonObject head = exprJson.getAsJsonObject("head");
                String headText = head != null && head.has("text") ? head.get("text").getAsString() : "";

                JsonArray spans = exprJson.getAsJsonArray("templateSpans");

                StringBuilder sb = new StringBuilder();
                sb.append("`");

                // Add head text
                sb.append(escapeTemplateLiteral(headText));

                if (spans != null) {
                    for (JsonElement spanElem : spans) {
                        JsonObject span = spanElem.getAsJsonObject();

                        // Handle interpolation expression
                        JsonObject expr = span.getAsJsonObject("expression");
                        String exprStr = expr != null ? convertExpressionToString(expr) : "";
                        sb.append("${").append(exprStr).append("}");

                        // Handle text after interpolation
                        JsonObject literal = span.getAsJsonObject("literal");
                        String litText = literal != null && literal.has("text") ? literal.get("text").getAsString() : "";
                        sb.append(escapeTemplateLiteral(litText));
                    }
                }

                sb.append("`");
                return sb.toString();
            }
            case "NoSubstitutionTemplateLiteral": {
                // Handle template strings without interpolation: `hello world`
                // The text field contains just the content (without backticks)
                String templateText = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
                return "`" + escapeTemplateLiteral(templateText) + "`";
            }
            case "ImportExpression": {
                // Handle dynamic import: await import('module')
                // TypeScript ImportExpression uses 'expression' field (not 'argument')
                JsonObject expr = exprJson.getAsJsonObject("expression");
                String modulePath = expr != null ? convertExpressionToString(expr) : "";
                return "import(" + modulePath + ")";
            }
            default:
                // Fallback: return the text if available
                String fallbackText = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
                return fallbackText.isEmpty() ? exprJson.toString() : fallbackText.trim();
        }
    }

    /**
     * Converts a BinaryExpression to JavaScript string.
     */
    private String convertBinaryExpressionToString(JsonObject json) {
        JsonObject left = json.getAsJsonObject("left");
        String operator = json.has("operator") ? json.get("operator").getAsString() : "";
        JsonObject right = json.getAsJsonObject("right");

        String leftStr = convertExpressionToString(left);
        String rightStr = convertExpressionToString(right);

        return leftStr + " " + operator + " " + rightStr;
    }

    /**
     * Converts a ConditionalExpression to JavaScript string.
     */
    private String convertConditionalExpressionToString(JsonObject json) {
        JsonObject condition = json.getAsJsonObject("condition");
        JsonObject whenTrue = json.getAsJsonObject("whenTrue");
        JsonObject whenFalse = json.getAsJsonObject("whenFalse");

        String condStr = convertExpressionToString(condition);
        String trueStr = convertExpressionToString(whenTrue);
        String falseStr = convertExpressionToString(whenFalse);

        return condStr + " ? " + trueStr + " : " + falseStr;
    }

    /**
     * Converts an ArrayLiteralExpression to JavaScript string.
     */
    private String convertArrayLiteralToString(JsonObject json) {
        JsonArray elements = json.getAsJsonArray("elements");
        if (elements == null || elements.size() == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(convertExpressionToString(elements.get(i).getAsJsonObject()));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Converts an ObjectLiteralExpression to JavaScript string.
     */
    private String convertObjectLiteralToString(JsonObject json) {
        JsonArray properties = json.getAsJsonArray("properties");
        if (properties == null || properties.size() == 0) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < properties.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(convertExpressionToString(properties.get(i).getAsJsonObject()));
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Converts a CallExpression to JavaScript string.
     */
    private String convertCallExpressionToString(JsonObject json) {
        JsonObject expression = json.getAsJsonObject("expression");
        String base = convertExpressionToString(expression);

        // Check for dynamic import pattern: import('module')
        if ("import".equals(base)) {
            JsonArray argsArray = json.getAsJsonArray("arguments");
            if (argsArray != null && argsArray.size() > 0) {
                JsonElement argElement = argsArray.get(0);
                if (argElement.isJsonObject()) {
                    String modulePath = convertExpressionToString(argElement.getAsJsonObject());
                    return "import(" + modulePath + ")";
                } else if (argElement.isJsonPrimitive() && argElement.getAsJsonPrimitive().isString()) {
                    return "import(" + argElement.getAsString() + ")";
                }
            }
        }

        JsonArray argsArray = json.getAsJsonArray("arguments");
        StringBuilder args = new StringBuilder();
        if (argsArray != null) {
            for (int i = 0; i < argsArray.size(); i++) {
                if (i > 0) args.append(", ");
                JsonElement argElement = argsArray.get(i);
                String arg = "";
                if (argElement.isJsonObject()) {
                    // It's a complex expression like ArrowFunction - convert it properly
                    arg = convertExpressionToString(argElement.getAsJsonObject());
                } else if (argElement.isJsonPrimitive() && argElement.getAsJsonPrimitive().isString()) {
                    // It's a simple string argument
                    arg = argElement.getAsString();
                } else if (argElement.isJsonNull()) {
                    arg = "null";
                }
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

    /**
     * Escapes special characters in template literals.
     */
    private String escapeTemplateLiteral(String str) {
        if (str == null) return "";
        // Escape backslashes, backticks, and ${
        return str.replace("\\", "\\\\")
                  .replace("`", "\\`")
                  .replace("${", "\\${");
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
        // Use the pre-generated text if available
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").getAsString());
        }

        JsonObject initializer = json.getAsJsonObject("initializer");
        JsonObject expression = json.getAsJsonObject("expression");
        JsonObject statement = json.getAsJsonObject("statement");
        boolean awaitModifier = json.has("awaitModifier") && json.get("awaitModifier").getAsBoolean();

        String initStr = initializer != null ? convertExpressionToString(initializer) : "";
        String exprStr = expression != null ? convertExpressionToString(expression) : "";

        StringBuilder sb = new StringBuilder();
        if (awaitModifier) {
            sb.append("for await (");
        } else {
            sb.append("for (");
        }
        sb.append(initStr).append(" of ").append(exprStr).append(") {\n");

        // Process loop body statements
        if (statement != null) {
            AstNode stmt = convertJsonNode(statement);
            if (stmt instanceof Block) {
                Block block = (Block) stmt;
                for (AstNode blockStmt : block.getStatements()) {
                    String stmtCode = blockStmt.accept(new CodeGenerator());
                    sb.append("  ").append(stmtCode);
                }
            } else {
                String stmtCode = stmt.accept(new CodeGenerator());
                sb.append("  ").append(stmtCode);
            }
        }

        sb.append("}");

        return new ExpressionStatement(sb.toString());
    }

    private AstNode convertForInStatement(JsonObject json) {
        // Use the pre-generated text if available
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").getAsString());
        }

        JsonObject initializer = json.getAsJsonObject("initializer");
        JsonObject expression = json.getAsJsonObject("expression");
        JsonObject statement = json.getAsJsonObject("statement");

        String initStr = initializer != null ? convertExpressionToString(initializer) : "";
        String exprStr = expression != null ? convertExpressionToString(expression) : "";

        StringBuilder sb = new StringBuilder();
        sb.append("for (").append(initStr).append(" in ").append(exprStr).append(") {\n");

        if (statement != null) {
            AstNode stmt = convertJsonNode(statement);
            if (stmt instanceof Block) {
                Block block = (Block) stmt;
                for (AstNode blockStmt : block.getStatements()) {
                    String stmtCode = blockStmt.accept(new CodeGenerator());
                    sb.append("  ").append(stmtCode);
                }
            } else {
                String stmtCode = stmt.accept(new CodeGenerator());
                sb.append("  ").append(stmtCode);
            }
        }

        sb.append("}");

        return new ExpressionStatement(sb.toString());
    }

    private AstNode convertWhileStatement(JsonObject json) {
        // Use the pre-generated text if available
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").getAsString());
        }

        JsonObject expression = json.getAsJsonObject("expression");
        JsonObject statement = json.getAsJsonObject("statement");

        String condition = convertExpressionToString(expression);

        StringBuilder sb = new StringBuilder();
        sb.append("while (").append(condition).append(") {\n");

        if (statement != null) {
            AstNode stmt = convertJsonNode(statement);
            if (stmt instanceof Block) {
                Block block = (Block) stmt;
                for (AstNode blockStmt : block.getStatements()) {
                    String stmtCode = blockStmt.accept(new CodeGenerator());
                    sb.append("  ").append(stmtCode);
                }
            } else {
                String stmtCode = stmt.accept(new CodeGenerator());
                sb.append("  ").append(stmtCode);
            }
        }

        sb.append("}");

        return new ExpressionStatement(sb.toString());
    }

    private AstNode convertDoStatement(JsonObject json) {
        // Use the pre-generated text if available
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").getAsString());
        }

        JsonObject expression = json.getAsJsonObject("expression");
        JsonObject statement = json.getAsJsonObject("statement");

        String condition = convertExpressionToString(expression);

        StringBuilder sb = new StringBuilder();
        sb.append("do {\n");

        if (statement != null) {
            AstNode stmt = convertJsonNode(statement);
            if (stmt instanceof Block) {
                Block block = (Block) stmt;
                for (AstNode blockStmt : block.getStatements()) {
                    String stmtCode = blockStmt.accept(new CodeGenerator());
                    sb.append("  ").append(stmtCode);
                }
            } else {
                String stmtCode = stmt.accept(new CodeGenerator());
                sb.append("  ").append(stmtCode);
            }
        }

        sb.append("} while (").append(condition).append(")");

        return new ExpressionStatement(sb.toString());
    }

    private AstNode convertForStatement(JsonObject json) {
        // Use the pre-generated text if available
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").getAsString());
        }

        JsonObject initializer = json.getAsJsonObject("initializer");
        JsonObject condition = json.getAsJsonObject("condition");
        JsonObject incrementor = json.getAsJsonObject("incrementor");
        JsonObject statement = json.getAsJsonObject("statement");

        String initStr = initializer != null ? convertExpressionToString(initializer) : "";
        String condStr = condition != null ? convertExpressionToString(condition) : "";
        String incrStr = incrementor != null ? convertExpressionToString(incrementor) : "";

        StringBuilder sb = new StringBuilder();
        sb.append("for (").append(initStr).append("; ");
        sb.append(condStr).append("; ");
        sb.append(incrStr).append(") {\n");

        if (statement != null) {
            AstNode stmt = convertJsonNode(statement);
            if (stmt instanceof Block) {
                Block block = (Block) stmt;
                for (AstNode blockStmt : block.getStatements()) {
                    String stmtCode = blockStmt.accept(new CodeGenerator());
                    sb.append("  ").append(stmtCode);
                }
            } else {
                String stmtCode = stmt.accept(new CodeGenerator());
                sb.append("  ").append(stmtCode);
            }
        }

        sb.append("}");

        return new ExpressionStatement(sb.toString());
    }

    private AstNode convertSwitchStatement(JsonObject json) {
        // Use the pre-generated text if available
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").getAsString());
        }

        JsonObject expression = json.getAsJsonObject("expression");
        JsonObject caseBlock = json.getAsJsonObject("caseBlock");

        String exprStr = expression != null ? convertExpressionToString(expression) : "";

        StringBuilder sb = new StringBuilder();
        sb.append("switch (").append(exprStr).append(") {\n");

        if (caseBlock != null) {
            JsonArray clauses = caseBlock.getAsJsonArray("clauses");
            if (clauses != null) {
                for (JsonElement clauseElem : clauses) {
                    JsonObject clause = clauseElem.getAsJsonObject();
                    String kindName = clause.has("kindName") ? clause.get("kindName").getAsString() : "";

                    if ("CaseClause".equals(kindName)) {
                        JsonObject clauseExpr = clause.getAsJsonObject("expression");
                        String caseExpr = clauseExpr != null ? convertExpressionToString(clauseExpr) : "";
                        sb.append("  case ").append(caseExpr).append(":\n");

                        JsonArray stmts = clause.getAsJsonArray("statements");
                        if (stmts != null) {
                            for (JsonElement stmtElem : stmts) {
                                JsonObject stmt = stmtElem.getAsJsonObject();
                                AstNode stmtNode = convertJsonNode(stmt);
                                if (stmtNode != null) {
                                    String stmtCode = stmtNode.accept(new CodeGenerator());
                                    sb.append("    ").append(stmtCode);
                                }
                            }
                        }
                        sb.append("    break;\n");
                    } else if ("DefaultClause".equals(kindName)) {
                        sb.append("  default:\n");

                        JsonArray stmts = clause.getAsJsonArray("statements");
                        if (stmts != null) {
                            for (JsonElement stmtElem : stmts) {
                                JsonObject stmt = stmtElem.getAsJsonObject();
                                AstNode stmtNode = convertJsonNode(stmt);
                                if (stmtNode != null) {
                                    String stmtCode = stmtNode.accept(new CodeGenerator());
                                    sb.append("    ").append(stmtCode);
                                }
                            }
                        }
                    }
                }
            }
        }

        sb.append("}");

        return new ExpressionStatement(sb.toString());
    }

    private AstNode convertTryStatement(JsonObject json) {
        // Use the pre-generated text if available
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").getAsString());
        }

        JsonObject tryBlock = json.getAsJsonObject("tryBlock");
        JsonObject catchClause = json.getAsJsonObject("catchClause");
        JsonObject finallyBlock = json.getAsJsonObject("finallyBlock");

        StringBuilder sb = new StringBuilder();
        sb.append("try {\n");

        if (tryBlock != null) {
            AstNode tryNode = convertJsonNode(tryBlock);
            if (tryNode instanceof Block) {
                Block block = (Block) tryNode;
                for (AstNode blockStmt : block.getStatements()) {
                    String stmtCode = blockStmt.accept(new CodeGenerator());
                    sb.append("  ").append(stmtCode);
                }
            }
        }

        sb.append("\n}");

        if (catchClause != null) {
            JsonObject varDecl = catchClause.getAsJsonObject("variableDeclaration");
            String varName = "";
            if (varDecl != null) {
                JsonArray declarations = varDecl.getAsJsonArray("declarations");
                if (declarations != null && declarations.size() > 0) {
                    JsonObject decl = declarations.get(0).getAsJsonObject();
                    varName = decl.has("name") ? decl.get("name").getAsString() : "";
                }
            }

            if (!varName.isEmpty()) {
                sb.append(" catch (").append(varName).append(") {\n");
            } else {
                sb.append(" catch {\n");
            }

            JsonObject catchBlock = catchClause.getAsJsonObject("block");
            if (catchBlock != null) {
                AstNode catchNode = convertJsonNode(catchBlock);
                if (catchNode instanceof Block) {
                    Block block = (Block) catchNode;
                    for (AstNode blockStmt : block.getStatements()) {
                        String stmtCode = blockStmt.accept(new CodeGenerator());
                        sb.append("  ").append(stmtCode);
                    }
                }
            }

            sb.append("\n}");
        }

        if (finallyBlock != null) {
            sb.append(" finally {\n");

            AstNode finallyNode = convertJsonNode(finallyBlock);
            if (finallyNode instanceof Block) {
                Block block = (Block) finallyNode;
                for (AstNode blockStmt : block.getStatements()) {
                    String stmtCode = blockStmt.accept(new CodeGenerator());
                    sb.append("  ").append(stmtCode);
                }
            }

            sb.append("\n}");
        }

        return new ExpressionStatement(sb.toString());
    }

    private AstNode convertVariableStatement(JsonObject json) {
        JsonObject declarationList = json.getAsJsonObject("declarationList");
        if (declarationList != null) {
            JsonArray declarations = declarationList.getAsJsonArray("declarations");
            if (declarations != null && declarations.size() > 0) {
                JsonElement declElement = declarations.get(0);
                if (declElement.isJsonObject()) {
                    JsonObject decl = declElement.getAsJsonObject();
                    String name = decl.has("name") ? decl.get("name").getAsString() : "";
                    String type = decl.has("type") ? decl.get("type").getAsString() : "";

                    String init = "";
                    JsonElement initElement = decl.get("initializer");
                    if (initElement != null && initElement.isJsonObject()) {
                        init = convertExpressionToString(initElement.getAsJsonObject());
                    }

                    // Get the declaration keyword (let, const, var) from the source
                    String declarationKind = "const"; // default
                    if (declarationList.has("declarationKind")) {
                        declarationKind = declarationList.get("declarationKind").getAsString();
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append(declarationKind).append(" ").append(name);
                    // Type annotations are not valid in JavaScript, so we skip them
                    // if (type != null && !type.isEmpty()) {
                    //     sb.append(": ").append(type);
                    // }
                    if (!init.isEmpty()) {
                        sb.append(" = ").append(init);
                    }
                    return new ExpressionStatement(sb.toString());
                }
            }
        }
        return new ExpressionStatement("// variable declaration");
    }

    private AstNode convertImportDeclaration(JsonObject json) {
        String moduleSpecifier = json.get("moduleSpecifier").getAsString();
        // Remove quotes from module specifier
        moduleSpecifier = moduleSpecifier.replaceAll("^['\"]|['\"]$", "");

        ImportStatement importStmt = new ImportStatement(moduleSpecifier);

        JsonObject importClauseObj = json.getAsJsonObject("importClause");
        if (importClauseObj != null) {
            // Check for default import
            if (importClauseObj.has("name") && !importClauseObj.get("name").isJsonNull()) {
                String defaultName = importClauseObj.get("name").getAsString();
                importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                    defaultName, defaultName, ImportStatement.ImportSpecifier.SpecifierKind.DEFAULT));
            }

            // Check for named imports or namespace import
            JsonArray namedBindings = importClauseObj.getAsJsonArray("namedBindings");
            if (namedBindings != null) {
                for (JsonElement bindingElement : namedBindings) {
                    JsonObject bindingObj = bindingElement.getAsJsonObject();

                    if (bindingObj.has("kind") && "namespace".equals(bindingObj.get("kind").getAsString())) {
                        // Namespace import: * as Module
                        String name = bindingObj.get("name").getAsString();
                        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                            "*", name, ImportStatement.ImportSpecifier.SpecifierKind.NAMESPACE));
                    } else {
                        // Named import: { A, B as C }
                        String name = bindingObj.get("name").getAsString();
                        String propertyName = bindingObj.has("propertyName") && !bindingObj.get("propertyName").isJsonNull()
                            ? bindingObj.get("propertyName").getAsString() : name;
                        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                            propertyName, name, ImportStatement.ImportSpecifier.SpecifierKind.NAMED));
                    }
                }
            }
        }

        return importStmt;
    }

    private AstNode convertExportDeclaration(JsonObject json) {
        // Check if this is a type-only export
        boolean isTypeOnly = json.has("isTypeOnly") && json.get("isTypeOnly").getAsBoolean();

        // Get module specifier if present (for re-exports)
        String moduleSpecifier = null;
        if (json.has("moduleSpecifier") && !json.get("moduleSpecifier").isJsonNull()) {
            moduleSpecifier = json.get("moduleSpecifier").getAsString();
            moduleSpecifier = moduleSpecifier.replaceAll("^['\"]|['\"]$", "");
        }

        // Build the export statement string
        StringBuilder exportStr = new StringBuilder();

        // Handle named exports/re-exports
        JsonObject exportClauseObj = json.getAsJsonObject("exportClause");
        if (exportClauseObj != null) {
            JsonArray elementsArray = exportClauseObj.getAsJsonArray("elements");
            if (elementsArray != null && elementsArray.size() > 0) {
                exportStr.append("{ ");
                for (int i = 0; i < elementsArray.size(); i++) {
                    if (i > 0) {
                        exportStr.append(", ");
                    }
                    JsonObject element = elementsArray.get(i).getAsJsonObject();
                    String name = element.has("name") ? element.get("name").getAsString() : "";
                    String propertyName = element.has("propertyName") && !element.get("propertyName").isJsonNull()
                        ? element.get("propertyName").getAsString() : null;

                    if (propertyName != null && !propertyName.equals(name)) {
                        // { propertyName as name }
                        exportStr.append(propertyName).append(" as ").append(name);
                    } else {
                        // { name }
                        exportStr.append(name);
                    }
                }
                exportStr.append(" }");

                // Add module specifier if present (re-export)
                if (moduleSpecifier != null && !moduleSpecifier.isEmpty()) {
                    exportStr.append(" from '").append(moduleSpecifier).append("'");
                }
            }
        }

        // Create ExportStatement with the generated string
        String declaration = exportStr.length() > 0 ? exportStr.toString() : null;
        return new ExportStatement(null, isTypeOnly, declaration);
    }

    private AstNode convertBinaryExpression(JsonObject json) {
        // Binary expressions are handled via convertExpressionToString
        return new ExpressionStatement(convertExpressionToString(json));
    }

    private AstNode convertConditionalExpression(JsonObject json) {
        // Conditional expressions are handled via convertExpressionToString
        return new ExpressionStatement(convertExpressionToString(json));
    }

    private AstNode convertArrayLiteralExpression(JsonObject json) {
        // Array literal expressions are handled via convertExpressionToString
        return new ExpressionStatement(convertExpressionToString(json));
    }

    private AstNode convertObjectLiteralExpression(JsonObject json) {
        // Object literal expressions are handled via convertExpressionToString
        return new ExpressionStatement(convertExpressionToString(json));
    }

    private AstNode convertPropertyAssignment(JsonObject json) {
        // Property assignments are handled via convertExpressionToString
        return new ExpressionStatement(convertExpressionToString(json));
    }

    private AstNode convertShorthandPropertyAssignment(JsonObject json) {
        // Shorthand properties are handled via convertExpressionToString
        return new ExpressionStatement(convertExpressionToString(json));
    }

    private AstNode convertLiteralExpression(JsonObject json) {
        // Literal expressions (true, false, null, undefined) are handled via convertExpressionToString
        return new ExpressionStatement(convertExpressionToString(json));
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
