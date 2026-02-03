package com.ets2jsc.domain.model.ast;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a UI component expression in ETS.
 * e.g., Text('Hello'), Column() { ... }
 * Transforms to create/pop pattern.
 */
@Getter
public class ComponentExpression implements AstNode {
    @Setter
    private String componentName;
    private final List<AstNode> arguments;
    private final List<MethodCall> chainedCalls;
    private final List<AstNode> children;
    @Setter
    private String objectLiteral;

    @Getter
    @Setter
    public static class MethodCall {
        private final String methodName;
        private final List<AstNode> arguments;

        public MethodCall(String methodName) {
            this.methodName = methodName;
            this.arguments = new ArrayList<>();
        }

        public void addArgument(AstNode argument) {
            this.arguments.add(argument);
        }
    }

    public ComponentExpression(String componentName) {
        this.componentName = componentName;
        this.arguments = new ArrayList<>();
        this.chainedCalls = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    @Override
    public String getType() {
        return "ComponentExpression";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void addArgument(AstNode argument) {
        this.arguments.add(argument);
    }

    public void addChainedCall(MethodCall call) {
        this.chainedCalls.add(call);
    }

    public void addChild(AstNode child) {
        this.children.add(child);
    }

    /**
     * Returns true if this is a built-in component.
     */
    public boolean isBuiltinComponent() {
        return BuiltInComponents.isBuiltin(componentName);
    }

    /**
     * Returns true if this is a container component.
     */
    public boolean isContainerComponent() {
        return BuiltInComponents.isContainer(componentName);
    }
}
