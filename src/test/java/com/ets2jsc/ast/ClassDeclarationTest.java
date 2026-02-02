package com.ets2jsc.domain.model.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ClassDeclaration
 */
@DisplayName("Class Declaration Tests")
class ClassDeclarationTest {

    @Test
    @DisplayName("Test class declaration creation")
    void testClassDeclarationCreation() {
        ClassDeclaration classDecl = new ClassDeclaration("MyClass");

        assertEquals("ClassDeclaration", classDecl.getType());
        assertEquals("MyClass", classDecl.getName());
        assertFalse(classDecl.isExport());
        assertFalse(classDecl.isStruct());
        assertNull(classDecl.getSuperClass());
        assertNotNull(classDecl.getDecorators());
        assertNotNull(classDecl.getMembers());
    }

    @Test
    @DisplayName("TestGetClass名")
    void testGetName() {
        ClassDeclaration classDecl = new ClassDeclaration("TestClass");

        assertEquals("TestClass", classDecl.getName());
    }

    @Test
    @DisplayName("TestSet父Class")
    void testSetSuperClass() {
        ClassDeclaration classDecl = new ClassDeclaration("MyView");
        classDecl.setSuperClass("View");

        assertEquals("View", classDecl.getSuperClass());
    }

    @Test
    @DisplayName("TestGet父Class")
    void testGetSuperClass() {
        ClassDeclaration classDecl = new ClassDeclaration("MyView");
        classDecl.setSuperClass("View");

        assertEquals("View", classDecl.getSuperClass());
    }

    @Test
    @DisplayName("TestSetExport")
    void testSetExport() {
        ClassDeclaration classDecl = new ClassDeclaration("MyClass");
        assertFalse(classDecl.isExport());

        classDecl.setExport(true);
        assertTrue(classDecl.isExport());

        classDecl.setExport(false);
        assertFalse(classDecl.isExport());
    }

    @Test
    @DisplayName("TestSet struct")
    void testSetStruct() {
        ClassDeclaration classDecl = new ClassDeclaration("MyComponent");
        classDecl.setStruct(true);

        assertTrue(classDecl.isStruct());

        classDecl.setStruct(false);
        assertFalse(classDecl.isStruct());
    }

    @Test
    @DisplayName("TestAddDecorator")
    void testAddDecorator() {
        ClassDeclaration classDecl = new ClassDeclaration("MyComponent");
        Decorator decorator1 = new Decorator("Component");
        Decorator decorator2 = new Decorator("State");

        classDecl.addDecorator(decorator1);
        classDecl.addDecorator(decorator2);

        assertEquals(2, classDecl.getDecorators().size());
        assertEquals(decorator1, classDecl.getDecorators().get(0));
        assertEquals(decorator2, classDecl.getDecorators().get(1));
    }

    @Test
    @DisplayName("TestGetDecorator")
    void testGetDecorators() {
        ClassDeclaration classDecl = new ClassDeclaration("MyComponent");
        Decorator decorator = new Decorator("Component");

        classDecl.addDecorator(decorator);

        assertEquals(1, classDecl.getDecorators().size());
        assertEquals(decorator, classDecl.getDecorators().get(0));
    }

    @Test
    @DisplayName("TestAdd成员")
    void testAddMember() {
        ClassDeclaration classDecl = new ClassDeclaration("MyClass");
        PropertyDeclaration prop = new PropertyDeclaration("myProp");
        MethodDeclaration method = new MethodDeclaration("myMethod");

        classDecl.addMember(prop);
        classDecl.addMember(method);

        assertEquals(2, classDecl.getMembers().size());
    }

    @Test
    @DisplayName("TestGet成员")
    void testGetMembers() {
        ClassDeclaration classDecl = new ClassDeclaration("MyClass");
        PropertyDeclaration prop = new PropertyDeclaration("value");

        classDecl.addMember(prop);

        assertEquals(1, classDecl.getMembers().size());
        assertEquals(prop, classDecl.getMembers().get(0));
    }

    @Test
    @DisplayName("Test hasDecorator")
    void testHasDecorator() {
        ClassDeclaration classDecl = new ClassDeclaration("MyComponent");
        classDecl.addDecorator(new Decorator("Component"));
        classDecl.addDecorator(new Decorator("State"));

        assertTrue(classDecl.hasDecorator("Component"));
        assertTrue(classDecl.hasDecorator("State"));
        assertFalse(classDecl.hasDecorator("Prop"));
    }

    @Test
    @DisplayName("TestGet属性")
    void testGetProperties() {
        ClassDeclaration classDecl = new ClassDeclaration("MyClass");

        PropertyDeclaration prop1 = new PropertyDeclaration("prop1");
        PropertyDeclaration prop2 = new PropertyDeclaration("prop2");
        MethodDeclaration method = new MethodDeclaration("myMethod");

        classDecl.addMember(prop1);
        classDecl.addMember(method);
        classDecl.addMember(prop2);

        assertEquals(2, classDecl.getProperties().size());
        assertTrue(classDecl.getProperties().contains(prop1));
        assertTrue(classDecl.getProperties().contains(prop2));
    }

    @Test
    @DisplayName("TestGetMethod")
    void testGetMethods() {
        ClassDeclaration classDecl = new ClassDeclaration("MyClass");

        MethodDeclaration method1 = new MethodDeclaration("method1");
        MethodDeclaration method2 = new MethodDeclaration("method2");
        PropertyDeclaration prop = new PropertyDeclaration("value");

        classDecl.addMember(method1);
        classDecl.addMember(prop);
        classDecl.addMember(method2);

        assertEquals(2, classDecl.getMethods().size());
        assertTrue(classDecl.getMethods().contains(method1));
        assertTrue(classDecl.getMethods().contains(method2));
    }

    @Test
    @DisplayName("TestEmptyClassDeclaration")
    void testEmptyClassDeclaration() {
        ClassDeclaration classDecl = new ClassDeclaration("EmptyClass");

        assertTrue(classDecl.getDecorators().isEmpty());
        assertTrue(classDecl.getMembers().isEmpty());
        assertTrue(classDecl.getProperties().isEmpty());
        assertTrue(classDecl.getMethods().isEmpty());
    }

    @Test
    @DisplayName("Test accept Method")
    void testAcceptMethod() {
        ClassDeclaration classDecl = new ClassDeclaration("MyClass");

        assertNotNull(classDecl);
        assertEquals("ClassDeclaration", classDecl.getType());
    }

    @Test
    @DisplayName("TestMultipleDecorator")
    void testMultipleDecorators() {
        ClassDeclaration classDecl = new ClassDeclaration("MyComponent");

        classDecl.addDecorator(new Decorator("Component"));
        classDecl.addDecorator(new Decorator("State"));
        classDecl.addDecorator(new Decorator("Prop"));
        classDecl.addDecorator(new Decorator("Link"));

        assertEquals(4, classDecl.getDecorators().size());
        assertTrue(classDecl.hasDecorator("Component"));
        assertTrue(classDecl.hasDecorator("State"));
        assertTrue(classDecl.hasDecorator("Prop"));
        assertTrue(classDecl.hasDecorator("Link"));
    }

    @Test
    @DisplayName("Test混合成员")
    void testMixedMembers() {
        ClassDeclaration classDecl = new ClassDeclaration("MyClass");

        classDecl.addMember(new PropertyDeclaration("prop1"));
        classDecl.addMember(new MethodDeclaration("method1"));
        classDecl.addMember(new PropertyDeclaration("prop2"));
        classDecl.addMember(new MethodDeclaration("method2"));
        classDecl.addMember(new PropertyDeclaration("prop3"));

        assertEquals(5, classDecl.getMembers().size());
        assertEquals(3, classDecl.getProperties().size());
        assertEquals(2, classDecl.getMethods().size());
    }

    @Test
    @DisplayName("TestInherit链")
    void testInheritanceChain() {
        ClassDeclaration child = new ClassDeclaration("ChildClass");
        child.setSuperClass("ParentClass");

        ClassDeclaration parent = new ClassDeclaration("ParentClass");
        parent.setSuperClass("BaseClass");

        assertEquals("ParentClass", child.getSuperClass());
        assertEquals("BaseClass", parent.getSuperClass());
    }

    @Test
    @DisplayName("TestExport of结构体Class")
    void testExportedStructClass() {
        ClassDeclaration classDecl = new ClassDeclaration("MyView");

        classDecl.setStruct(true);
        classDecl.setExport(true);
        classDecl.setSuperClass("View");

        assertTrue(classDecl.isStruct());
        assertTrue(classDecl.isExport());
        assertEquals("View", classDecl.getSuperClass());
    }

    @Test
    @DisplayName("TestDecorator大小写敏感")
    void testHasDecoratorCaseSensitive() {
        ClassDeclaration classDecl = new ClassDeclaration("MyComponent");
        classDecl.addDecorator(new Decorator("Component"));

        assertTrue(classDecl.hasDecorator("Component"));
        assertFalse(classDecl.hasDecorator("component"));
        assertFalse(classDecl.hasDecorator("COMPONENT"));
    }
}
