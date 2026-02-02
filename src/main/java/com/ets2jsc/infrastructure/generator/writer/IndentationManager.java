package com.ets2jsc.infrastructure.generator.writer;

/**
 * Manages indentation level for code generation.
 * <p>
 * This class tracks the current indentation level and provides
 * methods to increase, decrease, and retrieve indentation strings.
 */
public class IndentationManager {

    private final String indentString;
    private int currentLevel;

    /**
     * Creates an indentation manager with the specified indent string.
     *
     * @param indentString the string to use for each indentation level (e.g., "  " or "\t")
     */
    public IndentationManager(String indentString) {
        this.indentString = indentString != null ? indentString : "  ";
        this.currentLevel = 0;
    }

    /**
     * Creates an indentation manager with default 2-space indent.
     */
    public IndentationManager() {
        this("  ");
    }

    /**
     * Gets the current indentation level.
     *
     * @return the current level (0 = no indentation)
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Sets the current indentation level.
     *
     * @param level the new level
     */
    public void setCurrentLevel(int level) {
        this.currentLevel = Math.max(0, level);
    }

    /**
     * Increases the indentation level by one.
     */
    public void indent() {
        currentLevel++;
    }

    /**
     * Decreases the indentation level by one.
     * Will not go below 0.
     */
    public void dedent() {
        if (currentLevel > 0) {
            currentLevel--;
        }
    }

    /**
     * Gets the current indentation as a string.
     *
     * @return the indentation string for the current level
     */
    public String getCurrentIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentLevel; i++) {
            sb.append(indentString);
        }
        return sb.toString();
    }

    /**
     * Gets the indentation string for a specific level.
     *
     * @param level the level
     * @return the indentation string
     */
    public String getIndent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append(indentString);
        }
        return sb.toString();
    }

    /**
     * Resets the indentation level to 0.
     */
    public void reset() {
        currentLevel = 0;
    }

    /**
     * Creates a new indentation manager with the same settings.
     *
     * @return a new indentation manager
     */
    public IndentationManager copy() {
        IndentationManager copy = new IndentationManager(indentString);
        copy.setCurrentLevel(currentLevel);
        return copy;
    }

    /**
     * Executes a block of code with increased indentation.
     * The indentation is automatically decreased after the block completes.
     *
     * @param runnable the code to execute
     */
    public void withIndent(Runnable runnable) {
        indent();
        try {
            runnable.run();
        } finally {
            dedent();
        }
    }
}
