package com.ets2jsc.transformer.decorators;

import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.constant.Decorators;
import com.ets2jsc.transformer.decorators.impl.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.ets2jsc.ast.MethodDeclaration;

/**
 * Unit tests for PropertyTransformerRegistry and concrete transformers.
 */
public class PropertyTransformerRegistryTest {

    @Test
    public void testRegistry_DefaultTransformers() {
        PropertyTransformerRegistry registry = new PropertyTransformerRegistry();

        assertEquals(5, registry.size());
    }

    @Test
    public void testRegistry_FindStateTransformer() {
        PropertyTransformerRegistry registry = new PropertyTransformerRegistry();

        PropertyDeclaration prop = createProperty(Decorators.STATE, "count", "number");
        PropertyTransformer transformer = registry.findTransformer(prop);

        assertNotNull(transformer);
        assertTrue(transformer instanceof StatePropertyTransformer);
    }

    @Test
    public void testRegistry_FindPropTransformer() {
        PropertyTransformerRegistry registry = new PropertyTransformerRegistry();

        PropertyDeclaration prop = createProperty(Decorators.PROP, "title", "string");
        PropertyTransformer transformer = registry.findTransformer(prop);

        assertNotNull(transformer);
        assertInstanceOf(PropPropertyTransformer.class, transformer);
    }

    @Test
    public void testRegistry_FindLinkTransformer() {
        PropertyTransformerRegistry registry = new PropertyTransformerRegistry();

        PropertyDeclaration prop = createProperty(Decorators.LINK, "value", "number");
        PropertyTransformer transformer = registry.findTransformer(prop);

        assertNotNull(transformer);
        assertInstanceOf(LinkPropertyTransformer.class, transformer);
    }

    @Test
    public void testRegistry_FindProvideTransformer() {
        PropertyTransformerRegistry registry = new PropertyTransformerRegistry();

        PropertyDeclaration prop = createProperty(Decorators.PROVIDE, "data", "Object");
        PropertyTransformer transformer = registry.findTransformer(prop);

        assertNotNull(transformer);
        assertInstanceOf(ProvidePropertyTransformer.class, transformer);
    }

    @Test
    public void testRegistry_FindConsumeTransformer() {
        PropertyTransformerRegistry registry = new PropertyTransformerRegistry();

        PropertyDeclaration prop = createProperty(Decorators.CONSUME, "theme", "string");
        PropertyTransformer transformer = registry.findTransformer(prop);

        assertNotNull(transformer);
        assertInstanceOf(ConsumePropertyTransformer.class, transformer);
    }

    @Test
    public void testRegistry_FindUnknownDecorator() {
        PropertyTransformerRegistry registry = new PropertyTransformerRegistry();

        PropertyDeclaration prop = createProperty("UnknownDecorator", "value", "any");
        PropertyTransformer transformer = registry.findTransformer(prop);

        assertNull(transformer);
    }

    @Test
    public void testRegistry_CanTransform() {
        PropertyTransformerRegistry registry = new PropertyTransformerRegistry();

        PropertyDeclaration stateProp = createProperty(Decorators.STATE, "count", "number");
        PropertyDeclaration unknownProp = createProperty("Unknown", "value", "any");

        assertTrue(registry.canTransform(stateProp));
        assertFalse(registry.canTransform(unknownProp));
    }

    @Test
    public void testRegistry_RegisterCustom() {
        PropertyTransformerRegistry registry = new PropertyTransformerRegistry();
        int originalSize = registry.size();

        PropertyTransformer custom = new PropertyTransformer() {
            @Override
            protected String getObservedPropertyType() {
                return "CustomType";
            }

            @Override
            protected String getInitializer(PropertyDeclaration prop) {
                return null;
            }

            @Override
            public boolean canHandle(PropertyDeclaration prop) {
                return prop.hasDecorator("Custom");
            }
        };

        registry.register(custom);
        assertEquals(originalSize + 1, registry.size());
    }

    @Test
    public void testRegistry_Clear() {
        PropertyTransformerRegistry registry = new PropertyTransformerRegistry();
        assertTrue(registry.size() > 0);

        registry.clear();
        assertEquals(0, registry.size());
    }

    @Test
    public void testStatePropertyTransformer_Transform() {
        StatePropertyTransformer transformer = new StatePropertyTransformer();
        ClassDeclaration classDecl = new ClassDeclaration("TestComponent");
        PropertyDeclaration prop = createProperty(Decorators.STATE, "count", "number");

        assertEquals(0, classDecl.getMembers().size());

        transformer.transform(classDecl, prop);

        // Should have 3 members: private property, getter, setter
        assertEquals(3, classDecl.getMembers().size());

        // Check private property exists
        PropertyDeclaration privateProp = findProperty(classDecl, "count__");
        assertNotNull(privateProp);
        assertEquals("PRIVATE", privateProp.getVisibility().toString());

        // Check getter exists
        MethodDeclaration getter = findMethod(classDecl, "get count");
        assertNotNull(getter);
        assertEquals("number", getter.getReturnType());

        // Check setter exists
        MethodDeclaration setter = findMethod(classDecl, "set count");
        assertNotNull(setter);
    }

    @Test
    public void testPropPropertyTransformer_Transform() {
        PropPropertyTransformer transformer = new PropPropertyTransformer();
        ClassDeclaration classDecl = new ClassDeclaration("TestComponent");
        PropertyDeclaration prop = createProperty(Decorators.PROP, "title", "string");

        transformer.transform(classDecl, prop);

        assertEquals(3, classDecl.getMembers().size());

        PropertyDeclaration privateProp = findProperty(classDecl, "title__");
        assertNotNull(privateProp);

        MethodDeclaration getter = findMethod(classDecl, "get title");
        assertNotNull(getter);
    }

    @Test
    public void testLinkPropertyTransformer_Transform() {
        LinkPropertyTransformer transformer = new LinkPropertyTransformer();
        ClassDeclaration classDecl = new ClassDeclaration("TestComponent");
        PropertyDeclaration prop = createProperty(Decorators.LINK, "value", "number");

        transformer.transform(classDecl, prop);

        assertEquals(3, classDecl.getMembers().size());

        PropertyDeclaration privateProp = findProperty(classDecl, "value__");
        assertNotNull(privateProp);
    }

    @Test
    public void testProvidePropertyTransformer_Transform() {
        ProvidePropertyTransformer transformer = new ProvidePropertyTransformer();
        ClassDeclaration classDecl = new ClassDeclaration("TestComponent");
        PropertyDeclaration prop = createProperty(Decorators.PROVIDE, "data", "Object");

        transformer.transform(classDecl, prop);

        assertEquals(3, classDecl.getMembers().size());

        PropertyDeclaration privateProp = findProperty(classDecl, "data__");
        assertNotNull(privateProp);
    }

    @Test
    public void testConsumePropertyTransformer_Transform() {
        ConsumePropertyTransformer transformer = new ConsumePropertyTransformer();
        ClassDeclaration classDecl = new ClassDeclaration("TestComponent");
        PropertyDeclaration prop = createProperty(Decorators.CONSUME, "theme", "string");

        transformer.transform(classDecl, prop);

        assertEquals(3, classDecl.getMembers().size());

        PropertyDeclaration privateProp = findProperty(classDecl, "theme__");
        assertNotNull(privateProp);
    }

    // Helper methods

    private PropertyDeclaration createProperty(String decorator, String name, String type) {
        PropertyDeclaration prop = new PropertyDeclaration(name);
        prop.setTypeAnnotation(type);
        prop.addDecorator(new Decorator(decorator));
        prop.setInitializer("null");
        return prop;
    }

    private PropertyDeclaration findProperty(ClassDeclaration classDecl, String name) {
        for (Object member : classDecl.getMembers()) {
            if (member instanceof PropertyDeclaration prop) {
                if (name.equals(prop.getName())) {
                    return prop;
                }
            }
        }
        return null;
    }

    private MethodDeclaration findMethod(ClassDeclaration classDecl, String name) {
        for (Object member : classDecl.getMembers()) {
            if (member instanceof MethodDeclaration method) {
                if (name.equals(method.getName())) {
                    return method;
                }
            }
        }
        return null;
    }
}
