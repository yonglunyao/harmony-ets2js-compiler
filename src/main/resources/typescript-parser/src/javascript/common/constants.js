/**
 * Constants for ETS/TypeScript Parser
 * All magic numbers and strings extracted to named constants
 * @module lib/constants
 */

// Resource type ID mapping for $r() function calls
const RESOURCE_TYPE_IDS = {
    'color': 10001,
    'float': 10002,
    'string': 10003,
    'plural': 10004,
    'boolean': 10005,
    'intarray': 10006,
    'integer': 10007,
    'pattern': 10008,
    'strarray': 10009,
    'media': 10010,
    'font': 10011,
    'profile': 10012
};

// Default resource type ID (string type) when type cannot be determined
const DEFAULT_RESOURCE_TYPE_ID = 10003;

// Resource reference function names
const RESOURCE_REF_FUNCTIONS = {
    R: '$r',
    RAWFILE: '$rawfile'
};

// Resource reference type identifiers
const RESOURCE_REF_TYPES = {
    R: 'r',
    RAWFILE: 'rawfile'
};

// Special component names that require JSON objects as arguments
const SPECIAL_COMPONENTS = {
    FOR_EACH: 'ForEach',
    IF: 'If'
};

// Syntax kind names for decorators and modifiers
const SYNTAX_KIND_NAMES = {
    EXPORT_KEYWORD: 'ExportKeyword',
    DECORATOR: 'Decorator',
    OBJECT_BINDING_PATTERN: 'ObjectBindingPattern',
    ARRAY_BINDING_PATTERN: 'ArrayBindingPattern',
    EXTENDS_KEYWORD: 'ExtendsKeyword',
    ASYNC_KEYWORD: 'AsyncKeyword'
};

// Variable declaration kinds
const DECLARATION_KINDS = {
    LET: 'let',
    CONST: 'const',
    VAR: 'var',
    DEFAULT: 'const'
};

// Common keyword strings
const KEYWORDS = {
    STRUCT: 'struct',
    CLASS: 'class',
    EXPORT: 'export',
    IMPORT: 'import',
    ASYNC: 'async',
    FUNCTION: 'function',
    CONSTRUCTOR: 'constructor',
    GET: 'get',
    SET: 'set',
    FOR: 'for',
    WHILE: 'while',
    DO: 'do',
    SWITCH: 'switch',
    CASE: 'case',
    DEFAULT: 'default',
    BREAK: 'break',
    CONTINUE: 'continue',
    RETURN: 'return',
    THROW: 'throw',
    TRY: 'try',
    CATCH: 'catch',
    FINALLY: 'finally',
    IF: 'if',
    ELSE: 'else',
    NEW: 'new',
    TYPEOF: 'typeof',
    AWAIT: 'await',
    DELETE: 'delete',
    EXTENDS: 'extends'
};

// Empty/default value strings
const EMPTY_VALUES = {
    EMPTY_STRING: '',
    UNDEFINED: 'undefined',
    NULL: 'null'
};

// Error messages
const ERROR_MESSAGES = {
    USAGE: 'Usage: node parse-ets.js <source-file> <output-file>',
    PARSE_FAILED: 'Error parsing file:',
    SUCCESS: 'Successfully parsed:'
};

// Regex patterns for ETS preprocessing
const REGEX_PATTERNS = {
    STRUCT_DECORATOR: /(@\w+\s*(?:\([^)]*\))?\s*)\b(?:export\s+)?struct\s+/g,
    INDIVIDUAL_DECORATOR: /@(\w+)(?:\s*\([^)]*\))?/g,
    STRUCT_KEYWORD: /\bstruct\s+/g,
    ENTRY_PATTERN: /@Entry\s*\n(?:@Component\s*\n)?(?:export\s+)?class\s+(\w+)/g,
    DECORATOR_NAME: /@(\w+)/,
    QUOTES: /^['"]|['"]$/g
};

// Operators and punctuation
const OPERATORS = {
    QUESTION_DOT: '?.',
    DOT: '.',
    OPEN_BRACE: '{',
    CLOSE_BRACE: '}',
    OPEN_PAREN: '(',
    CLOSE_PAREN: ')',
    OPEN_BRACKET: '[',
    CLOSE_BRACKET: ']',
    SEMICOLON: ';',
    COMMA: ',',
    COLON: ':',
    SPREAD: '...',
    EQUALS: ' = ',
    ARROW: ' => ',
    BACKTICK: '`',
    DOUBLE_QUOTE: '"'
};

module.exports = {
    RESOURCE_TYPE_IDS,
    DEFAULT_RESOURCE_TYPE_ID,
    RESOURCE_REF_FUNCTIONS,
    RESOURCE_REF_TYPES,
    SPECIAL_COMPONENTS,
    SYNTAX_KIND_NAMES,
    DECLARATION_KINDS,
    KEYWORDS,
    EMPTY_VALUES,
    ERROR_MESSAGES,
    REGEX_PATTERNS,
    OPERATORS
};
