package com.ets2jsc.domain.model.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a class or struct declaration in ETS.
 * Key node for @Component decorator transformation.
 */
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class ClassDeclaration implements AstNode {
    @Setter
    private String name;
    private final List<Decorator> decorators;
    private final List<AstNode> members;
    @Setter
    private boolean isStruct;
    @Setter
    private String superClass;
    @Setter
    private String heritageClause;
    @Setter
    private boolean isExport;

    public ClassDeclaration(String name) {
        this.name = name;
        this.decorators = new ArrayList<>();
        this.members = new ArrayList<>();
        this.isStruct = false;
        this.isExport = false;
    }

    @Override
    public String getType() {
        return "ClassDeclaration";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void addDecorator(Decorator decorator) {
        this.decorators.add(decorator);
    }

    public boolean hasDecorator(String decoratorName) {
        if (decorators == null || decoratorName == null) {
            return false;
        }
        return decorators.stream()
                .filter(Objects::nonNull)
                .map(Decorator::getName)
                .filter(Objects::nonNull)
                .anyMatch(decoratorName::equals);
    }

    public Decorator getDecorator(String decoratorName) {
        if (decoratorName == null) {
            return null;
        }
        return decorators.stream()
                .filter(Objects::nonNull)
                .filter(d -> decoratorName.equals(d.getName()))
                .findFirst()
                .orElse(null);
    }

    public void addMember(AstNode member) {
        this.members.add(member);
    }

    /**
     * Returns all properties from this class declaration.
     */
    public List<PropertyDeclaration> getProperties() {
        final List<PropertyDeclaration> properties = new ArrayList<>();
        for (final AstNode member : members) {
            if (member instanceof PropertyDeclaration) {
                properties.add((PropertyDeclaration) member);
            }
        }
        return properties;
    }

    /**
     * Returns all methods from this class declaration.
     */
    public List<MethodDeclaration> getMethods() {
        final List<MethodDeclaration> methods = new ArrayList<>();
        for (final AstNode member : members) {
            if (member instanceof MethodDeclaration) {
                methods.add((MethodDeclaration) member);
            }
        }
        return methods;
    }

    /**
     * Returns all @Builder method names from this class declaration.
     * Used for transforming @Builder method call sites.
     */
    public List<String> getBuilderMethodNames() {
        final List<String> builderMethodNames = new ArrayList<>();
        for (final AstNode member : members) {
            if (member instanceof MethodDeclaration method && method.isBuilderMethod()) {
                builderMethodNames.add(method.getName());
            }
        }
        return builderMethodNames;
    }
}
