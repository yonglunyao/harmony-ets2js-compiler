package com.ets2jsc.infrastructure.generator;

import com.ets2jsc.shared.constant.Symbols;

/**
 * Helper class for string literal operations in code generation.
 * Handles quoting detection and string escaping.
 */
public final class StringLiteralHelper {

    // String delimiters
    private static final String SINGLE_QUOTE = "'";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String BACKTICK = "`";

    // Patterns that don't need quoting
    private static final String NEW_PREFIX = "new ";
    private static final String THIS_PREFIX = "this.";
    private static final String DOLLAR_PREFIX = "$";

    // Patterns that indicate complex expressions
    private static final String LEFT_PAREN = "(";
    private static final String LEFT_BRACE = "{";
    private static final String LEFT_BRACKET = "[";

    private StringLiteralHelper() {
        // Prevent instantiation
    }

    /**
     * Checks if a value looks like it needs quotes (unquoted string literal).
     * CC: 5 (early returns + or chain)
     */
    public static boolean needsQuoting(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        // Already quoted - don't quote again
        if (isAlreadyQuoted(value)) {
            return false;
        }

        // Literals and keywords that don't need quoting
        if (isLiteral(value)) {
            return false;
        }

        // Complex expressions that don't need quoting
        if (isComplexExpression(value)) {
            return false;
        }

        // Resource references don't need quoting
        if (isResourceReference(value)) {
            return false;
        }

        // Otherwise, it might be an unquoted string literal
        return true;
    }

    /**
     * Checks if string is already quoted.
     * CC: 2 (prefix checks)
     */
    private static boolean isAlreadyQuoted(String value) {
        return value.startsWith(DOUBLE_QUOTE) ||
               value.startsWith(SINGLE_QUOTE) ||
               value.startsWith(BACKTICK);
    }

    /**
     * Checks if value is a JavaScript literal.
     * CC: 5 (equals checks)
     */
    private static boolean isLiteral(String value) {
        return value.matches(Symbols.DIGITS_REGEX) ||
               Symbols.TRUE_LITERAL.equals(value) ||
               Symbols.FALSE_LITERAL.equals(value) ||
               Symbols.NULL_LITERAL.equals(value) ||
               Symbols.UNDEFINED_LITERAL.equals(value);
    }

    /**
     * Checks if value is a complex expression.
     * CC: 5 (startsWith checks)
     */
    private static boolean isComplexExpression(String value) {
        return value.startsWith(THIS_PREFIX) ||
               value.startsWith(NEW_PREFIX) ||
               value.contains(LEFT_PAREN) ||
               value.contains(LEFT_BRACE) ||
               value.contains(LEFT_BRACKET);
    }

    /**
     * Checks if value is a resource reference.
     * CC: 1
     */
    private static boolean isResourceReference(String value) {
        return value.startsWith(DOLLAR_PREFIX);
    }

    /**
     * Escapes special characters in JavaScript strings.
     * CC: 1 (method chain)
     */
    public static String escapeJsString(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
