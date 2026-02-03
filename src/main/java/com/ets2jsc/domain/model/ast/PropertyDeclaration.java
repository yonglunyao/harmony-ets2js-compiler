package com.ets2jsc.domain.model.ast;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a property declaration in ETS.
 * Used for @State, @Prop, @Link decorator transformations.
 */
@Getter
public class PropertyDeclaration implements AstNode {
    @Setter
    private String name;
    @Setter
    private String propertyType;
    @Setter
    private String typeAnnotation;
    @Setter
    private String initializer;
    private final List<Decorator> decorators;
    @Setter
    private Visibility visibility;
    @Setter
    private boolean isReadOnly;

    public enum Visibility {
        PUBLIC, PRIVATE, PROTECTED, INTERNAL
    }

    public PropertyDeclaration(String name) {
        this.name = name;
        this.decorators = new ArrayList<>();
        this.visibility = Visibility.INTERNAL;
        this.isReadOnly = false;
    }

    @Override
    public String getType() {
        return "PropertyDeclaration";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void addDecorator(Decorator decorator) {
        this.decorators.add(decorator);
    }

    public boolean hasDecorator(String decoratorName) {
        return decorators.stream()
                .anyMatch(d -> d.getName().equals(decoratorName));
    }

    /**
     * Returns the decorator with the specified name.
     *
     * @param decoratorName the decorator name to search for
     * @return an Optional containing the decorator, or empty if not found
     */
    public Optional<Decorator> getDecorator(String decoratorName) {
        return decorators.stream()
                .filter(d -> d.getName().equals(decoratorName))
                .findFirst();
    }

    /**
     * Returns the private variable name used for state properties.
     * e.g., "count" -> "count__"
     */
    public String getPrivateVarName() {
        return name + "__";
    }

    /**
     * Returns the ObservedProperty type name for this property.
     */
    public String getObservedPropertyType() {
        if (hasDecorator("State")) {
            return "ObservedPropertySimple";
        } else if (hasDecorator("Prop")) {
            return "SynchedPropertySimpleOneWay";
        } else if (hasDecorator("Link")) {
            return "SynchedPropertySimpleTwoWay";
        }
        return "ObservedPropertySimple";
    }
}
