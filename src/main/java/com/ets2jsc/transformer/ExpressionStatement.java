package com.ets2jsc.transformer;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.AstVisitor;

/**
 * Simple expression statement for use in transformers.
 * Used to store expression strings during transformation.
 */
public class ExpressionStatement implements AstNode {
    private final String expression;

    public ExpressionStatement(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String getType() {
        return "ExpressionStatement";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return null;
    }
}
