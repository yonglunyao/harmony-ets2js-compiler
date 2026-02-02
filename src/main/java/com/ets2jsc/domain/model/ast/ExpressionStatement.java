package com.ets2jsc.domain.model.ast;

/**
 * Expression statement node.
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
        return visitor.visit(this);
    }
}
