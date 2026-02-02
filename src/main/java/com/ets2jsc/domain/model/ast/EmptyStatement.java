package com.ets2jsc.domain.model.ast;

/**
 * Empty statement node.
 * Represents an empty statement (a standalone semicolon) in the source code.
 */
public class EmptyStatement implements AstNode {

    @Override
    public String getType() {
        return "EmptyStatement";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
