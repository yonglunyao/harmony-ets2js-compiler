package com.ets2jsc.infrastructure.generator.writer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IndentationManager.
 */
public class IndentationManagerTest {

    @Test
    public void testConstructor_Default() {
        IndentationManager indent = new IndentationManager();
        assertEquals("", indent.getCurrentIndent());
        assertEquals(0, indent.getCurrentLevel());
    }

    @Test
    public void testConstructor_CustomIndent() {
        IndentationManager indent = new IndentationManager("    ");
        assertEquals("", indent.getCurrentIndent());
        assertEquals(0, indent.getCurrentLevel());
    }

    @Test
    public void testConstructor_TabIndent() {
        IndentationManager indent = new IndentationManager("\t");
        assertEquals("", indent.getCurrentIndent());
    }

    @Test
    public void testGetCurrentLevel_Initial() {
        IndentationManager indent = new IndentationManager();
        assertEquals(0, indent.getCurrentLevel());
    }

    @Test
    public void testSetCurrentLevel() {
        IndentationManager indent = new IndentationManager();
        indent.setCurrentLevel(3);
        assertEquals(3, indent.getCurrentLevel());
        assertEquals("      ", indent.getCurrentIndent());
    }

    @Test
    public void testSetCurrentLevel_Negative() {
        IndentationManager indent = new IndentationManager();
        indent.setCurrentLevel(-1);
        assertEquals(0, indent.getCurrentLevel());
    }

    @Test
    public void testIndent() {
        IndentationManager indent = new IndentationManager();
        assertEquals(0, indent.getCurrentLevel());
        indent.indent();
        assertEquals(1, indent.getCurrentLevel());
        assertEquals("  ", indent.getCurrentIndent());
        indent.indent();
        assertEquals(2, indent.getCurrentLevel());
        assertEquals("    ", indent.getCurrentIndent());
    }

    @Test
    public void testDedent() {
        IndentationManager indent = new IndentationManager();
        indent.indent();
        indent.indent();
        assertEquals(2, indent.getCurrentLevel());
        indent.dedent();
        assertEquals(1, indent.getCurrentLevel());
        assertEquals("  ", indent.getCurrentIndent());
    }

    @Test
    public void testDedent_AtZero() {
        IndentationManager indent = new IndentationManager();
        assertEquals(0, indent.getCurrentLevel());
        indent.dedent();
        assertEquals(0, indent.getCurrentLevel());
        assertEquals("", indent.getCurrentIndent());
    }

    @Test
    public void testGetCurrentIndent_VariousLevels() {
        IndentationManager indent = new IndentationManager();
        assertEquals("", indent.getCurrentIndent());

        indent.setCurrentLevel(1);
        assertEquals("  ", indent.getCurrentIndent());

        indent.setCurrentLevel(2);
        assertEquals("    ", indent.getCurrentIndent());

        indent.setCurrentLevel(3);
        assertEquals("      ", indent.getCurrentIndent());
    }

    @Test
    public void testGetIndent_SpecificLevel() {
        IndentationManager indent = new IndentationManager();
        indent.setCurrentLevel(2);

        assertEquals("", indent.getIndent(0));
        assertEquals("  ", indent.getIndent(1));
        assertEquals("    ", indent.getIndent(2));
        assertEquals("      ", indent.getIndent(3));
    }

    @Test
    public void testReset() {
        IndentationManager indent = new IndentationManager();
        indent.indent();
        indent.indent();
        indent.indent();
        assertEquals(3, indent.getCurrentLevel());

        indent.reset();
        assertEquals(0, indent.getCurrentLevel());
        assertEquals("", indent.getCurrentIndent());
    }

    @Test
    public void testCopy() {
        IndentationManager original = new IndentationManager();
        original.indent();
        original.indent();
        assertEquals(2, original.getCurrentLevel());

        IndentationManager copy = original.copy();
        assertEquals(2, copy.getCurrentLevel());
        assertEquals("    ", copy.getCurrentIndent());

        // Modify original, copy should be unchanged
        original.indent();
        assertEquals(3, original.getCurrentLevel());
        assertEquals(2, copy.getCurrentLevel());
    }

    @Test
    public void testWithIndent() {
        IndentationManager indent = new IndentationManager();
        assertEquals(0, indent.getCurrentLevel());

        indent.withIndent(() -> {
            assertEquals(1, indent.getCurrentLevel());
            assertEquals("  ", indent.getCurrentIndent());
        });

        // After withIndent, level should be restored
        assertEquals(0, indent.getCurrentLevel());
        assertEquals("", indent.getCurrentIndent());
    }

    @Test
    public void testWithIndent_Nested() {
        IndentationManager indent = new IndentationManager();

        indent.withIndent(() -> {
            assertEquals(1, indent.getCurrentLevel());
            indent.withIndent(() -> {
                assertEquals(2, indent.getCurrentLevel());
            });
            assertEquals(1, indent.getCurrentLevel());
        });

        assertEquals(0, indent.getCurrentLevel());
    }

    @Test
    public void testWithIndent_ExceptionInRunnable() {
        IndentationManager indent = new IndentationManager();
        indent.indent();
        assertEquals(1, indent.getCurrentLevel());

        try {
            indent.withIndent(() -> {
                assertEquals(2, indent.getCurrentLevel());
                throw new RuntimeException("test");
            });
            fail("Expected exception");
        } catch (RuntimeException e) {
            // Expected
        }

        // Level should still be restored even after exception
        assertEquals(1, indent.getCurrentLevel());
    }

    @Test
    public void testWithIndent_DedentInRunnable() {
        IndentationManager indent = new IndentationManager();
        indent.indent();
        indent.indent();
        assertEquals(2, indent.getCurrentLevel());

        indent.withIndent(() -> {
            assertEquals(3, indent.getCurrentLevel());
            indent.dedent();
            assertEquals(2, indent.getCurrentLevel());
        });

        // withIndent should dedent once, so level should be 1
        assertEquals(1, indent.getCurrentLevel());
    }

    @Test
    public void testFourSpaceIndent() {
        IndentationManager indent = new IndentationManager("    ");
        indent.indent();
        assertEquals("    ", indent.getCurrentIndent());
        indent.indent();
        assertEquals("        ", indent.getCurrentIndent());
    }

    @Test
    public void testTabIndent() {
        IndentationManager indent = new IndentationManager("\t");
        indent.indent();
        assertEquals("\t", indent.getCurrentIndent());
        indent.indent();
        assertEquals("\t\t", indent.getCurrentIndent());
    }
}
