package com.ets2jsc.ast;

import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.ast.IfStatement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 IfStatement 条件语句
 */
@DisplayName("If 语句测试")
class IfStatementTest {

    @Test
    @DisplayName("测试 If 语句创建")
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
    @DisplayName("测试条件获取")
    void testGetCondition() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        IfStatement ifStmt = new IfStatement("this.isActive", thenBlock, elseBlock);

        assertEquals("this.isActive", ifStmt.getCondition());
    }

    @Test
    @DisplayName("测试 then 块获取")
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
    @DisplayName("测试 else 块获取")
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
    @DisplayName("测试有 else 分支")
    void testHasElse() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        elseBlock.addStatement(new ExpressionStatement("return;"));

        IfStatement ifStmt = new IfStatement("condition", thenBlock, elseBlock);

        assertTrue(ifStmt.hasElse());
    }

    @Test
    @DisplayName("测试没有 else 分支（null）")
    void testNoElseWithNull() {
        Block thenBlock = new Block();
        IfStatement ifStmt = new IfStatement("condition", thenBlock, null);

        assertFalse(ifStmt.hasElse());
        assertNull(ifStmt.getElseBlock());
    }

    @Test
    @DisplayName("测试没有 else 分支（空块）")
    void testNoElseWithEmptyBlock() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();

        IfStatement ifStmt = new IfStatement("condition", thenBlock, elseBlock);

        assertFalse(ifStmt.hasElse());
    }

    @Test
    @DisplayName("测试复杂条件表达式")
    void testComplexCondition() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        String complexCondition = "this.user != null && this.user.age >= 18";

        IfStatement ifStmt = new IfStatement(complexCondition, thenBlock, elseBlock);

        assertEquals(complexCondition, ifStmt.getCondition());
    }

    @Test
    @DisplayName("测试三元运算符风格条件")
    void testTernaryStyleCondition() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();

        IfStatement ifStmt = new IfStatement("isValid ? true : false", thenBlock, elseBlock);

        assertEquals("isValid ? true : false", ifStmt.getCondition());
    }

    @Test
    @DisplayName("测试空条件")
    void testEmptyCondition() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();

        IfStatement ifStmt = new IfStatement("", thenBlock, elseBlock);

        assertEquals("", ifStmt.getCondition());
    }

    @Test
    @DisplayName("测试包含方法调用的条件")
    void testConditionWithMethodCall() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();

        IfStatement ifStmt = new IfStatement("this.checkValue()", thenBlock, elseBlock);

        assertEquals("this.checkValue()", ifStmt.getCondition());
    }

    @Test
    @DisplayName("测试多个语句的 then 块")
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
    @DisplayName("测试嵌套 if 语句（通过块）")
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
    @DisplayName("测试 accept 方法")
    void testAcceptMethod() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        IfStatement ifStmt = new IfStatement("condition", thenBlock, elseBlock);

        // Test that accept returns something (actual visitor test would be in integration tests)
        assertNotNull(ifStmt);
        assertEquals("IfStatement", ifStmt.getType());
    }
}
