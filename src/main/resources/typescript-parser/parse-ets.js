#!/usr/bin/env node

/**
 * ETS/TypeScript to AST Parser
 *
 * Parses ETS (ArkTS) and TypeScript source code and outputs an AST as JSON.
 * Handles ETS-specific syntax like struct by preprocessing to class.
 *
 * @module parse-ets
 * @author ETS Compiler Team
 * @since 1.0
 */

const ts = require('typescript');
const fs = require('fs');

// =============================================================================
// CONSTANTS - All magic numbers and strings extracted to named constants
// =============================================================================

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

// =============================================================================
// MAIN FUNCTION
// =============================================================================

/**
 * Parse ETS/TypeScript source code and output AST as JSON.
 * Handles ETS-specific syntax like struct by preprocessing.
 */
function main() {
    const args = process.argv.slice(2);
    if (args.length < 2) {
        console.error(ERROR_MESSAGES.USAGE);
        process.exit(1);
    }

    const [sourceFilePath, outputFile] = args;

    try {
        const sourceCode = fs.readFileSync(sourceFilePath, 'utf-8');
        const preprocessResult = preprocessEts(sourceCode);

        const tsSourceFile = ts.createSourceFile(
            sourceFilePath,
            preprocessResult.code,
            ts.ScriptTarget.Latest,
            true,
            ts.ScriptKind.TS
        );

        const astJson = convertAstToJson(tsSourceFile, preprocessResult.decorators);
        fs.writeFileSync(outputFile, JSON.stringify(astJson, null, 2));
        console.error(ERROR_MESSAGES.SUCCESS, sourceFilePath);

    } catch (error) {
        console.error(ERROR_MESSAGES.PARSE_FAILED, error.message);
        console.error(error.stack);
        process.exit(1);
    }
}

// =============================================================================
// ETS PREPROCESSING
// =============================================================================

/**
 * Preprocess ETS code to convert struct to class and extract decorators.
 * @param {string} sourceCode - The source code to preprocess
 * @returns {Object} Object containing processed code and extracted decorators
 */
function preprocessEts(sourceCode) {
    let processedCode = sourceCode;
    const extractedDecorators = extractDecorators(sourceCode);

    processedCode = processedCode.replace(REGEX_PATTERNS.STRUCT_KEYWORD, 'class ');
    processedCode = ensureExportForEntryClasses(processedCode);

    return {
        code: processedCode,
        decorators: extractedDecorators
    };
}

/**
 * Extract decorators from struct declarations.
 * @param {string} sourceCode - The source code to search
 * @returns {Array} Array of extracted decorator objects
 */
function extractDecorators(sourceCode) {
    const extractedDecorators = [];
    let match;

    while ((match = REGEX_PATTERNS.STRUCT_DECORATOR.exec(sourceCode)) !== null) {
        const decoratorText = match[1];
        REGEX_PATTERNS.INDIVIDUAL_DECORATOR.lastIndex = 0;
        let decMatch;

        while ((decMatch = REGEX_PATTERNS.INDIVIDUAL_DECORATOR.exec(decoratorText)) !== null) {
            extractedDecorators.push({
                name: decMatch[1],
                fullText: decMatch[0]
            });
        }
    }

    return extractedDecorators;
}

/**
 * Ensure @Entry decorated classes have export keyword.
 * @param {string} processedCode - The processed source code
 * @returns {string} Code with export added to Entry classes
 */
function ensureExportForEntryClasses(processedCode) {
    let code = processedCode;
    let match;

    REGEX_PATTERNS.ENTRY_PATTERN.lastIndex = 0;
    while ((match = REGEX_PATTERNS.ENTRY_PATTERN.exec(code)) !== null) {
        const classDecl = match[0];
        if (!classDecl.includes(KEYWORDS.EXPORT)) {
            code = code.replace(classDecl, classDecl.replace('class ', 'export class '));
        }
    }

    return code;
}

// =============================================================================
// AST TO JSON CONVERSION - CORE
// =============================================================================

/**
 * Convert TypeScript AST to JSON-serializable format.
 * @param {Object} node - The TypeScript AST node
 * @param {Array} extractedDecorators - Decorators extracted during preprocessing
 * @returns {Object} JSON-serializable AST representation
 */
function convertAstToJson(node, extractedDecorators = []) {
    if (!node) {
        return null;
    }

    const result = {
        kind: node.kind,
        kindName: getSyntaxKindName(node.kind)
    };

    const converter = getAstConverter(node.kind);
    if (converter) {
        converter(result, node, extractedDecorators);
    }

    return result;
}

/**
 * Map of syntax kind to converter function.
 * Each converter processes a specific AST node type.
 */
const AST_CONVERTERS = {
    [ts.SyntaxKind.SourceFile]: convertSourceFile,
    [ts.SyntaxKind.ClassDeclaration]: convertClassDeclaration,
    [ts.SyntaxKind.ClassExpression]: convertClassExpression,
    [ts.SyntaxKind.MethodDeclaration]: convertMethodDeclaration,
    [ts.SyntaxKind.GetAccessor]: convertGetAccessor,
    [ts.SyntaxKind.SetAccessor]: convertSetAccessor,
    [ts.SyntaxKind.FunctionDeclaration]: convertFunctionDeclaration,
    [ts.SyntaxKind.Constructor]: convertConstructor,
    [ts.SyntaxKind.PropertyDeclaration]: convertPropertyDeclaration,
    [ts.SyntaxKind.Decorator]: convertDecorator,
    [ts.SyntaxKind.Block]: convertBlock,
    [ts.SyntaxKind.VariableStatement]: convertVariableStatement,
    [ts.SyntaxKind.VariableDeclarationList]: convertVariableDeclarationList,
    [ts.SyntaxKind.VariableDeclaration]: convertVariableDeclaration,
    [ts.SyntaxKind.ExpressionStatement]: convertExpressionStatement,
    [ts.SyntaxKind.CallExpression]: convertCallExpression,
    [ts.SyntaxKind.Identifier]: convertIdentifier,
    [ts.SyntaxKind.ImportKeyword]: convertImportKeyword,
    [ts.SyntaxKind.PropertyAccessExpression]: convertPropertyAccessExpression,
    [ts.SyntaxKind.NewExpression]: convertNewExpression,
    [ts.SyntaxKind.ElementAccessExpression]: convertElementAccessExpression,
    [ts.SyntaxKind.ParenthesizedExpression]: convertParenthesizedExpression,
    [ts.SyntaxKind.TypeOfExpression]: convertTypeOfExpression,
    [ts.SyntaxKind.PrefixUnaryExpression]: convertPrefixUnaryExpression,
    [ts.SyntaxKind.PostfixUnaryExpression]: convertPostfixUnaryExpression,
    [ts.SyntaxKind.NonNullExpression]: convertNonNullExpression,
    [ts.SyntaxKind.AsExpression]: convertAsExpression,
    [ts.SyntaxKind.TypeAssertion]: convertTypeAssertion,
    [ts.SyntaxKind.ImportExpression]: convertImportExpression,
    [ts.SyntaxKind.InterfaceDeclaration]: convertInterfaceDeclaration,
    [ts.SyntaxKind.ImportDeclaration]: convertImportDeclaration,
    [ts.SyntaxKind.ExportDeclaration]: convertExportDeclaration,
    [ts.SyntaxKind.ArrowFunction]: convertArrowFunction,
    [ts.SyntaxKind.FunctionExpression]: convertFunctionExpression,
    [ts.SyntaxKind.StringLiteral]: convertStringLiteral,
    [ts.SyntaxKind.NumericLiteral]: convertNumericLiteral,
    [ts.SyntaxKind.TemplateExpression]: convertTemplateExpression,
    [ts.SyntaxKind.NoSubstitutionTemplateLiteral]: convertNoSubstitutionTemplateLiteral,
    [ts.SyntaxKind.TrueKeyword]: convertTrueKeyword,
    [ts.SyntaxKind.FalseKeyword]: convertFalseKeyword,
    [ts.SyntaxKind.NullKeyword]: convertNullKeyword,
    [ts.SyntaxKind.UndefinedKeyword]: convertUndefinedKeyword,
    [ts.SyntaxKind.AwaitExpression]: convertAwaitExpression,
    [ts.SyntaxKind.ThrowStatement]: convertThrowStatement,
    [ts.SyntaxKind.ReturnStatement]: convertReturnStatement,
    [ts.SyntaxKind.IfStatement]: convertIfStatement,
    [ts.SyntaxKind.ForOfStatement]: convertForOfStatement,
    [ts.SyntaxKind.ForInStatement]: convertForInStatement,
    [ts.SyntaxKind.WhileStatement]: convertWhileStatement,
    [ts.SyntaxKind.DoStatement]: convertDoStatement,
    [ts.SyntaxKind.ForStatement]: convertForStatement,
    [ts.SyntaxKind.SwitchStatement]: convertSwitchStatement,
    [ts.SyntaxKind.CaseBlock]: convertCaseBlock,
    [ts.SyntaxKind.CaseClause]: convertCaseClause,
    [ts.SyntaxKind.DefaultClause]: convertDefaultClause,
    [ts.SyntaxKind.TryStatement]: convertTryStatement,
    [ts.SyntaxKind.CatchClause]: convertCatchClause,
    [ts.SyntaxKind.BreakStatement]: convertBreakStatement,
    [ts.SyntaxKind.ContinueStatement]: convertContinueStatement,
    [ts.SyntaxKind.BinaryExpression]: convertBinaryExpression,
    [ts.SyntaxKind.ConditionalExpression]: convertConditionalExpression,
    [ts.SyntaxKind.ArrayLiteralExpression]: convertArrayLiteralExpression,
    [ts.SyntaxKind.SpreadElement]: convertSpreadElement,
    [ts.SyntaxKind.ObjectLiteralExpression]: convertObjectLiteralExpression,
    [ts.SyntaxKind.PropertyAssignment]: convertPropertyAssignment,
    [ts.SyntaxKind.ShorthandPropertyAssignment]: convertShorthandPropertyAssignment,
    [ts.SyntaxKind.SpreadAssignment]: convertSpreadAssignment,
    [ts.SyntaxKind.ObjectBindingPattern]: convertObjectBindingPattern,
    [ts.SyntaxKind.ArrayBindingPattern]: convertArrayBindingPattern,
    [ts.SyntaxKind.BindingElement]: convertBindingElement,
    [ts.SyntaxKind.DeleteExpression]: convertDeleteExpression
};

/**
 * Get the appropriate converter function for a syntax kind.
 * @param {number} kind - The TypeScript syntax kind
 * @returns {Function|null} The converter function or null
 */
function getAstConverter(kind) {
    return AST_CONVERTERS[kind] || null;
}

// =============================================================================
// NODE TYPE CONVERTERS
// =============================================================================

// Source File
function convertSourceFile(result, node, extractedDecorators) {
    result.fileName = node.fileName;
    result.statements = convertStatements(node.statements, extractedDecorators);
}

// Statements conversion helper
function convertStatements(statements, extractedDecorators) {
    const result = [];
    for (const stmt of statements) {
        const stmtJson = convertAstToJson(stmt, extractedDecorators);
        if (stmtJson) {
            result.push(stmtJson);
        }
    }
    return result;
}

// Class Declaration
function convertClassDeclaration(result, node, extractedDecorators) {
    result.name = node.name?.escapedText || '';
    result.isExport = hasExportModifier(node.modifiers);
    result.decorators = combineExtractedAndInlineDecorators(node.decorators, extractedDecorators);
    result.members = convertStatements(node.members);
    result.heritageClauses = convertHeritageClauses(node.heritageClauses);
}

// Class Expression
function convertClassExpression(result, node, extractedDecorators) {
    result.name = node.name?.escapedText || '';
    result.decorators = convertDecorators(node.decorators);
    result.members = convertStatements(node.members);
    result.heritageClauses = convertHeritageClauses(node.heritageClauses);
    result.text = generateClassExpressionText(node);
}

// Method Declaration
function convertMethodDeclaration(result, node, extractedDecorators) {
    result.name = node.name?.escapedText || '';
    result.decorators = convertDecoratorsWithModifierCheck(node.decorators, node.modifiers);
    result.modifiers = convertModifiers(node.modifiers);
    result.parameters = convertParameters(node.parameters);
    result.body = node.body ? convertAstToJson(node.body) : null;
    result.asteriskToken = !!node.asteriskToken;
}

// Get Accessor
function convertGetAccessor(result, node) {
    result.name = node.name?.escapedText || '';
    result.body = node.body ? convertAstToJson(node.body) : null;
    result.text = generateGetAccessorText(node);
}

// Set Accessor
function convertSetAccessor(result, node) {
    result.name = node.name?.escapedText || '';
    result.parameters = convertParametersSimple(node.parameters);
    result.body = node.body ? convertAstToJson(node.body) : null;
    result.text = generateSetAccessorText(node);
}

// Function Declaration
function convertFunctionDeclaration(result, node) {
    result.name = node.name?.escapedText || '';
    result.modifiers = convertModifiers(node.modifiers);
    result.parameters = convertParameters(node.parameters);
    result.body = node.body ? convertAstToJson(node.body) : null;
    result.asteriskToken = !!node.asteriskToken;
}

// Constructor
function convertConstructor(result, node) {
    result.name = KEYWORDS.CONSTRUCTOR;
    result.parameters = convertParameters(node.parameters);
    result.body = node.body ? convertAstToJson(node.body) : null;
}

// Property Declaration
function convertPropertyDeclaration(result, node) {
    result.name = node.name?.escapedText || '';
    result.type = node.type?.getText() || '';
    result.initializer = node.initializer ? convertAstToJson(node.initializer) : null;

    if (node.initializer) {
        const initJson = result.initializer;
        result.initializerText = initJson?.text || node.initializer?.getText() || '';
    }

    result.decorators = convertDecoratorsWithModifierCheck(node.decorators, node.modifiers);
    result.modifiers = convertModifiers(node.modifiers);
}

// Decorator
function convertDecorator(result, node) {
    const expression = node.expression;
    if (ts.isIdentifier(expression)) {
        result.name = expression.escapedText;
    } else if (ts.isCallExpression(expression)) {
        result.name = expression.expression.escapedText;
        result.arguments = [];
        for (const arg of expression.arguments || []) {
            result.arguments.push(arg.getText());
        }
    }
}

// Block
function convertBlock(result, node) {
    result.statements = convertStatements(node.statements);
}

// Variable Statement
function convertVariableStatement(result, node) {
    result.declarationList = convertAstToJson(node.declarationList);
}

// Variable Declaration List
function convertVariableDeclarationList(result, node) {
    result.declarations = [];
    result.declarationKind = getDeclarationKind(node.flags);
    for (const decl of node.declarations || []) {
        const declJson = convertAstToJson(decl);
        if (declJson) {
            result.declarations.push(declJson);
        }
    }
}

// Variable Declaration
function convertVariableDeclaration(result, node) {
    result.name = node.name?.getText() || '';
    result.type = node.type?.getText() || '';
    result.initializer = node.initializer ? convertAstToJson(node.initializer) : null;
}

// Expression Statement
function convertExpressionStatement(result, node) {
    result.expression = convertAstToJson(node.expression);
}

// Call Expression
function convertCallExpression(result, node, extractedDecorators) {
    result.expression = convertAstToJson(node.expression);
    result.arguments = convertCallArguments(node, result.expression);
    result.isChainedCall = isChainedCall(result.expression);
    result.componentName = extractComponentName(result.expression);
    result.methodName = extractMethodName(result.expression);
    result.isSpecialComponent = isSpecialComponent(result.expression);
}

// Identifier
function convertIdentifier(result, node) {
    result.name = node.escapedText;
    result.text = node.getText();

    const isResourceFunction = result.name === RESOURCE_REF_FUNCTIONS.R ||
                           result.name === RESOURCE_REF_FUNCTIONS.RAWFILE;
    if (isResourceFunction) {
        result.text = result.name;
    }
}

// Import Keyword
function convertImportKeyword(result) {
    result.name = KEYWORDS.IMPORT;
    result.text = KEYWORDS.IMPORT;
}

// Property Access Expression
function convertPropertyAccessExpression(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.name = node.name.escapedText;
    result.questionDotToken = !!node.questionDotToken;
    result.text = generatePropertyAccessExpressionText(node);
}

// New Expression
function convertNewExpression(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.arguments = [];
    for (const arg of node.arguments || []) {
        result.arguments.push(convertAstToJson(arg));
    }
}

// Element Access Expression
function convertElementAccessExpression(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.argumentExpression = convertAstToJson(node.argumentExpression);
    result.questionDotToken = !!node.questionDotToken;
}

// Parenthesized Expression
function convertParenthesizedExpression(result, node) {
    result.expression = convertAstToJson(node.expression);
}

// Type Of Expression
function convertTypeOfExpression(result, node) {
    result.expression = convertAstToJson(node.expression);
}

// Prefix Unary Expression
function convertPrefixUnaryExpression(result, node) {
    result.operator = ts.tokenToString(node.operator);
    result.operand = convertAstToJson(node.operand);
}

// Postfix Unary Expression
function convertPostfixUnaryExpression(result, node) {
    result.operator = ts.tokenToString(node.operator);
    result.operand = convertAstToJson(node.operand);
}

// Non Null Expression
function convertNonNullExpression(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.text = generateNonNullExpressionText(node);
}

// As Expression
function convertAsExpression(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.type = node.type ? node.type.getText() : null;
    result.text = generateAsExpressionText(node);
}

// Type Assertion
function convertTypeAssertion(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.type = node.type ? node.type.getText() : null;
    result.text = generateAsExpressionText(node);
}

// Import Expression
function convertImportExpression(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.text = generateImportExpressionText(node);
}

// Interface Declaration
function convertInterfaceDeclaration(result) {
    result.name = node.name?.escapedText || '';
    result.isTypeOnly = true;
}

// Import Declaration
function convertImportDeclaration(result, node) {
    result.moduleSpecifier = node.moduleSpecifier.getText();
    result.importClause = convertImportClause(node.importClause);
}

// Export Declaration
function convertExportDeclaration(result, node) {
    result.isTypeOnly = node.isTypeOnly || false;
    result.exportClause = convertExportClause(node.exportClause);
    result.moduleSpecifier = node.moduleSpecifier ? node.moduleSpecifier.getText() : null;
}

// Arrow Function
function convertArrowFunction(result, node) {
    result.name = node.name?.escapedText || node.name?.text || '';
    result.parameters = convertParameters(node.parameters);
    result.body = convertAstToJson(node.body);
    result.text = generateArrowFunctionText(node);
}

// Function Expression
function convertFunctionExpression(result, node) {
    result.name = node.name?.escapedText || node.name?.text || '';
    result.parameters = convertParameters(node.parameters);
    result.body = convertAstToJson(node.body);
    result.text = generateFunctionExpressionText(node);
}

// String Literal
function convertStringLiteral(result, node) {
    result.text = OPERATORS.DOUBLE_QUOTE + node.text + OPERATORS.DOUBLE_QUOTE;
}

// Numeric Literal
function convertNumericLiteral(result, node) {
    result.text = node.text;
    result.value = node.numericLiteralValue;
}

// Template Expression
function convertTemplateExpression(result, node) {
    result.head = { text: node.head.text };
    result.templateSpans = [];
    for (const span of node.templateSpans) {
        result.templateSpans.push({
            expression: convertAstToJson(span.expression),
            literal: { text: span.literal.text }
        });
    }
    result.text = generateTemplateExpressionText(node);
}

// No Substitution Template Literal
function convertNoSubstitutionTemplateLiteral(result, node) {
    result.text = OPERATORS.BACKTICK + node.text + OPERATORS.BACKTICK;
}

// True Keyword
function convertTrueKeyword(result) {
    result.text = 'true';
}

// False Keyword
function convertFalseKeyword(result) {
    result.text = 'false';
}

// Null Keyword
function convertNullKeyword(result) {
    result.text = 'null';
}

// Undefined Keyword
function convertUndefinedKeyword(result) {
    result.text = 'undefined';
}

// Await Expression
function convertAwaitExpression(result, node) {
    result.expression = convertAstToJson(node.expression);
}

// Throw Statement
function convertThrowStatement(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.text = generateThrowStatementText(node);
}

// Return Statement
function convertReturnStatement(result, node) {
    result.expression = convertAstToJson(node.expression);
}

// If Statement
function convertIfStatement(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.thenStatement = convertAstToJson(node.thenStatement);
    result.elseStatement = node.elseStatement ? convertAstToJson(node.elseStatement) : null;
}

// For Of Statement
function convertForOfStatement(result, node) {
    result.initializer = convertAstToJson(node.initializer);
    result.expression = convertAstToJson(node.expression);
    result.statement = convertAstToJson(node.statement);
    result.awaitModifier = !!node.awaitModifier;
    result.text = generateForOfStatementText(node);
}

// For In Statement
function convertForInStatement(result, node) {
    result.initializer = convertAstToJson(node.initializer);
    result.expression = convertAstToJson(node.expression);
    result.statement = convertAstToJson(node.statement);
    result.text = generateForInStatementText(node);
}

// While Statement
function convertWhileStatement(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.statement = convertAstToJson(node.statement);
    result.text = generateWhileStatementText(node);
}

// Do Statement
function convertDoStatement(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.statement = convertAstToJson(node.statement);
    result.text = generateDoStatementText(node);
}

// For Statement
function convertForStatement(result, node) {
    result.initializer = node.initializer ? convertAstToJson(node.initializer) : null;
    result.condition = node.condition ? convertAstToJson(node.condition) : null;
    result.incrementor = node.incrementor ? convertAstToJson(node.incrementor) : null;
    result.statement = convertAstToJson(node.statement);
    result.text = generateForStatementText(node);
}

// Switch Statement
function convertSwitchStatement(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.caseBlock = convertAstToJson(node.caseBlock);
    result.text = generateSwitchStatementText(node);
}

// Case Block
function convertCaseBlock(result, node) {
    result.clauses = [];
    for (const clause of node.clauses || []) {
        result.clauses.push(convertAstToJson(clause));
    }
}

// Case Clause
function convertCaseClause(result, node) {
    result.expression = convertAstToJson(node.expression);
    result.statements = convertStatements(node.statements);
}

// Default Clause
function convertDefaultClause(result, node) {
    result.statements = convertStatements(node.statements);
}

// Try Statement
function convertTryStatement(result, node) {
    result.tryBlock = convertAstToJson(node.tryBlock);
    result.catchClause = node.catchClause ? convertAstToJson(node.catchClause) : null;
    result.finallyBlock = node.finallyBlock ? convertAstToJson(node.finallyBlock) : null;
    result.text = generateTryStatementText(node);
}

// Catch Clause
function convertCatchClause(result, node) {
    result.variableDeclaration = node.variableDeclaration ? convertAstToJson(node.variableDeclaration) : null;
    result.block = convertAstToJson(node.block);
}

// Break Statement
function convertBreakStatement(result, node) {
    result.label = node.label ? node.label.getText() : null;
}

// Continue Statement
function convertContinueStatement(result, node) {
    result.label = node.label ? node.label.getText() : null;
}

// Binary Expression
function convertBinaryExpression(result, node) {
    result.left = convertAstToJson(node.left);
    result.operator = node.operatorToken.getText();
    result.right = convertAstToJson(node.right);
}

// Conditional Expression
function convertConditionalExpression(result, node) {
    result.condition = convertAstToJson(node.condition);
    result.whenTrue = convertAstToJson(node.whenTrue);
    result.whenFalse = convertAstToJson(node.whenFalse);
}

// Array Literal Expression
function convertArrayLiteralExpression(result, node) {
    result.elements = [];
    for (const elem of node.elements || []) {
        result.elements.push(convertAstToJson(elem));
    }
}

// Spread Element
function convertSpreadElement(result, node) {
    result.expression = convertAstToJson(node.expression);
}

// Object Literal Expression
function convertObjectLiteralExpression(result, node) {
    result.properties = [];
    for (const prop of node.properties || []) {
        result.properties.push(convertAstToJson(prop));
    }
    result.text = generateObjectLiteralExpressionText(node);
}

// Property Assignment
function convertPropertyAssignment(result, node) {
    result.name = node.name.getText();
    result.value = convertAstToJson(node.initializer);
}

// Shorthand Property Assignment
function convertShorthandPropertyAssignment(result, node) {
    result.name = node.name.getText();
    result.value = {
        kindName: 'Identifier',
        name: node.name.getText(),
        text: node.name.getText()
    };
}

// Spread Assignment
function convertSpreadAssignment(result, node) {
    result.expression = convertAstToJson(node.expression);
}

// Object Binding Pattern
function convertObjectBindingPattern(result, node) {
    result.elements = [];
    for (const elem of node.elements || []) {
        result.elements.push(convertAstToJson(elem));
    }
}

// Array Binding Pattern
function convertArrayBindingPattern(result, node) {
    result.elements = [];
    for (const elem of node.elements || []) {
        result.elements.push(convertAstToJson(elem));
    }
}

// Binding Element
function convertBindingElement(result, node) {
    result.name = node.name?.getText() || '';
    if (node.propertyName) {
        result.propertyName = node.propertyName.getText();
    }
    if (node.initializer) {
        result.initializer = convertAstToJson(node.initializer);
    }
    if (node.dotDotDotToken) {
        result.dotDotDotToken = true;
    }
}

// Delete Expression
function convertDeleteExpression(result, node) {
    result.expression = convertAstToJson(node.expression);
}

// =============================================================================
// HELPER FUNCTIONS - MODIFIERS, PARAMETERS, DECORATORS
// =============================================================================

/**
 * Check if a node has export modifier.
 * @param {Array} modifiers - Array of modifiers
 * @returns {boolean} True if has export modifier
 */
function hasExportModifier(modifiers) {
    if (!modifiers) return false;
    return modifiers.some(mod =>
        getSyntaxKindName(mod.kind) === SYNTAX_KIND_NAMES.EXPORT_KEYWORD
    );
}

/**
 * Convert heritage clauses (extends, implements).
 * @param {Array} heritageClauses - Array of heritage clauses
 * @returns {Array} Converted heritage clauses
 */
function convertHeritageClauses(heritageClauses) {
    if (!heritageClauses || heritageClauses.length === 0) {
        return null;
    }

    const result = [];
    for (const clause of heritageClauses) {
        const clauseJson = {
            token: getSyntaxKindName(clause.token) || '',
            types: []
        };
        if (clause.types) {
            for (const type of clause.types) {
                clauseJson.types.push(type.expression.getText());
            }
        }
        result.push(clauseJson);
    }
    return result;
}

/**
 * Convert decorators array to JSON.
 * @param {Array} decorators - Array of decorator nodes
 * @returns {Array} Converted decorators
 */
function convertDecorators(decorators) {
    const result = [];
    for (const dec of decorators || []) {
        const decJson = convertAstToJson(dec);
        if (decJson) {
            result.push(decJson);
        }
    }
    return result;
}

/**
 * Convert decorators with modifier check for ETS workaround.
 * @param {Array} decorators - Array of decorator nodes
 * @param {Array} modifiers - Array of modifiers
 * @returns {Array} Converted decorators
 */
function convertDecoratorsWithModifierCheck(decorators, modifiers) {
    const result = convertDecorators(decorators);

    // ETS workaround: check if modifiers contain decorators
    if (modifiers && result.length === 0) {
        for (const mod of modifiers) {
            const modKindName = getSyntaxKindName(mod.kind);
            if (modKindName === SYNTAX_KIND_NAMES.DECORATOR) {
                const decoratorName = extractDecoratorNameFromModifier(mod);
                if (decoratorName) {
                    result.push({ name: decoratorName });
                }
            }
        }
    }

    return result;
}

/**
 * Combine extracted decorators with inline decorators.
 * @param {Array} decorators - Array of inline decorator nodes
 * @param {Array} extractedDecorators - Array of extracted decorators from preprocessing
 * @returns {Array} Combined decorators
 */
function combineExtractedAndInlineDecorators(decorators, extractedDecorators) {
    const result = [];

    // Add extracted decorators (for @Component on struct)
    for (const dec of extractedDecorators || []) {
        result.push({
            name: dec.name
        });
    }

    // Add inline decorators
    for (const dec of decorators || []) {
        const decJson = convertAstToJson(dec);
        if (decJson) {
            result.push(decJson);
        }
    }

    return result;
}

/**
 * Extract decorator name from modifier text.
 * @param {Object} mod - Modifier node
 * @returns {string|null} Decorator name or null
 */
function extractDecoratorNameFromModifier(mod) {
    try {
        const modText = mod.getText();
        const decoratorMatch = modText.match(REGEX_PATTERNS.DECORATOR_NAME);
        return decoratorMatch ? decoratorMatch[1] : null;
    } catch (e) {
        return null;
    }
}

/**
 * Convert modifiers array to JSON.
 * @param {Array} modifiers - Array of modifier nodes
 * @returns {Array} Converted modifiers
 */
function convertModifiers(modifiers) {
    const result = [];
    for (const mod of modifiers || []) {
        result.push({
            kind: mod.kind,
            kindName: getSyntaxKindName(mod.kind)
        });
    }
    return result;
}

/**
 * Convert parameters array to JSON with binding pattern support.
 * @param {Array} parameters - Array of parameter nodes
 * @returns {Array} Converted parameters
 */
function convertParameters(parameters) {
    const result = [];
    for (const param of parameters || []) {
        const paramJson = {
            name: param.name?.escapedText || '',
            type: param.type?.getText() || '',
            hasDotDotDot: !!param.dotDotDotToken,
            questionToken: !!param.questionToken
        };

        if (param.name?.kind) {
            const bindingKind = getSyntaxKindName(param.name.kind);
            if (isBindingPattern(bindingKind)) {
                paramJson.bindingPattern = convertAstToJson(param.name);
                paramJson.name = param.name.getText() || '';
            }
        }

        if (param.initializer) {
            paramJson.initializer = convertAstToJson(param.initializer);
            if (paramJson.initializer?.text) {
                paramJson.initializerText = paramJson.initializer.text;
            } else {
                paramJson.initializerText = param.initializer?.getText() || '';
            }
        }

        result.push(paramJson);
    }
    return result;
}

/**
 * Convert parameters array to JSON (simple version without binding pattern).
 * @param {Array} parameters - Array of parameter nodes
 * @returns {Array} Converted parameters
 */
function convertParametersSimple(parameters) {
    const result = [];
    for (const param of parameters || []) {
        result.push({
            name: param.name?.escapedText || '',
            type: param.type?.getText() || ''
        });
    }
    return result;
}

/**
 * Check if kind name is a binding pattern.
 * @param {string} kindName - Syntax kind name
 * @returns {boolean} True if binding pattern
 */
function isBindingPattern(kindName) {
    return kindName === SYNTAX_KIND_NAMES.OBJECT_BINDING_PATTERN ||
           kindName === SYNTAX_KIND_NAMES.ARRAY_BINDING_PATTERN;
}

/**
 * Get declaration kind from flags.
 * @param {number} flags - Node flags
 * @returns {string} Declaration kind (let, const, var)
 */
function getDeclarationKind(flags) {
    if (flags & ts.NodeFlags.Let) {
        return DECLARATION_KINDS.LET;
    }
    if (flags & ts.NodeFlags.Const) {
        return DECLARATION_KINDS.CONST;
    }
    return DECLARATION_KINDS.VAR;
}

/**
 * Convert import clause to JSON.
 * @param {Object} importClause - Import clause node
 * @returns {Object} Converted import clause
 */
function convertImportClause(importClause) {
    if (!importClause) return null;

    const result = {
        name: importClause.name ? importClause.name.getText() : null,
        namedBindings: []
    };

    if (importClause.namedBindings) {
        if (importClause.namedBindings.kind === ts.SyntaxKind.NamedImports) {
            for (const element of importClause.namedBindings.elements) {
                result.namedBindings.push({
                    name: element.name.getText(),
                    propertyName: element.propertyName ? element.propertyName.getText() : null
                });
            }
        } else if (importClause.namedBindings.kind === ts.SyntaxKind.NamespaceImport) {
            result.namedBindings.push({
                kind: 'namespace',
                name: importClause.namedBindings.name.getText()
            });
        }
    }

    return result;
}

/**
 * Convert export clause to JSON.
 * @param {Object} exportClause - Export clause node
 * @returns {Object} Converted export clause
 */
function convertExportClause(exportClause) {
    if (!exportClause) return null;

    const result = { elements: [] };

    if (exportClause.kind === ts.SyntaxKind.NamedExports) {
        for (const element of exportClause.elements) {
            result.elements.push({
                name: element.name.getText(),
                propertyName: element.propertyName ? element.propertyName.getText() : null
            });
        }
    }

    return result;
}

// =============================================================================
// CALL EXPRESSION ARGUMENT PROCESSING
// =============================================================================

/**
 * Convert call expression arguments.
 * @param {Object} node - Call expression node
 * @param {Object} expressionJson - Converted expression
 * @returns {Array} Converted arguments
 */
function convertCallArguments(node, expressionJson) {
    const isResourceCall = isResourceReferenceCall(expressionJson);
    const isForEach = isForEachCall(expressionJson);

    const result = [];
    for (const arg of node.arguments || []) {
        if (isForEach) {
            result.push(convertAstToJson(arg));
        } else if (isResourceCall) {
            result.push(arg.getText());
        } else {
            result.push(convertCallArgument(arg));
        }
    }
    return result;
}

/**
 * Convert a single call argument.
 * @param {Object} arg - Argument node
 * @returns {string|Object} Converted argument
 */
function convertCallArgument(arg) {
    const argJson = convertAstToJson(arg);

    const isComplexExpression = argJson?.kindName &&
        (argJson.kindName === 'ObjectLiteralExpression' ||
         argJson.kindName === 'ArrayLiteralExpression' ||
         argJson.kindName === 'ArrowFunction');

    if (isComplexExpression) {
        return argJson;
    }

    if (argJson?.text) {
        if (argJson.kindName === 'StringLiteral') {
            const needsQuotes = !argJson.text.startsWith('"') && !argJson.text.startsWith("'");
            return needsQuotes ? OPERATORS.DOUBLE_QUOTE + argJson.text + OPERATORS.DOUBLE_QUOTE : argJson.text;
        }
        return argJson.text;
    }

    try {
        return arg.getText();
    } catch (e) {
        return JSON.stringify(argJson);
    }
}

/**
 * Check if expression is a resource reference call ($r or $rawfile).
 * @param {Object} expressionJson - Converted expression
 * @returns {boolean} True if resource reference call
 */
function isResourceReferenceCall(expressionJson) {
    return expressionJson?.kindName === 'Identifier' &&
           (expressionJson.name === RESOURCE_REF_FUNCTIONS.R ||
            expressionJson.name === RESOURCE_REF_FUNCTIONS.RAWFILE);
}

/**
 * Check if expression is a ForEach call.
 * @param {Object} expressionJson - Converted expression
 * @returns {boolean} True if ForEach call
 */
function isForEachCall(expressionJson) {
    return expressionJson?.kindName === 'Identifier' &&
           expressionJson.name === SPECIAL_COMPONENTS.FOR_EACH;
}

/**
 * Check if expression is a chained call.
 * @param {Object} expressionJson - Converted expression
 * @returns {boolean} True if chained call
 */
function isChainedCall(expressionJson) {
    return expressionJson?.kindName === 'PropertyAccessExpression' &&
           expressionJson.expression?.kindName === 'CallExpression';
}

/**
 * Extract component name from expression.
 * @param {Object} expressionJson - Converted expression
 * @returns {string|null} Component name or null
 */
function extractComponentName(expressionJson) {
    if (expressionJson?.expression?.expression?.kindName === 'Identifier') {
        return expressionJson.expression.expression.name;
    }
    return null;
}

/**
 * Extract method name from expression.
 * @param {Object} expressionJson - Converted expression
 * @returns {string|null} Method name or null
 */
function extractMethodName(expressionJson) {
    return expressionJson?.name || null;
}

/**
 * Check if expression is a special component (ForEach or If).
 * @param {Object} expressionJson - Converted expression
 * @returns {boolean} True if special component
 */
function isSpecialComponent(expressionJson) {
    return expressionJson?.kindName === 'Identifier' &&
           (expressionJson.name === SPECIAL_COMPONENTS.FOR_EACH ||
            expressionJson.name === SPECIAL_COMPONENTS.IF);
}

// =============================================================================
// SYNTAX KIND NAME MAPPING
// =============================================================================

/**
 * Get syntax kind name from kind number.
 * @param {number} kind - TypeScript syntax kind
 * @returns {string} Syntax kind name
 */
function getSyntaxKindName(kind) {
    const kindName = ts.SyntaxKind[kind];
    if (kindName) {
        return kindName;
    }

    const kindMap = {
        [ts.SyntaxKind.SourceFile]: 'SourceFile',
        [ts.SyntaxKind.ClassDeclaration]: 'ClassDeclaration',
        [ts.SyntaxKind.ClassExpression]: 'ClassExpression',
        [ts.SyntaxKind.MethodDeclaration]: 'MethodDeclaration',
        [ts.SyntaxKind.GetAccessor]: 'GetAccessor',
        [ts.SyntaxKind.SetAccessor]: 'SetAccessor',
        [ts.SyntaxKind.PropertyDeclaration]: 'PropertyDeclaration',
        [ts.SyntaxKind.Decorator]: 'Decorator',
        [ts.SyntaxKind.Block]: 'Block',
        [ts.SyntaxKind.ExpressionStatement]: 'ExpressionStatement',
        [ts.SyntaxKind.CallExpression]: 'CallExpression',
        [ts.SyntaxKind.Identifier]: 'Identifier',
        [ts.SyntaxKind.ImportKeyword]: 'ImportKeyword',
        [ts.SyntaxKind.PropertyAccessExpression]: 'PropertyAccessExpression',
        [ts.SyntaxKind.ArrowFunction]: 'ArrowFunction',
        [ts.SyntaxKind.StringLiteral]: 'StringLiteral',
        [ts.SyntaxKind.NumericLiteral]: 'NumericLiteral',
        [ts.SyntaxKind.ReturnStatement]: 'ReturnStatement',
        [ts.SyntaxKind.IfStatement]: 'IfStatement',
        [ts.SyntaxKind.ImportDeclaration]: 'ImportDeclaration',
        [ts.SyntaxKind.ExportDeclaration]: 'ExportDeclaration',
        [ts.SyntaxKind.BinaryExpression]: 'BinaryExpression',
        [ts.SyntaxKind.ConditionalExpression]: 'ConditionalExpression',
        [ts.SyntaxKind.ArrayLiteralExpression]: 'ArrayLiteralExpression',
        [ts.SyntaxKind.ObjectLiteralExpression]: 'ObjectLiteralExpression',
        [ts.SyntaxKind.PropertyAssignment]: 'PropertyAssignment',
        [ts.SyntaxKind.ShorthandPropertyAssignment]: 'ShorthandPropertyAssignment',
        [ts.SyntaxKind.TrueKeyword]: 'TrueLiteral',
        [ts.SyntaxKind.FalseKeyword]: 'FalseLiteral',
        [ts.SyntaxKind.NullKeyword]: 'NullLiteral',
        [ts.SyntaxKind.UndefinedKeyword]: 'UndefinedLiteral',
        [ts.SyntaxKind.VariableStatement]: 'VariableStatement',
        [ts.SyntaxKind.VariableDeclarationList]: 'VariableDeclarationList',
        [ts.SyntaxKind.VariableDeclaration]: 'VariableDeclaration',
        [ts.SyntaxKind.FirstStatement]: 'FirstStatement',
        [ts.SyntaxKind.NewExpression]: 'NewExpression',
        [ts.SyntaxKind.ElementAccessExpression]: 'ElementAccessExpression',
        [ts.SyntaxKind.ParenthesizedExpression]: 'ParenthesizedExpression',
        [ts.SyntaxKind.AsExpression]: 'AsExpression',
        [ts.SyntaxKind.TypeOfExpression]: 'TypeOfExpression',
        [ts.SyntaxKind.PrefixUnaryExpression]: 'PrefixUnaryExpression',
        [ts.SyntaxKind.PostfixUnaryExpression]: 'PostfixUnaryExpression',
        [ts.SyntaxKind.NonNullExpression]: 'NonNullExpression',
        [ts.SyntaxKind.TypeAssertion]: 'TypeAssertion',
        [ts.SyntaxKind.ForOfStatement]: 'ForOfStatement',
        [ts.SyntaxKind.ForInStatement]: 'ForInStatement',
        [ts.SyntaxKind.WhileStatement]: 'WhileStatement',
        [ts.SyntaxKind.DoStatement]: 'DoStatement',
        [ts.SyntaxKind.ForStatement]: 'ForStatement',
        [ts.SyntaxKind.SwitchStatement]: 'SwitchStatement',
        [ts.SyntaxKind.CaseBlock]: 'CaseBlock',
        [ts.SyntaxKind.CaseClause]: 'CaseClause',
        [ts.SyntaxKind.DefaultClause]: 'DefaultClause',
        [ts.SyntaxKind.TryStatement]: 'TryStatement',
        [ts.SyntaxKind.CatchClause]: 'CatchClause',
        [ts.SyntaxKind.BreakStatement]: 'BreakStatement',
        [ts.SyntaxKind.ContinueStatement]: 'ContinueStatement',
        [ts.SyntaxKind.SpreadElement]: 'SpreadElement',
        [ts.SyntaxKind.SpreadAssignment]: 'SpreadAssignment',
        [ts.SyntaxKind.DeleteExpression]: 'DeleteExpression',
        [ts.SyntaxKind.ObjectBindingPattern]: 'ObjectBindingPattern',
        [ts.SyntaxKind.ArrayBindingPattern]: 'ArrayBindingPattern',
        [ts.SyntaxKind.BindingElement]: 'BindingElement',
        [ts.SyntaxKind.TemplateExpression]: 'TemplateExpression',
        [ts.SyntaxKind.NoSubstitutionTemplateLiteral]: 'NoSubstitutionTemplateLiteral',
        [ts.SyntaxKind.ImportExpression]: 'ImportExpression',
        [ts.SyntaxKind.InterfaceDeclaration]: 'InterfaceDeclaration',
        [ts.SyntaxKind.FunctionDeclaration]: 'FunctionDeclaration',
        [ts.SyntaxKind.FunctionExpression]: 'FunctionExpression'
    };

    return kindMap[kind] || `Unknown_${kind}`;
}

// =============================================================================
// TEXT GENERATION FUNCTIONS
// =============================================================================

/**
 * Generate text representation for for...of statement.
 * @param {Object} node - ForOfStatement node
 * @returns {string} Generated text
 */
function generateForOfStatementText(node) {
    const initStr = generateInitializerText(node.initializer);
    const exprStr = node.expression ? jsonToCodeString(convertAstToJson(node.expression)) : '';
    const stmtStr = node.statement ? jsonToCodeString(convertAstToJson(node.statement)) : '';
    const awaitModifier = node.awaitModifier ? KEYWORDS.AWAIT + ' ' : '';

    const result = `${KEYWORDS.FOR} ${awaitModifier}(${initStr} of ${exprStr}) {${stmtStr}}`;
    return result;
}

/**
 * Generate text representation for for...in statement.
 * @param {Object} node - ForInStatement node
 * @returns {string} Generated text
 */
function generateForInStatementText(node) {
    const initStr = generateInitializerText(node.initializer);
    const exprStr = node.expression ? jsonToCodeString(convertAstToJson(node.expression)) : '';
    const stmtStr = node.statement ? jsonToCodeString(convertAstToJson(node.statement)) : '';

    return `${KEYWORDS.FOR} (${initStr} in ${exprStr}) {${stmtStr}}`;
}

/**
 * Generate text representation for while statement.
 * @param {Object} node - WhileStatement node
 * @returns {string} Generated text
 */
function generateWhileStatementText(node) {
    const condStr = node.expression ? jsonToCodeString(convertAstToJson(node.expression)) : '';
    const stmtStr = node.statement ? jsonToCodeString(convertAstToJson(node.statement)) : '';

    return `${KEYWORDS.WHILE} (${condStr}) {${stmtStr}}`;
}

/**
 * Generate text representation for do...while statement.
 * @param {Object} node - DoStatement node
 * @returns {string} Generated text
 */
function generateDoStatementText(node) {
    const stmtStr = node.statement ? jsonToCodeString(convertAstToJson(node.statement)) : '';
    const condStr = node.expression ? jsonToCodeString(convertAstToJson(node.expression)) : '';

    return `${KEYWORDS.DO} {${stmtStr}} ${KEYWORDS.WHILE} (${condStr})`;
}

/**
 * Generate text representation for for statement.
 * @param {Object} node - ForStatement node
 * @returns {string} Generated text
 */
function generateForStatementText(node) {
    const initStr = generateForInitializerText(node.initializer);
    const condStr = node.condition ? jsonToCodeString(convertAstToJson(node.condition)) : '';
    const incrStr = node.incrementor ? jsonToCodeString(convertAstToJson(node.incrementor)) : '';
    const stmtStr = node.statement ? jsonToCodeString(convertAstToJson(node.statement)) : '';

    return `${KEYWORDS.FOR} (${initStr}; ${condStr}; ${incrStr}) {${stmtStr}}`;
}

/**
 * Generate initializer text with declaration kind.
 * @param {Object} initializer - Initializer node
 * @returns {string} Generated text
 */
function generateInitializerText(initializer) {
    if (!initializer) return '';

    const initJson = convertAstToJson(initializer);
    let initStr = jsonToCodeString(initJson);

    if (initJson?.kindName === 'VariableDeclarationList' && initStr) {
        initStr = DECLARATION_KINDS.CONST + ' ' + initStr;
    }

    return initStr;
}

/**
 * Generate for statement initializer text with proper declaration kind.
 * @param {Object} initializer - Initializer node
 * @returns {string} Generated text
 */
function generateForInitializerText(initializer) {
    if (!initializer) return '';

    const initJson = convertAstToJson(initializer);
    let initStr = jsonToCodeString(initJson);

    if (initJson?.kindName === 'VariableDeclarationList' && initStr) {
        const declKind = initJson.declarationKind || DECLARATION_KINDS.DEFAULT;
        initStr = declKind + ' ' + initStr;
    }

    return initStr;
}

/**
 * Generate text representation for switch statement.
 * @param {Object} node - SwitchStatement node
 * @returns {string} Generated text
 */
function generateSwitchStatementText(node) {
    const exprStr = node.expression ? jsonToCodeString(convertAstToJson(node.expression)) : '';
    const caseBlock = node.caseBlock ? convertAstToJson(node.caseBlock) : null;

    let result = `${KEYWORDS.SWITCH} (${exprStr}) {`;

    if (caseBlock?.clauses) {
        for (const clause of caseBlock.clauses) {
            if (clause.kindName === 'CaseClause') {
                result += generateCaseClauseText(clause);
            } else if (clause.kindName === 'DefaultClause') {
                result += generateDefaultClauseText(clause);
            }
        }
    }

    return result + '}';
}

/**
 * Generate text representation for case clause.
 * @param {Object} clause - CaseClause JSON
 * @returns {string} Generated text
 */
function generateCaseClauseText(clause) {
    const caseExpr = clause.expression ? jsonToCodeString(clause.expression) : '';
    let result = `${KEYWORDS.CASE} ${caseExpr}:`;

    if (clause.statements?.length > 0) {
        for (const stmt of clause.statements) {
            result += ' ' + jsonToCodeString(stmt);
        }
        result += ' ' + KEYWORDS.BREAK + ';';
    }

    return result;
}

/**
 * Generate text representation for default clause.
 * @param {Object} clause - DefaultClause JSON
 * @returns {string} Generated text
 */
function generateDefaultClauseText(clause) {
    let result = `${KEYWORDS.DEFAULT}:`;

    if (clause.statements?.length > 0) {
        for (const stmt of clause.statements) {
            result += ' ' + jsonToCodeString(stmt);
        }
    }

    return result;
}

/**
 * Generate text representation for try-catch-finally statement.
 * @param {Object} node - TryStatement node
 * @returns {string} Generated text
 */
function generateTryStatementText(node) {
    const tryBlock = node.tryBlock ? jsonToCodeString(convertAstToJson(node.tryBlock)) : '';
    const catchClause = node.catchClause ? convertAstToJson(node.catchClause) : null;
    const finallyBlock = node.finallyBlock ? convertAstToJson(node.finallyBlock) : null;

    let result = `${KEYWORDS.TRY} {${tryBlock}}`;

    if (catchClause) {
        const varDecl = catchClause.variableDeclaration;
        const varName = varDecl ? jsonToCodeString(varDecl) : '';
        const catchBlock = catchClause.block ? jsonToCodeString(catchClause.block) : '';

        if (varName) {
            result += ` ${KEYWORDS.CATCH} (${varName}) {${catchBlock}}`;
        } else {
            result += ` ${KEYWORDS.CATCH} {${catchBlock}}`;
        }
    }

    if (finallyBlock) {
        result += ` ${KEYWORDS.FINALLY} {${jsonToCodeString(finallyBlock)}}`;
    }

    return result;
}

/**
 * Generate text representation for arrow function.
 * @param {Object} node - ArrowFunction node
 * @returns {string} Generated text
 */
function generateArrowFunctionText(node) {
    const params = generateFunctionParameters(node.parameters);
    const hasAsync = hasAsyncModifier(node.modifiers);

    let result = hasAsync ? KEYWORDS.ASYNC + ' ' : '';
    result += OPERATORS.OPEN_PAREN + params + OPERATORS.CLOSE_PAREN + OPERATORS.ARROW;

    if (node.body) {
        const bodyJson = convertAstToJson(node.body);
        const bodyCode = jsonToCodeString(bodyJson);

        if (node.body.kind === ts.SyntaxKind.Block) {
            result += OPERATORS.OPEN_BRACE + '\n' + bodyCode + '\n' + OPERATORS.CLOSE_BRACE;
        } else {
            result += bodyCode;
        }
    }

    return result;
}

/**
 * Generate text representation for function expression.
 * @param {Object} node - FunctionExpression node
 * @returns {string} Generated text
 */
function generateFunctionExpressionText(node) {
    const params = generateFunctionParameters(node.parameters);
    const name = node.name?.escapedText || node.name?.text || '';
    const hasAsync = hasAsyncModifier(node.modifiers);

    let result = hasAsync ? KEYWORDS.ASYNC + ' ' : '';
    result += KEYWORDS.FUNCTION + ' ' + name + OPERATORS.OPEN_PAREN + params + OPERATORS.CLOSE_PAREN + ' ';

    if (node.body) {
        const bodyJson = convertAstToJson(node.body);
        const bodyCode = jsonToCodeString(bodyJson);

        if (node.body.kind === ts.SyntaxKind.Block) {
            result += OPERATORS.OPEN_BRACE + '\n' + bodyCode + '\n' + OPERATORS.CLOSE_BRACE;
        } else {
            result += OPERATORS.OPEN_BRACE + ' ' + bodyCode + ' ' + OPERATORS.CLOSE_BRACE;
        }
    }

    return result;
}

/**
 * Generate function parameters string without type annotations.
 * @param {Array} parameters - Array of parameter nodes
 * @returns {string} Parameters string
 */
function generateFunctionParameters(parameters) {
    return (parameters || []).map(param => {
        let paramText = '';

        if (param.dotDotDotToken) {
            paramText += OPERATORS.SPREAD;
        }

        if (param.name?.kind) {
            const bindingKind = getSyntaxKindName(param.name.kind);
            if (isBindingPattern(bindingKind)) {
                paramText += param.name.getText();
            } else {
                paramText += param.name?.escapedText || param.name?.text || '';
            }
        } else {
            paramText += param.name?.escapedText || param.name?.text || '';
        }

        if (param.initializer) {
            const initJson = convertAstToJson(param.initializer);
            paramText += OPERATORS.EQUALS + jsonToCodeString(initJson);
        }

        return paramText;
    }).join(OPERATORS.COMMA + ' ');
}

/**
 * Check if modifiers contain async keyword.
 * @param {Array} modifiers - Array of modifiers
 * @returns {boolean} True if has async modifier
 */
function hasAsyncModifier(modifiers) {
    return modifiers && modifiers.some(m => m.kind === ts.SyntaxKind.AsyncKeyword);
}

/**
 * Generate text representation for get accessor.
 * @param {Object} node - GetAccessor node
 * @returns {string} Generated text
 */
function generateGetAccessorText(node) {
    const name = node.name ? node.name.getText() : '';
    const bodyCode = node.body ? jsonToCodeString(convertAstToJson(node.body)) : '';

    return `${KEYWORDS.GET} ${name}() {${bodyCode}}`;
}

/**
 * Generate text representation for set accessor.
 * @param {Object} node - SetAccessor node
 * @returns {string} Generated text
 */
function generateSetAccessorText(node) {
    const name = node.name ? node.name.getText() : '';
    const params = (node.parameters || []).map(p => p.name?.getText() || '').join(', ');
    const bodyCode = node.body ? jsonToCodeString(convertAstToJson(node.body)) : '';

    return `${KEYWORDS.SET} ${name}(${params}) {${bodyCode}}`;
}

/**
 * Generate text representation for class expression.
 * @param {Object} node - ClassExpression node
 * @returns {string} Generated text
 */
function generateClassExpressionText(node) {
    const className = node.name ? node.name.getText() : '';
    let result = KEYWORDS.CLASS + ' ';

    if (className) {
        result += className + ' ';
    }

    result += generateExtendsClause(node);
    result += OPERATORS.OPEN_BRACE + ' ';
    result += generateClassMembers(node);
    result += ' ' + OPERATORS.CLOSE_BRACE;

    return result;
}

/**
 * Generate extends clause text.
 * @param {Object} node - Class node
 * @returns {string} Extends clause text
 */
function generateExtendsClause(node) {
    if (!node.heritageClauses) return '';

    for (const clause of node.heritageClauses) {
        const tokenName = getSyntaxKindName(clause.token);
        if (tokenName === SYNTAX_KIND_NAMES.EXTENDS_KEYWORD) {
            if (clause.types?.length > 0) {
                return `${KEYWORDS.EXTENDS} ${clause.types[0].expression.getText()} `;
            }
        }
    }

    return '';
}

/**
 * Generate class members text.
 * @param {Object} node - Class node
 * @returns {string} Members text
 */
function generateClassMembers(node) {
    if (!node.members) return '';

    const memberTexts = [];
    for (const member of node.members) {
        const memberKind = getSyntaxKindName(member.kind);
        if (memberKind === 'Constructor') {
            memberTexts.push(generateConstructorText(member));
        } else if (memberKind === 'MethodDeclaration') {
            memberTexts.push(generateMethodDeclarationText(member));
        } else if (memberKind === 'PropertyDeclaration') {
            memberTexts.push(generatePropertyDeclarationText(member));
        }
    }

    return memberTexts.join(' ');
}

/**
 * Generate text representation for constructor.
 * @param {Object} node - Constructor node
 * @returns {string} Generated text
 */
function generateConstructorText(node) {
    const params = (node.parameters || []).map(p => p.name?.getText() || '').join(', ');
    const bodyCode = node.body ? generateBlockBodyText(node.body) : '';

    return `${KEYWORDS.CONSTRUCTOR}(${params}) {${bodyCode}}`;
}

/**
 * Generate text representation for method declaration.
 * @param {Object} node - MethodDeclaration node
 * @returns {string} Generated text
 */
function generateMethodDeclarationText(node) {
    const name = node.name?.getText() || '';
    const params = (node.parameters || []).map(p => p.name?.getText() || '').join(', ');
    const bodyCode = node.body ? generateBlockBodyText(node.body) : '';

    return `${name}(${params}) {${bodyCode}}`;
}

/**
 * Generate text representation for property declaration.
 * @param {Object} node - PropertyDeclaration node
 * @returns {string} Generated text
 */
function generatePropertyDeclarationText(node) {
    const name = node.name?.getText() || '';

    if (node.initializer) {
        const initText = node.initializer.getText();
        return `${name} = ${initText};`;
    }

    return `${name};`;
}

/**
 * Generate text representation for block body.
 * @param {Object} blockNode - Block node
 * @returns {string} Body text
 */
function generateBlockBodyText(blockNode) {
    if (!blockNode?.statements) return '';

    const statements = [];
    for (const stmt of blockNode.statements) {
        const stmtText = stmt.getText();
        if (stmtText) {
            statements.push(stmtText);
        }
    }
    return statements.join(' ');
}

/**
 * Generate text representation for throw statement.
 * @param {Object} node - ThrowStatement node
 * @returns {string} Generated text
 */
function generateThrowStatementText(node) {
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    return expr ? KEYWORDS.THROW + ' ' + jsonToCodeString(expr) : KEYWORDS.THROW;
}

/**
 * Generate text representation for property access expression.
 * @param {Object} node - PropertyAccessExpression node
 * @returns {string} Generated text
 */
function generatePropertyAccessExpressionText(node) {
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    const name = node.name ? node.name.escapedText : '';
    const dotOperator = node.questionDotToken ? OPERATORS.QUESTION_DOT : OPERATORS.DOT;

    return expr ? jsonToCodeString(expr) + dotOperator + name : name;
}

/**
 * Generate text representation for non-null assertion.
 * @param {Object} node - NonNullExpression node
 * @returns {string} Generated text
 */
function generateNonNullExpressionText(node) {
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    return expr ? jsonToCodeString(expr) : '';
}

/**
 * Generate text representation for as-expression.
 * @param {Object} node - AsExpression node
 * @returns {string} Generated text
 */
function generateAsExpressionText(node) {
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    return expr ? jsonToCodeString(expr) : '';
}

/**
 * Generate text representation for import expression.
 * @param {Object} node - ImportExpression node
 * @returns {string} Generated text
 */
function generateImportExpressionText(node) {
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    const modulePath = expr ? jsonToCodeString(expr) : '';

    return 'import(' + modulePath + ')';
}

/**
 * Generate text representation for object literal expression.
 * @param {Object} node - ObjectLiteralExpression node
 * @returns {string} Generated text
 */
function generateObjectLiteralExpressionText(node) {
    if (!node.properties || node.properties.length === 0) {
        return '{}';
    }

    const props = node.properties.map(prop => {
        if (prop.kind === ts.SyntaxKind.PropertyAssignment) {
            const name = prop.name.getText();
            const valueJson = convertAstToJson(prop.initializer);
            const valueStr = valueJson ? jsonToCodeString(valueJson) : '';
            return name + ': ' + valueStr;
        } else if (prop.kind === ts.SyntaxKind.ShorthandPropertyAssignment) {
            return prop.name.getText();
        } else if (prop.kind === ts.SyntaxKind.SpreadAssignment) {
            const exprJson = convertAstToJson(prop.expression);
            const exprStr = exprJson ? jsonToCodeString(exprJson) : '';
            return '...' + exprStr;
        } else if (prop.kind === ts.SyntaxKind.MethodDeclaration) {
            const methodJson = convertAstToJson(prop);
            return methodJson ? jsonToCodeString(methodJson) : '';
        } else if (prop.kind === ts.SyntaxKind.GetAccessor) {
            const accessorJson = convertAstToJson(prop);
            return accessorJson ? jsonToCodeString(accessorJson) : '';
        } else if (prop.kind === ts.SyntaxKind.SetAccessor) {
            const accessorJson = convertAstToJson(prop);
            return accessorJson ? jsonToCodeString(accessorJson) : '';
        }
        return '';
    }).join(', ');

    return OPERATORS.OPEN_BRACE + props + OPERATORS.CLOSE_BRACE;
}

/**
 * Generate text representation for template expression.
 * @param {Object} node - TemplateExpression node
 * @returns {string} Generated text
 */
function generateTemplateExpressionText(node) {
    if (!node.templateSpans || node.templateSpans.length === 0) {
        return OPERATORS.BACKTICK + escapeTemplateLiteral(node.head?.text || '') + OPERATORS.BACKTICK;
    }

    let result = OPERATORS.BACKTICK;
    result += escapeTemplateLiteral(node.head?.text || '');

    for (const span of node.templateSpans) {
        const exprJson = convertAstToJson(span.expression);
        const exprStr = exprJson ? jsonToCodeString(exprJson) : '';
        result += '${' + exprStr + '}';
        result += escapeTemplateLiteral(span.literal?.text || '');
    }

    return result + OPERATORS.BACKTICK;
}

/**
 * Escape special characters in template literals.
 * @param {string} str - String to escape
 * @returns {string} Escaped string
 */
function escapeTemplateLiteral(str) {
    if (!str) return '';
    return str.replace(/\\/g, '\\\\').replace(/`/g, '\\`').replace(/\$\{/g, '\\${');
}

// =============================================================================
// JSON TO CODE STRING GENERATION
// =============================================================================

/**
 * Map of kind names to code generator functions.
 */
const CODE_GENERATORS = {
    'Block': generateBlockCode,
    'ExpressionStatement': generateExpressionStatementCode,
    'ReturnStatement': generateReturnStatementCode,
    'VariableStatement': generateVariableStatementCode,
    'FirstStatement': generateVariableStatementCode,
    'VariableDeclarationList': generateVariableDeclarationListCode,
    'VariableDeclaration': generateVariableDeclarationCode,
    'Identifier': generateIdentifierCode,
    'ThisKeyword': () => 'this',
    'FirstLiteralToken': json => json.text || '',
    'StringLiteral': generateStringLiteralCode,
    'PropertyAccessExpression': generatePropertyAccessExpressionCode,
    'ElementAccessExpression': generateElementAccessExpressionCode,
    'BinaryExpression': generateBinaryExpressionCode,
    'PrefixUnaryExpression': generatePrefixUnaryExpressionCode,
    'PostfixUnaryExpression': generatePostfixUnaryExpressionCode,
    'CallExpression': generateCallExpressionCode,
    'IfStatement': generateIfStatementCode,
    'ConditionalExpression': generateConditionalExpressionCode,
    'TypeOfExpression': generateTypeOfExpressionCode,
    'ArrayLiteralExpression': generateArrayLiteralExpressionCode,
    'ParenthesizedExpression': generateParenthesizedExpressionCode,
    'AsExpression': generateAsExpressionCode,
    'NonNullExpression': generateNonNullExpressionCode,
    'AwaitExpression': generateAwaitExpressionCode,
    'ThrowStatement': generateThrowStatementCode,
    'ObjectLiteralExpression': generateObjectLiteralExpressionCode,
    'PropertyAssignment': generatePropertyAssignmentCode,
    'NewExpression': generateNewExpressionCode,
    'ArrowFunction': generateArrowFunctionCode,
    'FunctionExpression': generateFunctionExpressionCode,
    'ClassExpression': generateClassExpressionCode,
    'GetAccessor': generateGetAccessorCode,
    'SetAccessor': generateSetAccessorCode,
    'SpreadElement': generateSpreadElementCode,
    'SpreadAssignment': generateSpreadAssignmentCode,
    'DeleteExpression': generateDeleteExpressionCode,
    'ObjectBindingPattern': generateObjectBindingPatternCode,
    'ArrayBindingPattern': generateArrayBindingPatternCode,
    'BindingElement': generateBindingElementCode,
    'TemplateExpression': generateTemplateExpressionCode,
    'NoSubstitutionTemplateLiteral': generateNoSubstitutionTemplateLiteralCode,
    'ImportExpression': generateImportExpressionCode,
    'BreakStatement': generateBreakStatementCode,
    'ContinueStatement': generateContinueStatementCode,
    'TryStatement': generateTryStatementCode,
    'ResourceReferenceExpression': generateResourceReferenceExpressionCode
};

/**
 * Generate text representation from JSON AST node.
 * @param {Object} json - JSON AST node
 * @returns {string} Generated code string
 */
function jsonToCodeString(json) {
    if (!json) return '';

    const generator = CODE_GENERATORS[json.kindName];
    if (generator) {
        return generator(json);
    }

    if (json.text) return json.text;
    return JSON.stringify(json);
}

/**
 * Generate block code.
 * @param {Object} json - Block JSON
 * @returns {string} Generated code
 */
function generateBlockCode(json) {
    if (json.statements?.length > 0) {
        return json.statements.map(s => '  ' + jsonToCodeString(s)).join('\n');
    }
    return '';
}

/**
 * Generate expression statement code.
 * @param {Object} json - ExpressionStatement JSON
 * @returns {string} Generated code
 */
function generateExpressionStatementCode(json) {
    const expr = json.expression;
    if (!expr) return '';
    return jsonToCodeString(expr) + OPERATORS.SEMICOLON;
}

/**
 * Generate return statement code.
 * @param {Object} json - ReturnStatement JSON
 * @returns {string} Generated code
 */
function generateReturnStatementCode(json) {
    const retExpr = json.expression;
    if (retExpr) {
        return KEYWORDS.RETURN + ' ' + jsonToCodeString(retExpr) + OPERATORS.SEMICOLON;
    }
    return KEYWORDS.RETURN + OPERATORS.SEMICOLON;
}

/**
 * Generate variable statement code.
 * @param {Object} json - VariableStatement JSON
 * @returns {string} Generated code
 */
function generateVariableStatementCode(json) {
    const declList = json.declarationList;
    if (!declList?.declarations?.length > 0) return '// variable';

    const decl = declList.declarations[0];
    const declKind = declList.declarationKind || DECLARATION_KINDS.DEFAULT;
    let result = declKind + ' ' + decl.name;

    if (decl.initializer) {
        result += OPERATORS.EQUALS + jsonToCodeString(decl.initializer);
    } else if (declKind === DECLARATION_KINDS.CONST) {
        result += OPERATORS.EQUALS + EMPTY_VALUES.UNDEFINED;
    }

    return result + OPERATORS.SEMICOLON;
}

/**
 * Generate variable declaration list code.
 * @param {Object} json - VariableDeclarationList JSON
 * @returns {string} Generated code
 */
function generateVariableDeclarationListCode(json) {
    if (Array.isArray(json.declarations) && json.declarations.length > 0) {
        return json.declarations.map(decl => jsonToCodeString(decl)).join(', ');
    }
    return jsonToCodeString(json.declarations);
}

/**
 * Generate variable declaration code.
 * @param {Object} json - VariableDeclaration JSON
 * @returns {string} Generated code
 */
function generateVariableDeclarationCode(json) {
    let result = json.name;
    if (json.initializer) {
        result += OPERATORS.EQUALS + jsonToCodeString(json.initializer);
    }
    return result;
}

/**
 * Generate identifier code.
 * @param {Object} json - Identifier JSON
 * @returns {string} Generated code
 */
function generateIdentifierCode(json) {
    return json.text || json.name || '';
}

/**
 * Generate string literal code.
 * @param {Object} json - StringLiteral JSON
 * @returns {string} Generated code
 */
function generateStringLiteralCode(json) {
    if (json.text && (json.text.startsWith('"') || json.text.startsWith("'"))) {
        return json.text;
    }
    const val = json.text || json.value || '';
    return OPERATORS.DOUBLE_QUOTE + val + OPERATORS.DOUBLE_QUOTE;
}

/**
 * Generate property access expression code.
 * @param {Object} json - PropertyAccessExpression JSON
 * @returns {string} Generated code
 */
function generatePropertyAccessExpressionCode(json) {
    const expr = json.expression;
    const name = json.name;
    const dotOperator = json.questionDotToken ? OPERATORS.QUESTION_DOT : OPERATORS.DOT;
    return jsonToCodeString(expr) + dotOperator + name;
}

/**
 * Generate element access expression code.
 * @param {Object} json - ElementAccessExpression JSON
 * @returns {string} Generated code
 */
function generateElementAccessExpressionCode(json) {
    const elemExpr = json.expression;
    const arg = json.argument || json.argumentExpression;
    const accessor = json.questionDotToken ? OPERATORS.QUESTION_DOT : '';

    return jsonToCodeString(elemExpr) + accessor + OPERATORS.OPEN_BRACKET + jsonToCodeString(arg) + OPERATORS.CLOSE_BRACKET;
}

/**
 * Generate binary expression code.
 * @param {Object} json - BinaryExpression JSON
 * @returns {string} Generated code
 */
function generateBinaryExpressionCode(json) {
    const left = json.left;
    const op = json.operator;
    const right = json.right;
    return jsonToCodeString(left) + ' ' + op + ' ' + jsonToCodeString(right);
}

/**
 * Generate prefix unary expression code.
 * @param {Object} json - PrefixUnaryExpression JSON
 * @returns {string} Generated code
 */
function generatePrefixUnaryExpressionCode(json) {
    return json.operator + jsonToCodeString(json.operand);
}

/**
 * Generate postfix unary expression code.
 * @param {Object} json - PostfixUnaryExpression JSON
 * @returns {string} Generated code
 */
function generatePostfixUnaryExpressionCode(json) {
    return jsonToCodeString(json.operand) + json.operator;
}

/**
 * Generate call expression code.
 * @param {Object} json - CallExpression JSON
 * @returns {string} Generated code
 */
function generateCallExpressionCode(json) {
    const callExpr = json.expression;
    const args = json.arguments || [];
    const argStr = args.map(arg => {
        if (typeof arg === 'string' && !arg.startsWith('{')) {
            return arg;
        }
        return jsonToCodeString(arg);
    }).join(', ');

    return jsonToCodeString(callExpr) + OPERATORS.OPEN_PAREN + argStr + OPERATORS.CLOSE_PAREN;
}

/**
 * Generate if statement code.
 * @param {Object} json - IfStatement JSON
 * @returns {string} Generated code
 */
function generateIfStatementCode(json) {
    const ifExpr = json.expression;
    const thenStmt = json.thenStatement;
    const elseStmt = json.elseStatement;

    let result = `${KEYWORDS.IF} (${jsonToCodeString(ifExpr)}) {\n${jsonToCodeString(thenStmt)}\n}`;
    if (elseStmt) {
        result += ` ${KEYWORDS.ELSE} {\n${jsonToCodeString(elseStmt)}\n}`;
    }
    return result;
}

/**
 * Generate conditional expression code.
 * @param {Object} json - ConditionalExpression JSON
 * @returns {string} Generated code
 */
function generateConditionalExpressionCode(json) {
    const cond = json.condition;
    const whenTrue = json.whenTrue;
    const whenFalse = json.whenFalse;
    return jsonToCodeString(cond) + ' ? ' + jsonToCodeString(whenTrue) + ' : ' + jsonToCodeString(whenFalse);
}

/**
 * Generate typeof expression code.
 * @param {Object} json - TypeOfExpression JSON
 * @returns {string} Generated code
 */
function generateTypeOfExpressionCode(json) {
    return KEYWORDS.TYPEOF + ' ' + jsonToCodeString(json.expression);
}

/**
 * Generate array literal expression code.
 * @param {Object} json - ArrayLiteralExpression JSON
 * @returns {string} Generated code
 */
function generateArrayLiteralExpressionCode(json) {
    if (json.elements) {
        const elemStr = json.elements.map(e => jsonToCodeString(e)).join(', ');
        return OPERATORS.OPEN_BRACKET + elemStr + OPERATORS.CLOSE_BRACKET;
    }
    return '[]';
}

/**
 * Generate parenthesized expression code.
 * @param {Object} json - ParenthesizedExpression JSON
 * @returns {string} Generated code
 */
function generateParenthesizedExpressionCode(json) {
    return OPERATORS.OPEN_PAREN + jsonToCodeString(json.expression) + OPERATORS.CLOSE_PAREN;
}

/**
 * Generate as expression code.
 * @param {Object} json - AsExpression JSON
 * @returns {string} Generated code
 */
function generateAsExpressionCode(json) {
    return jsonToCodeString(json.expression);
}

/**
 * Generate non-null expression code.
 * @param {Object} json - NonNullExpression JSON
 * @returns {string} Generated code
 */
function generateNonNullExpressionCode(json) {
    return jsonToCodeString(json.expression);
}

/**
 * Generate await expression code.
 * @param {Object} json - AwaitExpression JSON
 * @returns {string} Generated code
 */
function generateAwaitExpressionCode(json) {
    return KEYWORDS.AWAIT + ' ' + jsonToCodeString(json.expression);
}

/**
 * Generate throw statement code.
 * @param {Object} json - ThrowStatement JSON
 * @returns {string} Generated code
 */
function generateThrowStatementCode(json) {
    const expr = json.expression ? jsonToCodeString(json.expression) : '';
    return KEYWORDS.THROW + ' ' + expr;
}

/**
 * Generate object literal expression code.
 * @param {Object} json - ObjectLiteralExpression JSON
 * @returns {string} Generated code
 */
function generateObjectLiteralExpressionCode(json) {
    if (json.properties) {
        const propStr = json.properties.map(p => jsonToCodeString(p)).join(', ');
        return OPERATORS.OPEN_BRACE + propStr + OPERATORS.CLOSE_BRACE;
    }
    return '{}';
}

/**
 * Generate property assignment code.
 * @param {Object} json - PropertyAssignment JSON
 * @returns {string} Generated code
 */
function generatePropertyAssignmentCode(json) {
    return json.name + ': ' + jsonToCodeString(json.value);
}

/**
 * Generate new expression code.
 * @param {Object} json - NewExpression JSON
 * @returns {string} Generated code
 */
function generateNewExpressionCode(json) {
    const expr = json.expression;
    const args = json.arguments || [];
    const argStr = args.map(arg => jsonToCodeString(arg)).join(', ');
    return KEYWORDS.NEW + ' ' + jsonToCodeString(expr) + OPERATORS.OPEN_PAREN + argStr + OPERATORS.CLOSE_PAREN;
}

/**
 * Generate arrow function code.
 * @param {Object} json - ArrowFunction JSON
 * @returns {string} Generated code
 */
function generateArrowFunctionCode(json) {
    if (json.text) return json.text;

    const params = generateJsonFunctionParameters(json);
    const body = json.body ? jsonToCodeString(json.body) : '{}';
    const hasBrace = body.startsWith(OPERATORS.OPEN_BRACE);

    if (hasBrace) {
        return OPERATORS.OPEN_PAREN + params + OPERATORS.CLOSE_PAREN + OPERATORS.ARROW + body;
    }
    return OPERATORS.OPEN_PAREN + params + OPERATORS.CLOSE_PAREN + OPERATORS.ARROW + ' { ' + body + ' }';
}

/**
 * Generate function expression code.
 * @param {Object} json - FunctionExpression JSON
 * @returns {string} Generated code
 */
function generateFunctionExpressionCode(json) {
    if (json.text) return json.text;

    const name = json.name || '';
    const params = generateJsonFunctionParameters(json);
    const body = json.body ? jsonToCodeString(json.body) : '{}';

    if (name) {
        return `${KEYWORDS.FUNCTION} ${name}(${params}) ${body}`;
    }
    return `${KEYWORDS.FUNCTION}(${params}) ${body}`;
}

/**
 * Generate class expression code.
 * @param {Object} json - ClassExpression JSON
 * @returns {string} Generated code
 */
function generateClassExpressionCode(json) {
    if (json.text) return json.text;
    return 'class {}';
}

/**
 * Generate get accessor code.
 * @param {Object} json - GetAccessor JSON
 * @returns {string} Generated code
 */
function generateGetAccessorCode(json) {
    if (json.text) return json.text;

    const name = json.name || '';
    const body = json.body ? jsonToCodeString(json.body) : '{}';
    return `${KEYWORDS.GET} ${name}() { ${body} }`;
}

/**
 * Generate set accessor code.
 * @param {Object} json - SetAccessor JSON
 * @returns {string} Generated code
 */
function generateSetAccessorCode(json) {
    if (json.text) return json.text;

    const name = json.name || '';
    const params = (json.parameters || []).map(p => p.name || '').join(', ');
    const body = json.body ? jsonToCodeString(json.body) : '{}';
    return `${KEYWORDS.SET} ${name}(${params}) { ${body} }`;
}

/**
 * Generate spread element/assignment code.
 * @param {Object} json - SpreadElement or SpreadAssignment JSON
 * @returns {string} Generated code
 */
function generateSpreadElementCode(json) {
    return OPERATORS.SPREAD + jsonToCodeString(json.expression);
}

function generateSpreadAssignmentCode(json) {
    return OPERATORS.SPREAD + jsonToCodeString(json.expression);
}

/**
 * Generate delete expression code.
 * @param {Object} json - DeleteExpression JSON
 * @returns {string} Generated code
 */
function generateDeleteExpressionCode(json) {
    const expr = json.expression;
    if (expr) {
        return KEYWORDS.DELETE + ' ' + jsonToCodeString(expr);
    }
    return KEYWORDS.DELETE + ' ' + EMPTY_VALUES.UNDEFINED;
}

/**
 * Generate object binding pattern code.
 * @param {Object} json - ObjectBindingPattern JSON
 * @returns {string} Generated code
 */
function generateObjectBindingPatternCode(json) {
    const elements = json.elements || [];
    if (elements.length === 0) return '{}';

    const props = elements.map(elem => jsonToCodeString(elem)).join(', ');
    return OPERATORS.OPEN_BRACE + props + OPERATORS.CLOSE_BRACE;
}

/**
 * Generate array binding pattern code.
 * @param {Object} json - ArrayBindingPattern JSON
 * @returns {string} Generated code
 */
function generateArrayBindingPatternCode(json) {
    const elements = json.elements || [];
    const props = elements.map(elem => jsonToCodeString(elem)).join(', ');
    return OPERATORS.OPEN_BRACKET + props + OPERATORS.CLOSE_BRACKET;
}

/**
 * Generate binding element code.
 * @param {Object} json - BindingElement JSON
 * @returns {string} Generated code
 */
function generateBindingElementCode(json) {
    let elem = '';
    if (json.dotDotDotToken) {
        elem += OPERATORS.SPREAD;
    }
    if (json.propertyName) {
        elem += json.propertyName + ': ';
    }
    elem += json.name || '';
    if (json.initializer) {
        elem += OPERATORS.EQUALS + jsonToCodeString(json.initializer);
    }
    return elem;
}

/**
 * Generate template expression code.
 * @param {Object} json - TemplateExpression JSON
 * @returns {string} Generated code
 */
function generateTemplateExpressionCode(json) {
    if (json.text) return json.text;
    return '/* template expression */';
}

/**
 * Generate no substitution template literal code.
 * @param {Object} json - NoSubstitutionTemplateLiteral JSON
 * @returns {string} Generated code
 */
function generateNoSubstitutionTemplateLiteralCode(json) {
    const text = json.text || '';
    return OPERATORS.BACKTICK + escapeTemplateLiteral(text) + OPERATORS.BACKTICK;
}

/**
 * Generate import expression code.
 * @param {Object} json - ImportExpression JSON
 * @returns {string} Generated code
 */
function generateImportExpressionCode(json) {
    const expr = json.expression;
    const modulePath = expr ? jsonToCodeString(expr) : '';
    return 'import(' + modulePath + ')';
}

/**
 * Generate break statement code.
 * @param {Object} json - BreakStatement JSON
 * @returns {string} Generated code
 */
function generateBreakStatementCode(json) {
    if (json.label) {
        return KEYWORDS.BREAK + ' ' + json.label + OPERATORS.SEMICOLON;
    }
    return KEYWORDS.BREAK + OPERATORS.SEMICOLON;
}

/**
 * Generate continue statement code.
 * @param {Object} json - ContinueStatement JSON
 * @returns {string} Generated code
 */
function generateContinueStatementCode(json) {
    if (json.label) {
        return KEYWORDS.CONTINUE + ' ' + json.label + OPERATORS.SEMICOLON;
    }
    return KEYWORDS.CONTINUE + OPERATORS.SEMICOLON;
}

/**
 * Generate try statement code.
 * @param {Object} json - TryStatement JSON
 * @returns {string} Generated code
 */
function generateTryStatementCode(json) {
    if (json.text) return json.text;
    return '/* try statement */';
}

/**
 * Generate resource reference expression code.
 * @param {Object} json - ResourceReferenceExpression JSON
 * @returns {string} Generated code
 */
function generateResourceReferenceExpressionCode(json) {
    if (json.resourceRefType === RESOURCE_REF_TYPES.R) {
        return generateRResourceReference(json);
    }
    if (json.resourceRefType === RESOURCE_REF_TYPES.RAWFILE) {
        return generateRawfileResourceReference(json);
    }
    return json.text || '';
}

/**
 * Generate $r() resource reference code.
 * @param {Object} json - ResourceReferenceExpression JSON
 * @returns {string} Generated code
 */
function generateRResourceReference(json) {
    if (json.arguments?.length > 0) {
        const resourcePath = json.arguments[0].replace(REGEX_PATTERNS.QUOTES, '');
        const parts = resourcePath.split('.');

        if (parts.length >= 3) {
            const module = parts[0];
            const type = parts[parts.length - 2];
            const name = parts[parts.length - 1];
            const typeId = RESOURCE_TYPE_IDS[type] || DEFAULT_RESOURCE_TYPE_ID;
            return `__getResourceId__(${typeId}, ${EMPTY_VALUES.UNDEFINED}, "${module}", "${name}")`;
        }
    }

    return `__getResourceId__(${DEFAULT_RESOURCE_TYPE_ID}, ${EMPTY_VALUES.UNDEFINED}, "${EMPTY_VALUES.EMPTY_STRING}", "${EMPTY_VALUES.EMPTY_STRING}")`;
}

/**
 * Generate $rawfile() resource reference code.
 * @param {Object} json - ResourceReferenceExpression JSON
 * @returns {string} Generated code
 */
function generateRawfileResourceReference(json) {
    if (json.arguments?.length > 0) {
        const filename = json.arguments[0].replace(REGEX_PATTERNS.QUOTES, '');
        return `__getRawFileId__("${filename}")`;
    }
    return '__getRawFileId__("")';
}

/**
 * Generate function parameters from JSON.
 * @param {Object} json - ArrowFunction or FunctionExpression JSON
 * @returns {string} Parameters string
 */
function generateJsonFunctionParameters(json) {
    return (json.parameters || []).map(p => {
        let param = '';
        if (p.hasDotDotDot) param += OPERATORS.SPREAD;

        if (p.bindingPattern) {
            param += jsonToCodeString(p.bindingPattern);
        } else {
            param += p.name;
        }

        if (p.initializerText || (p.initializer && p.initializer.text)) {
            param += OPERATORS.EQUALS + (p.initializerText || p.initializer.text);
        }

        return param;
    }).join(', ');
}

// =============================================================================
// ENTRY POINT
// =============================================================================

main();
