package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import com.ets2jsc.constant.Decorators;
import com.ets2jsc.constant.RuntimeFunctions;
import com.ets2jsc.constant.Symbols;
import com.ets2jsc.transformer.decorators.PropertyTransformer;
import com.ets2jsc.transformer.decorators.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms decorators in ETS code.
 * Handles @Component, @State, @Prop, @Link, @Provide, @Consume.
 */
public class DecoratorTransformer implements AstTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecoratorTransformer.class);

    private final List<PropertyTransformer> propertyTransformers;

    public DecoratorTransformer(boolean partialUpdateMode) {
        this.propertyTransformers = createPropertyTransformers();
    }

    /**
     * Creates property transformers for each decorator type.
     * CC: 1 (just list creation)
     */
    private List<PropertyTransformer> createPropertyTransformers() {
        List<PropertyTransformer> transformers = new ArrayList<>();
        transformers.add(new StatePropertyTransformer());
        transformers.add(new PropPropertyTransformer());
        transformers.add(new LinkPropertyTransformer());
        transformers.add(new ProvidePropertyTransformer());
        transformers.add(new ConsumePropertyTransformer());
        return transformers;
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
            return hasComponentDecorator((ClassDeclaration) node);
        }
        if (node instanceof PropertyDeclaration) {
            return hasStateDecorator((PropertyDeclaration) node);
        }
        return false;
    }

    /**
     * Checks if class has component decorator.
     * CC: 1
     */
    private boolean hasComponentDecorator(ClassDeclaration classDecl) {
        return classDecl.getDecorators().stream()
                .anyMatch(Decorator::isComponentDecorator);
    }

    /**
     * Checks if property has state decorator.
     * CC: 1
     */
    private boolean hasStateDecorator(PropertyDeclaration prop) {
        return prop.getDecorators().stream()
                .anyMatch(d -> d.isStateDecorator() || d.isPropDecorator() || d.isLinkDecorator());
    }

    /**
     * Transforms a class declaration with decorators.
     * CC: 3 (if checks + method calls)
     */
    private ClassDeclaration transformClassDeclaration(ClassDeclaration classDecl) {
        boolean isComponent = classDecl.hasDecorator(Decorators.COMPONENT);
        boolean isEntry = classDecl.hasDecorator(Decorators.ENTRY);

        if (isEntry) {
            validateEntryDecorator(classDecl);
            classDecl.setExport(true);
        }

        if (isComponent || isEntry) {
            transformToViewClass(classDecl);
        }

        return classDecl;
    }

    /**
     * Transforms struct to View class.
     * CC: 2 (method calls)
     */
    private void transformToViewClass(ClassDeclaration classDecl) {
        classDecl.setStruct(false);
        classDecl.setSuperClass(RuntimeFunctions.VIEW);

        // First collect state properties before transformation
        List<PropertyDeclaration> stateProperties = findStateProperties(classDecl);

        transformProperties(classDecl);

        // Add constructor if there are state properties
        if (!stateProperties.isEmpty()) {
            addConstructor(classDecl, stateProperties);
        }
    }

    /**
     * Transforms all decorated properties.
     * CC: 2 (loop + method call)
     */
    private void transformProperties(ClassDeclaration classDecl) {
        List<PropertyDeclaration> properties = classDecl.getProperties();

        for (PropertyDeclaration prop : properties) {
            transformProperty(classDecl, prop);
        }
    }

    /**
     * Transforms a single property using appropriate transformer.
     * CC: 2 (loop + early continue)
     */
    private void transformProperty(ClassDeclaration classDecl, PropertyDeclaration prop) {
        for (PropertyTransformer transformer : propertyTransformers) {
            if (transformer.canHandle(prop)) {
                transformer.transform(classDecl, prop);
                return;
            }
        }
    }

    // Removed - now handled in transformToViewClass to avoid timing issues

    /**
     * Finds all State properties in class.
     * CC: 2 (loop + condition)
     */
    private List<PropertyDeclaration> findStateProperties(ClassDeclaration classDecl) {
        List<PropertyDeclaration> stateProperties = new ArrayList<>();

        for (PropertyDeclaration prop : classDecl.getProperties()) {
            if (prop.hasDecorator(Decorators.STATE)) {
                stateProperties.add(prop);
            }
        }

        return stateProperties;
    }

    /**
     * Adds constructor with State property initialization.
     * CC: 2 (loop + string building)
     */
    private void addConstructor(ClassDeclaration classDecl, List<PropertyDeclaration> stateProps) {
        MethodDeclaration constructor = new MethodDeclaration("constructor");
        constructor.setBody(new com.ets2jsc.ast.ExpressionStatement(buildConstructorBody(stateProps)));
        classDecl.getMembers().add(0, constructor);
    }

    /**
     * Builds constructor body with State property initialization.
     * CC: 2 (loop + string building)
     */
    private String buildConstructorBody(List<PropertyDeclaration> stateProps) {
        StringBuilder sb = new StringBuilder();
        sb.append("super();\n");

        for (PropertyDeclaration stateProp : stateProps) {
            String propName = stateProp.getName();
            String privateName = propName + Symbols.PRIVATE_PROPERTY_SUFFIX;
            String initValue = getInitializerValue(stateProp);

            sb.append(buildStateInit(propName, privateName, initValue));
        }

        return sb.toString();
    }

    /**
     * Gets initializer value for property.
     * CC: 2 (null check + ternary)
     */
    private String getInitializerValue(PropertyDeclaration stateProp) {
        String initializer = stateProp.getInitializer();
        if (initializer != null && !initializer.isEmpty()) {
            return initializer;
        }
        return Symbols.UNDEFINED_LITERAL;
    }

    /**
     * Builds State property initialization statement.
     * CC: 1
     */
    private String buildStateInit(String propName, String privateName, String initValue) {
        return Symbols.THIS_KEYWORD_FULL + "." + privateName + " = "
                + Symbols.THIS_KEYWORD_FULL + "." + RuntimeFunctions.CREATE_STATE + "("
                + "'" + propName + "', "
                + "() => " + initValue
                + ");\n";
    }

    /**
     * Transforms a property declaration (no-op, handled at class level).
     * CC: 1
     */
    private PropertyDeclaration transformPropertyDeclaration(PropertyDeclaration prop) {
        // State properties are handled at class level
        return prop;
    }

    /**
     * Transforms a method declaration with decorators.
     * CC: 1
     */
    private MethodDeclaration transformMethodDeclaration(MethodDeclaration method) {
        if (method.isBuilderMethod()) {
            addBuilderParameter(method);
        }
        return method;
    }

    /**
     * Adds BuilderParam parameter to builder method.
     * CC: 1
     */
    private void addBuilderParameter(MethodDeclaration method) {
        MethodDeclaration.Parameter builderParam = new MethodDeclaration.Parameter(
                Symbols.BUILDER_PARAM_NAME);
        builderParam.setType(RuntimeFunctions.BUILDER_PARAM);
        method.addParameter(builderParam);
    }

    /**
     * Validates @Entry decorator usage.
     * CC: 2 (if checks)
     */
    private void validateEntryDecorator(ClassDeclaration classDecl) {
        if (!classDecl.isStruct()) {
            LOGGER.warn("@Entry decorator should only be used on struct components. Found on class: {}",
                classDecl.getName());
        }

        if (!classDecl.hasDecorator(Decorators.COMPONENT)) {
            LOGGER.warn("@Entry decorator should be used together with @Component. Found on class: {}",
                classDecl.getName());
        }
    }
}
