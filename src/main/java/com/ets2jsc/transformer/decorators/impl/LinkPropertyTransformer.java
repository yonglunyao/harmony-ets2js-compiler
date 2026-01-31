package com.ets2jsc.transformer.decorators.impl;

import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.constant.Decorators;
import com.ets2jsc.constant.RuntimeFunctions;
import com.ets2jsc.transformer.decorators.PropertyTransformer;

/**
 * Transforms @Link decorated properties.
 * <p>
 * Link properties allow two-way data flow between parent and child.
 * They use ObservedPropertySimpleTwoWay and are set using createLink().
 */
public class LinkPropertyTransformer extends PropertyTransformer {

    @Override
    protected String getObservedPropertyType() {
        return RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE_TWO_WAY;
    }

    @Override
    protected String getInitializer(PropertyDeclaration prop) {
        // Link properties are set by parent component, no initializer here
        return null;
    }

    @Override
    protected boolean needsConstructorInit() {
        // Links are initialized by parent, not in constructor
        return false;
    }

    @Override
    protected String getCreateMethodName() {
        return RuntimeFunctions.CREATE_LINK;
    }

    @Override
    public boolean canHandle(PropertyDeclaration prop) {
        return prop.hasDecorator(Decorators.LINK);
    }
}
