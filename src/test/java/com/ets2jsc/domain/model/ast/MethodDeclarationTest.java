package com.ets2jsc.domain.model.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test MethodDeclaration MethodDeclaration
 */
@DisplayName("Method Declaration Tests")
class MethodDeclarationTest {

    @Test
    @DisplayName("TestMethodDeclarationCreation")
    void testMethodDeclarationCreation() {
        MethodDeclaration method = new MethodDeclaration("myMethod");

        assertEquals("MethodDeclaration", method.getType());
        assertEquals("myMethod", method.getName());
        assertNotNull(method.getParameters());
        assertNotNull(method.getDecorators());
        assertFalse(method.isAsync());
        assertFalse(method.isStatic());
    }

    @Test
    @DisplayName("TestGetMethodName")
    void testGetName() {
        MethodDeclaration method = new MethodDeclaration("testMethod");

        assertEquals("testMethod", method.getName());
    }

    @Test
    @DisplayName("TestSetMethodName")
    void testSetName() {
        MethodDeclaration method = new MethodDeclaration("oldName");
        method.setName("newName");

        assertEquals("newName", method.getName());
    }

    @Test
    @DisplayName("TestGetReturnType")
    void testGetReturnType() {
        MethodDeclaration method = new MethodDeclaration("test");
        method.setReturnType("void");

        assertEquals("void", method.getReturnType());
    }

    @Test
    @DisplayName("TestSetReturnType")
    void testSetReturnType() {
        MethodDeclaration method = new MethodDeclaration("getValue");
        method.setReturnType("number");

        assertEquals("number", method.getReturnType());
    }

    @Test
    @DisplayName("TestAddParameter")
    void testAddParameter() {
        MethodDeclaration method = new MethodDeclaration("test");
        MethodDeclaration.Parameter param1 = new MethodDeclaration.Parameter("x");
        MethodDeclaration.Parameter param2 = new MethodDeclaration.Parameter("y");

        method.addParameter(param1);
        method.addParameter(param2);

        assertEquals(2, method.getParameters().size());
    }

    @Test
    @DisplayName("TestGetParameterList")
    void testGetParameters() {
        MethodDeclaration method = new MethodDeclaration("test");
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value");

        method.addParameter(param);

        assertEquals(1, method.getParameters().size());
        assertEquals(param, method.getParameters().get(0));
    }

    @Test
    @DisplayName("TestAddDecorator")
    void testAddDecorator() {
        MethodDeclaration method = new MethodDeclaration("build");
        Decorator decorator = new Decorator("Builder");

        method.addDecorator(decorator);

        assertEquals(1, method.getDecorators().size());
    }

    @Test
    @DisplayName("TestGetDecoratorList")
    void testGetDecorators() {
        MethodDeclaration method = new MethodDeclaration("test");
        Decorator decorator = new Decorator("Watch");

        method.addDecorator(decorator);

        assertEquals(1, method.getDecorators().size());
        assertEquals(decorator, method.getDecorators().get(0));
    }

    @Test
    @DisplayName("TestGetAndSetMethodBody")
    void testGetSetBody() {
        MethodDeclaration method = new MethodDeclaration("test");
        Block body = new Block();
        body.addStatement(new ExpressionStatement("return true;"));

        method.setBody(body);

        assertEquals(body, method.getBody());
    }

    @Test
    @DisplayName("TestAsyncMark")
    void testAsyncFlag() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertFalse(method.isAsync());

        method.setAsync(true);
        assertTrue(method.isAsync());

        method.setAsync(false);
        assertFalse(method.isAsync());
    }

    @Test
    @DisplayName("TestStaticMark")
    void testStaticFlag() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertFalse(method.isStatic());

        method.setStatic(true);
        assertTrue(method.isStatic());

        method.setStatic(false);
        assertFalse(method.isStatic());
    }

    @Test
    @DisplayName("Test isBuildMethod")
    void testIsBuildMethod() {
        MethodDeclaration buildMethod = new MethodDeclaration("build");
        MethodDeclaration otherMethod = new MethodDeclaration("other");

        assertTrue(buildMethod.isBuildMethod());
        assertFalse(otherMethod.isBuildMethod());
    }

    @Test
    @DisplayName("Test isBuilderMethod")
    void testIsBuilderMethod() {
        MethodDeclaration method1 = new MethodDeclaration("build");
        method1.addDecorator(new Decorator("Builder"));

        MethodDeclaration method2 = new MethodDeclaration("build");

        MethodDeclaration method3 = new MethodDeclaration("other");
        method3.addDecorator(new Decorator("Builder"));

        assertTrue(method1.isBuilderMethod());
        assertFalse(method2.isBuilderMethod());
        assertTrue(method3.isBuilderMethod());
    }

    @Test
    @DisplayName("Test Parameter - Creation")
    void testParameterCreation() {
        MethodDeclaration.Parameter param1 = new MethodDeclaration.Parameter("x");
        MethodDeclaration.Parameter param2 = new MethodDeclaration.Parameter("y", "number");

        assertEquals("x", param1.getName());
        assertEquals("y", param2.getName());
        assertEquals("number", param2.getType());
    }

    @Test
    @DisplayName("Test Parameter - getName")
    void testParameterGetName() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("myParam");

        assertEquals("myParam", param.getName());
    }

    @Test
    @DisplayName("Test Parameter - setName")
    void testParameterSetName() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("oldName");
        param.setName("newName");

        assertEquals("newName", param.getName());
    }

    @Test
    @DisplayName("Test Parameter - getType")
    void testParameterGetType() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value", "string");

        assertEquals("string", param.getType());
    }

    @Test
    @DisplayName("Test Parameter - setType")
    void testParameterSetType() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value");
        param.setType("number");

        assertEquals("number", param.getType());
    }

    @Test
    @DisplayName("Test Parameter - hasDefault")
    void testParameterHasDefault() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value");

        assertFalse(param.isHasDefault());

        param.setHasDefault(true);
        assertTrue(param.isHasDefault());
    }

    @Test
    @DisplayName("Test Parameter - getDefaultValue")
    void testParameterGetDefaultValue() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value");
        param.setDefaultValue("0");

        assertEquals("0", param.getDefaultValue());
    }

    @Test
    @DisplayName("Test Parameter - setDefaultValue")
    void testParameterSetDefaultValue() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value");
        param.setDefaultValue("default");
        param.setHasDefault(true);

        assertEquals("default", param.getDefaultValue());
        assertTrue(param.isHasDefault());
    }

    @Test
    @DisplayName("TestMultipleParameter")
    void testMultipleParameters() {
        MethodDeclaration method = new MethodDeclaration("test");

        method.addParameter(new MethodDeclaration.Parameter("x", "number"));
        method.addParameter(new MethodDeclaration.Parameter("y", "number"));
        method.addParameter(new MethodDeclaration.Parameter("z", "number"));

        assertEquals(3, method.getParameters().size());
    }

    @Test
    @DisplayName("TestMultipleDecorator")
    void testMultipleDecorators() {
        MethodDeclaration method = new MethodDeclaration("test");

        method.addDecorator(new Decorator("Builder"));
        method.addDecorator(new Decorator("Watch"));
        method.addDecorator(new Decorator("Styles"));

        assertEquals(3, method.getDecorators().size());
    }

    @Test
    @DisplayName("TestParameterWithType")
    void testParameterWithTypes() {
        MethodDeclaration.Parameter stringParam = new MethodDeclaration.Parameter("str", "string");
        MethodDeclaration.Parameter numberParam = new MethodDeclaration.Parameter("num", "number");
        MethodDeclaration.Parameter boolParam = new MethodDeclaration.Parameter("flag", "boolean");
        MethodDeclaration.Parameter anyParam = new MethodDeclaration.Parameter("value", "any");

        assertEquals("string", stringParam.getType());
        assertEquals("number", numberParam.getType());
        assertEquals("boolean", boolParam.getType());
        assertEquals("any", anyParam.getType());
    }

    @Test
    @DisplayName("TestParameterWithDefaultValue")
    void testParameterWithDefault() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("count");
        param.setDefaultValue("10");
        param.setHasDefault(true);

        assertEquals("10", param.getDefaultValue());
        assertTrue(param.isHasDefault());
    }

    @Test
    @DisplayName("Test accept Method")
    void testAcceptMethod() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertNotNull(method);
        assertEquals("MethodDeclaration", method.getType());
    }

    @Test
    @DisplayName("TestAsyncStaticMethod")
    void testAsyncStaticMethod() {
        MethodDeclaration method = new MethodDeclaration("helper");
        method.setAsync(true);
        method.setStatic(true);

        assertTrue(method.isAsync());
        assertTrue(method.isStatic());
    }

    @Test
    @DisplayName("TestMethodBodyInitiallyIsNull")
    void testBodyInitiallyNull() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertNull(method.getBody());
    }

    @Test
    @DisplayName("TestReturnTypeInitiallyIsNull")
    void testReturnTypeInitiallyNull() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertNull(method.getReturnType());
    }

    @Test
    @DisplayName("TestEmptyParameterAndDecorator")
    void testEmptyParametersAndDecorators() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertTrue(method.getParameters().isEmpty());
        assertTrue(method.getDecorators().isEmpty());
    }
}
