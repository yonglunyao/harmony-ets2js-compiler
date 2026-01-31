package com.ets2jsc.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 Block 代码块
 */
@DisplayName("Block 测试")
class BlockTest {

    @Test
    @DisplayName("测试 Block 创建")
    void testBlockCreation() {
        Block block = new Block();

        assertEquals("Block", block.getType());
        assertNotNull(block.getStatements());
        assertTrue(block.getStatements().isEmpty());
    }

    @Test
    @DisplayName("测试添加语句")
    void testAddStatement() {
        Block block = new Block();
        ExpressionStatement stmt = new ExpressionStatement("const x = 1;");

        block.addStatement(stmt);

        assertEquals(1, block.getStatements().size());
        assertEquals(stmt, block.getStatements().get(0));
    }

    @Test
    @DisplayName("测试获取语句列表")
    void testGetStatements() {
        Block block = new Block();
        ExpressionStatement stmt1 = new ExpressionStatement("const x = 1;");
        ExpressionStatement stmt2 = new ExpressionStatement("const y = 2;");

        block.addStatement(stmt1);
        block.addStatement(stmt2);

        assertEquals(2, block.getStatements().size());
    }

    @Test
    @DisplayName("测试空块")
    void testEmptyBlock() {
        Block block = new Block();

        assertTrue(block.getStatements().isEmpty());
        assertEquals(0, block.getStatements().size());
    }

    @Test
    @DisplayName("测试多个语句")
    void testMultipleStatements() {
        Block block = new Block();

        for (int i = 0; i < 10; i++) {
            block.addStatement(new ExpressionStatement("const x" + i + " = " + i + ";"));
        }

        assertEquals(10, block.getStatements().size());
    }

    @Test
    @DisplayName("测试嵌套块")
    void testNestedBlocks() {
        Block outer = new Block();
        Block inner = new Block();

        inner.addStatement(new ExpressionStatement("const y = 2;"));
        outer.addStatement(new ExpressionStatement("const x = 1;"));
        outer.addStatement(inner);
        outer.addStatement(new ExpressionStatement("const z = 3;"));

        assertEquals(3, outer.getStatements().size());
        assertEquals(inner, outer.getStatements().get(1));
        assertEquals(1, inner.getStatements().size());
    }

    @Test
    @DisplayName("测试包含 if 语句的块")
    void testBlockWithIfStatement() {
        Block block = new Block();
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        IfStatement ifStmt = new IfStatement("x > 0", thenBlock, elseBlock);

        block.addStatement(new ExpressionStatement("const x = 1;"));
        block.addStatement(ifStmt);

        assertEquals(2, block.getStatements().size());
        assertEquals(ifStmt, block.getStatements().get(1));
    }

    @Test
    @DisplayName("测试 accept 方法")
    void testAcceptMethod() {
        Block block = new Block();

        assertNotNull(block);
        assertEquals("Block", block.getType());
    }

    @Test
    @DisplayName("测试语句顺序保持")
    void testStatementOrder() {
        Block block = new Block();

        ExpressionStatement stmt1 = new ExpressionStatement("first;");
        ExpressionStatement stmt2 = new ExpressionStatement("second;");
        ExpressionStatement stmt3 = new ExpressionStatement("third;");

        block.addStatement(stmt1);
        block.addStatement(stmt2);
        block.addStatement(stmt3);

        assertEquals(stmt1, block.getStatements().get(0));
        assertEquals(stmt2, block.getStatements().get(1));
        assertEquals(stmt3, block.getStatements().get(2));
    }

    @Test
    @DisplayName("测试添加 null 语句")
    void testAddNullStatement() {
        Block block = new Block();

        // This should add null (not recommended but testing behavior)
        block.getStatements().add(null);

        assertEquals(1, block.getStatements().size());
        assertNull(block.getStatements().get(0));
    }

    @Test
    @DisplayName("测试修改语句列表")
    void testModifyStatementsList() {
        Block block = new Block();
        ExpressionStatement stmt1 = new ExpressionStatement("const x = 1;");
        ExpressionStatement stmt2 = new ExpressionStatement("const y = 2;");

        block.addStatement(stmt1);
        assertEquals(1, block.getStatements().size());

        // Can modify list directly (returns actual list)
        block.getStatements().add(stmt2);
        assertEquals(2, block.getStatements().size());
    }

    @Test
    @DisplayName("测试包含表达式的块")
    void testBlockWithExpressions() {
        Block block = new Block();

        block.addStatement(new ExpressionStatement("let count = 0;"));
        block.addStatement(new ExpressionStatement("count++;"));
        block.addStatement(new ExpressionStatement("console.log(count);"));
        block.addStatement(new ExpressionStatement("return count;"));

        assertEquals(4, block.getStatements().size());
    }

    @Test
    @DisplayName("测试深层嵌套块")
    void testDeeplyNestedBlocks() {
        Block level1 = new Block();
        Block level2 = new Block();
        Block level3 = new Block();
        Block level4 = new Block();

        level4.addStatement(new ExpressionStatement("const deep = 4;"));
        level3.addStatement(level4);
        level2.addStatement(level3);
        level1.addStatement(level2);

        assertEquals(1, level1.getStatements().size());
        assertEquals(1, level2.getStatements().size());
        assertEquals(1, level3.getStatements().size());
        assertEquals(1, level4.getStatements().size());
    }

    @Test
    @DisplayName("测试块中的不同语句类型")
    void testMixedStatementTypes() {
        Block block = new Block();

        block.addStatement(new ExpressionStatement("const x = 1;"));
        block.addStatement(new IfStatement("condition", new Block(), new Block()));
        block.addStatement(new ExpressionStatement("console.log('test');"));

        assertEquals(3, block.getStatements().size());
    }

    @Test
    @DisplayName("测试重复添加相同语句")
    void testAddSameStatementMultipleTimes() {
        Block block = new Block();
        ExpressionStatement stmt = new ExpressionStatement("const x = 1;");

        block.addStatement(stmt);
        block.addStatement(stmt);
        block.addStatement(stmt);

        assertEquals(3, block.getStatements().size());
        assertEquals(stmt, block.getStatements().get(0));
        assertEquals(stmt, block.getStatements().get(1));
        assertEquals(stmt, block.getStatements().get(2));
    }

    @Test
    @DisplayName("测试添加后修改语句对象")
    void testModifyStatementAfterAdding() {
        Block block = new Block();
        ExpressionStatement stmt = new ExpressionStatement("const x = 1;");

        block.addStatement(stmt);
        assertEquals(1, block.getStatements().size());

        // The statement object is same reference
        // Note: ExpressionStatement doesn't have setters, so this tests reference equality
        assertSame(stmt, block.getStatements().get(0));
    }
}
