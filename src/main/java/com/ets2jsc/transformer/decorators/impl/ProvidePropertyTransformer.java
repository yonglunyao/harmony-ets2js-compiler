package com.ets2jsc.transformer.decorators.impl;

import com.ets2jsc.domain.model.ast.PropertyDeclaration;
import com.ets2jsc.shared.constant.Decorators;
import com.ets2jsc.shared.constant.RuntimeFunctions;
import com.ets2jsc.transformer.decorators.PropertyTransformer;

/**
 * Transforms @Provide decorated properties.
 * <p>
 * Provide properties provide data to descendant components.
 * They use ObservedPropertySimple and are initialized at declaration.
 */
public class ProvidePropertyTransformer extends PropertyTransformer {

    @Override
    protected String getObservedPropertyType() {
        return RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE;
    }

    @Override
    protected String getInitializer(PropertyDeclaration prop) {
        // Provide properties are initialized at declaration
        return prop.getInitializer();
    }

    @Override
    protected boolean needsConstructorInit() {
        // Provide properties use declaration initializer
        return false;
    }

    @Override
    protected String getCreateMethodName() {
        return RuntimeFunctions.INITIALIZE_PROVIDE;
    }

    @Override
    public boolean canHandle(PropertyDeclaration prop) {
        return prop.hasDecorator(Decorators.PROVIDE);
    }
}
