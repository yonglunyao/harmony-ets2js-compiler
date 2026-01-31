package com.ets2jsc.generator;

import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.ast.*;
import com.ets2jsc.config.CompilerConfig;

/**
 * Generates code for method declarations.
 */
public class MethodGenerator {

    private final MethodDeclaration method;
    private final IndentationManager indentation;
    private final AstVisitor<String> codeGenerator;

    public MethodGenerator(MethodDeclaration method, IndentationManager indentation, AstVisitor<String> codeGenerator) {
        this.method = method;
        this.indentation = indentation;
        this.codeGenerator = codeGenerator;
    }

    public String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append(indentation.getCurrent());

        generateSignature(sb);
        indentation.increase();
        generateBody(sb);
        indentation.decrease();

        sb.append(indentation.getCurrent()).append("}\n");
        return sb.toString();
    }

    private void generateSignature(StringBuilder sb) {
        String name = method.getName();

        if ("constructor".equals(name)) {
            sb.append("constructor() {\n");
        } else if (name.startsWith("get ")) {
            sb.append("get ").append(name.substring(4)).append("() {\n");
        } else if (name.startsWith("set ")) {
            generateSetterSignature(sb, name);
        } else {
            generateRegularSignature(sb);
        }
    }

    private void generateSetterSignature(StringBuilder sb, String name) {
        sb.append("set ").append(name.substring(4)).append("(");

        if (!method.getParameters().isEmpty()) {
            MethodDeclaration.Parameter param = method.getParameters().get(0);
            sb.append(param.getName());
        } else {
            sb.append("newValue");
        }

        sb.append(") {\n");
    }

    private void generateRegularSignature(StringBuilder sb) {
        if (method.isStatic()) {
            sb.append("static ");
        }
        if (method.isAsync()) {
            sb.append("async ");
        }

        sb.append(method.getName()).append("(");
        generateParameters(sb);
        sb.append(") {\n");
    }

    private void generateParameters(StringBuilder sb) {
        for (int i = 0; i < method.getParameters().size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }

            MethodDeclaration.Parameter param = method.getParameters().get(i);
            sb.append(param.getName());

            if (param.hasDefault() && param.getDefaultValue() != null) {
                sb.append(" = ").append(param.getDefaultValue());
            }

            if (param.getType() != null) {
                sb.append(" /* ").append(param.getType()).append(" */");
            }
        }
    }

    /**
     * Generates method body.
     * CC: 2 (null check + line iteration)
     */
    private void generateBody(StringBuilder sb) {
        if (method.getBody() == null) {
            return;
        }

        String bodyCode = generateBodyCode(method.getBody());

        // Split into lines and format with proper indentation
        String[] lines = bodyCode.split("\n");
        for (String line : lines) {
            if (!line.isEmpty()) {
                sb.append(indentation.getCurrent()).append(line).append("\n");
            }
        }
    }

    private String generateBodyCode(AstNode body) {
        // Use the codeGenerator to properly visit AST nodes
        if (body instanceof ExpressionStatement) {
            return ((ExpressionStatement) body).getExpression();
        }
        return body.accept(codeGenerator);
    }
}
