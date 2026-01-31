package com.ets2jsc.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a method declaration in ETS.
 * Used for build() method transformation and other methods.
 */
public class MethodDeclaration implements AstNode {
    private final List<Parameter> parameters;
    private final List<Decorator> decorators;
    private String name;
    private String returnType;
    private AstNode body;
    private boolean isAsync;
    private boolean isStatic;

    public static class Parameter {
        private String name;
        private String type;
        private boolean hasDefault;
        private String defaultValue;

        public Parameter(String name) {
            this.name = name;
        }

        public Parameter(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean hasDefault() {
            return hasDefault;
        }

        public void setHasDefault(boolean hasDefault) {
            this.hasDefault = hasDefault;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    public MethodDeclaration(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
        this.decorators = new ArrayList<>();
        this.isAsync = false;
        this.isStatic = false;
    }

    @Override
    public String getType() {
        return "MethodDeclaration";
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

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void addParameter(Parameter parameter) {
        this.parameters.add(parameter);
    }

    public List<Decorator> getDecorators() {
        return decorators;
    }

    public void addDecorator(Decorator decorator) {
        this.decorators.add(decorator);
    }

    public AstNode getBody() {
        return body;
    }

    public void setBody(AstNode body) {
        this.body = body;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean staticFlag) {
        isStatic = staticFlag;
    }

    /**
     * Returns true if this is the build() method that needs transformation.
     */
    public boolean isBuildMethod() {
        return "build".equals(name);
    }

    /**
     * Returns true if this method has a @Builder decorator.
     */
    public boolean isBuilderMethod() {
        return decorators.stream()
                .anyMatch(d -> "Builder".equals(d.getName()));
    }
}
