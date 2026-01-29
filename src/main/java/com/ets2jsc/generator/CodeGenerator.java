package com.ets2jsc.generator;

import com.ets2jsc.ast.*;
import com.ets2jsc.constant.RuntimeFunctions;

import java.util.List;

/**
 * Generates JavaScript code from transformed AST.
 * Traverses the AST and outputs formatted JavaScript code.
 */
public class CodeGenerator implements AstVisitor<String> {

    private final StringBuilder output;
    private final String indent;
    private int currentIndent;

    public CodeGenerator() {
        this.output = new StringBuilder();
        this.indent = "  ";
        this.currentIndent = 0;
    }

    /**
     * Generates JavaScript code from an AST node.
     */
    public String generate(AstNode node) {
        if (node == null) {
            return "";
        }
        return node.accept(this);
    }

    /**
     * Generates JavaScript code for a source file.
     */
    public String generate(SourceFile sourceFile) {
        output.setLength(0);
        currentIndent = 0;

        // Generate imports first
        for (String importPath : sourceFile.getImports()) {
            writeLine(importPath);
        }

        // Generate statements
        for (AstNode statement : sourceFile.getStatements()) {
            String code = statement.accept(this);
            if (!code.isEmpty()) {
                writeLine(code);
            }
        }

        return output.toString();
    }

    @Override
    public String visit(SourceFile node) {
        StringBuilder sb = new StringBuilder();

        for (AstNode statement : node.getStatements()) {
            sb.append(statement.accept(this));
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String visit(ClassDeclaration node) {
        StringBuilder sb = new StringBuilder();

        // Generate decorators (comments for now)
        for (Decorator decorator : node.getDecorators()) {
            if (!decorator.getName().equals("Component")) {
                sb.append("// @").append(decorator.getName()).append("\n");
            }
        }

        // Class declaration
        sb.append("class ").append(node.getName());

        // Extends clause
        if (node.getSuperClass() != null) {
            sb.append(" extends ").append(node.getSuperClass());
        }

        sb.append(" {\n");
        currentIndent++;

        // Generate members
        for (AstNode member : node.getMembers()) {
            String memberCode = member.accept(this);
            if (!memberCode.isEmpty()) {
                sb.append(memberCode).append("\n");
            }
        }

        currentIndent--;
        sb.append("}\n");

        return sb.toString();
    }

    @Override
    public String visit(MethodDeclaration node) {
        StringBuilder sb = new StringBuilder();

        // Method signature
        sb.append(getIndent());

        if ("constructor".equals(node.getName())) {
            sb.append("constructor() {\n");
            currentIndent++;
        } else if (node.getName().startsWith("get ")) {
            sb.append("get ")
              .append(node.getName().substring(4))
              .append("() {\n");
            currentIndent++;
        } else if (node.getName().startsWith("set ")) {
            sb.append("set ")
              .append(node.getName().substring(4))
              .append("(");
            // Add value parameter
            if (!node.getParameters().isEmpty()) {
                MethodDeclaration.Parameter param = node.getParameters().get(0);
                sb.append(param.getName());
            } else {
                sb.append("newValue");
            }
            sb.append(") {\n");
            currentIndent++;
        } else {
            // Add static keyword if present
            if (node.isStatic()) {
                sb.append("static ");
            }
            // Add async keyword if present
            if (node.isAsync()) {
                sb.append("async ");
            }

            sb.append(node.getName()).append("(");

            // Parameters
            List<MethodDeclaration.Parameter> params = node.getParameters();
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                MethodDeclaration.Parameter param = params.get(i);
                sb.append(param.getName());
                // Optional type annotation (as comment for JS)
                if (param.getType() != null) {
                    sb.append(" /* ").append(param.getType()).append(" */");
                }
            }

            sb.append(") {\n");
            currentIndent++;
        }

        // Method body
        if (node.getBody() != null) {
            String bodyCode = node.getBody().accept(this);
            // If body is ExpressionStatement, it may contain multiple lines (e.g., constructor with \n)
            if (node.getBody() instanceof com.ets2jsc.ast.ExpressionStatement) {
                // Split by newlines and indent each line
                String[] lines = bodyCode.split("\n");
                for (String line : lines) {
                    if (!line.isEmpty()) {
                        sb.append(getIndent()).append(line).append("\n");
                    }
                }
            } else {
                sb.append(bodyCode);
            }
        }

        currentIndent--;
        sb.append(getIndent()).append("}\n");

        return sb.toString();
    }

    @Override
    public String visit(PropertyDeclaration node) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent());

        // Visibility
        if (node.getVisibility() == PropertyDeclaration.Visibility.PRIVATE) {
            sb.append("private ");
        }

        // Name and type
        sb.append(node.getName());

        if (node.getTypeAnnotation() != null) {
            sb.append(": ").append(node.getTypeAnnotation());
        }

        // Initializer
        if (node.getInitializer() != null) {
            sb.append(" = ").append(node.getInitializer());
        }

        sb.append(";\n");

        return sb.toString();
    }

    @Override
    public String visit(Decorator node) {
        // Decorators are handled at class level
        return "";
    }

    @Override
    public String visit(ComponentExpression node) {
        // Component expressions are handled by the transformer
        return "";
    }

    @Override
    public String visit(CallExpression node) {
        StringBuilder sb = new StringBuilder();

        sb.append(node.getFunctionName()).append("(");

        List<AstNode> args = node.getArguments();
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(args.get(i).accept(this));
        }

        sb.append(")");

        return sb.toString();
    }

    @Override
    public String visit(ExpressionStatement node) {
        // Return the expression string with semicolon
        String expr = node.getExpression();
        if (expr == null || expr.isEmpty()) {
            return "";
        }

        // Don't add semicolon if expression already ends with one or is a block
        String trimmed = expr.trim();
        if (trimmed.endsWith(";") || trimmed.endsWith("}") || trimmed.startsWith("{")) {
            return expr;
        }

        // Add semicolon
        return expr + ";";
    }

    @Override
    public String visit(Block node) {
        StringBuilder sb = new StringBuilder();
        for (AstNode stmt : node.getStatements()) {
            String stmtCode = stmt.accept(this);
            if (stmtCode != null && !stmtCode.isEmpty()) {
                // Don't add indentation for nested blocks in component methods
                // (they represent component closures, not code blocks)
                if (stmt instanceof Block) {
                    sb.append(stmtCode);
                } else {
                    sb.append(getIndent()).append(stmtCode).append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Helper methods for indentation and output.
     */
    private String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentIndent; i++) {
            sb.append(indent);
        }
        return sb.toString();
    }

    private void write(String text) {
        output.append(text);
    }

    private void writeLine(String text) {
        output.append(text).append("\n");
    }

    private void writeIndent() {
        output.append(getIndent());
    }

    /**
     * Gets the generated code as a string.
     */
    public String getOutput() {
        return output.toString();
    }
}
