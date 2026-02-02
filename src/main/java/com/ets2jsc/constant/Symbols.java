package com.ets2jsc.constant;

/**
 * Constants for code generation symbols and naming conventions.
 * Centralizes all magic strings and naming patterns used in the compiler.
 */
public final class Symbols {

    // Property name suffixes
    public static final String PRIVATE_PROPERTY_SUFFIX = "__";

    // Accessor prefixes
    public static final String GETTER_PREFIX = "get ";
    public static final String SETTER_PREFIX = "set ";

    // Special names
    public static final String CONSTRUCTOR_NAME = "constructor";
    public static final String NEW_KEYWORD = "new";
    public static final String THIS_KEYWORD = "this";

    // TypeScript/JavaScript keywords and literals
    public static final String NULL_LITERAL = "null";
    public static final String UNDEFINED_LITERAL = "undefined";
    public static final String TRUE_LITERAL = "true";
    public static final String FALSE_LITERAL = "false";
    public static final String THIS_KEYWORD_FULL = "this";

    // Statement keywords
    public static final String RETURN_KEYWORD = "return";
    public static final String BREAK_KEYWORD = "break";
    public static final String CONTINUE_KEYWORD = "continue";
    public static final String IF_KEYWORD = "if";
    public static final String ELSE_KEYWORD = "else";
    public static final String FOR_KEYWORD = "for";
    public static final String WHILE_KEYWORD = "while";
    public static final String DO_KEYWORD = "do";
    public static final String SWITCH_KEYWORD = "switch";
    public static final String CASE_KEYWORD = "case";
    public static final String DEFAULT_KEYWORD = "default";
    public static final String TRY_KEYWORD = "try";
    public static final String CATCH_KEYWORD = "catch";
    public static final String FINALLY_KEYWORD = "finally";
    public static final String THROW_KEYWORD = "throw";
    public static final String AWAIT_KEYWORD = "await";
    public static final String ASYNC_KEYWORD = "async";
    public static final String FUNCTION_KEYWORD = "function";

    // Variable declaration keywords
    public static final String VAR_KEYWORD = "var";
    public static final String LET_KEYWORD = "let";
    public static final String CONST_KEYWORD = "const";
    public static final String DEFAULT_DECLARATION_KIND = CONST_KEYWORD;

    // Import/Export keywords
    public static final String IMPORT_KEYWORD = "import";
    public static final String EXPORT_KEYWORD = "export";
    public static final String FROM_KEYWORD = "from";

    // Type-related keywords
    public static final String TYPEOF_KEYWORD = "typeof";
    public static final String INSTANCEOF_KEYWORD = "instanceof";

    // Operators
    public static final String SPREAD_OPERATOR = "...";
    public static final String ARROW_OPERATOR = "=>";

    // Brackets and delimiters
    public static final String LEFT_PAREN = "(";
    public static final String RIGHT_PAREN = ")";
    public static final String LEFT_BRACE = "{";
    public static final String RIGHT_BRACE = "}";
    public static final String LEFT_BRACKET = "[";
    public static final String RIGHT_BRACKET = "]";
    public static final String LEFT_ANGLE = "<";
    public static final String RIGHT_ANGLE = ">";
    public static final String SEMICOLON = ";";
    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final String QUESTION_MARK = "?";

    // String quotes
    public static final String SINGLE_QUOTE = "'";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String BACKTICK = "`";

    // Whitespace
    public static final String SPACE = " ";
    public static final String NEWLINE = "\n";
    public static final String CARRIAGE_RETURN = "\r";
    public static final String TAB = "\t";

    // Empty/Default values
    public static final String EMPTY_STRING = "";
    public static final int INDEX_ZERO = 0;
    public static final int EMPTY_STRING_LENGTH = 0;

    // Thread and timeout constants
    public static final int THREAD_TERMINATION_TIMEOUT_SECONDS = 60;
    public static final int MAX_THREAD_MULTIPLIER = 2;

    // File paths and prefixes
    public static final String TYPESCRIPT_PARSER_PATH = "typescript-parser/index.js";
    public static final String TEMP_SOURCE_PREFIX = "ets-source-";
    public static final String TEMP_AST_PREFIX = "ets-ast-";

    // Indentation constants
    public static final String DEFAULT_INDENT = "  ";
    public static final int INDENT_SPACES = 2;

    // Regex patterns
    public static final String DIGITS_REGEX = "\\d+";
    public static final int SPLIT_KEEP_EMPTY_LINES = -1;

    // Converter priority constants
    public static final int NUMERIC_LITERAL_CONVERTER_PRIORITY = 100;

    // Processing constants
    public static final int CHILD_BLOCK_SKIP_COUNT = 2;

    // Generated variable names (should be unique to avoid conflicts)
    public static final String BUILDER_PARAM_NAME = "__builder__";
    public static final String ITEM_GEN_FUNCTION_NAME = "__itemGenFunction__";
    public static final String KEY_GEN_FUNCTION_NAME = "__keyGenFunction__";

    // Comments
    public static final String SINGLE_LINE_COMMENT_PREFIX = "//";
    public static final String MULTI_LINE_COMMENT_START = "/*";
    public static final String MULTI_LINE_COMMENT_END = "*/";

    // File extensions
    public static final String ETS_EXTENSION = ".ets";
    public static final String TS_EXTENSION = ".ts";
    public static final String JS_EXTENSION = ".js";
    public static final String JSON_EXTENSION = ".json";
    public static final String MAP_EXTENSION = ".map";

    private Symbols() {
        // Prevent instantiation
    }

    /**
     * Gets the private property name for a given property name.
     *
     * @param propertyName the public property name
     * @return the private property name with suffix
     */
    public static String privatePropertyName(String propertyName) {
        return propertyName + PRIVATE_PROPERTY_SUFFIX;
    }

    /**
     * Gets the getter method name for a given property name.
     *
     * @param propertyName the property name
     * @return the getter method name
     */
    public static String getterName(String propertyName) {
        return GETTER_PREFIX + propertyName;
    }

    /**
     * Gets the setter method name for a given property name.
     *
     * @param propertyName the property name
     * @return the setter method name
     */
    public static String setterName(String propertyName) {
        return SETTER_PREFIX + propertyName;
    }
}
