package com.ets2jsc.domain.model.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PropertyDeclaration
 */
@DisplayName("Property Declaration Tests")
class PropertyDeclarationTest {

    @Test
    @DisplayName("Test property creation and basic properties")
    void testPropertyCreation() {
        PropertyDeclaration prop = new PropertyDeclaration("myProperty");

        assertEquals("myProperty", prop.getName());
        assertEquals("PropertyDeclaration", prop.getType());
        assertFalse(prop.isReadOnly());
        assertEquals(PropertyDeclaration.Visibility.INTERNAL, prop.getVisibility());
    }

    @Test
    @DisplayName("Test type annotation")
    void testTypeAnnotation() {
        PropertyDeclaration prop = new PropertyDeclaration("count");

        prop.setTypeAnnotation("number");
        assertEquals("number", prop.getTypeAnnotation());

        prop.setTypeAnnotation("string");
        assertEquals("string", prop.getTypeAnnotation());

        prop.setTypeAnnotation("boolean");
        assertEquals("boolean", prop.getTypeAnnotation());
    }

    @Test
    @DisplayName("Test initializer")
    void testInitializer() {
        PropertyDeclaration prop = new PropertyDeclaration("message");

        prop.setInitializer("\"Hello\"");
        assertEquals("\"Hello\"", prop.getInitializer());

        prop.setInitializer("42");
        assertEquals("42", prop.getInitializer());

        prop.setInitializer("true");
        assertEquals("true", prop.getInitializer());
    }

    @Test
    @DisplayName("Test decorator addition and check")
    void testDecorators() {
        PropertyDeclaration prop = new PropertyDeclaration("count");

        assertFalse(prop.hasDecorator("State"));
        assertFalse(prop.hasDecorator("Prop"));

        Decorator stateDecorator = new Decorator("State");
        prop.addDecorator(stateDecorator);

        assertTrue(prop.hasDecorator("State"));
        assertFalse(prop.hasDecorator("Prop"));

        Decorator propDecorator = new Decorator("Prop");
        prop.addDecorator(propDecorator);

        assertTrue(prop.hasDecorator("State"));
        assertTrue(prop.hasDecorator("Prop"));
    }

    @Test
    @DisplayName("Test get decorator")
    void testGetDecorator() {
        PropertyDeclaration prop = new PropertyDeclaration("data");

        Decorator stateDecorator = new Decorator("State");
        prop.addDecorator(stateDecorator);

        Optional<Decorator> retrieved = prop.getDecorator("State");
        assertTrue(retrieved.isPresent());
        assertEquals("State", retrieved.get().getName());

        Optional<Decorator> notFound = prop.getDecorator("Prop");
        assertFalse(notFound.isPresent());
    }

    @Test
    @DisplayName("Test visibility setting")
    void testVisibility() {
        PropertyDeclaration prop = new PropertyDeclaration("secret");

        prop.setVisibility(PropertyDeclaration.Visibility.PRIVATE);
        assertEquals(PropertyDeclaration.Visibility.PRIVATE, prop.getVisibility());

        prop.setVisibility(PropertyDeclaration.Visibility.PUBLIC);
        assertEquals(PropertyDeclaration.Visibility.PUBLIC, prop.getVisibility());

        prop.setVisibility(PropertyDeclaration.Visibility.PROTECTED);
        assertEquals(PropertyDeclaration.Visibility.PROTECTED, prop.getVisibility());
    }

    @Test
    @DisplayName("Test read-only property")
    void testReadOnly() {
        PropertyDeclaration prop = new PropertyDeclaration("constant");

        assertFalse(prop.isReadOnly());

        prop.setReadOnly(true);
        assertTrue(prop.isReadOnly());

        prop.setReadOnly(false);
        assertFalse(prop.isReadOnly());
    }

    @Test
    @DisplayName("Test get private variable name")
    void testGetPrivateVarName() {
        PropertyDeclaration prop = new PropertyDeclaration("count");

        assertEquals("count__", prop.getPrivateVarName());

        PropertyDeclaration prop2 = new PropertyDeclaration("message");
        assertEquals("message__", prop2.getPrivateVarName());
    }

    @Test
    @DisplayName("Test get ObservedProperty type")
    void testGetObservedPropertyType() {
        PropertyDeclaration stateProp = new PropertyDeclaration("count");
        stateProp.addDecorator(new Decorator("State"));
        assertEquals("ObservedPropertySimple", stateProp.getObservedPropertyType());

        PropertyDeclaration propProp = new PropertyDeclaration("title");
        propProp.addDecorator(new Decorator("Prop"));
        assertEquals("SynchedPropertySimpleOneWay", propProp.getObservedPropertyType());

        PropertyDeclaration linkProp = new PropertyDeclaration("data");
        linkProp.addDecorator(new Decorator("Link"));
        assertEquals("SynchedPropertySimpleTwoWay", linkProp.getObservedPropertyType());
    }

    @Test
    @DisplayName("Test multiple decorators")
    void testMultipleDecorators() {
        PropertyDeclaration prop = new PropertyDeclaration("value");

        prop.addDecorator(new Decorator("State"));
        prop.addDecorator(new Decorator("CustomDecorator"));
        prop.addDecorator(new Decorator("AnotherDecorator"));

        assertTrue(prop.hasDecorator("State"));
        assertTrue(prop.hasDecorator("CustomDecorator"));
        assertTrue(prop.hasDecorator("AnotherDecorator"));
        assertFalse(prop.hasDecorator("Prop"));
    }

    @Test
    @DisplayName("Test ObservedProperty type with various decorators")
    void testObservedPropertyTypeWithVariousDecorators() {
        // Provide decorator should use ObservedPropertySimple
        PropertyDeclaration provideProp = new PropertyDeclaration("theme");
        provideProp.addDecorator(new Decorator("Provide"));
        assertEquals("ObservedPropertySimple", provideProp.getObservedPropertyType());

        // Consume decorator should use ObservedPropertySimple
        PropertyDeclaration consumeProp = new PropertyDeclaration("inheritedTheme");
        consumeProp.addDecorator(new Decorator("Consume"));
        assertEquals("ObservedPropertySimple", consumeProp.getObservedPropertyType());
    }
}
