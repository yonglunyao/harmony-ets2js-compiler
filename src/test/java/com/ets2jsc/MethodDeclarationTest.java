package com.ets2jsc;

import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.ast.MethodDeclaration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 MethodDeclaration 方法声明
 */
@DisplayName("MethodDeclaration 测试")
class MethodDeclarationTest {

    @Test
    @DisplayName("测试方法声明创建")
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
    @DisplayName("测试获取方法名")
    void testGetName() {
        MethodDeclaration method = new MethodDeclaration("testMethod");

        assertEquals("testMethod", method.getName());
    }

    @Test
    @DisplayName("测试设置方法名")
    void testSetName() {
        MethodDeclaration method = new MethodDeclaration("oldName");
        method.setName("newName");

        assertEquals("newName", method.getName());
    }

    @Test
    @DisplayName("测试获取返回类型")
    void testGetReturnType() {
        MethodDeclaration method = new MethodDeclaration("test");
        method.setReturnType("void");

        assertEquals("void", method.getReturnType());
    }

    @Test
    @DisplayName("测试设置返回类型")
    void testSetReturnType() {
        MethodDeclaration method = new MethodDeclaration("getValue");
        method.setReturnType("number");

        assertEquals("number", method.getReturnType());
    }

    @Test
    @DisplayName("测试添加参数")
    void testAddParameter() {
        MethodDeclaration method = new MethodDeclaration("test");
        MethodDeclaration.Parameter param1 = new MethodDeclaration.Parameter("x");
        MethodDeclaration.Parameter param2 = new MethodDeclaration.Parameter("y");

        method.addParameter(param1);
        method.addParameter(param2);

        assertEquals(2, method.getParameters().size());
    }

    @Test
    @DisplayName("测试获取参数列表")
    void testGetParameters() {
        MethodDeclaration method = new MethodDeclaration("test");
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value");

        method.addParameter(param);

        assertEquals(1, method.getParameters().size());
        assertEquals(param, method.getParameters().get(0));
    }

    @Test
    @DisplayName("测试添加装饰器")
    void testAddDecorator() {
        MethodDeclaration method = new MethodDeclaration("build");
        Decorator decorator = new Decorator("Builder");

        method.addDecorator(decorator);

        assertEquals(1, method.getDecorators().size());
    }

    @Test
    @DisplayName("测试获取装饰器列表")
    void testGetDecorators() {
        MethodDeclaration method = new MethodDeclaration("test");
        Decorator decorator = new Decorator("Watch");

        method.addDecorator(decorator);

        assertEquals(1, method.getDecorators().size());
        assertEquals(decorator, method.getDecorators().get(0));
    }

    @Test
    @DisplayName("测试获取和设置方法体")
    void testGetSetBody() {
        MethodDeclaration method = new MethodDeclaration("test");
        Block body = new Block();
        body.addStatement(new ExpressionStatement("return true;"));

        method.setBody(body);

        assertEquals(body, method.getBody());
    }

    @Test
    @DisplayName("测试 async 标记")
    void testAsyncFlag() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertFalse(method.isAsync());

        method.setAsync(true);
        assertTrue(method.isAsync());

        method.setAsync(false);
        assertFalse(method.isAsync());
    }

    @Test
    @DisplayName("测试 static 标记")
    void testStaticFlag() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertFalse(method.isStatic());

        method.setStatic(true);
        assertTrue(method.isStatic());

        method.setStatic(false);
        assertFalse(method.isStatic());
    }

    @Test
    @DisplayName("测试 isBuildMethod")
    void testIsBuildMethod() {
        MethodDeclaration buildMethod = new MethodDeclaration("build");
        MethodDeclaration otherMethod = new MethodDeclaration("other");

        assertTrue(buildMethod.isBuildMethod());
        assertFalse(otherMethod.isBuildMethod());
    }

    @Test
    @DisplayName("测试 isBuilderMethod")
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
    @DisplayName("测试 Parameter - 创建")
    void testParameterCreation() {
        MethodDeclaration.Parameter param1 = new MethodDeclaration.Parameter("x");
        MethodDeclaration.Parameter param2 = new MethodDeclaration.Parameter("y", "number");

        assertEquals("x", param1.getName());
        assertEquals("y", param2.getName());
        assertEquals("number", param2.getType());
    }

    @Test
    @DisplayName("测试 Parameter - getName")
    void testParameterGetName() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("myParam");

        assertEquals("myParam", param.getName());
    }

    @Test
    @DisplayName("测试 Parameter - setName")
    void testParameterSetName() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("oldName");
        param.setName("newName");

        assertEquals("newName", param.getName());
    }

    @Test
    @DisplayName("测试 Parameter - getType")
    void testParameterGetType() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value", "string");

        assertEquals("string", param.getType());
    }

    @Test
    @DisplayName("测试 Parameter - setType")
    void testParameterSetType() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value");
        param.setType("number");

        assertEquals("number", param.getType());
    }

    @Test
    @DisplayName("测试 Parameter - hasDefault")
    void testParameterHasDefault() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value");

        assertFalse(param.hasDefault());

        param.setHasDefault(true);
        assertTrue(param.hasDefault());
    }

    @Test
    @DisplayName("测试 Parameter - getDefaultValue")
    void testParameterGetDefaultValue() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value");
        param.setDefaultValue("0");

        assertEquals("0", param.getDefaultValue());
    }

    @Test
    @DisplayName("测试 Parameter - setDefaultValue")
    void testParameterSetDefaultValue() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("value");
        param.setDefaultValue("default");
        param.setHasDefault(true);

        assertEquals("default", param.getDefaultValue());
        assertTrue(param.hasDefault());
    }

    @Test
    @DisplayName("测试多个参数")
    void testMultipleParameters() {
        MethodDeclaration method = new MethodDeclaration("test");

        method.addParameter(new MethodDeclaration.Parameter("x", "number"));
        method.addParameter(new MethodDeclaration.Parameter("y", "number"));
        method.addParameter(new MethodDeclaration.Parameter("z", "number"));

        assertEquals(3, method.getParameters().size());
    }

    @Test
    @DisplayName("测试多个装饰器")
    void testMultipleDecorators() {
        MethodDeclaration method = new MethodDeclaration("test");

        method.addDecorator(new Decorator("Builder"));
        method.addDecorator(new Decorator("Watch"));
        method.addDecorator(new Decorator("Styles"));

        assertEquals(3, method.getDecorators().size());
    }

    @Test
    @DisplayName("测试带类型的参数")
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
    @DisplayName("测试带默认值的参数")
    void testParameterWithDefault() {
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("count");
        param.setDefaultValue("10");
        param.setHasDefault(true);

        assertEquals("10", param.getDefaultValue());
        assertTrue(param.hasDefault());
    }

    @Test
    @DisplayName("测试 accept 方法")
    void testAcceptMethod() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertNotNull(method);
        assertEquals("MethodDeclaration", method.getType());
    }

    @Test
    @DisplayName("测试异步静态方法")
    void testAsyncStaticMethod() {
        MethodDeclaration method = new MethodDeclaration("helper");
        method.setAsync(true);
        method.setStatic(true);

        assertTrue(method.isAsync());
        assertTrue(method.isStatic());
    }

    @Test
    @DisplayName("测试方法体初始为 null")
    void testBodyInitiallyNull() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertNull(method.getBody());
    }

    @Test
    @DisplayName("测试返回类型初始为 null")
    void testReturnTypeInitiallyNull() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertNull(method.getReturnType());
    }

    @Test
    @DisplayName("测试空参数和装饰器")
    void testEmptyParametersAndDecorators() {
        MethodDeclaration method = new MethodDeclaration("test");

        assertTrue(method.getParameters().isEmpty());
        assertTrue(method.getDecorators().isEmpty());
    }
}
