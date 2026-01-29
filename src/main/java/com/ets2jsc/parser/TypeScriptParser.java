package com.ets2jsc.parser;

import com.ets2jsc.ast.*;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/**
 * TypeScript/ETS parser using GraalVM JavaScript engine.
 * Uses TypeScript Compiler API to parse source code into AST.
 */
public class TypeScriptParser {
    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    private Context context;
    private Value typescript;
    private Value ts;
    private boolean graalvmDisabled = false;

    public TypeScriptParser() {
        // Don't create context in constructor to avoid classpath issues on Windows
        // Context will be created lazily when needed
        this.context = null;
        this.typescript = null;
        this.ts = null;

        // On Windows, GraalVM has classpath issues, so disable it
        if (IS_WINDOWS) {
            this.graalvmDisabled = true;
        }
    }

    /**
     * Initialize the GraalVM context if needed.
     */
    private void initContext() {
        if (context == null && !graalvmDisabled) {
            try {
                // Create GraalVM context with JavaScript
                // Disable classpath isolation to avoid Windows path issues
                this.context = Context.newBuilder("js")
                        .allowAllAccess(true)
                        .option("engine.WarnInterpreterOnly", "false")
                        .allowHostClassLoading(false)
                        .build();

                // Try to load TypeScript from node_modules or global installation
                Value typescriptValue = context.eval("js",
                    "try {\n" +
                    "  require('typescript');\n" +
                    "} catch (e) {\n" +
                    "  null;\n" +
                    "}"
                );

                if (typescriptValue != null && !typescriptValue.isNull()) {
                    this.ts = typescriptValue;
                } else {
                    this.ts = null;
                }
                this.typescript = typescriptValue;
            } catch (Throwable e) {
                // Context creation failed, disable GraalVM
                this.graalvmDisabled = true;
                this.ts = null;
                this.typescript = null;
                this.context = null;
            }
        }
    }

    /**
     * Parses ETS/TypeScript source code into a SourceFile AST node.
     */
    public SourceFile parse(String fileName, String sourceCode) {
        // Try to use TypeScript parser if available, fall back to simple parser
        if (!graalvmDisabled) {
            try {
                // Initialize context lazily
                initContext();

                if (ts != null && !ts.isNull()) {
                    return parseWithTypeScript(fileName, sourceCode);
                }
            } catch (Throwable t) {
                // GraalVM or TypeScript parsing failed, disable it
                this.graalvmDisabled = true;
                this.context = null;
                this.ts = null;
                this.typescript = null;
            }
        }

        // Use simple parser
        return parseWithSimpleParser(fileName, sourceCode);
    }

    /**
     * Parses using TypeScript Compiler API.
     */
    private SourceFile parseWithTypeScript(String fileName, String sourceCode) {
        try {
            // Create TypeScript source file
            Value sourceFile = ts.invokeMember("createSourceFile",
                fileName,
                sourceCode,
                ts.getMember("ScriptTarget").getMember("Latest"),
                true
            );

            // Convert TypeScript AST to our AST format
            return convertSourceFile(sourceFile);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse with TypeScript: " + e.getMessage(), e);
        }
    }

    /**
     * Simple regex-based parser as fallback.
     * Parses basic ETS structures without full TypeScript support.
     */
    private SourceFile parseWithSimpleParser(String fileName, String sourceCode) {
        SourceFile sourceFile = new SourceFile(fileName, sourceCode);

        // Simple parsing logic using regex patterns
        String[] lines = sourceCode.split("\n");

        for (String line : lines) {
            line = line.trim();

            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }

            // Detect imports
            if (line.startsWith("import ")) {
                sourceFile.addImport(line);
            }
            // Detect struct/class declarations
            else if (line.startsWith("struct ") || line.startsWith("@Component")) {
                // Parse struct/class
                parseStructDeclaration(sourceCode, sourceFile);
            }
        }

        return sourceFile;
    }

    /**
     * Converts a TypeScript SourceFile to our AST format.
     */
    private SourceFile convertSourceFile(Value tsSourceFile) {
        String fileName = tsSourceFile.getMember("fileName").asString();
        SourceFile sourceFile = new SourceFile(fileName);

        // Process statements
        Value statements = tsSourceFile.getMember("statements");
        if (statements != null && statements.hasArrayElements()) {
            long count = statements.getArraySize();
            for (long i = 0; i < count; i++) {
                Value stmt = statements.getArrayElement(i);
                AstNode node = convertNode(stmt);
                if (node != null) {
                    sourceFile.addStatement(node);
                }
            }
        }

        return sourceFile;
    }

    /**
     * Converts a TypeScript AST node to our AST format.
     */
    private AstNode convertNode(Value tsNode) {
        if (tsNode == null || tsNode.isNull()) {
            return null;
        }

        int kind = tsNode.getMember("kind").asInt();
        String kindName = getSyntaxKindName(kind);

        switch (kindName) {
            case "ClassDeclaration":
            case "StructDeclaration":
                return convertClassDeclaration(tsNode);
            case "MethodDeclaration":
                return convertMethodDeclaration(tsNode);
            case "PropertyDeclaration":
                return convertPropertyDeclaration(tsNode);
            case "Decorator":
                return convertDecorator(tsNode);
            default:
                return null;
        }
    }

    /**
     * Converts a TypeScript ClassDeclaration to our AST format.
     */
    private ClassDeclaration convertClassDeclaration(Value tsClass) {
        String name = tsClass.getMember("name").getMember("escapedText").asString();
        ClassDeclaration classDecl = new ClassDeclaration(name);

        // Check if it's a struct
        int kind = tsClass.getMember("kind").asInt();
        classDecl.setStruct(kind == 252); // StructDeclaration kind

        // Process decorators
        Value decorators = tsClass.getMember("decorators");
        if (decorators != null && decorators.hasArrayElements()) {
            long count = decorators.getArraySize();
            for (long i = 0; i < count; i++) {
                Value dec = decorators.getArrayElement(i);
                Decorator decorator = convertDecorator(dec);
                if (decorator != null) {
                    classDecl.addDecorator(decorator);
                }
            }
        }

        // Process members
        Value members = tsClass.getMember("members");
        if (members != null && members.hasArrayElements()) {
            long count = members.getArraySize();
            for (long i = 0; i < count; i++) {
                Value member = members.getArrayElement(i);
                AstNode node = convertNode(member);
                if (node != null) {
                    classDecl.addMember(node);
                }
            }
        }

        return classDecl;
    }

    /**
     * Converts a TypeScript MethodDeclaration to our AST format.
     */
    private MethodDeclaration convertMethodDeclaration(Value tsMethod) {
        String name = tsMethod.getMember("name").getMember("escapedText").asString();
        MethodDeclaration methodDecl = new MethodDeclaration(name);

        // Process decorators
        Value decorators = tsMethod.getMember("decorators");
        if (decorators != null && decorators.hasArrayElements()) {
            long count = decorators.getArraySize();
            for (long i = 0; i < count; i++) {
                Value dec = decorators.getArrayElement(i);
                Decorator decorator = convertDecorator(dec);
                if (decorator != null) {
                    methodDecl.addDecorator(decorator);
                }
            }
        }

        return methodDecl;
    }

    /**
     * Converts a TypeScript PropertyDeclaration to our AST format.
     */
    private PropertyDeclaration convertPropertyDeclaration(Value tsProperty) {
        String name = tsProperty.getMember("name").getMember("escapedText").asString();
        PropertyDeclaration propDecl = new PropertyDeclaration(name);

        // Process decorators
        Value decorators = tsProperty.getMember("decorators");
        if (decorators != null && decorators.hasArrayElements()) {
            long count = decorators.getArraySize();
            for (long i = 0; i < count; i++) {
                Value dec = decorators.getArrayElement(i);
                Decorator decorator = convertDecorator(dec);
                if (decorator != null) {
                    propDecl.addDecorator(decorator);
                }
            }
        }

        return propDecl;
    }

    /**
     * Converts a TypeScript Decorator to our AST format.
     */
    private Decorator convertDecorator(Value tsDecorator) {
        Value expression = tsDecorator.getMember("expression");
        if (expression == null) {
            return null;
        }

        String name;
        if (expression.hasMember("expression")) {
            // CallExpression decorator
            Value callee = expression.getMember("expression");
            name = callee.getMember("escapedText").asString();
        } else {
            // Simple identifier
            name = expression.getMember("escapedText").asString();
        }

        return new Decorator(name);
    }

    /**
     * Gets the syntax kind name from its numeric value.
     */
    private String getSyntaxKindName(int kind) {
        // Map common TypeScript syntax kinds to names
        switch (kind) {
            case 252: return "StructDeclaration";
            case 223: return "ClassDeclaration";
            case 218: return "MethodDeclaration";
            case 169: return "PropertyDeclaration";
            case 141: return "Decorator";
            default: return "Unknown";
        }
    }

    /**
     * Parses struct declaration from source code (simple parser).
     */
    private void parseStructDeclaration(String sourceCode, SourceFile sourceFile) {
        // This is a simplified parser for basic ETS structures
        // In production, you would use the full TypeScript parser

        // Find struct keyword
        int structIndex = sourceCode.indexOf("struct ");
        if (structIndex == -1) {
            return;
        }

        // Extract struct name
        int nameStart = structIndex + 7;
        int nameEnd = sourceCode.indexOf("{", nameStart);
        if (nameEnd == -1) {
            return;
        }

        String structName = sourceCode.substring(nameStart, nameEnd).trim();
        ClassDeclaration classDecl = new ClassDeclaration(structName);
        classDecl.setStruct(true);

        // Check for @Component decorator
        int componentIndex = sourceCode.lastIndexOf("@Component", structIndex);
        if (componentIndex != -1) {
            classDecl.addDecorator(new Decorator("Component"));
        }

        sourceFile.addStatement(classDecl);
    }

    /**
     * Closes the parser and releases resources.
     */
    public void close() {
        if (context != null) {
            context.close();
        }
    }
}
