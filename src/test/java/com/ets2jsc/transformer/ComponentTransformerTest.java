package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ComponentTransformer.
 */
@DisplayName("ComponentTransformer Tests")
class ComponentTransformerTest {

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        ComponentTransformer transformer = new ComponentTransformer();

        assertNotNull(transformer);
    }

    @Test
    @DisplayName("Test canTransform returns true for built-in component")
    void testCanTransformReturnsTrueForBuiltinComponent() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Text");

        assertTrue(transformer.canTransform(expr));
    }

    @Test
    @DisplayName("Test canTransform returns true for Column component")
    void testCanTransformReturnsTrueForColumnComponent() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Column");

        assertTrue(transformer.canTransform(expr));
    }

    @Test
    @DisplayName("Test canTransform returns false for custom component")
    void testCanTransformReturnsFalseForCustomComponent() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("MyCustomComponent");

        assertFalse(transformer.canTransform(expr));
    }

    @Test
    @DisplayName("Test canTransform returns false for non-ComponentExpression")
    void testCanTransformReturnsFalseForNonComponentExpression() {
        ComponentTransformer transformer = new ComponentTransformer();
        MethodDeclaration method = new MethodDeclaration("test");

        assertFalse(transformer.canTransform(method));
    }

    @Test
    @DisplayName("Test transform returns same node for non-component expressions")
    void testTransformReturnsSameNodeForNonComponentExpressions() {
        ComponentTransformer transformer = new ComponentTransformer();
        MethodDeclaration method = new MethodDeclaration("test");

        AstNode result = transformer.transform(method);

        assertSame(method, result);
    }

    @Test
    @DisplayName("Test transform for built-in component expression")
    void testTransformForBuiltinComponentExpression() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Text");

        AstNode result = transformer.transform(expr);

        assertNotNull(result);
        assertTrue(result instanceof ComponentExpression);
    }

    @Test
    @DisplayName("Test transform for custom component expression")
    void testTransformForCustomComponentExpression() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("MyCustomComponent");

        AstNode result = transformer.transform(expr);

        assertNotNull(result);
        assertSame(expr, result);
    }

    @Test
    @DisplayName("Test generateCreateStatement for Text component")
    void testGenerateCreateStatementForTextComponent() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Text");

        String result = transformer.generateCreateStatement(expr);

        assertEquals("Text.create();", result);
    }

    @Test
    @DisplayName("Test generatePopStatement for Text component")
    void testGeneratePopStatementForTextComponent() {
        ComponentTransformer transformer = new ComponentTransformer();

        String result = transformer.generatePopStatement("Text");

        assertEquals("Text.pop();", result);
    }

    @Test
    @DisplayName("Test generatePopStatement for Column component")
    void testGeneratePopStatementForColumnComponent() {
        ComponentTransformer transformer = new ComponentTransformer();

        String result = transformer.generatePopStatement("Column");

        assertEquals("Column.pop();", result);
    }

    @Test
    @DisplayName("Test generateAttributeStatements with no chained calls")
    void testGenerateAttributeStatementsWithNoChainedCalls() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Text");

        List<String> result = transformer.generateAttributeStatements(expr);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test generateAttributeStatements with single chained call")
    void testGenerateAttributeStatementsWithSingleChainedCall() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Text");

        ComponentExpression.MethodCall call = new ComponentExpression.MethodCall("fontSize");

        expr.addChainedCall(call);

        List<String> result = transformer.generateAttributeStatements(expr);

        assertEquals(1, result.size());
        assertEquals("Text.fontSize();", result.get(0));
    }

    @Test
    @DisplayName("Test generateChildStatements with no children")
    void testGenerateChildStatementsWithNoChildren() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Column");

        List<String> result = transformer.generateChildStatements(expr);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test generateCreateStatement preserves component name")
    void testGenerateCreateStatementPreservesComponentName() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Button");

        String result = transformer.generateCreateStatement(expr);

        assertTrue(result.startsWith("Button.create("));
    }

    @Test
    @DisplayName("Test generateAttributeStatements with multiple chained calls")
    void testGenerateAttributeStatementsWithMultipleChainedCalls() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Text");

        ComponentExpression.MethodCall call1 = new ComponentExpression.MethodCall("fontSize");
        call1.addArgument(new ExpressionStatement("16"));

        ComponentExpression.MethodCall call2 = new ComponentExpression.MethodCall("fontColor");
        call2.addArgument(new ExpressionStatement("'red'"));

        expr.addChainedCall(call1);
        expr.addChainedCall(call2);

        List<String> result = transformer.generateAttributeStatements(expr);

        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("Text.fontSize("));
        assertTrue(result.get(1).contains("Text.fontColor("));
    }

    @Test
    @DisplayName("Test generateChildStatements with children")
    void testGenerateChildStatementsWithChildren() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Column");

        ComponentExpression child = new ComponentExpression("Text");
        child.addArgument(new ExpressionStatement("'Hello'"));

        expr.addChild(child);

        List<String> result = transformer.generateChildStatements(expr);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Test transform returns modified node for built-in components")
    void testTransformReturnsModifiedNodeForBuiltinComponents() {
        ComponentTransformer transformer = new ComponentTransformer();
        ComponentExpression expr = new ComponentExpression("Row");

        AstNode result = transformer.transform(expr);

        assertNotNull(result);
        assertTrue(result instanceof ComponentExpression);
    }

    @Test
    @DisplayName("Test generatePopStatement for Row component")
    void testGeneratePopStatementForRowComponent() {
        ComponentTransformer transformer = new ComponentTransformer();

        String result = transformer.generatePopStatement("Row");

        assertEquals("Row.pop();", result);
    }

    @Test
    @DisplayName("Test generatePopStatement for Stack component")
    void testGeneratePopStatementForStackComponent() {
        ComponentTransformer transformer = new ComponentTransformer();

        String result = transformer.generatePopStatement("Stack");

        assertEquals("Stack.pop();", result);
    }
}
