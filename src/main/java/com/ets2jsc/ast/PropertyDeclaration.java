package com.ets2jsc.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a property declaration in ETS.
 * Used for @State, @Prop, @Link decorator transformations.
 */
public class PropertyDeclaration implements AstNode {
    private String name;
    private String type;
    private String initializer;
    private final List<Decorator> decorators;
    private Visibility visibility;
    private boolean _isReadOnly;

    public enum Visibility {
        PUBLIC, PRIVATE, PROTECTED, INTERNAL
    }

    public PropertyDeclaration(String name) {
        this.name = name;
        this.decorators = new ArrayList<>();
        this.visibility = Visibility.INTERNAL;
        this._isReadOnly = false;
    }

    @Override
    public String getType() {
        return "PropertyDeclaration";
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

    public String getTypeAnnotation() {
        return type;
    }

    public void setTypeAnnotation(String type) {
        this.type = type;
    }

    public String getInitializer() {
        return initializer;
    }

    public void setInitializer(String initializer) {
        this.initializer = initializer;
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

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public boolean isReadOnly() {
        return _isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        _isReadOnly = readOnly;
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
