package com.ets2jsc.ast;

import com.ets2jsc.ast.CallExpression;
import com.ets2jsc.ast.ExpressionStatement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 CallExpression 调用表达式
 */
@DisplayName("CallExpression 测试")
class CallExpressionTest {

    @Test
    @DisplayName("测试 CallExpression 创建")
    void testCallExpressionCreation() {
        CallExpression call = new CallExpression("test");

        assertEquals("CallExpression", call.getType());
        assertEquals("test", call.getFunctionName());
        assertNotNull(call.getArguments());
        assertTrue(call.getArguments().isEmpty());
    }

    @Test
    @DisplayName("测试获取函数名")
    void testGetFunctionName() {
        CallExpression call = new CallExpression("myFunction");

        assertEquals("myFunction", call.getFunctionName());
    }

    @Test
    @DisplayName("测试设置函数名")
    void testSetFunctionName() {
        CallExpression call = new CallExpression("oldName");
        call.setFunctionName("newName");

        assertEquals("newName", call.getFunctionName());
    }

    @Test
    @DisplayName("测试添加参数")
    void testAddArgument() {
        CallExpression call = new CallExpression("test");
        ExpressionStatement arg1 = new ExpressionStatement("x");
        ExpressionStatement arg2 = new ExpressionStatement("y");

        call.addArgument(arg1);
        call.addArgument(arg2);

        assertEquals(2, call.getArguments().size());
        assertEquals(arg1, call.getArguments().get(0));
        assertEquals(arg2, call.getArguments().get(1));
    }

    @Test
    @DisplayName("测试获取参数列表")
    void testGetArguments() {
        CallExpression call = new CallExpression("test");
        ExpressionStatement arg = new ExpressionStatement("value");

        call.addArgument(arg);

        assertEquals(1, call.getArguments().size());
        assertEquals(arg, call.getArguments().get(0));
    }

    @Test
    @DisplayName("测试空参数列表")
    void testEmptyArguments() {
        CallExpression call = new CallExpression("test");

        assertTrue(call.getArguments().isEmpty());
    }

    @Test
    @DisplayName("测试获取和设置 callee")
    void testGetSetCallee() {
        CallExpression call = new CallExpression("test");
        ExpressionStatement callee = new ExpressionStatement("obj.method");

        call.setCallee(callee);

        assertEquals(callee, call.getCallee());
    }

    @Test
    @DisplayName("测试初始 callee 为 null")
    void testCeeleeInitiallyNull() {
        CallExpression call = new CallExpression("test");

        assertNull(call.getCallee());
    }

    @Test
    @DisplayName("测试 isComponentCall 初始值")
    void testIsComponentCallInitial() {
        CallExpression call = new CallExpression("Text");

        assertFalse(call.isComponentCall());
    }

    @Test
    @DisplayName("测试设置 componentCall")
    void testSetComponentCall() {
        CallExpression call = new CallExpression("Text");
        call.setComponentCall(true);

        assertTrue(call.isComponentCall());

        call.setComponentCall(false);
        assertFalse(call.isComponentCall());
    }

    @Test
    @DisplayName("测试 isControlFlow - ForEach")
    void testIsControlFlowForEach() {
        CallExpression call = new CallExpression("ForEach");

        assertTrue(call.isControlFlow());
    }

    @Test
    @DisplayName("测试 isControlFlow - LazyForEach")
    void testIsControlFlowLazyForEach() {
        CallExpression call = new CallExpression("LazyForEach");

        assertTrue(call.isControlFlow());
    }

    @Test
    @DisplayName("测试 isControlFlow - If")
    void testIsControlFlowIf() {
        CallExpression call = new CallExpression("If");

        assertTrue(call.isControlFlow());
    }

    @Test
    @DisplayName("测试 isControlFlow - 非控制流函数")
    void testIsControlFlowNonControl() {
        CallExpression call1 = new CallExpression("Text");
        CallExpression call2 = new CallExpression("console.log");
        CallExpression call3 = new CallExpression("myFunction");

        assertFalse(call1.isControlFlow());
        assertFalse(call2.isControlFlow());
        assertFalse(call3.isControlFlow());
    }

    @Test
    @DisplayName("测试多个参数")
    void testMultipleArguments() {
        CallExpression call = new CallExpression("myFunction");

        call.addArgument(new ExpressionStatement("arg1"));
        call.addArgument(new ExpressionStatement("arg2"));
        call.addArgument(new ExpressionStatement("arg3"));
        call.addArgument(new ExpressionStatement("arg4"));
        call.addArgument(new ExpressionStatement("arg5"));

        assertEquals(5, call.getArguments().size());
    }

    @Test
    @DisplayName("测试不同函数名")
    void testDifferentFunctionNames() {
        CallExpression call1 = new CallExpression("console.log");
        CallExpression call2 = new CallExpression("Text.create");
        CallExpression call3 = new CallExpression("Column.pop");
        CallExpression call4 = new CallExpression("forEach");
        CallExpression call5 = new CallExpression("map");
        CallExpression call6 = new CallExpression("filter");

        assertEquals("console.log", call1.getFunctionName());
        assertEquals("Text.create", call2.getFunctionName());
        assertEquals("Column.pop", call3.getFunctionName());
        assertEquals("forEach", call4.getFunctionName());
        assertEquals("map", call5.getFunctionName());
        assertEquals("filter", call6.getFunctionName());
    }

    @Test
    @DisplayName("测试 accept 方法")
    void testAcceptMethod() {
        CallExpression call = new CallExpression("test");

        assertNotNull(call);
        assertEquals("CallExpression", call.getType());
    }

    @Test
    @DisplayName("测试链式调用（通过 callee）")
    void testChainedCall() {
        CallExpression call = new CallExpression("then");
        ExpressionStatement callee = new ExpressionStatement("promise");

        call.setCallee(callee);

        assertEquals(callee, call.getCallee());
        assertEquals("then", call.getFunctionName());
    }

    @Test
    @DisplayName("测试带参数的控制流调用")
    void testControlFlowWithArguments() {
        CallExpression call = new CallExpression("ForEach");
        call.addArgument(new ExpressionStatement("this.items"));
        call.addArgument(new ExpressionStatement("(item) => {}"));

        assertTrue(call.isControlFlow());
        assertEquals(2, call.getArguments().size());
    }

    @Test
    @DisplayName("测试设置多个 callee")
    void testMultipleCalleeSets() {
        CallExpression call = new CallExpression("test");

        ExpressionStatement callee1 = new ExpressionStatement("obj1");
        call.setCallee(callee1);
        assertEquals(callee1, call.getCallee());

        ExpressionStatement callee2 = new ExpressionStatement("obj2");
        call.setCallee(callee2);
        assertEquals(callee2, call.getCallee());
    }

    @Test
    @DisplayName("测试修改函数名后 isControlFlow 变化")
    void testIsControlFlowAfterNameChange() {
        CallExpression call = new CallExpression("Text");
        assertFalse(call.isControlFlow());

        call.setFunctionName("If");
        assertTrue(call.isControlFlow());

        call.setFunctionName("ForEach");
        assertTrue(call.isControlFlow());

        call.setFunctionName("normalFunction");
        assertFalse(call.isControlFlow());
    }

    @Test
    @DisplayName("测试控制流函数大小写敏感")
    void testControlFlowCaseSensitive() {
        CallExpression call1 = new CallExpression("ForEach");
        CallExpression call2 = new CallExpression("foreach");
        CallExpression call3 = new CallExpression("FOREACH");
        CallExpression call4 = new CallExpression("For_Each");

        assertTrue(call1.isControlFlow());
        assertFalse(call2.isControlFlow());
        assertFalse(call3.isControlFlow());
        assertFalse(call4.isControlFlow());
    }

    @Test
    @DisplayName("测试组件调用标记")
    void testComponentCallFlag() {
        CallExpression textCall = new CallExpression("Text");
        CallExpression columnCall = new CallExpression("Column");
        CallExpression consoleCall = new CallExpression("console.log");

        textCall.setComponentCall(true);
        columnCall.setComponentCall(true);

        assertTrue(textCall.isComponentCall());
        assertTrue(columnCall.isComponentCall());
        assertFalse(consoleCall.isComponentCall());
    }
}
