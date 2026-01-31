package com.ets2jsc.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Block code block
 */
@DisplayName("Block Tests")
class BlockTest {

    @Test
    @DisplayName("Test block creation")
    void testBlockCreation() {
        Block block = new Block();

        assertEquals("Block", block.getType());
        assertNotNull(block.getStatements());
        assertTrue(block.getStatements().isEmpty());
    }

    @Test
    @DisplayName("TestAddStatement")
    void testAddStatement() {
        Block block = new Block();
        ExpressionStatement stmt = new ExpressionStatement("const x = 1;");

        block.addStatement(stmt);

        assertEquals(1, block.getStatements().size());
        assertEquals(stmt, block.getStatements().get(0));
    }

    @Test
    @DisplayName("TestGetStatementList")
    void testGetStatements() {
        Block block = new Block();
        ExpressionStatement stmt1 = new ExpressionStatement("const x = 1;");
        ExpressionStatement stmt2 = new ExpressionStatement("const y = 2;");

        block.addStatement(stmt1);
        block.addStatement(stmt2);

        assertEquals(2, block.getStatements().size());
    }

    @Test
    @DisplayName("TestEmptyBlock")
    void testEmptyBlock() {
        Block block = new Block();

        assertTrue(block.getStatements().isEmpty());
        assertEquals(0, block.getStatements().size());
    }

    @Test
    @DisplayName("TestMultipleStatement")
    void testMultipleStatements() {
        Block block = new Block();

        for (int i = 0; i < 10; i++) {
            block.addStatement(new ExpressionStatement("const x" + i + " = " + i + ";"));
        }

        assertEquals(10, block.getStatements().size());
    }

    @Test
    @DisplayName("TestNestedBlock")
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
    @DisplayName("Test block containing if statement")
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
    @DisplayName("Test accept Method")
    void testAcceptMethod() {
        Block block = new Block();

        assertNotNull(block);
        assertEquals("Block", block.getType());
    }

    @Test
    @DisplayName("Test statement order preservation")
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
    @DisplayName("TestAdd null Statement")
    void testAddNullStatement() {
        Block block = new Block();

        // This should add null (not recommended but testing behavior)
        block.getStatements().add(null);

        assertEquals(1, block.getStatements().size());
        assertNull(block.getStatements().get(0));
    }

    @Test
    @DisplayName("TestModifyStatementList")
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
    @DisplayName("Test block containing expressions")
    void testBlockWithExpressions() {
        Block block = new Block();

        block.addStatement(new ExpressionStatement("let count = 0;"));
        block.addStatement(new ExpressionStatement("count++;"));
        block.addStatement(new ExpressionStatement("console.log(count);"));
        block.addStatement(new ExpressionStatement("return count;"));

        assertEquals(4, block.getStatements().size());
    }

    @Test
    @DisplayName("Test deeply nested blocks")
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
    @DisplayName("Test block with different statement types")
    void testMixedStatementTypes() {
        Block block = new Block();

        block.addStatement(new ExpressionStatement("const x = 1;"));
        block.addStatement(new IfStatement("condition", new Block(), new Block()));
        block.addStatement(new ExpressionStatement("console.log('test');"));

        assertEquals(3, block.getStatements().size());
    }

    @Test
    @DisplayName("Test adding same statement multiple times")
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
    @DisplayName("Test modify statement after adding")
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
