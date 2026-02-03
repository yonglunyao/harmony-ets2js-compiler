package com.ets2jsc.domain.model.ast;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Block statement node.
 * Represents a block of statements enclosed in braces.
 */
@Getter
public class Block implements AstNode {
    private final List<AstNode> statements = new ArrayList<>();

    public void addStatement(AstNode statement) {
        statements.add(statement);
    }

    @Override
    public String getType() {
        return "Block";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
