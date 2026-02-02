package com.ets2jsc.generator;

import com.ets2jsc.domain.model.ast.PropertyDeclaration;

/**
 * Generates code for property declarations.
 */
public class PropertyGenerator {

    private final PropertyDeclaration property;
    private final IndentationManager indentation;

    public PropertyGenerator(PropertyDeclaration property, IndentationManager indentation) {
        this.property = property;
        this.indentation = indentation;
    }

    public String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append(indentation.getCurrent());
        sb.append(property.getName());

        if (property.getInitializer() != null) {
            sb.append(generateInitializer());
        }

        sb.append(";\n");
        return sb.toString();
    }

    private String generateInitializer() {
        String initializer = property.getInitializer();

        if (StringLiteralHelper.needsQuoting(initializer)) {
            return " = \"" + StringLiteralHelper.escapeJsString(initializer) + "\"";
        }

        return " = " + initializer;
    }
}
