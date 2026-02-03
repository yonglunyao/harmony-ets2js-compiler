package com.ets2jsc.domain.model.ast;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a method declaration in ETS.
 * Used for build() method transformation and other methods.
 */
@Getter
public class MethodDeclaration implements AstNode {
    private final List<Parameter> parameters;
    private final List<Decorator> decorators;
    @Setter
    private String name;
    @Setter
    private String returnType;
    @Setter
    private AstNode body;
    @Setter
    private boolean isAsync;
    @Setter
    private boolean isStatic;

    /**
     * Parameter class for method declarations.
     */
    @Getter
    @Setter
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

    public void addParameter(Parameter parameter) {
        this.parameters.add(parameter);
    }

    public void addDecorator(Decorator decorator) {
        this.decorators.add(decorator);
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
