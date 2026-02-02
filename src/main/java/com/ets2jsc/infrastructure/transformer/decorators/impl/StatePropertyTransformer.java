package com.ets2jsc.infrastructure.transformer.decorators.impl;

import com.ets2jsc.domain.model.ast.PropertyDeclaration;
import com.ets2jsc.shared.constant.Decorators;
import com.ets2jsc.shared.constant.RuntimeFunctions;
import com.ets2jsc.infrastructure.transformer.decorators.PropertyTransformer;

/**
 * Transforms @State decorated properties.
 * <p>
 * State properties are the core reactive state in ArkUI components.
 * They use ObservedPropertySimple and are initialized in the constructor
 * using createState().
 * <p>
 * Note: getInitializer() returns null to indicate no declaration-time initialization.
 * The createPrivateProperty() method handles this by not setting an initializer
 * on the private property, which is the correct behavior for @State properties.
 */
public class StatePropertyTransformer extends PropertyTransformer {

    @Override
    protected String getObservedPropertyType() {
        return RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE;
    }

    @Override
    protected String getInitializer(PropertyDeclaration prop) {
        // State properties are initialized in constructor, not at declaration.
        // Returning null signals createPrivateProperty() to skip initializer setting.
        return null;
    }

    @Override
    protected boolean needsConstructorInit() {
        // State properties need to be initialized in the constructor
        return true;
    }

    @Override
    public boolean canHandle(PropertyDeclaration prop) {
        return prop.hasDecorator(Decorators.STATE);
    }
}
