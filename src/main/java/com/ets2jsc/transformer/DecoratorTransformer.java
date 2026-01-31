package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import com.ets2jsc.constant.Decorators;
import com.ets2jsc.constant.RuntimeFunctions;
import com.ets2jsc.constant.Symbols;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms decorators in ETS code.
 * Handles @Component, @State, @Prop, @Link, etc.
 */
public class DecoratorTransformer implements AstTransformer {

    private final boolean partialUpdateMode;

    /**
     * Property type configuration for transformation.
     */
    private static class PropertyTypeConfig {
        final String observedPropertyType;
        final String runtimeCreateMethod;
        final boolean useInitializer;

        PropertyTypeConfig(String observedPropertyType, String runtimeCreateMethod, boolean useInitializer) {
            this.observedPropertyType = observedPropertyType;
            this.runtimeCreateMethod = runtimeCreateMethod;
            this.useInitializer = useInitializer;
        }
    }

    // Property type configurations
    private static final PropertyTypeConfig STATE_CONFIG = new PropertyTypeConfig(
        RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE,
        RuntimeFunctions.CREATE_STATE,
        false  // State properties are initialized in constructor via createState()
    );

    private static final PropertyTypeConfig PROP_CONFIG = new PropertyTypeConfig(
        RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE_ONE_WAY,
        RuntimeFunctions.CREATE_PROP,
        false
    );

    private static final PropertyTypeConfig LINK_CONFIG = new PropertyTypeConfig(
        RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE_TWO_WAY,
        RuntimeFunctions.CREATE_LINK,
        false
    );

    private static final PropertyTypeConfig PROVIDE_CONFIG = new PropertyTypeConfig(
        RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE,
        RuntimeFunctions.CREATE_STATE,
        true
    );

    private static final PropertyTypeConfig CONSUME_CONFIG = new PropertyTypeConfig(
        RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE,
        RuntimeFunctions.CREATE_STATE,
        true
    );

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
                    .anyMatch(d -> d.isStateDecorator() || d.isPropDecorator() || d.isLinkDecorator());
        }
        return false;
    }

    /**
     * Transforms a class declaration with decorators.
     * Handles @Component struct → class View transformation.
     * Handles @Entry decorator validation and export.
     */
    private ClassDeclaration transformClassDeclaration(ClassDeclaration classDecl) {
        // Check if this is a @Component struct or @Entry component
        boolean isComponent = classDecl.hasDecorator(Decorators.COMPONENT);
        boolean isEntry = classDecl.hasDecorator(Decorators.ENTRY);

        if (isEntry) {
            // Validate @Entry usage
            validateEntryDecorator(classDecl);
            // Mark for default export
            classDecl.setExport(true);
        }

        if (isComponent || isEntry) {
            // Transform struct to class extending View
            classDecl.setStruct(false);
            classDecl.setSuperClass(RuntimeFunctions.VIEW);

            // Add private properties for state/prop/link/provide/consume variables
            List<PropertyDeclaration> properties = classDecl.getProperties();
            List<PropertyDeclaration> stateProperties = new ArrayList<>();
            List<PropertyDeclaration> propProperties = new ArrayList<>();
            List<PropertyDeclaration> linkProperties = new ArrayList<>();
            List<PropertyDeclaration> provideProperties = new ArrayList<>();
            List<PropertyDeclaration> consumeProperties = new ArrayList<>();

            for (PropertyDeclaration prop : properties) {
                if (prop.hasDecorator(Decorators.STATE)) {
                    stateProperties.add(prop);
                } else if (prop.hasDecorator(Decorators.PROP)) {
                    propProperties.add(prop);
                } else if (prop.hasDecorator(Decorators.LINK)) {
                    linkProperties.add(prop);
                } else if (prop.hasDecorator(Decorators.PROVIDE)) {
                    provideProperties.add(prop);
                } else if (prop.hasDecorator(Decorators.CONSUME)) {
                    consumeProperties.add(prop);
                }
            }

            // Transform State properties
            for (PropertyDeclaration stateProp : stateProperties) {
                transformStateProperty(classDecl, stateProp);
            }

            // Transform Prop properties
            for (PropertyDeclaration propProp : propProperties) {
                transformPropProperty(classDecl, propProp);
            }

            // Transform Link properties
            for (PropertyDeclaration linkProp : linkProperties) {
                transformLinkProperty(classDecl, linkProp);
            }

            // Transform Provide properties
            for (PropertyDeclaration provideProp : provideProperties) {
                transformProvideProperty(classDecl, provideProp);
            }

            // Transform Consume properties
            for (PropertyDeclaration consumeProp : consumeProperties) {
                transformConsumeProperty(classDecl, consumeProp);
            }

            // Add constructor if needed (only for State properties)
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
            MethodDeclaration.Parameter builderParam = new MethodDeclaration.Parameter(Symbols.BUILDER_PARAM_NAME);
            builderParam.setType(RuntimeFunctions.BUILDER_PARAM);
            method.addParameter(builderParam);
        }
        return method;
    }

    /**
     * Generic property transformation method.
     * Handles State, Prop, Link, Provide, Consume properties.
     */
    private void transformProperty(ClassDeclaration classDecl, PropertyDeclaration prop, PropertyTypeConfig config) {
        String propName = prop.getName();
        String privateName = Symbols.privatePropertyName(propName);
        String propType = prop.getTypeAnnotation();

        // Create private property declaration
        PropertyDeclaration privateProp = new PropertyDeclaration(privateName);
        privateProp.setTypeAnnotation(config.observedPropertyType + Symbols.LEFT_ANGLE + propType + Symbols.RIGHT_ANGLE);
        privateProp.setVisibility(PropertyDeclaration.Visibility.PRIVATE);
        if (config.useInitializer) {
            privateProp.setInitializer(prop.getInitializer());
        }

        // Create getter
        MethodDeclaration getter = new MethodDeclaration(Symbols.getterName(propName));
        getter.setReturnType(propType);
        getter.setBody(new ExpressionStatement("return this." + privateName + ".get()"));

        // Create setter
        MethodDeclaration setter = new MethodDeclaration(Symbols.setterName(propName));
        MethodDeclaration.Parameter valueParam = new MethodDeclaration.Parameter("newValue", propType);
        setter.addParameter(valueParam);
        setter.setReturnType("void");
        setter.setBody(new ExpressionStatement("this." + privateName + ".set(newValue)"));

        // Find and remove original property from class
        classDecl.getMembers().remove(prop);

        // Add new members: private property, getter, setter
        classDecl.addMember(privateProp);
        classDecl.addMember(getter);
        classDecl.addMember(setter);
    }

    /**
     * Transforms a state property.
     * Creates the private variable with __ suffix and getter/setter.
     */
    private void transformStateProperty(ClassDeclaration classDecl, PropertyDeclaration stateProp) {
        transformProperty(classDecl, stateProp, STATE_CONFIG);
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
            String privateName = propName + "__";
            String initializer = stateProp.getInitializer();

            // Initialize state property with its initial value
            // Use the initializer value directly, or undefined if not provided
            String initValue = (initializer != null && !initializer.isEmpty()) ? initializer : "undefined";

            initCode.append("this.").append(privateName).append(" = ")
                   .append("this.").append(RuntimeFunctions.CREATE_STATE).append("(")
                   .append("'").append(propName).append("', ")
                   .append("() => ").append(initValue)
                   .append(");\n");
        }

        constructor.setBody(new ExpressionStatement(initCode.toString()));

        // Add constructor at the beginning of the class
        classDecl.getMembers().add(0, constructor);
    }

    /**
     * Transforms a Prop property.
     * Creates a private variable with ObservedPropertySimpleOneWay and getter/setter.
     * Prop properties are passed from parent component (one-way data flow).
     * They use createProp() instead of createState().
     */
    private void transformPropProperty(ClassDeclaration classDecl, PropertyDeclaration propProp) {
        transformProperty(classDecl, propProp, PROP_CONFIG);
    }

    /**
     * Transforms a Link property.
     * Creates a private variable with ObservedPropertySimpleTwoWay and getter/setter.
     * Link properties allow two-way data flow between parent and child.
     * They use createLink() instead of createProp().
     */
    private void transformLinkProperty(ClassDeclaration classDecl, PropertyDeclaration linkProp) {
        transformProperty(classDecl, linkProp, LINK_CONFIG);
    }

    /**
     * Transforms a Provide property.
     * Creates a private variable with ObservedPropertySimple and getter/setter.
     * Provide properties provide data to descendant components.
     */
    private void transformProvideProperty(ClassDeclaration classDecl, PropertyDeclaration provideProp) {
        transformProperty(classDecl, provideProp, PROVIDE_CONFIG);
    }

    /**
     * Transforms a Consume property.
     * Creates a private variable with ObservedPropertySimple and getter/setter.
     * Consume properties consume data from ancestor components.
     */
    private void transformConsumeProperty(ClassDeclaration classDecl, PropertyDeclaration consumeProp) {
        transformProperty(classDecl, consumeProp, CONSUME_CONFIG);
    }

    /**
     * Validates @Entry decorator usage.
     * @Entry should only be used on class components (struct).
     */
    private void validateEntryDecorator(ClassDeclaration classDecl) {
        // @Entry must be used on a struct (component)
        if (!classDecl.isStruct()) {
            System.err.println("Warning: @Entry decorator should only be used on struct components. " +
                "Found on class: " + classDecl.getName());
        }

        // @Entry typically requires @Component as well
        if (!classDecl.hasDecorator(Decorators.COMPONENT)) {
            System.err.println("Warning: @Entry decorator should be used together with @Component. " +
                "Found on class: " + classDecl.getName());
        }
    }
}
