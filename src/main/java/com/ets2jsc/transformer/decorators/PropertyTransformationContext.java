package com.ets2jsc.transformer.decorators;

import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.constant.Decorators;

/**
 * Context class for property transformation.
 * Holds the class being transformed and provides helper methods.
 */
public class PropertyTransformationContext {

    private final ClassDeclaration classDecl;
    private final PropertyDeclaration property;

    public PropertyTransformationContext(ClassDeclaration classDecl, PropertyDeclaration property) {
        this.classDecl = classDecl;
        this.property = property;
    }

    public ClassDeclaration getClassDeclaration() {
        return classDecl;
    }

    public PropertyDeclaration getProperty() {
        return property;
    }

    /**
     * Gets the decorator name from the property.
     * CC: 1
     */
    public String getDecoratorName() {
        if (property.hasDecorator(Decorators.STATE)) {
            return Decorators.STATE;
        }
        if (property.hasDecorator(Decorators.PROP)) {
            return Decorators.PROP;
        }
        if (property.hasDecorator(Decorators.LINK)) {
            return Decorators.LINK;
        }
        if (property.hasDecorator(Decorators.PROVIDE)) {
            return Decorators.PROVIDE;
        }
        if (property.hasDecorator(Decorators.CONSUME)) {
            return Decorators.CONSUME;
        }
        return "";
    }
}
