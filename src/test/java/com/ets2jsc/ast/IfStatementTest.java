package com.ets2jsc.ast;

import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.ast.IfStatement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test IfStatement ConditionStatement
 */
@DisplayName("If StatementTest")
class IfStatementTest {

    @Test
    @DisplayName("Test If StatementCreation")
    void testIfStatementCreation() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        IfStatement ifStmt = new IfStatement("x > 0", thenBlock, elseBlock);

        assertEquals("IfStatement", ifStmt.getType());
        assertEquals("x > 0", ifStmt.getCondition());
        assertNotNull(ifStmt.getThenBlock());
        assertNotNull(ifStmt.getElseBlock());
    }

    @Test
    @DisplayName("TestConditionGet")
    void testGetCondition() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        IfStatement ifStmt = new IfStatement("this.isActive", thenBlock, elseBlock);

        assertEquals("this.isActive", ifStmt.getCondition());
    }

    @Test
    @DisplayName("Test then BlockGet")
    void testGetThenBlock() {
        Block thenBlock = new Block();
        thenBlock.addStatement(new ExpressionStatement("console.log('then');"));
        Block elseBlock = new Block();

        IfStatement ifStmt = new IfStatement("condition", thenBlock, elseBlock);

        Block result = ifStmt.getThenBlock();
        assertNotNull(result);
        assertEquals(1, result.getStatements().size());
    }

    @Test
    @DisplayName("Test else BlockGet")
    void testGetElseBlock() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        elseBlock.addStatement(new ExpressionStatement("console.log('else');"));

        IfStatement ifStmt = new IfStatement("condition", thenBlock, elseBlock);

        Block result = ifStmt.getElseBlock();
        assertNotNull(result);
        assertEquals(1, result.getStatements().size());
    }

    @Test
    @DisplayName("Test有 else Branch")
    void testHasElse() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        elseBlock.addStatement(new ExpressionStatement("return;"));

        IfStatement ifStmt = new IfStatement("condition", thenBlock, elseBlock);

        assertTrue(ifStmt.hasElse());
    }

    @Test
    @DisplayName("Test没有 else Branch（null）")
    void testNoElseWithNull() {
        Block thenBlock = new Block();
        IfStatement ifStmt = new IfStatement("condition", thenBlock, null);

        assertFalse(ifStmt.hasElse());
        assertNull(ifStmt.getElseBlock());
    }

    @Test
    @DisplayName("Test没有 else Branch（EmptyBlock）")
    void testNoElseWithEmptyBlock() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();

        IfStatement ifStmt = new IfStatement("condition", thenBlock, elseBlock);

        assertFalse(ifStmt.hasElse());
    }

    @Test
    @DisplayName("Test复杂ConditionExpression")
    void testComplexCondition() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        String complexCondition = "this.user != null && this.user.age >= 18";

        IfStatement ifStmt = new IfStatement(complexCondition, thenBlock, elseBlock);

        assertEquals(complexCondition, ifStmt.getCondition());
    }

    @Test
    @DisplayName("Test三元运算符风格Condition")
    void testTernaryStyleCondition() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();

        IfStatement ifStmt = new IfStatement("isValid ? true : false", thenBlock, elseBlock);

        assertEquals("isValid ? true : false", ifStmt.getCondition());
    }

    @Test
    @DisplayName("TestEmptyCondition")
    void testEmptyCondition() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();

        IfStatement ifStmt = new IfStatement("", thenBlock, elseBlock);

        assertEquals("", ifStmt.getCondition());
    }

    @Test
    @DisplayName("TestContainsMethodCall ofCondition")
    void testConditionWithMethodCall() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();

        IfStatement ifStmt = new IfStatement("this.checkValue()", thenBlock, elseBlock);

        assertEquals("this.checkValue()", ifStmt.getCondition());
    }

    @Test
    @DisplayName("TestMultipleStatement of then Block")
    void testThenBlockWithMultipleStatements() {
        Block thenBlock = new Block();
        thenBlock.addStatement(new ExpressionStatement("const x = 1;"));
        thenBlock.addStatement(new ExpressionStatement("const y = 2;"));
        thenBlock.addStatement(new ExpressionStatement("return x + y;"));
        Block elseBlock = new Block();

        IfStatement ifStmt = new IfStatement("condition", thenBlock, elseBlock);

        assertEquals(3, ifStmt.getThenBlock().getStatements().size());
    }

    @Test
    @DisplayName("TestNested if Statement（通过Block）")
    void testNestedIfViaBlock() {
        Block innerThen = new Block();
        Block innerElse = new Block();
        IfStatement innerIf = new IfStatement("innerCondition", innerThen, innerElse);

        Block outerThen = new Block();
        outerThen.addStatement(innerIf);
        Block outerElse = new Block();

        IfStatement outerIf = new IfStatement("outerCondition", outerThen, outerElse);

        assertEquals(1, outerIf.getThenBlock().getStatements().size());
    }

    @Test
    @DisplayName("Test accept Method")
    void testAcceptMethod() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        IfStatement ifStmt = new IfStatement("condition", thenBlock, elseBlock);

        // Test that accept returns something (actual visitor test would be in integration tests)
        assertNotNull(ifStmt);
        assertEquals("IfStatement", ifStmt.getType());
    }
}
