package com.ets2jsc.domain.model.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a UI component expression in ETS.
 * e.g., Text('Hello'), Column() { ... }
 * Transforms to create/pop pattern.
 */
public class ComponentExpression implements AstNode {
    private String componentName;
    private final List<AstNode> arguments;
    private final List<MethodCall> chainedCalls;
    private final List<AstNode> children;
    private String objectLiteral;

    public static class MethodCall {
        private String methodName;
        private final List<AstNode> arguments;

        public MethodCall(String methodName) {
            this.methodName = methodName;
            this.arguments = new ArrayList<>();
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public List<AstNode> getArguments() {
            return arguments;
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

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public List<AstNode> getArguments() {
        return arguments;
    }

    public void addArgument(AstNode argument) {
        this.arguments.add(argument);
    }

    public List<MethodCall> getChainedCalls() {
        return chainedCalls;
    }

    public void addChainedCall(MethodCall call) {
        this.chainedCalls.add(call);
    }

    public List<AstNode> getChildren() {
        return children;
    }

    public void addChild(AstNode child) {
        this.children.add(child);
    }

    public String getObjectLiteral() {
        return objectLiteral;
    }

    public void setObjectLiteral(String objectLiteral) {
        this.objectLiteral = objectLiteral;
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
