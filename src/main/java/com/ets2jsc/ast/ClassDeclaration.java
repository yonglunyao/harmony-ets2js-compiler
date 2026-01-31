package com.ets2jsc.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class or struct declaration in ETS.
 * Key node for @Component decorator transformation.
 */
public class ClassDeclaration implements AstNode {
    private String name;
    private List<Decorator> decorators;
    private List<AstNode> members;
    private boolean isStruct;
    private String superClass;
    private String heritageClause;
    private boolean isExport; // Track if this class is exported

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
        return decorators.stream()
                .anyMatch(d -> d.getName().equals(decoratorName));
    }

    public Decorator getDecorator(String decoratorName) {
        return decorators.stream()
                .filter(d -> d.getName().equals(decoratorName))
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
        return isStruct;
    }

    public void setStruct(boolean struct) {
        isStruct = struct;
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
        return isExport;
    }

    public void setExport(boolean export) {
        isExport = export;
    }

    /**
     * Returns all properties from this class declaration.
     */
    public List<PropertyDeclaration> getProperties() {
        List<PropertyDeclaration> properties = new ArrayList<>();
        for (AstNode member : members) {
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
        List<MethodDeclaration> methods = new ArrayList<>();
        for (AstNode member : members) {
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
        List<String> builderMethodNames = new ArrayList<>();
        for (AstNode member : members) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                if (method.isBuilderMethod()) {
                    builderMethodNames.add(method.getName());
                }
            }
        }
        return builderMethodNames;
    }
}
