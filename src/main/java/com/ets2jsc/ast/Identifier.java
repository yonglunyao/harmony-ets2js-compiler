package com.ets2jsc.ast;

/**
 * Simple Identifier node for tracking.
 * Represents a named identifier in the source code.
 */
public class Identifier implements AstNode {
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
