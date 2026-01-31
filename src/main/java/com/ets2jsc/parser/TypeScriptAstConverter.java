package com.ets2jsc.parser;

import com.ets2jsc.ast.*;
import org.graalvm.polyglot.Value;

/**
 * Handles conversion from GraalVM TypeScript AST to internal AST format.
 * Extracted from TypeScriptParser to reduce class size and complexity.
 */
public class TypeScriptAstConverter {

    // TypeScript SyntaxKind constants
    private static final int STRUCT_DECLARATION_KIND = 252;
    private static final int CLASS_DECLARATION_KIND = 223;
    private static final int METHOD_DECLARATION_KIND = 218;
    private static final int PROPERTY_DECLARATION_KIND = 169;
    private static final int DECORATOR_KIND = 141;

    /**
     * Converts a TypeScript SourceFile to our AST format.
     * CC: 3 (null check + loop + null check)
     */
    public SourceFile convertSourceFile(Value tsSourceFile) {
        String fileName = tsSourceFile.getMember("fileName").asString();
        SourceFile sourceFile = new SourceFile(fileName);

        Value statements = tsSourceFile.getMember("statements");
        if (statements == null || !statements.hasArrayElements()) {
            return sourceFile;
        }

        addStatements(sourceFile, statements);
        return sourceFile;
    }

    /**
     * Adds statements from TypeScript AST to source file.
     * CC: 2 (loop + null check)
     */
    private void addStatements(SourceFile sourceFile, Value statements) {
        long count = statements.getArraySize();
        for (long i = 0; i < count; i++) {
            Value stmt = statements.getArrayElement(i);
            AstNode node = convertNode(stmt);
            if (node != null) {
                sourceFile.addStatement(node);
            }
        }
    }

    /**
     * Converts a TypeScript AST node to our AST format.
     * CC: 2 (null check + switch)
     */
    public AstNode convertNode(Value tsNode) {
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
     * CC: 3 (null checks + loop)
     */
    private ClassDeclaration convertClassDeclaration(Value tsClass) {
        String name = tsClass.getMember("name").getMember("escapedText").asString();
        ClassDeclaration classDecl = new ClassDeclaration(name);

        // Check if it's a struct
        int kind = tsClass.getMember("kind").asInt();
        classDecl.setStruct(kind == STRUCT_DECLARATION_KIND);

        // Process decorators
        addDecorators(classDecl, tsClass);

        // Process members
        addMembers(classDecl, tsClass);

        return classDecl;
    }

    /**
     * Adds decorators to class declaration.
     * CC: 2 (null check + loop)
     */
    private void addDecorators(ClassDeclaration classDecl, Value tsClass) {
        Value decorators = tsClass.getMember("decorators");
        if (decorators == null || !decorators.hasArrayElements()) {
            return;
        }

        long count = decorators.getArraySize();
        for (long i = 0; i < count; i++) {
            Value dec = decorators.getArrayElement(i);
            Decorator decorator = convertDecorator(dec);
            if (decorator != null) {
                classDecl.addDecorator(decorator);
            }
        }
    }

    /**
     * Adds members to class declaration.
     * CC: 2 (null check + loop)
     */
    private void addMembers(ClassDeclaration classDecl, Value tsClass) {
        Value members = tsClass.getMember("members");
        if (members == null || !members.hasArrayElements()) {
            return;
        }

        long count = members.getArraySize();
        for (long i = 0; i < count; i++) {
            Value member = members.getArrayElement(i);
            AstNode node = convertNode(member);
            if (node != null) {
                classDecl.addMember(node);
            }
        }
    }

    /**
     * Converts a TypeScript MethodDeclaration to our AST format.
     * CC: 2 (null check + loop)
     */
    private MethodDeclaration convertMethodDeclaration(Value tsMethod) {
        String name = tsMethod.getMember("name").getMember("escapedText").asString();
        MethodDeclaration methodDecl = new MethodDeclaration(name);

        addDecorators(methodDecl, tsMethod);
        return methodDecl;
    }

    /**
     * Adds decorators to method declaration.
     * CC: 2 (null check + loop)
     */
    private void addDecorators(MethodDeclaration methodDecl, Value tsMethod) {
        Value decorators = tsMethod.getMember("decorators");
        if (decorators == null || !decorators.hasArrayElements()) {
            return;
        }

        long count = decorators.getArraySize();
        for (long i = 0; i < count; i++) {
            Value dec = decorators.getArrayElement(i);
            Decorator decorator = convertDecorator(dec);
            if (decorator != null) {
                methodDecl.addDecorator(decorator);
            }
        }
    }

    /**
     * Converts a TypeScript PropertyDeclaration to our AST format.
     * CC: 2 (null check + loop)
     */
    private PropertyDeclaration convertPropertyDeclaration(Value tsProperty) {
        String name = tsProperty.getMember("name").getMember("escapedText").asString();
        PropertyDeclaration propDecl = new PropertyDeclaration(name);

        addDecorators(propDecl, tsProperty);
        return propDecl;
    }

    /**
     * Adds decorators to property declaration.
     * CC: 2 (null check + loop)
     */
    private void addDecorators(PropertyDeclaration propDecl, Value tsProperty) {
        Value decorators = tsProperty.getMember("decorators");
        if (decorators == null || !decorators.hasArrayElements()) {
            return;
        }

        long count = decorators.getArraySize();
        for (long i = 0; i < count; i++) {
            Value dec = decorators.getArrayElement(i);
            Decorator decorator = convertDecorator(dec);
            if (decorator != null) {
                propDecl.addDecorator(decorator);
            }
        }
    }

    /**
     * Converts a TypeScript Decorator to our AST format.
     * CC: 2 (null checks)
     */
    private Decorator convertDecorator(Value tsDecorator) {
        Value expression = tsDecorator.getMember("expression");
        if (expression == null) {
            return null;
        }

        String name = extractDecoratorName(expression);
        return new Decorator(name);
    }

    /**
     * Extracts decorator name from expression.
     * CC: 2 (has check + else)
     */
    private String extractDecoratorName(Value expression) {
        if (expression.hasMember("expression")) {
            // CallExpression decorator
            Value callee = expression.getMember("expression");
            return callee.getMember("escapedText").asString();
        }
        // Simple identifier
        return expression.getMember("escapedText").asString();
    }

    /**
     * Gets the syntax kind name from its numeric value.
     * CC: 1 (switch with default)
     */
    private String getSyntaxKindName(int kind) {
        switch (kind) {
            case STRUCT_DECLARATION_KIND: return "StructDeclaration";
            case CLASS_DECLARATION_KIND: return "ClassDeclaration";
            case METHOD_DECLARATION_KIND: return "MethodDeclaration";
            case PROPERTY_DECLARATION_KIND: return "PropertyDeclaration";
            case DECORATOR_KIND: return "Decorator";
            default: return "Unknown";
        }
    }
}
