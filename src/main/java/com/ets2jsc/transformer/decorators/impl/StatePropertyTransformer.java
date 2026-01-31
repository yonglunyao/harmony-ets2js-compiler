package com.ets2jsc.transformer.decorators.impl;

import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.constant.Decorators;
import com.ets2jsc.constant.RuntimeFunctions;
import com.ets2jsc.transformer.decorators.PropertyTransformer;

/**
 * Transforms @State decorated properties.
 * <p>
 * State properties are the core reactive state in ArkUI components.
 * They use ObservedPropertySimple and are initialized in the constructor
 * using createState().
 */
public class StatePropertyTransformer extends PropertyTransformer {

    @Override
    protected String getObservedPropertyType() {
        return RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE;
    }

    @Override
    protected String getInitializer(PropertyDeclaration prop) {
        // State properties use the original initializer as the default value
        return prop.getInitializer();
    }

    @Override
    protected boolean needsConstructorInit() {
        // State properties need to be initialized in the constructor
        return true;
    }

    @Override
    protected String getCreateMethodName() {
        return RuntimeFunctions.CREATE_STATE;
    }

    @Override
    public boolean canHandle(PropertyDeclaration prop) {
        return prop.hasDecorator(Decorators.STATE);
    }
}
