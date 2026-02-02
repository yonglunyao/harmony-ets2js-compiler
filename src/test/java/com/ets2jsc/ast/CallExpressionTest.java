package com.ets2jsc.ast;

import com.ets2jsc.domain.model.ast.CallExpression;
import com.ets2jsc.domain.model.ast.ExpressionStatement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CallExpression
 */
@DisplayName("Call Expression Tests")
class CallExpressionTest {

    @Test
    @DisplayName("Test call expression creation")
    void testCallExpressionCreation() {
        CallExpression call = new CallExpression("test");

        assertEquals("CallExpression", call.getType());
        assertEquals("test", call.getFunctionName());
        assertNotNull(call.getArguments());
        assertTrue(call.getArguments().isEmpty());
    }

    @Test
    @DisplayName("TestGetFunctionName")
    void testGetFunctionName() {
        CallExpression call = new CallExpression("myFunction");

        assertEquals("myFunction", call.getFunctionName());
    }

    @Test
    @DisplayName("TestSetFunctionName")
    void testSetFunctionName() {
        CallExpression call = new CallExpression("oldName");
        call.setFunctionName("newName");

        assertEquals("newName", call.getFunctionName());
    }

    @Test
    @DisplayName("TestAddParameter")
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
    @DisplayName("TestGetParameterList")
    void testGetArguments() {
        CallExpression call = new CallExpression("test");
        ExpressionStatement arg = new ExpressionStatement("value");

        call.addArgument(arg);

        assertEquals(1, call.getArguments().size());
        assertEquals(arg, call.getArguments().get(0));
    }

    @Test
    @DisplayName("TestEmptyParameterList")
    void testEmptyArguments() {
        CallExpression call = new CallExpression("test");

        assertTrue(call.getArguments().isEmpty());
    }

    @Test
    @DisplayName("TestGetAndSetCallee")
    void testGetSetCallee() {
        CallExpression call = new CallExpression("test");
        ExpressionStatement callee = new ExpressionStatement("obj.method");

        call.setCallee(callee);

        assertEquals(callee, call.getCallee());
    }

    @Test
    @DisplayName("TestInitialCalleeIsNull")
    void testCeeleeInitiallyNull() {
        CallExpression call = new CallExpression("test");

        assertNull(call.getCallee());
    }

    @Test
    @DisplayName("TestIsComponentCallInitialValue")
    void testIsComponentCallInitial() {
        CallExpression call = new CallExpression("Text");

        assertFalse(call.isComponentCall());
    }

    @Test
    @DisplayName("TestSet componentCall")
    void testSetComponentCall() {
        CallExpression call = new CallExpression("Text");
        call.setComponentCall(true);

        assertTrue(call.isComponentCall());

        call.setComponentCall(false);
        assertFalse(call.isComponentCall());
    }

    @Test
    @DisplayName("Test isControlFlow - ForEach")
    void testIsControlFlowForEach() {
        CallExpression call = new CallExpression("ForEach");

        assertTrue(call.isControlFlow());
    }

    @Test
    @DisplayName("Test isControlFlow - LazyForEach")
    void testIsControlFlowLazyForEach() {
        CallExpression call = new CallExpression("LazyForEach");

        assertTrue(call.isControlFlow());
    }

    @Test
    @DisplayName("Test isControlFlow - If")
    void testIsControlFlowIf() {
        CallExpression call = new CallExpression("If");

        assertTrue(call.isControlFlow());
    }

    @Test
    @DisplayName("TestIsControlFlowNonControlFlowFunction")
    void testIsControlFlowNonControl() {
        CallExpression call1 = new CallExpression("Text");
        CallExpression call2 = new CallExpression("console.log");
        CallExpression call3 = new CallExpression("myFunction");

        assertFalse(call1.isControlFlow());
        assertFalse(call2.isControlFlow());
        assertFalse(call3.isControlFlow());
    }

    @Test
    @DisplayName("TestMultipleParameter")
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
    @DisplayName("TestDifferentFunctionNames")
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
    @DisplayName("Test accept Method")
    void testAcceptMethod() {
        CallExpression call = new CallExpression("test");

        assertNotNull(call);
        assertEquals("CallExpression", call.getType());
    }

    @Test
    @DisplayName("TestChainedCallViaCallee")
    void testChainedCall() {
        CallExpression call = new CallExpression("then");
        ExpressionStatement callee = new ExpressionStatement("promise");

        call.setCallee(callee);

        assertEquals(callee, call.getCallee());
        assertEquals("then", call.getFunctionName());
    }

    @Test
    @DisplayName("TestControlFlowCallWithParameters")
    void testControlFlowWithArguments() {
        CallExpression call = new CallExpression("ForEach");
        call.addArgument(new ExpressionStatement("this.items"));
        call.addArgument(new ExpressionStatement("(item) => {}"));

        assertTrue(call.isControlFlow());
        assertEquals(2, call.getArguments().size());
    }

    @Test
    @DisplayName("TestSetMultiple callee")
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
    @DisplayName("TestIsControlFlowChangesAfterModifyFunctionName")
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
    @DisplayName("TestControlFlowFunctionCaseSensitive")
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
    @DisplayName("TestComponentCallMarking")
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
