package com.ets2jsc.generator;

import com.ets2jsc.shared.constant.Symbols;

/**
 * Manages indentation level and generates indent strings.
 * Extracted from CodeGenerator for reusability.
 */
public class IndentationManager {

    private final String indent;
    private int currentIndent;

    public IndentationManager(String indentString) {
        this.indent = indentString != null ? indentString : Symbols.DEFAULT_INDENT;
        this.currentIndent = 0;
    }

    public IndentationManager() {
        this(Symbols.DEFAULT_INDENT);
    }

    /**
     * Gets current indentation string.
     * CC: 1
     */
    public String getCurrent() {
        return indent.repeat(currentIndent);
    }

    /**
     * Increases indentation level.
     * CC: 1
     */
    public void increase() {
        currentIndent++;
    }

    /**
     * Decreases indentation level.
     * CC: 1
     */
    public void decrease() {
        if (currentIndent > 0) {
            currentIndent--;
        }
    }

    /**
     * Gets current indentation level.
     * CC: 1
     */
    public int getLevel() {
        return currentIndent;
    }

    /**
     * Sets indentation level.
     * CC: 1
     */
    public void setLevel(int level) {
        this.currentIndent = Math.max(0, level);
    }

    /**
     * Resets indentation to zero.
     * CC: 1
     */
    public void reset() {
        currentIndent = 0;
    }
}
