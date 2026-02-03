package com.ets2jsc.infrastructure.generator.writer;

import lombok.Getter;

import com.ets2jsc.shared.constant.Symbols;

import java.util.List;

/**
 * Writes code with automatic indentation management.
 * <p>
 * This class provides a fluent API for writing code with proper
 * indentation. It manages line tracking and indentation automatically.
 */
@Getter
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
     * Creates a new code writer with specified indentation manager.
     *
     * @param indentation indentation manager
     */
    public CodeWriter(IndentationManager indentation) {
        this(indentation, System.lineSeparator());
    }

    /**
     * Creates a new code writer with specified indentation manager and line separator.
     *
     * @param indentation indentation manager
     * @param lineSeparator line separator string
     */
    public CodeWriter(IndentationManager indentation, String lineSeparator) {
        this.output = new StringBuilder();
        this.indentation = indentation != null ? indentation : new IndentationManager();
        this.lineSeparator = lineSeparator != null ? lineSeparator : System.lineSeparator();
        this.atLineStart = true;
    }

    /**
     * Writes text to output.
     *
     * @param text text to write
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
     * @param text text to write
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
     * Writes text with current indentation.
     *
     * @param text text to write
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
     * @param text text to write
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
     * Gets the current indentation level.
     *
     * @return current indentation level
     */
    public int getCurrentLevel() {
        return indentation.getCurrentLevel();
    }

    /**
     * Gets the output as a string.
     *
     * @return the output string
     */
    public String getOutput() {
        return output.toString();
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
     * Gets length of current output.
     *
     * @return output length
     */
    public int getLength() {
        return output.length();
    }

    /**
     * Checks if output is empty.
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
     * Writes text at the beginning of output.
     *
     * @param text text to prepend
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
     * @param lines lines to write
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
     * @param lines lines to write
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
     * @param content content to write inside block
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
     * Writes a code block enclosed in braces, running provided code inside.
     *
     * @param contentWriter code to execute inside block
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
     * @param condition condition
     * @param contentWriter code to execute inside if block
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
     * @param condition condition
     * @param ifWriter code for if block
     * @param elseWriter code for else block
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
