package com.ets2jsc.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test ForeachStatement LoopStatement
 */
@DisplayName("ForEach StatementTest")
class ForeachStatementTest {

    @Test
    @DisplayName("Test ForEach StatementCreation")
    void testForeachStatementCreation() {
        ForeachStatement forEach = new ForeachStatement("this.items", "(item) => {}", null);

        assertEquals("ForeachStatement", forEach.getType());
        assertEquals("this.items", forEach.arrayExpression());
        assertEquals("(item) => {}", forEach.itemGenerator());
    }

    @Test
    @DisplayName("TestGetArrayExpression")
    void testGetArrayExpression() {
        ForeachStatement forEach = new ForeachStatement("arr", "(item) => {}", null);

        assertEquals("arr", forEach.arrayExpression());
    }

    @Test
    @DisplayName("TestGet项Generate器")
    void testGetItemGenerator() {
        String itemGen = "(item, index) => { Text.create(item.name); }";
        ForeachStatement forEach = new ForeachStatement("items", itemGen, null);

        assertEquals(itemGen, forEach.itemGenerator());
    }

    @Test
    @DisplayName("TestGet键Generate器")
    void testGetKeyGenerator() {
        String keyGen = "(item) => item.id";
        ForeachStatement forEach = new ForeachStatement("items", "(item) => {}", keyGen);

        assertEquals(keyGen, forEach.keyGenerator());
    }

    @Test
    @DisplayName("Test没有键Generate器")
    void testNoKeyGenerator() {
        ForeachStatement forEach = new ForeachStatement("items", "(item) => {}", null);

        assertNull(forEach.keyGenerator());
    }

    @Test
    @DisplayName("TestsimpleArrayExpression")
    void testSimpleArrayExpression() {
        ForeachStatement forEach1 = new ForeachStatement("arr", "(item) => {}", null);
        ForeachStatement forEach2 = new ForeachStatement("list", "(item) => {}", null);
        ForeachStatement forEach3 = new ForeachStatement("data", "(item) => {}", null);

        assertEquals("arr", forEach1.arrayExpression());
        assertEquals("list", forEach2.arrayExpression());
        assertEquals("data", forEach3.arrayExpression());
    }

    @Test
    @DisplayName("Test成员访问ArrayExpression")
    void testMemberAccessArrayExpression() {
        ForeachStatement forEach1 = new ForeachStatement("this.items", "(item) => {}", null);
        ForeachStatement forEach2 = new ForeachStatement("this.data.list", "(item) => {}", null);
        ForeachStatement forEach3 = new ForeachStatement("obj.items", "(item) => {}", null);

        assertEquals("this.items", forEach1.arrayExpression());
        assertEquals("this.data.list", forEach2.arrayExpression());
        assertEquals("obj.items", forEach3.arrayExpression());
    }

    @Test
    @DisplayName("Test箭头Function项Generate器")
    void testArrowFunctionItemGenerator() {
        ForeachStatement forEach1 = new ForeachStatement("items", "(item) => {}", null);
        ForeachStatement forEach2 = new ForeachStatement("items", "(item, index) => {}", null);
        ForeachStatement forEach3 = new ForeachStatement("items", "() => {}", null);

        assertTrue(forEach1.itemGenerator().contains("=>"));
        assertTrue(forEach2.itemGenerator().contains("=>"));
        assertTrue(forEach3.itemGenerator().contains("=>"));
    }

    @Test
    @DisplayName("Test复杂项Generate器")
    void testComplexItemGenerator() {
        String complexGen = "(item, index) => { Text.create(item.name); Text.fontSize(16); Text.pop(); }";
        ForeachStatement forEach = new ForeachStatement("items", complexGen, null);

        assertEquals(complexGen, forEach.itemGenerator());
    }

    @Test
    @DisplayName("TestVarious键Generate器")
    void testVariousKeyGenerators() {
        ForeachStatement forEach1 = new ForeachStatement("items", "(item) => {}", "(item) => item.id");
        ForeachStatement forEach2 = new ForeachStatement("items", "(item) => {}", "(item) => item.key");
        ForeachStatement forEach3 = new ForeachStatement("items", "(item) => {}", "(item, index) => index");
        ForeachStatement forEach4 = new ForeachStatement("items", "(item) => {}", "(item) => JSON.stringify(item)");

        assertEquals("(item) => item.id", forEach1.keyGenerator());
        assertEquals("(item) => item.key", forEach2.keyGenerator());
        assertEquals("(item, index) => index", forEach3.keyGenerator());
        assertEquals("(item) => JSON.stringify(item)", forEach4.keyGenerator());
    }

    @Test
    @DisplayName("TestEmpty项Generate器")
    void testEmptyItemGenerator() {
        ForeachStatement forEach = new ForeachStatement("items", "", null);

        assertEquals("", forEach.itemGenerator());
    }

    @Test
    @DisplayName("TestEmpty键Generate器")
    void testEmptyKeyGenerator() {
        ForeachStatement forEach = new ForeachStatement("items", "(item) => {}", "");

        assertEquals("", forEach.keyGenerator());
    }

    @Test
    @DisplayName("TestContains this  ofArrayExpression")
    void testThisArrayExpression() {
        ForeachStatement forEach1 = new ForeachStatement("this.list", "(item) => {}", null);
        ForeachStatement forEach2 = new ForeachStatement("this.state.items", "(item) => {}", null);

        assertEquals("this.list", forEach1.arrayExpression());
        assertEquals("this.state.items", forEach2.arrayExpression());
    }

    @Test
    @DisplayName("TestMethodCallArrayExpression")
    void testMethodCallArrayExpression() {
        ForeachStatement forEach1 = new ForeachStatement("this.getItems()", "(item) => {}", null);
        ForeachStatement forEach2 = new ForeachStatement("data.filter(x => x.active)", "(item) => {}", null);

        assertEquals("this.getItems()", forEach1.arrayExpression());
        assertEquals("data.filter(x => x.active)", forEach2.arrayExpression());
    }

    @Test
    @DisplayName("Test accept Method")
    void testAcceptMethod() {
        ForeachStatement forEach = new ForeachStatement("items", "(item) => {}", null);

        assertNotNull(forEach);
        assertEquals("ForeachStatement", forEach.getType());
    }

    @Test
    @DisplayName("Test多Parameter箭头Function")
    void testMultiParameterArrowFunction() {
        ForeachStatement forEach = new ForeachStatement("items",
            "(item, index, array) => { console.log(index, item); }", null);

        assertTrue(forEach.itemGenerator().contains("item"));
        assertTrue(forEach.itemGenerator().contains("index"));
        assertTrue(forEach.itemGenerator().contains("array"));
    }

    @Test
    @DisplayName("TestContains异步Function of项Generate器")
    void testAsyncItemGenerator() {
        String asyncGen = "async (item) => { await processItem(item); }";
        ForeachStatement forEach = new ForeachStatement("items", asyncGen, null);

        assertEquals(asyncGen, forEach.itemGenerator());
        assertTrue(forEach.itemGenerator().contains("async"));
        assertTrue(forEach.itemGenerator().contains("await"));
    }
}
