package com.ets2jsc.transformer.decorators;

import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.constant.RuntimeFunctions;
import com.ets2jsc.constant.Symbols;

/**
 * Abstract base class for property transformers.
 * <p>
 * Uses the Template Method pattern to define the algorithm for transforming
 * decorated properties into private properties with getter/setter methods.
 * <p>
 * Subclasses implement hook methods to provide decorator-specific behavior:
 * <ul>
 *   <li>{@link #getObservedPropertyType()} - the ObservedProperty subtype</li>
 *   <li>{@link #getInitializer(PropertyDeclaration)} - the property initializer</li>
 *   <li>{@link #needsConstructorInit()} - whether to initialize in constructor</li>
 *   <li>{@link #getCreateMethodName()} - the runtime create method name</li>
 * </ul>
 */
public abstract class PropertyTransformer {

    /**
     * Transforms a property declaration into a private property with getter/setter.
     * This is the template method that defines the transformation algorithm.
     *
     * @param classDecl the class declaration to modify
     * @param prop the property declaration to transform
     */
    public final void transform(ClassDeclaration classDecl, PropertyDeclaration prop) {
        String propName = prop.getName();
        String privateName = getPrivateName(propName);
        String propType = prop.getTypeAnnotation();

        // Create private property declaration
        PropertyDeclaration privateProp = createPrivateProperty(privateName, propType, prop);
        privateProp.setVisibility(PropertyDeclaration.Visibility.PRIVATE);

        // Create getter and setter
        MethodDeclaration getter = createGetter(propName, privateName, propType);
        MethodDeclaration setter = createSetter(propName, privateName, propType);

        // Update class declaration
        updateClassDeclaration(classDecl, prop, privateProp, getter, setter);
    }

    /**
     * Hook method: Gets the ObservedProperty type for this decorator.
     *
     * @return the ObservedProperty type name
     */
    protected abstract String getObservedPropertyType();

    /**
     * Hook method: Gets the initializer expression for the private property.
     *
     * @param prop the original property declaration
     * @return the initializer expression, or null if no initializer
     */
    protected abstract String getInitializer(PropertyDeclaration prop);

    /**
     * Hook method: Checks if this property needs constructor initialization.
     *
     * @return true if constructor initialization is needed
     */
    protected boolean needsConstructorInit() {
        return false;
    }

    /**
     * Hook method: Gets the runtime create method name.
     *
     * @return the create method name (e.g., "createState", "createProp")
     */
    protected String getCreateMethodName() {
        return RuntimeFunctions.CREATE_STATE;
    }

    /**
     * Hook method: Gets the private property suffix.
     *
     * @return the private property suffix
     */
    protected String getPrivatePropertySuffix() {
        return Symbols.PRIVATE_PROPERTY_SUFFIX;
    }

    /**
     * Gets the private property name for a given public property name.
     *
     * @param propertyName the public property name
     * @return the private property name
     */
    protected String getPrivateName(String propertyName) {
        return propertyName + getPrivatePropertySuffix();
    }

    /**
     * Creates the private property declaration.
     *
     * @param privateName the private property name
     * @param propType the property type
     * @param originalProp the original property declaration
     * @return the private property declaration
     */
    protected PropertyDeclaration createPrivateProperty(String privateName, String propType,
                                                        PropertyDeclaration originalProp) {
        PropertyDeclaration privateProp = new PropertyDeclaration(privateName);
        privateProp.setTypeAnnotation(getObservedPropertyType() + "<" + propType + ">");
        privateProp.setInitializer(getInitializer(originalProp));
        return privateProp;
    }

    /**
     * Creates the getter method declaration.
     *
     * @param propName the public property name
     * @param privateName the private property name
     * @param propType the property type
     * @return the getter method declaration
     */
    protected MethodDeclaration createGetter(String propName, String privateName, String propType) {
        MethodDeclaration getter = new MethodDeclaration(Symbols.getterName(propName));
        getter.setReturnType(propType);
        getter.setBody(new com.ets2jsc.ast.ExpressionStatement(
                "return " + Symbols.THIS_KEYWORD_FULL + "." + privateName + "." + RuntimeFunctions.GET + "()"));
        return getter;
    }

    /**
     * Creates the setter method declaration.
     *
     * @param propName the public property name
     * @param privateName the private property name
     * @param propType the property type
     * @return the setter method declaration
     */
    protected MethodDeclaration createSetter(String propName, String privateName, String propType) {
        MethodDeclaration setter = new MethodDeclaration(Symbols.setterName(propName));
        MethodDeclaration.Parameter valueParam = new MethodDeclaration.Parameter("newValue", propType);
        setter.addParameter(valueParam);
        setter.setReturnType("void");
        setter.setBody(new com.ets2jsc.ast.ExpressionStatement(
                Symbols.THIS_KEYWORD_FULL + "." + privateName + "." + RuntimeFunctions.SET + "(newValue)"));
        return setter;
    }

    /**
     * Updates the class declaration by removing the original property and
     * adding the new private property, getter, and setter.
     *
     * @param classDecl the class declaration to modify
     * @param originalProp the original property declaration
     * @param privateProp the private property declaration
     * @param getter the getter method
     * @param setter the setter method
     */
    protected void updateClassDeclaration(ClassDeclaration classDecl, PropertyDeclaration originalProp,
                                         PropertyDeclaration privateProp, MethodDeclaration getter,
                                         MethodDeclaration setter) {
        // Find and remove original property from class
        classDecl.getMembers().remove(originalProp);

        // Add new members: private property, getter, setter
        classDecl.addMember(privateProp);
        classDecl.addMember(getter);
        classDecl.addMember(setter);
    }

    /**
     * Creates constructor initialization code for this property.
     *
     * @param propName the public property name
     * @param privateName the private property name
     * @return the initialization code
     */
    protected String createConstructorInit(String propName, String privateName) {
        return Symbols.THIS_KEYWORD_FULL + "." + privateName + " = "
                + Symbols.THIS_KEYWORD_FULL + "." + getCreateMethodName() + "("
                + "\"" + propName + "\", "
                + "() => " + Symbols.THIS_KEYWORD_FULL + "." + propName
                + ");";
    }

    /**
     * Checks if this transformer can handle the given property.
     *
     * @param prop the property declaration
     * @return true if this transformer can handle the property
     */
    public abstract boolean canHandle(PropertyDeclaration prop);
}
