package com.ets2jsc.transformer.decorators.impl;

import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.constant.Decorators;
import com.ets2jsc.constant.RuntimeFunctions;
import com.ets2jsc.transformer.decorators.PropertyTransformer;

/**
 * Transforms @Prop decorated properties.
 * <p>
 * Prop properties represent one-way data flow from parent to child.
 * They use ObservedPropertySimpleOneWay and are set by the parent component
 * using createProp().
 */
public class PropPropertyTransformer extends PropertyTransformer {

    @Override
    protected String getObservedPropertyType() {
        return RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE_ONE_WAY;
    }

    @Override
    protected String getInitializer(PropertyDeclaration prop) {
        // Prop properties are set by parent component, no initializer here
        return null;
    }

    @Override
    protected boolean needsConstructorInit() {
        // Props are initialized by parent, not in constructor
        return false;
    }

    @Override
    protected String getCreateMethodName() {
        return RuntimeFunctions.CREATE_PROP;
    }

    @Override
    public boolean canHandle(PropertyDeclaration prop) {
        return prop.hasDecorator(Decorators.PROP);
    }
}
