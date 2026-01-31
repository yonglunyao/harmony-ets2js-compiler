package com.ets2jsc.generator.writer;

import com.ets2jsc.constant.Symbols;

import java.util.ArrayList;
import java.util.List;

/**
 * Writes code with automatic indentation management.
 * <p>
 * This class provides a fluent API for writing code with proper
 * indentation. It manages line tracking and indentation automatically.
 */
public class CodeWriter {

    private final StringBuilder output;
    private final IndentationManager indentation;
    private final String lineSeparator;
    private boolean atLineStart;

    /**
     * Creates a new code writer with default indentation.
     */
    public CodeWriter() {
        this(new IndentationManager(), System.lineSeparator());
    }

    /**
     * Creates a new code writer with the specified indentation manager.
     *
     * @param indentation the indentation manager
     */
    public CodeWriter(IndentationManager indentation) {
        this(indentation, System.lineSeparator());
    }

    /**
     * Creates a new code writer with the specified indentation manager and line separator.
     *
     * @param indentation the indentation manager
     * @param lineSeparator the line separator string
     */
    public CodeWriter(IndentationManager indentation, String lineSeparator) {
        this.output = new StringBuilder();
        this.indentation = indentation != null ? indentation : new IndentationManager();
        this.lineSeparator = lineSeparator != null ? lineSeparator : System.lineSeparator();
        this.atLineStart = true;
    }

    /**
     * Writes text to the output.
     *
     * @param text the text to write
     * @return this writer for chaining
     */
    public CodeWriter write(String text) {
        if (text == null) {
            return this;
        }

        // Handle multi-line text
        String[] lines = text.split("\\r?\\n", Symbols.SPLIT_KEEP_EMPTY_LINES);
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                writeNewline();
            }
            writeLineInternal(lines[i]);
        }

        return this;
    }

    /**
     * Writes text followed by a newline.
     *
     * @param text the text to write
     * @return this writer for chaining
     */
    public CodeWriter writeLine(String text) {
        write(text);
        writeNewline();
        return this;
    }

    /**
     * Writes a blank line.
     *
     * @return this writer for chaining
     */
    public CodeWriter writeBlankLine() {
        writeNewline();
        return this;
    }

    /**
     * Writes text with the current indentation.
     *
     * @param text the text to write
     * @return this writer for chaining
     */
    public CodeWriter writeIndented(String text) {
        if (atLineStart) {
            write(indentation.getCurrentIndent());
        }
        write(text);
        return this;
    }

    /**
     * Writes text followed by a newline, with current indentation.
     *
     * @param text the text to write
     * @return this writer for chaining
     */
    public CodeWriter writeIndentedLine(String text) {
        writeIndented(text);
        writeNewline();
        return this;
    }

    /**
     * Writes a newline.
     *
     * @return this writer for chaining
     */
    public CodeWriter writeNewline() {
        output.append(lineSeparator);
        atLineStart = true;
        return this;
    }

    /**
     * Increases the indentation level.
     *
     * @return this writer for chaining
     */
    public CodeWriter indent() {
        indentation.indent();
        return this;
    }

    /**
     * Decreases the indentation level.
     *
     * @return this writer for chaining
     */
    public CodeWriter dedent() {
        indentation.dedent();
        return this;
    }

    /**
     * Executes a block with increased indentation.
     *
     * @param runnable the code to execute
     * @return this writer for chaining
     */
    public CodeWriter withIndent(Runnable runnable) {
        indentation.withIndent(runnable);
        return this;
    }

    /**
     * Gets the indentation manager.
     *
     * @return the indentation manager
     */
    public IndentationManager getIndentation() {
        return indentation;
    }

    /**
     * Gets the current indentation level.
     *
     * @return the current level
     */
    public int getCurrentLevel() {
        return indentation.getCurrentLevel();
    }

    /**
     * Gets the current output as a string.
     *
     * @return the generated code
     */
    public String getOutput() {
        return output.toString();
    }

    /**
     * Gets the length of the current output.
     *
     * @return the output length
     */
    public int getLength() {
        return output.length();
    }

    /**
     * Checks if the output is empty.
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return output.length() == 0;
    }

    /**
     * Clears all output.
     *
     * @return this writer for chaining
     */
    public CodeWriter clear() {
        output.setLength(0);
        indentation.reset();
        atLineStart = true;
        return this;
    }

    /**
     * Writes text at the beginning of the output.
     *
     * @param text the text to prepend
     * @return this writer for chaining
     */
    public CodeWriter prepend(String text) {
        int originalLength = output.length();
        output.insert(0, text);
        return this;
    }

    /**
     * Writes multiple lines.
     *
     * @param lines the lines to write
     * @return this writer for chaining
     */
    public CodeWriter writeLines(String... lines) {
        for (String line : lines) {
            writeLine(line);
        }
        return this;
    }

    /**
     * Writes multiple lines with indentation.
     *
     * @param lines the lines to write
     * @return this writer for chaining
     */
    public CodeWriter writeIndentedLines(String... lines) {
        for (String line : lines) {
            writeIndentedLine(line);
        }
        return this;
    }

    /**
     * Writes a code block enclosed in braces.
     *
     * @param content the content to write inside the block
     * @return this writer for chaining
     */
    public CodeWriter writeBlock(String content) {
        writeIndentedLine("{");
        indent();
        write(content);
        dedent();
        writeIndentedLine("}");
        return this;
    }

    /**
     * Writes a code block enclosed in braces, running the provided code inside.
     *
     * @param contentWriter the code to execute inside the block
     * @return this writer for chaining
     */
    public CodeWriter writeBlock(Consumer contentWriter) {
        writeIndentedLine("{");
        indent();
        if (contentWriter != null) {
            contentWriter.accept(this);
        }
        dedent();
        writeIndentedLine("}");
        return this;
    }

    /**
     * Writes an if statement.
     *
     * @param condition the condition
     * @param contentWriter the code to execute inside the if block
     * @return this writer for chaining
     */
    public CodeWriter writeIf(String condition, Consumer contentWriter) {
        writeIndented("if (").write(condition).writeLine(") {");
        indent();
        if (contentWriter != null) {
            contentWriter.accept(this);
        }
        dedent();
        writeIndentedLine("}");
        return this;
    }

    /**
     * Writes an if-else statement.
     *
     * @param condition the condition
     * @param ifWriter the code for the if block
     * @param elseWriter the code for the else block
     * @return this writer for chaining
     */
    public CodeWriter writeIfElse(String condition, Consumer ifWriter,
                                   Consumer elseWriter) {
        writeIndented("if (").write(condition).writeLine(") {");
        indent();
        if (ifWriter != null) {
            ifWriter.accept(this);
        }
        dedent();
        writeIndentedLine("} else {");
        indent();
        if (elseWriter != null) {
            elseWriter.accept(this);
        }
        dedent();
        writeIndentedLine("}");
        return this;
    }

    /**
     * Internal method to write a line.
     */
    private void writeLineInternal(String text) {
        if (atLineStart && text.length() > 0) {
            output.append(indentation.getCurrentIndent());
            atLineStart = false;
        }
        output.append(text);
        if (text.length() == Symbols.EMPTY_STRING_LENGTH) {
            atLineStart = true;
        }
    }

    @Override
    public String toString() {
        return output.toString();
    }

    /**
     * Functional interface for CodeWriter operations.
     */
    @FunctionalInterface
    public interface Consumer {
        void accept(CodeWriter writer);
    }
}
