package com.ets2jsc.generator.writer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CodeWriter.
 */
public class CodeWriterTest {

    @Test
    public void testConstructor_Default() {
        CodeWriter writer = new CodeWriter();
        assertEquals("", writer.getOutput());
        assertEquals(0, writer.getIndentation().getCurrentLevel());
        assertTrue(writer.isEmpty());
    }

    @Test
    public void testConstructor_WithIndentation() {
        IndentationManager indent = new IndentationManager("    ");
        CodeWriter writer = new CodeWriter(indent);
        assertEquals(0, writer.getCurrentLevel());
    }

    @Test
    public void testWrite_SimpleText() {
        CodeWriter writer = new CodeWriter();
        writer.write("hello");
        assertEquals("hello", writer.getOutput());
    }

    @Test
    public void testWrite_Null() {
        CodeWriter writer = new CodeWriter();
        writer.write(null);
        assertEquals("", writer.getOutput());
    }

    @Test
    public void testWrite_MultipleLines() {
        CodeWriter writer = new CodeWriter();
        writer.write("line1\nline2");
        String expected = "line1" + System.lineSeparator() + "line2";
        assertEquals(expected, writer.getOutput());
    }

    @Test
    public void testWriteLine_SimpleText() {
        CodeWriter writer = new CodeWriter();
        writer.writeLine("hello");
        assertEquals("hello" + System.lineSeparator(), writer.getOutput());
    }

    @Test
    public void testWriteLine_MultipleLines() {
        CodeWriter writer = new CodeWriter();
        writer.writeLine("line1");
        writer.writeLine("line2");
        String expected = "line1" + System.lineSeparator() + "line2" + System.lineSeparator();
        assertEquals(expected, writer.getOutput());
    }

    @Test
    public void testWriteBlankLine() {
        CodeWriter writer = new CodeWriter();
        writer.writeLine("hello");
        writer.writeBlankLine();
        writer.writeLine("world");
        String expected = "hello" + System.lineSeparator() + System.lineSeparator() + "world" + System.lineSeparator();
        assertEquals(expected, writer.getOutput());
    }

    @Test
    public void testWriteIndented_NoIndent() {
        CodeWriter writer = new CodeWriter();
        writer.writeIndented("hello");
        assertEquals("hello", writer.getOutput());
    }

    @Test
    public void testWriteIndented_WithIndent() {
        CodeWriter writer = new CodeWriter();
        writer.indent();
        writer.writeIndented("hello");
        assertEquals("    hello", writer.getOutput());
    }

    @Test
    public void testWriteIndentedLine_Multiple() {
        CodeWriter writer = new CodeWriter();
        writer.indent();
        writer.writeIndentedLine("line1");
        writer.writeIndentedLine("line2");
        String expected = "    line1" + System.lineSeparator() + "    line2" + System.lineSeparator();
        assertEquals(expected, writer.getOutput());
    }

    @Test
    public void testIndent() {
        CodeWriter writer = new CodeWriter();
        assertEquals(0, writer.getCurrentLevel());
        writer.indent();
        assertEquals(1, writer.getCurrentLevel());
        writer.indent();
        assertEquals(2, writer.getCurrentLevel());
    }

    @Test
    public void testDedent() {
        CodeWriter writer = new CodeWriter();
        writer.indent();
        writer.indent();
        assertEquals(2, writer.getCurrentLevel());
        writer.dedent();
        assertEquals(1, writer.getCurrentLevel());
    }

    @Test
    public void testDedent_BelowZero() {
        CodeWriter writer = new CodeWriter();
        assertEquals(0, writer.getCurrentLevel());
        writer.dedent();
        assertEquals(0, writer.getCurrentLevel());
    }

    @Test
    public void testWithIndent() {
        CodeWriter writer = new CodeWriter();
        writer.writeLine("start");
        writer.withIndent(() -> {
            writer.writeLine("indented");
        });
        writer.writeLine("end");
        String expected = "start" + System.lineSeparator() +
                        "  indented" + System.lineSeparator() +
                        "end" + System.lineSeparator();
        assertEquals(expected, writer.getOutput());
        assertEquals(0, writer.getCurrentLevel());
    }

    @Test
    public void testWriteBlock_String() {
        CodeWriter writer = new CodeWriter();
        writer.writeBlock("content");
        String output = writer.getOutput();

        // The writeBlock method does not indent the content
        assertTrue(output.contains("{"));
        assertTrue(output.contains("content"));
        assertTrue(output.contains("}"));
        assertTrue(output.contains(System.lineSeparator()));
    }

    @Test
    public void testWriteBlock_Consumer() {
        CodeWriter writer = new CodeWriter();
        writer.writeBlock(w -> {
            w.writeLine("line1");
            w.writeLine("line2");
        });
        String expected = "{" + System.lineSeparator() +
                        "  line1" + System.lineSeparator() +
                        "  line2" + System.lineSeparator() +
                        "}" + System.lineSeparator();
        assertEquals(expected, writer.getOutput());
    }

    @Test
    public void testWriteIf_Simple() {
        CodeWriter writer = new CodeWriter();
        writer.writeIf("x > 0", w -> w.writeLine("return true;"));
        String expected = "if (x > 0) {" + System.lineSeparator() +
                        "  return true;" + System.lineSeparator() +
                        "}" + System.lineSeparator();
        assertEquals(expected, writer.getOutput());
    }

    @Test
    public void testWriteIfElse() {
        CodeWriter writer = new CodeWriter();
        writer.writeIfElse("x > 0",
            w -> w.writeLine("return true;"),
            w -> w.writeLine("return false;")
        );
        String expected = "if (x > 0) {" + System.lineSeparator() +
                        "  return true;" + System.lineSeparator() +
                        "} else {" + System.lineSeparator() +
                        "  return false;" + System.lineSeparator() +
                        "}" + System.lineSeparator();
        assertEquals(expected, writer.getOutput());
    }

    @Test
    public void testWriteLines_Various() {
        CodeWriter writer = new CodeWriter();
        writer.writeLines("a", "b", "c");
        String expected = "a" + System.lineSeparator() +
                        "b" + System.lineSeparator() +
                        "c" + System.lineSeparator();
        assertEquals(expected, writer.getOutput());
    }

    @Test
    public void testWriteIndentedLines_Various() {
        CodeWriter writer = new CodeWriter();
        writer.indent();
        writer.writeIndentedLines("a", "b", "c");
        String expected = "    a" + System.lineSeparator() +
                        "    b" + System.lineSeparator() +
                        "    c" + System.lineSeparator();
        assertEquals(expected, writer.getOutput());
    }

    @Test
    public void testClear() {
        CodeWriter writer = new CodeWriter();
        writer.writeLine("hello");
        writer.indent();
        assertFalse(writer.isEmpty());
        writer.clear();
        assertTrue(writer.isEmpty());
        assertEquals("", writer.getOutput());
        assertEquals(0, writer.getCurrentLevel());
    }

    @Test
    public void testGetLength() {
        CodeWriter writer = new CodeWriter();
        assertEquals(0, writer.getLength());
        writer.write("hello");
        assertEquals(5, writer.getLength());
    }

    @Test
    public void testToString() {
        CodeWriter writer = new CodeWriter();
        writer.write("hello");
        assertEquals("hello", writer.toString());
    }

    @Test
    public void testChaining() {
        CodeWriter writer = new CodeWriter();
        writer.write("hello")
              .writeLine(" world")
              .indent()
              .writeLine("indented")
              .dedent();
        String expected = "hello world" + System.lineSeparator() +
                        "  indented" + System.lineSeparator();
        assertEquals(expected, writer.getOutput());
    }
}
