package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import com.ets2jsc.constant.Decorators;
import com.ets2jsc.constant.RuntimeFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms decorators in ETS code.
 * Handles @Component, @State, @Prop, @Link, etc.
 */
public class DecoratorTransformer implements AstTransformer {

    private final boolean partialUpdateMode;

    public DecoratorTransformer(boolean partialUpdateMode) {
        this.partialUpdateMode = partialUpdateMode;
    }

    @Override
    public AstNode transform(AstNode node) {
        if (node instanceof ClassDeclaration) {
            return transformClassDeclaration((ClassDeclaration) node);
        } else if (node instanceof PropertyDeclaration) {
            return transformPropertyDeclaration((PropertyDeclaration) node);
        } else if (node instanceof MethodDeclaration) {
            return transformMethodDeclaration((MethodDeclaration) node);
        }
        return node;
    }

    @Override
    public boolean canTransform(AstNode node) {
        if (node instanceof ClassDeclaration) {
            ClassDeclaration classDecl = (ClassDeclaration) node;
            return classDecl.getDecorators().stream()
                    .anyMatch(d -> d.isComponentDecorator());
        }
        if (node instanceof PropertyDeclaration) {
            PropertyDeclaration prop = (PropertyDeclaration) node;
            return prop.getDecorators().stream()
                    .anyMatch(d -> d.isStateDecorator());
        }
        return false;
    }

    /**
     * Transforms a class declaration with decorators.
     * Handles @Component struct → class View transformation.
     */
    private ClassDeclaration transformClassDeclaration(ClassDeclaration classDecl) {
        // Check if this is a @Component struct
        boolean isComponent = classDecl.hasDecorator(Decorators.COMPONENT);

        if (isComponent) {
            // Transform struct to class extending View
            classDecl.setStruct(false);
            classDecl.setSuperClass(RuntimeFunctions.VIEW);

            // Add private properties for state variables
            List<PropertyDeclaration> properties = classDecl.getProperties();
            List<PropertyDeclaration> stateProperties = new ArrayList<>();

            for (PropertyDeclaration prop : properties) {
                if (prop.hasDecorator(Decorators.STATE)) {
                    stateProperties.add(prop);
                }
            }

            // Transform state properties
            for (PropertyDeclaration stateProp : stateProperties) {
                transformStateProperty(classDecl, stateProp);
            }

            // Add constructor if needed
            if (!stateProperties.isEmpty()) {
                addConstructor(classDecl, stateProperties);
            }
        }

        return classDecl;
    }

    /**
     * Transforms a property declaration with decorators.
     * Creates the private property and getter/setter.
     */
    private PropertyDeclaration transformPropertyDeclaration(PropertyDeclaration prop) {
        if (prop.hasDecorator(Decorators.STATE)) {
            // State properties are handled at class level
            return prop;
        }
        return prop;
    }

    /**
     * Transforms a method declaration with decorators.
     */
    private MethodDeclaration transformMethodDeclaration(MethodDeclaration method) {
        if (method.isBuilderMethod()) {
            // Add BuilderParam parameter
            MethodDeclaration.Parameter builderParam = new MethodDeclaration.Parameter("__builder__");
            builderParam.setType(RuntimeFunctions.BUILDER_PARAM);
            method.addParameter(builderParam);
        }
        return method;
    }

    /**
     * Transforms a state property.
     * Creates the private variable with __ suffix and getter/setter.
     */
    private void transformStateProperty(ClassDeclaration classDecl, PropertyDeclaration stateProp) {
        String propName = stateProp.getName();
        String privateName = propName + "__";
        String propType = stateProp.getTypeAnnotation();

        // Create private property declaration
        PropertyDeclaration privateProp = new PropertyDeclaration(privateName);
        privateProp.setTypeAnnotation(RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE + "<" + propType + ">");
        privateProp.setVisibility(PropertyDeclaration.Visibility.PRIVATE);

        // Create getter
        MethodDeclaration getter = new MethodDeclaration("get " + propName);
        getter.setReturnType(propType);
        getter.setBody(new ExpressionStatement("return this." + privateName + ".get()"));

        // Create setter
        MethodDeclaration setter = new MethodDeclaration("set " + propName);
        MethodDeclaration.Parameter valueParam = new MethodDeclaration.Parameter("newValue", propType);
        setter.addParameter(valueParam);
        setter.setBody(new ExpressionStatement("this." + privateName + ".set(newValue)"));

        // Add to class (replace original property)
        // Note: In real implementation, we'd need to properly replace the property
    }

    /**
     * Adds a constructor that initializes state properties.
     */
    private void addConstructor(ClassDeclaration classDecl, List<PropertyDeclaration> stateProps) {
        MethodDeclaration constructor = new MethodDeclaration("constructor");

        StringBuilder initCode = new StringBuilder();
        initCode.append("super();\n");

        for (PropertyDeclaration stateProp : stateProps) {
            String propName = stateProp.getName();
            String privateName = stateProp.getPrivateVarName();

            // Initialize state property
            initCode.append("this.").append(privateName).append(" = ")
                   .append("this.").append(RuntimeFunctions.CREATE_STATE).append("(")
                   .append("'").append(propName).append("', ")
                   .append("() => this.").append(propName)
                   .append(");\n");
        }

        constructor.setBody(new ExpressionStatement(initCode.toString()));
        // Add to class
    }

    /**
     * Returns the state creation method name for a decorator.
     */
    private String getStateCreationMethod(Decorator decorator) {
        String name = decorator.getName();
        switch (name) {
            case Decorators.STATE:
                return RuntimeFunctions.CREATE_STATE;
            case Decorators.PROP:
                return RuntimeFunctions.CREATE_PROP;
            case Decorators.LINK:
                return RuntimeFunctions.CREATE_LINK;
            default:
                return RuntimeFunctions.CREATE_STATE;
        }
    }

    /**
     * Returns the ObservedProperty type for a decorator.
     */
    private String getObservedPropertyType(Decorator decorator) {
        String name = decorator.getName();
        switch (name) {
            case Decorators.STATE:
                return RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE;
            case Decorators.PROP:
                return RuntimeFunctions.SYNCHED_PROPERTY_SIMPLE_ONE_WAY;
            case Decorators.LINK:
                return RuntimeFunctions.SYNCHED_PROPERTY_SIMPLE_TWO_WAY;
            default:
                return RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE;
        }
    }
}
