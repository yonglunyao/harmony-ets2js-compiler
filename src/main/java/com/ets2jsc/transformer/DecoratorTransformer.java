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
                    .anyMatch(d -> d.isStateDecorator() || d.isPropDecorator() || d.isLinkDecorator());
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
        privateProp.setInitializer(stateProp.getInitializer());

        // Create getter
        MethodDeclaration getter = new MethodDeclaration("get " + propName);
        getter.setReturnType(propType);
        getter.setBody(new ExpressionStatement("return this." + privateName + ".get()"));

        // Create setter
        MethodDeclaration setter = new MethodDeclaration("set " + propName);
        MethodDeclaration.Parameter valueParam = new MethodDeclaration.Parameter("newValue", propType);
        setter.addParameter(valueParam);
        setter.setReturnType("void");
        setter.setBody(new ExpressionStatement("this." + privateName + ".set(newValue)"));

        // Find and remove original property from class
        classDecl.getMembers().remove(stateProp);

        // Add new members: private property, getter, setter
        classDecl.addMember(privateProp);
        classDecl.addMember(getter);
        classDecl.addMember(setter);
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

            // Initialize state property
            initCode.append("this.").append(privateName).append(" = ")
                   .append("this.").append(RuntimeFunctions.CREATE_STATE).append("(")
                   .append("'").append(propName).append("', ")
                   .append("() => this.").append(propName)
                   .append(");\n");
        }

        constructor.setBody(new ExpressionStatement(initCode.toString()));

        // Add constructor at the beginning of the class
        classDecl.getMembers().add(0, constructor);
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
                return RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE_ONE_WAY;
            case Decorators.LINK:
                return RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE_TWO_WAY;
            default:
                return RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE;
        }
    }

    /**
     * Transforms a Prop property.
     * Creates a private variable with ObservedPropertySimpleOneWay and getter/setter.
     * Prop properties are passed from parent component (one-way data flow).
     * They use createProp() instead of createState().
     */
    private void transformPropProperty(ClassDeclaration classDecl, PropertyDeclaration propProp) {
        String propName = propProp.getName();
        String privateName = propName + "__";
        String propType = propProp.getTypeAnnotation();

        // Create private property declaration
        PropertyDeclaration privateProp = new PropertyDeclaration(privateName);
        privateProp.setTypeAnnotation(RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE_ONE_WAY + "<" + propType + ">");
        privateProp.setVisibility(PropertyDeclaration.Visibility.PRIVATE);
        // No initializer - Prop properties are set by parent component

        // Create getter
        MethodDeclaration getter = new MethodDeclaration("get " + propName);
        getter.setReturnType(propType);
        getter.setBody(new ExpressionStatement("return this." + privateName + ".get()"));

        // Create setter
        MethodDeclaration setter = new MethodDeclaration("set " + propName);
        MethodDeclaration.Parameter valueParam = new MethodDeclaration.Parameter("newValue", propType);
        setter.addParameter(valueParam);
        setter.setReturnType("void");
        setter.setBody(new ExpressionStatement("this." + privateName + ".set(newValue)"));

        // Find and remove original property from class
        classDecl.getMembers().remove(propProp);

        // Add new members: private property, getter, setter
        classDecl.addMember(privateProp);
        classDecl.addMember(getter);
        classDecl.addMember(setter);
    }

    /**
     * Transforms a Link property.
     * Creates a private variable with ObservedPropertySimpleTwoWay and getter/setter.
     * Link properties allow two-way data flow between parent and child.
     * They use createLink() instead of createProp().
     */
    private void transformLinkProperty(ClassDeclaration classDecl, PropertyDeclaration linkProp) {
        String propName = linkProp.getName();
        String privateName = propName + "__";
        String propType = linkProp.getTypeAnnotation();

        // Create private property declaration
        PropertyDeclaration privateProp = new PropertyDeclaration(privateName);
        privateProp.setTypeAnnotation(RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE_TWO_WAY + "<" + propType + ">");
        privateProp.setVisibility(PropertyDeclaration.Visibility.PRIVATE);
        // No initializer - Link properties are set by parent component

        // Create getter
        MethodDeclaration getter = new MethodDeclaration("get " + propName);
        getter.setReturnType(propType);
        getter.setBody(new ExpressionStatement("return this." + privateName + ".get()"));

        // Create setter
        MethodDeclaration setter = new MethodDeclaration("set " + propName);
        MethodDeclaration.Parameter valueParam = new MethodDeclaration.Parameter("newValue", propType);
        setter.addParameter(valueParam);
        setter.setReturnType("void");
        setter.setBody(new ExpressionStatement("this." + privateName + ".set(newValue)"));

        // Find and remove original property from class
        classDecl.getMembers().remove(linkProp);

        // Add new members: private property, getter, setter
        classDecl.addMember(privateProp);
        classDecl.addMember(getter);
        classDecl.addMember(setter);
    }

    /**
     * Transforms a Provide property.
     * Creates a private variable with ObservedPropertySimple and getter/setter.
     * Provide properties provide data to descendant components.
     */
    private void transformProvideProperty(ClassDeclaration classDecl, PropertyDeclaration provideProp) {
        String propName = provideProp.getName();
        String privateName = propName + "__";
        String propType = provideProp.getTypeAnnotation();

        // Create private property declaration
        PropertyDeclaration privateProp = new PropertyDeclaration(privateName);
        privateProp.setTypeAnnotation(RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE + "<" + propType + ">");
        privateProp.setVisibility(PropertyDeclaration.Visibility.PRIVATE);
        // Provide properties are initialized at declaration
        privateProp.setInitializer(provideProp.getInitializer());

        // Create getter
        MethodDeclaration getter = new MethodDeclaration("get " + propName);
        getter.setReturnType(propType);
        getter.setBody(new ExpressionStatement("return this." + privateName + ".get()"));

        // Create setter
        MethodDeclaration setter = new MethodDeclaration("set " + propName);
        MethodDeclaration.Parameter valueParam = new MethodDeclaration.Parameter("newValue", propType);
        setter.addParameter(valueParam);
        setter.setReturnType("void");
        setter.setBody(new ExpressionStatement("this." + privateName + ".set(newValue)"));

        // Find and remove original property from class
        classDecl.getMembers().remove(provideProp);

        // Add new members: private property, getter, setter
        classDecl.addMember(privateProp);
        classDecl.addMember(getter);
        classDecl.addMember(setter);
    }

    /**
     * Transforms a Consume property.
     * Creates a private variable with ObservedPropertySimple and getter/setter.
     * Consume properties consume data from ancestor components.
     */
    private void transformConsumeProperty(ClassDeclaration classDecl, PropertyDeclaration consumeProp) {
        String propName = consumeProp.getName();
        String privateName = propName + "__";
        String propType = consumeProp.getTypeAnnotation();

        // Create private property declaration
        PropertyDeclaration privateProp = new PropertyDeclaration(privateName);
        privateProp.setTypeAnnotation(RuntimeFunctions.OBSERVED_PROPERTY_SIMPLE + "<" + propType + ">");
        privateProp.setVisibility(PropertyDeclaration.Visibility.PRIVATE);
        // Consume properties are initialized at declaration
        privateProp.setInitializer(consumeProp.getInitializer());

        // Create getter
        MethodDeclaration getter = new MethodDeclaration("get " + propName);
        getter.setReturnType(propType);
        getter.setBody(new ExpressionStatement("return this." + privateName + ".get()"));

        // Create setter
        MethodDeclaration setter = new MethodDeclaration("set " + propName);
        MethodDeclaration.Parameter valueParam = new MethodDeclaration.Parameter("newValue", propType);
        setter.addParameter(valueParam);
        setter.setReturnType("void");
        setter.setBody(new ExpressionStatement("this." + privateName + ".set(newValue)"));

        // Find and remove original property from class
        classDecl.getMembers().remove(consumeProp);

        // Add new members: private property, getter, setter
        classDecl.addMember(privateProp);
        classDecl.addMember(getter);
        classDecl.addMember(setter);
    }
}
