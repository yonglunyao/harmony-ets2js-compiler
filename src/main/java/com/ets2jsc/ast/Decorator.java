package com.ets2jsc.ast;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a decorator in ETS.
 * Decorators like @Component, @State, @Prop, @Builder, etc.
 */
public class Decorator implements AstNode {
    private String name;
    private Map<String, Object> arguments;
    private String rawExpression;

    public Decorator(String name) {
        this.name = name;
        this.arguments = new HashMap<>();
    }

    @Override
    public String getType() {
        return "Decorator";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArgument(String key, Object value) {
        this.arguments.put(key, value);
    }

    public Object getArgument(String key) {
        return arguments.get(key);
    }

    public String getRawExpression() {
        return rawExpression;
    }

    public void setRawExpression(String rawExpression) {
        this.rawExpression = rawExpression;
    }

    /**
     * Returns true if this is a component decorator.
     */
    public boolean isComponentDecorator() {
        return "Component".equals(name) || "Entry".equals(name) ||
               "Preview".equals(name) || "CustomDialog".equals(name);
    }

    /**
     * Returns true if this is a state decorator.
     */
    public boolean isStateDecorator() {
        return "State".equals(name) || "Prop".equals(name) ||
               "Link".equals(name) || "Provide".equals(name) ||
               "Consume".equals(name);
    }

    /**
     * Returns true if this is a method decorator.
     */
    public boolean isMethodDecorator() {
        return "Builder".equals(name) || "Extend".equals(name) ||
               "Styles".equals(name) || "Watch".equals(name);
    }
}
