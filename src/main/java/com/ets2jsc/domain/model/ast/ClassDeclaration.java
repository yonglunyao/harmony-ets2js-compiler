package com.ets2jsc.domain.model.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a class or struct declaration in ETS.
 * Key node for @Component decorator transformation.
 */
public class ClassDeclaration implements AstNode {
    private String name;
    private final List<Decorator> decorators;
    private final List<AstNode> members;
    private boolean _isStruct;
    private String superClass;
    private String heritageClause;
    private boolean _isExport; // Track if this class is exported

    public ClassDeclaration(String name) {
        this.name = name;
        this.decorators = new ArrayList<>();
        this.members = new ArrayList<>();
        this._isStruct = false;
        this._isExport = false;
    }

    @Override
    public String getType() {
        return "ClassDeclaration";
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

    public List<Decorator> getDecorators() {
        return decorators;
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

    public List<AstNode> getMembers() {
        return members;
    }

    public void addMember(AstNode member) {
        this.members.add(member);
    }

    public boolean isStruct() {
        return _isStruct;
    }

    public void setStruct(boolean struct) {
        _isStruct = struct;
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public String getHeritageClause() {
        return heritageClause;
    }

    public void setHeritageClause(String heritageClause) {
        this.heritageClause = heritageClause;
    }

    public boolean isExport() {
        return _isExport;
    }

    public void setExport(boolean export) {
        _isExport = export;
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
