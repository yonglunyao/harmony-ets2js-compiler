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
    ARRAY_BINDING_PATTERN: 'ArrayBindingPattern'
};

// Variable declaration kinds
const DECLARATION_KINDS = {
    LET: 'let',
    CONST: 'const',
    VAR: 'var',
    DEFAULT: 'const'  // Default for const declarations without initializer
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
    // Pattern: decorators followed by optional export keyword and struct
    STRUCT_DECORATOR: /(@\w+\s*(?:\([^)]*\))?\s*)\b(?:export\s+)?struct\s+/g,
    // Pattern for individual decorators
    INDIVIDUAL_DECORATOR: /@(\w+)(?:\s*\([^)]*\))?/g,
    // Pattern for struct keyword
    STRUCT_KEYWORD: /\bstruct\s+/g,
    // Pattern for @Entry decorator with optional @Component
    ENTRY_PATTERN: /@Entry\s*\n(?:@Component\s*\n)?(?:export\s+)?class\s+(\w+)/g,
    // Pattern for decorator name extraction
    DECORATOR_NAME: /@(\w+)/,
    // Pattern for removing quotes from strings
    QUOTES: /^['"]|['"]$/g
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
        // Read source file
        let sourceCode = fs.readFileSync(sourceFilePath, 'utf-8');

        // Preprocess ETS-specific syntax
        const preprocessResult = preprocessEts(sourceCode);

        // Parse using TypeScript Compiler API with preprocessed code
        const tsSourceFile = ts.createSourceFile(
            sourceFilePath,
            preprocessResult.code,
            ts.ScriptTarget.Latest,
            true, // setParentNodes
            ts.ScriptKind.TS
        );

        // Convert AST to serializable format
        const astJson = convertAstToJson(tsSourceFile, preprocessResult.decorators);

        // Write output
        fs.writeFileSync(outputFile, JSON.stringify(astJson, null, 2));
        console.error(ERROR_MESSAGES.SUCCESS, sourceFilePath);

    } catch (error) {
        console.error(ERROR_MESSAGES.PARSE_FAILED, error.message);
        console.error(error.stack);
        process.exit(1);
    }
}

/**
 * Preprocess ETS code to convert struct to class and extract decorators.
 */
function preprocessEts(sourceCode) {
    let processedCode = sourceCode;

    // Extract decorators immediately before struct/class declaration
    const extractedDecorators = [];

    // Find struct with preceding decorators
    let match;
    while ((match = REGEX_PATTERNS.STRUCT_DECORATOR.exec(sourceCode)) !== null) {
        // Extract individual decorators from the matched text
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

    // Replace struct with class
    processedCode = processedCode.replace(REGEX_PATTERNS.STRUCT_KEYWORD, 'class ');

    // Extract @Entry decorator and ensure export
    const entryClasses = [];
    REGEX_PATTERNS.ENTRY_PATTERN.lastIndex = 0;
    while ((match = REGEX_PATTERNS.ENTRY_PATTERN.exec(processedCode)) !== null) {
        entryClasses.push(match[1]);
        // Add export if missing
        const classDecl = match[0];
        if (!classDecl.includes(KEYWORDS.EXPORT)) {
            processedCode = processedCode.replace(classDecl, classDecl.replace('class ', 'export class '));
        }
    }

    return {
        code: processedCode,
        decorators: extractedDecorators
    };
}

/**
 * Convert TypeScript AST to JSON-serializable format.
 */
function convertAstToJson(node, extractedDecorators = []) {
    if (!node) {
        return null;
    }

    const result = {
        kind: node.kind,
        kindName: getSyntaxKindName(node.kind),
    };

    // Add type-specific properties
    switch (node.kind) {
        case ts.SyntaxKind.SourceFile:
            result.fileName = node.fileName;
            result.statements = [];
            for (const stmt of node.statements) {
                const stmtJson = convertAstToJson(stmt, extractedDecorators);
                if (stmtJson) {
                    result.statements.push(stmtJson);
                }
            }
            break;

        case ts.SyntaxKind.ClassDeclaration:
            result.name = node.name?.escapedText || '';
            result.isExport = false;
            // Check for export modifier
            if (node.modifiers) {
                for (const mod of node.modifiers) {
                    const modKindName = getSyntaxKindName(mod.kind);
                    if (modKindName === SYNTAX_KIND_NAMES.EXPORT_KEYWORD) {
                        result.isExport = true;
                        break;
                    }
                }
            }
            result.decorators = [];

            // Add extracted decorators (for @Component on struct)
            if (extractedDecorators.length > 0) {
                for (const dec of extractedDecorators) {
                    result.decorators.push({
                        name: dec.name
                    });
                }
            }

            // Add inline decorators
            for (const dec of (node.decorators || [])) {
                const decJson = convertAstToJson(dec);
                if (decJson) {
                    result.decorators.push(decJson);
                }
            }

            result.members = [];
            for (const member of (node.members || [])) {
                const memberJson = convertAstToJson(member);
                if (memberJson) {
                    result.members.push(memberJson);
                }
            }

            // Add heritage clauses (extends, implements)
            if (node.heritageClauses && node.heritageClauses.length > 0) {
                result.heritageClauses = [];
                for (const clause of node.heritageClauses) {
                    const clauseJson = {
                        token: getSyntaxKindName(clause.token) || '',
                        types: []
                    };
                    if (clause.types) {
                        for (const type of clause.types) {
                            clauseJson.types.push(type.expression.getText());
                        }
                    }
                    result.heritageClauses.push(clauseJson);
                }
            }
            break;

        case ts.SyntaxKind.ClassExpression:
            result.name = node.name?.escapedText || '';
            result.decorators = [];
            for (const dec of (node.decorators || [])) {
                const decJson = convertAstToJson(dec);
                if (decJson) {
                    result.decorators.push(decJson);
                }
            }
            result.members = [];
            for (const member of (node.members || [])) {
                const memberJson = convertAstToJson(member);
                if (memberJson) {
                    result.members.push(memberJson);
                }
            }
            // Add heritage clauses (extends, implements)
            if (node.heritageClauses && node.heritageClauses.length > 0) {
                result.heritageClauses = [];
                for (const clause of node.heritageClauses) {
                    const clauseJson = {
                        token: getSyntaxKindName(clause.token) || '',
                        types: []
                    };
                    if (clause.types) {
                        for (const type of clause.types) {
                            clauseJson.types.push(type.expression.getText());
                        }
                    }
                    result.heritageClauses.push(clauseJson);
                }
            }
            // Generate text representation for class expression
            result.text = generateClassExpressionText(node);
            break;

        case ts.SyntaxKind.MethodDeclaration:
            result.name = node.name?.escapedText || '';
            result.decorators = [];
            for (const dec of (node.decorators || [])) {
                const decJson = convertAstToJson(dec);
                if (decJson) {
                    result.decorators.push(decJson);
                }
            }
            // Add modifiers (static, async, etc.)
            result.modifiers = [];
            if (node.modifiers) {
                for (const mod of node.modifiers) {
                    const modInfo = {
                        kind: mod.kind,
                        kindName: getSyntaxKindName(mod.kind)
                    };
                    result.modifiers.push(modInfo);

                    // ETS workaround: check if modifier is a decorator and extract name from getText()
                    // This handles @Builder and other ETS method decorators
                    if (modInfo.kindName === SYNTAX_KIND_NAMES.DECORATOR && result.decorators.length === 0) {
                        // Try to get decorator name from the modifier's text
                        try {
                            const modText = mod.getText();
                            const decoratorMatch = modText.match(REGEX_PATTERNS.DECORATOR_NAME);
                            if (decoratorMatch) {
                                result.decorators.push({
                                    name: decoratorMatch[1]
                                });
                            }
                        } catch (e) {
                            // Ignore if getText() fails
                        }
                    }
                }
            }
            result.parameters = [];
            for (const param of (node.parameters || [])) {
                const paramJson = {
                    name: param.name?.escapedText || '',
                    type: param.type?.getText() || '',
                    hasDotDotDot: param.dotDotDotToken ? true : false,
                    questionToken: param.questionToken ? true : false
                };
                // Handle binding patterns (destructuring in parameters)
                if (param.name && param.name.kind) {
                    const bindingKind = getSyntaxKindName(param.name.kind);
                    if (bindingKind === SYNTAX_KIND_NAMES.OBJECT_BINDING_PATTERN || bindingKind === SYNTAX_KIND_NAMES.ARRAY_BINDING_PATTERN) {
                        paramJson.bindingPattern = convertAstToJson(param.name);
                        paramJson.name = param.name.getText() || '';
                    }
                }
                // Add initializer for default parameters
                if (param.initializer) {
                    paramJson.initializer = convertAstToJson(param.initializer);
                    if (paramJson.initializer && paramJson.initializer.text) {
                        paramJson.initializerText = paramJson.initializer.text;
                    } else {
                        paramJson.initializerText = param.initializer?.getText() || '';
                    }
                }
                result.parameters.push(paramJson);
            }
            result.body = node.body ? convertAstToJson(node.body) : null;
            result.asteriskToken = node.asteriskToken ? true : false;
            break;

        case ts.SyntaxKind.GetAccessor:
            result.name = node.name?.escapedText || '';
            result.body = node.body ? convertAstToJson(node.body) : null;
            // Generate text for getter
            result.text = generateGetAccessorText(node);
            break;

        case ts.SyntaxKind.SetAccessor:
            result.name = node.name?.escapedText || '';
            result.parameters = [];
            for (const param of (node.parameters || [])) {
                const paramJson = {
                    name: param.name?.escapedText || '',
                    type: param.type?.getText() || ''
                };
                result.parameters.push(paramJson);
            }
            result.body = node.body ? convertAstToJson(node.body) : null;
            // Generate text for setter
            result.text = generateSetAccessorText(node);
            break;

        case ts.SyntaxKind.FunctionDeclaration:
            result.name = node.name?.escapedText || '';
            result.modifiers = [];
            if (node.modifiers) {
                for (const mod of node.modifiers) {
                    result.modifiers.push({
                        kind: mod.kind,
                        kindName: getSyntaxKindName(mod.kind)
                    });
                }
            }
            result.parameters = [];
            for (const param of (node.parameters || [])) {
                const paramJson = {
                    name: param.name?.escapedText || '',
                    type: param.type?.getText() || '',
                    hasDotDotDot: param.dotDotDotToken ? true : false,
                    questionToken: param.questionToken ? true : false
                };
                // Handle binding patterns (destructuring in parameters)
                if (param.name && param.name.kind) {
                    const bindingKind = getSyntaxKindName(param.name.kind);
                    if (bindingKind === SYNTAX_KIND_NAMES.OBJECT_BINDING_PATTERN || bindingKind === SYNTAX_KIND_NAMES.ARRAY_BINDING_PATTERN) {
                        paramJson.bindingPattern = convertAstToJson(param.name);
                        paramJson.name = param.name.getText() || '';
                    }
                }
                // Add initializer for default parameters
                if (param.initializer) {
                    paramJson.initializer = convertAstToJson(param.initializer);
                    if (paramJson.initializer && paramJson.initializer.text) {
                        paramJson.initializerText = paramJson.initializer.text;
                    } else {
                        paramJson.initializerText = param.initializer?.getText() || '';
                    }
                }
                result.parameters.push(paramJson);
            }
            result.body = node.body ? convertAstToJson(node.body) : null;
            // Add asterisk for generator functions
            result.asteriskToken = node.asteriskToken ? true : false;
            break;

        case ts.SyntaxKind.Constructor:
            result.name = KEYWORDS.CONSTRUCTOR;
            result.parameters = [];
            for (const param of (node.parameters || [])) {
                const paramJson = {
                    name: param.name?.escapedText || '',
                    type: param.type?.getText() || '',
                    hasDotDotDot: param.dotDotDotToken ? true : false,
                    questionToken: param.questionToken ? true : false
                };
                // Handle binding patterns (destructuring in parameters)
                if (param.name && param.name.kind) {
                    const bindingKind = getSyntaxKindName(param.name.kind);
                    if (bindingKind === SYNTAX_KIND_NAMES.OBJECT_BINDING_PATTERN || bindingKind === SYNTAX_KIND_NAMES.ARRAY_BINDING_PATTERN) {
                        paramJson.bindingPattern = convertAstToJson(param.name);
                        paramJson.name = param.name.getText() || '';
                    }
                }
                // Add initializer for default parameters
                if (param.initializer) {
                    paramJson.initializer = convertAstToJson(param.initializer);
                    if (paramJson.initializer && paramJson.initializer.text) {
                        paramJson.initializerText = paramJson.initializer.text;
                    } else {
                        paramJson.initializerText = param.initializer?.getText() || '';
                    }
                }
                result.parameters.push(paramJson);
            }
            result.body = node.body ? convertAstToJson(node.body) : null;
            break;

        case ts.SyntaxKind.PropertyDeclaration:
            result.name = node.name?.escapedText || '';
            result.type = node.type?.getText() || '';
            // Convert initializer to JSON to properly handle AsExpression and other TS syntax
            result.initializer = node.initializer ? convertAstToJson(node.initializer) : null;
            // Add text representation for the initializer (with TS syntax stripped)
            if (node.initializer) {
                const initJson = result.initializer;
                if (initJson && initJson.text) {
                    result.initializerText = initJson.text;
                } else {
                    // Fallback to text if JSON conversion doesn't have text
                    result.initializerText = node.initializer?.getText() || '';
                }
            }
            result.decorators = [];
            for (const dec of (node.decorators || [])) {
                const decJson = convertAstToJson(dec);
                if (decJson) {
                    result.decorators.push(decJson);
                }
            }
            // Check if modifiers contain decorators (for ETS-specific decorators like @State)
            result.modifiers = [];
            if (node.modifiers) {
                for (const mod of node.modifiers) {
                    const modInfo = {
                        kind: mod.kind,
                        kindName: getSyntaxKindName(mod.kind)
                    };
                    result.modifiers.push(modInfo);

                    // ETS workaround: check if modifier is a decorator and extract name from getText()
                    if (modInfo.kindName === SYNTAX_KIND_NAMES.DECORATOR && result.decorators.length === 0) {
                        // Try to get decorator name from the modifier's text
                        try {
                            const modText = mod.getText();
                            const decoratorMatch = modText.match(REGEX_PATTERNS.DECORATOR_NAME);
                            if (decoratorMatch) {
                                result.decorators.push({
                                    name: decoratorMatch[1]
                                });
                            }
                        } catch (e) {
                            // Ignore if getText() fails
                        }
                    }
                }
            }
            break;

        case ts.SyntaxKind.Decorator:
            const expression = node.expression;
            if (ts.isIdentifier(expression)) {
                result.name = expression.escapedText;
            } else if (ts.isCallExpression(expression)) {
                result.name = expression.expression.escapedText;
                result.arguments = [];
                for (const arg of (expression.arguments || [])) {
                    result.arguments.push(arg.getText());
                }
            }
            break;

        case ts.SyntaxKind.Block:
            result.statements = [];
            for (const stmt of node.statements) {
                const stmtJson = convertAstToJson(stmt);
                if (stmtJson) {
                    result.statements.push(stmtJson);
                }
            }
            break;

        case ts.SyntaxKind.VariableStatement:
            result.declarationList = convertAstToJson(node.declarationList);
            break;

        case ts.SyntaxKind.VariableDeclarationList:
            result.declarations = [];
            // Capture variable declaration keyword (let, const, var)
            if (node.flags & ts.NodeFlags.Let) {
                result.declarationKind = DECLARATION_KINDS.LET;
            } else if (node.flags & ts.NodeFlags.Const) {
                result.declarationKind = DECLARATION_KINDS.CONST;
            } else {
                result.declarationKind = DECLARATION_KINDS.VAR;
            }
            for (const decl of (node.declarations || [])) {
                const declJson = convertAstToJson(decl);
                if (declJson) {
                    result.declarations.push(declJson);
                }
            }
            break;

        case ts.SyntaxKind.VariableDeclaration:
            result.name = node.name?.getText() || '';
            result.type = node.type?.getText() || '';
            result.initializer = node.initializer ? convertAstToJson(node.initializer) : null;
            break;

        case ts.SyntaxKind.ExpressionStatement:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.CallExpression:
            result.expression = convertAstToJson(node.expression);
            result.arguments = [];

            // Check if this is a resource reference call ($r or $rawfile)
            const isResourceCall = result.expression && result.expression.kindName === 'Identifier' &&
                                 (result.expression.name === RESOURCE_REF_FUNCTIONS.R || result.expression.name === RESOURCE_REF_FUNCTIONS.RAWFILE);

            // Check if this is a special component (ForEach) that needs JSON objects as arguments
            const isForEach = result.expression && result.expression.kindName === 'Identifier' &&
                            result.expression.name === SPECIAL_COMPONENTS.FOR_EACH;

            if (isResourceCall) {
                // Handle resource reference conversion
                const funcName = result.expression.name;
                if (funcName === RESOURCE_REF_FUNCTIONS.R) {
                    // Convert $r('app.string.name') to __getResourceId__(type, bundle, module, name)
                    result.resourceRefType = RESOURCE_REF_TYPES.R;
                    result.kindName = 'ResourceReferenceExpression';
                } else if (funcName === RESOURCE_REF_FUNCTIONS.RAWFILE) {
                    // Convert $rawfile('icon.png') to __getRawFileId__('icon.png')
                    result.resourceRefType = RESOURCE_REF_TYPES.RAWFILE;
                    result.kindName = 'ResourceReferenceExpression';
                }
            }

            for (const arg of (node.arguments || [])) {
                if (isForEach) {
                    // ForEach needs JSON objects for proper argument processing
                    result.arguments.push(convertAstToJson(arg));
                } else if (isResourceCall) {
                    // For resource calls, we need the raw argument string to preserve the resource path
                    // e.g., $r('sys.color.ohos_id_color_primary') -> we need 'sys.color.ohos_id_color_primary'
                    const argText = arg.getText();
                    result.arguments.push(argText);
                } else {
                    // For regular calls, always convert to JSON to strip TypeScript syntax
                    const argJson = convertAstToJson(arg);
                    // Store the JSON object for complex expressions (ObjectLiteral, ArrayLiteral, etc.)
                    // This allows the Java code to properly convert them to strings
                    if (argJson && argJson.kindName &&
                        (argJson.kindName === 'ObjectLiteralExpression' ||
                         argJson.kindName === 'ArrayLiteralExpression' ||
                         argJson.kindName === 'ArrowFunction')) {
                        // Store the JSON object, not the text, for proper processing in Java
                        result.arguments.push(argJson);
                    } else if (argJson && argJson.text) {
                        // For simple expressions (identifiers, literals, etc.), use the text
                        if (argJson.kindName === 'StringLiteral') {
                            // Check if text already has quotes
                            if (argJson.text.startsWith('"') || argJson.text.startsWith("'")) {
                                result.arguments.push(argJson.text);
                            } else {
                                result.arguments.push('"' + argJson.text + '"');
                            }
                        } else {
                            result.arguments.push(argJson.text);
                        }
                    } else {
                        try {
                            result.arguments.push(arg.getText());
                        } catch (e) {
                            // Last resort - use JSON string representation
                            result.arguments.push(JSON.stringify(argJson));
                        }
                    }
                }
            }
            // Check for chained calls (e.g., Text('Hello').fontSize(16).fontColor(Color.Red))
            // If expression is PropertyAccessExpression with CallExpression as its expression,
            // this is part of a chain
            if (result.expression && result.expression.kindName === 'PropertyAccessExpression') {
                const propExpr = result.expression;
                if (propExpr.expression && propExpr.expression.kindName === 'CallExpression') {
                    // This is a chained call, mark it
                    result.isChainedCall = true;
                    // Store the base component name (from the innermost CallExpression)
                    if (propExpr.expression.expression && propExpr.expression.expression.kindName === 'Identifier') {
                        result.componentName = propExpr.expression.expression.name;
                    }
                    // Store the method name (current PropertyAccessExpression)
                    result.methodName = propExpr.name;
                }
            }
            // Check for special components like ForEach and If
            if (result.expression && result.expression.kindName === 'Identifier') {
                const componentName = result.expression.name;
                if (componentName === SPECIAL_COMPONENTS.FOR_EACH || componentName === SPECIAL_COMPONENTS.IF) {
                    result.isSpecialComponent = true;
                    result.componentName = componentName;
                }
            }
            break;

        case ts.SyntaxKind.Identifier:
            result.name = node.escapedText;
            result.text = node.getText();

            // Preserve resource reference functions ($r, $rawfile) as-is
            // These are runtime functions that should not be transformed
            if (result.name === RESOURCE_REF_FUNCTIONS.R || result.name === RESOURCE_REF_FUNCTIONS.RAWFILE) {
                // Keep the original text for resource functions
                result.text = result.name;
            }

            break;

        case ts.SyntaxKind.ImportKeyword:
            // The "import" keyword - treat as an identifier with text "import"
            result.name = KEYWORDS.IMPORT;
            result.text = KEYWORDS.IMPORT;
            break;

        case ts.SyntaxKind.PropertyAccessExpression:
            result.expression = convertAstToJson(node.expression);
            result.name = node.name.escapedText;
            // Check for optional chaining (?.)
            result.questionDotToken = node.questionDotToken ? true : false;
            // Add text representation for code generation
            result.text = generatePropertyAccessExpressionText(node);
            break;

        case ts.SyntaxKind.NewExpression:
            result.expression = convertAstToJson(node.expression);
            result.arguments = [];
            if (node.arguments) {
                for (const arg of node.arguments) {
                    result.arguments.push(convertAstToJson(arg));
                }
            }
            break;

        case ts.SyntaxKind.ElementAccessExpression:
            result.expression = convertAstToJson(node.expression);
            result.argumentExpression = convertAstToJson(node.argumentExpression);
            // Check for optional chaining in element access
            result.questionDotToken = node.questionDotToken ? true : false;
            break;

        case ts.SyntaxKind.ParenthesizedExpression:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.TypeOfExpression:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.PrefixUnaryExpression:
            result.operator = ts.tokenToString(node.operator);
            result.operand = convertAstToJson(node.operand);
            break;

        case ts.SyntaxKind.PostfixUnaryExpression:
            result.operator = ts.tokenToString(node.operator);
            result.operand = convertAstToJson(node.operand);
            break;

        case ts.SyntaxKind.NonNullExpression:
            result.expression = convertAstToJson(node.expression);
            // Add text representation without the ! operator
            result.text = generateNonNullExpressionText(node);
            break;

        case ts.SyntaxKind.AsExpression:
        case ts.SyntaxKind.TypeAssertion:
            result.expression = convertAstToJson(node.expression);
            result.type = node.type ? node.type.getText() : null;
            // Add text representation without the type assertion (just the expression)
            result.text = generateAsExpressionText(node);
            break;

        case ts.SyntaxKind.ImportExpression:
            // ImportExpression has expression field which is the module specifier
            // Note: TypeScript uses 'expression' not 'argument' for ImportExpression
            result.expression = convertAstToJson(node.expression);
            // Add text representation for code generation
            result.text = generateImportExpressionText(node);
            break;

        case ts.SyntaxKind.InterfaceDeclaration:
            result.name = node.name?.escapedText || '';
            // Interface declarations don't generate runtime code
            result.isTypeOnly = true;
            break;

        case ts.SyntaxKind.ImportDeclaration:
            result.moduleSpecifier = node.moduleSpecifier.getText();
            result.importClause = null;
            if (node.importClause) {
                const importClause = {
                    name: node.importClause.name ? node.importClause.name.getText() : null,
                    namedBindings: []
                };
                if (node.importClause.namedBindings) {
                    if (node.importClause.namedBindings.kind === ts.SyntaxKind.NamedImports) {
                        for (const element of node.importClause.namedBindings.elements) {
                            importClause.namedBindings.push({
                                name: element.name.getText(),
                                propertyName: element.propertyName ? element.propertyName.getText() : null
                            });
                        }
                    } else if (node.importClause.namedBindings.kind === ts.SyntaxKind.NamespaceImport) {
                        importClause.namedBindings.push({
                            kind: 'namespace',
                            name: node.importClause.namedBindings.name.getText()
                        });
                    }
                }
                result.importClause = importClause;
            }
            break;

        case ts.SyntaxKind.ExportDeclaration:
            result.exportClause = null;
            // Check if this is a type-only export (property is on the node itself)
            result.isTypeOnly = node.isTypeOnly || false;
            if (node.exportClause) {
                const exportClause = { elements: [] };
                if (node.exportClause.kind === ts.SyntaxKind.NamedExports) {
                    for (const element of node.exportClause.elements) {
                        exportClause.elements.push({
                            name: element.name.getText(),
                            propertyName: element.propertyName ? element.propertyName.getText() : null
                        });
                    }
                }
                result.exportClause = exportClause;
            }
            result.moduleSpecifier = node.moduleSpecifier ? node.moduleSpecifier.getText() : null;
            break;

        case ts.SyntaxKind.ArrowFunction:
        case ts.SyntaxKind.FunctionExpression:
            result.name = node.name?.escapedText || node.name?.text || '';
            result.parameters = [];
            for (const param of (node.parameters || [])) {
                const paramJson = {
                    name: param.name?.escapedText || '',
                    type: param.type?.getText() || ''
                };
                // Handle binding patterns (destructuring in parameters)
                if (param.name && param.name.kind) {
                    const bindingKind = getSyntaxKindName(param.name.kind);
                    if (bindingKind === SYNTAX_KIND_NAMES.OBJECT_BINDING_PATTERN || bindingKind === SYNTAX_KIND_NAMES.ARRAY_BINDING_PATTERN) {
                        paramJson.bindingPattern = convertAstToJson(param.name);
                        // Use text for the binding pattern
                        paramJson.name = param.name.getText() || '';
                    }
                }
                result.parameters.push(paramJson);
            }
            result.body = convertAstToJson(node.body);
            // Generate JavaScript-compatible text without type annotations
            if (node.kind === ts.SyntaxKind.ArrowFunction) {
                result.text = generateArrowFunctionText(node);
            } else {
                result.text = generateFunctionExpressionText(node);
            }
            break;

        case ts.SyntaxKind.StringLiteral:
            // node.text doesn't include quotes, so we need to add them
            result.text = '"' + node.text + '"';
            break;

        case ts.SyntaxKind.NumericLiteral:
            result.text = node.text;
            // Add computed value for octal conversion
            result.value = node.numericLiteralValue;
            break;

        case ts.SyntaxKind.TemplateExpression:
            result.head = { text: node.head.text };
            result.templateSpans = [];
            for (const span of node.templateSpans) {
                result.templateSpans.push({
                    expression: convertAstToJson(span.expression),
                    literal: { text: span.literal.text }
                });
            }
            // Add text representation (strips TypeScript syntax)
            result.text = generateTemplateExpressionText(node);
            break;

        case ts.SyntaxKind.NoSubstitutionTemplateLiteral:
            result.text = '`' + node.text + '`';
            break;

        case ts.SyntaxKind.TrueKeyword:
            result.text = 'true';
            break;

        case ts.SyntaxKind.FalseKeyword:
            result.text = 'false';
            break;

        case ts.SyntaxKind.NullKeyword:
            result.text = 'null';
            break;

        case ts.SyntaxKind.UndefinedKeyword:
            result.text = 'undefined';
            break;

        case ts.SyntaxKind.AwaitExpression:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.ThrowStatement:
            result.expression = convertAstToJson(node.expression);
            result.text = generateThrowStatementText(node);
            break;

        case ts.SyntaxKind.ReturnStatement:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.IfStatement:
            result.expression = convertAstToJson(node.expression);
            result.thenStatement = convertAstToJson(node.thenStatement);
            result.elseStatement = convertAstToJson(node.elseStatement);
            break;

        case ts.SyntaxKind.ForOfStatement:
            result.initializer = convertAstToJson(node.initializer);
            result.expression = convertAstToJson(node.expression);
            result.statement = convertAstToJson(node.statement);
            result.awaitModifier = node.awaitModifier ? true : false;
            // Add text representation for direct code generation
            result.text = generateForOfStatementText(node);
            break;

        case ts.SyntaxKind.ForInStatement:
            result.initializer = convertAstToJson(node.initializer);
            result.expression = convertAstToJson(node.expression);
            result.statement = convertAstToJson(node.statement);
            // Add text representation for direct code generation
            result.text = generateForInStatementText(node);
            break;

        case ts.SyntaxKind.WhileStatement:
            result.expression = convertAstToJson(node.expression);
            result.statement = convertAstToJson(node.statement);
            result.text = generateWhileStatementText(node);
            break;

        case ts.SyntaxKind.DoStatement:
            result.expression = convertAstToJson(node.expression);
            result.statement = convertAstToJson(node.statement);
            result.text = generateDoStatementText(node);
            break;

        case ts.SyntaxKind.ForStatement:
            result.initializer = node.initializer ? convertAstToJson(node.initializer) : null;
            result.condition = node.condition ? convertAstToJson(node.condition) : null;
            result.incrementor = node.incrementor ? convertAstToJson(node.incrementor) : null;
            result.statement = convertAstToJson(node.statement);
            result.text = generateForStatementText(node);
            break;

        case ts.SyntaxKind.SwitchStatement:
            result.expression = convertAstToJson(node.expression);
            result.caseBlock = convertAstToJson(node.caseBlock);
            result.text = generateSwitchStatementText(node);
            break;

        case ts.SyntaxKind.CaseBlock:
            result.clauses = [];
            if (node.clauses) {
                for (const clause of node.clauses) {
                    result.clauses.push(convertAstToJson(clause));
                }
            }
            break;

        case ts.SyntaxKind.CaseClause:
            result.expression = convertAstToJson(node.expression);
            result.statements = [];
            if (node.statements) {
                for (const stmt of node.statements) {
                    result.statements.push(convertAstToJson(stmt));
                }
            }
            break;

        case ts.SyntaxKind.DefaultClause:
            result.statements = [];
            if (node.statements) {
                for (const stmt of node.statements) {
                    result.statements.push(convertAstToJson(stmt));
                }
            }
            break;

        case ts.SyntaxKind.TryStatement:
            result.tryBlock = convertAstToJson(node.tryBlock);
            result.catchClause = node.catchClause ? convertAstToJson(node.catchClause) : null;
            result.finallyBlock = node.finallyBlock ? convertAstToJson(node.finallyBlock) : null;
            result.text = generateTryStatementText(node);
            break;

        case ts.SyntaxKind.CatchClause:
            result.variableDeclaration = node.variableDeclaration ? convertAstToJson(node.variableDeclaration) : null;
            result.block = convertAstToJson(node.block);
            break;

        case ts.SyntaxKind.BreakStatement:
            result.label = node.label ? node.label.getText() : null;
            break;

        case ts.SyntaxKind.ContinueStatement:
            result.label = node.label ? node.label.getText() : null;
            break;

        case ts.SyntaxKind.BinaryExpression:
            result.left = convertAstToJson(node.left);
            result.operator = node.operatorToken.getText();
            result.right = convertAstToJson(node.right);
            break;

        case ts.SyntaxKind.ConditionalExpression:
            result.condition = convertAstToJson(node.condition);
            result.whenTrue = convertAstToJson(node.whenTrue);
            result.whenFalse = convertAstToJson(node.whenFalse);
            break;

        case ts.SyntaxKind.ArrayLiteralExpression:
            result.elements = [];
            if (node.elements) {
                for (const elem of node.elements) {
                    result.elements.push(convertAstToJson(elem));
                }
            }
            break;

        case ts.SyntaxKind.SpreadElement:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.ObjectLiteralExpression:
            result.properties = [];
            if (node.properties) {
                for (const prop of node.properties) {
                    result.properties.push(convertAstToJson(prop));
                }
            }
            // Add text representation (strips TypeScript syntax like 'as')
            result.text = generateObjectLiteralExpressionText(node);
            break;

        case ts.SyntaxKind.PropertyAssignment:
            result.name = node.name.getText();
            result.value = convertAstToJson(node.initializer);
            break;

        case ts.SyntaxKind.ShorthandPropertyAssignment:
            result.name = node.name.getText();
            result.value = { kindName: 'Identifier', name: node.name.getText(), text: node.name.getText() };
            break;

        case ts.SyntaxKind.SpreadAssignment:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.ObjectBindingPattern:
            result.elements = [];
            if (node.elements) {
                for (const elem of node.elements) {
                    result.elements.push(convertAstToJson(elem));
                }
            }
            break;

        case ts.SyntaxKind.ArrayBindingPattern:
            result.elements = [];
            if (node.elements) {
                for (const elem of node.elements) {
                    result.elements.push(convertAstToJson(elem));
                }
            }
            break;

        case ts.SyntaxKind.BindingElement:
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
            break;

        case ts.SyntaxKind.DeleteExpression:
            result.expression = convertAstToJson(node.expression);
            break;

        // Add more cases as needed
    }

    return result;
}

/**
 * Get syntax kind name from kind number.
 */
function getSyntaxKindName(kind) {
    const kindName = ts.SyntaxKind[kind];
    if (kindName) {
        return kindName;
    }

    // Map common kinds
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
        [ts.SyntaxKind.FunctionExpression]: 'FunctionExpression',
    };

    return kindMap[kind] || `Unknown_${kind}`;
}

/**
 * Generate text representation for for...of statement
 */
function generateForOfStatementText(node) {
    const initializer = node.initializer ? convertAstToJson(node.initializer) : null;
    const expression = node.expression ? convertAstToJson(node.expression) : null;
    const statement = node.statement ? convertAstToJson(node.statement) : null;
    const awaitModifier = node.awaitModifier ? true : false;

    let initStr = initializer ? jsonToCodeString(initializer) : '';
    // Add const/let prefix for VariableDeclarationList
    if (initializer && initializer.kindName === 'VariableDeclarationList' && initStr) {
        initStr = DECLARATION_KINDS.CONST + ' ' + initStr;
    }
    const exprStr = expression ? jsonToCodeString(expression) : '';

    let result = awaitModifier ? KEYWORDS.FOR + ' await (' : KEYWORDS.FOR + ' (';
    result += initStr + ' of ' + exprStr + ') ';

    if (statement) {
        result += '{\n' + jsonToCodeString(statement) + '\n}';
    } else {
        result += '{ }';
    }

    return result;
}

/**
 * Generate text representation for for...in statement
 */
function generateForInStatementText(node) {
    const initializer = node.initializer ? convertAstToJson(node.initializer) : null;
    const expression = node.expression ? convertAstToJson(node.expression) : null;
    const statement = node.statement ? convertAstToJson(node.statement) : null;

    let initStr = initializer ? jsonToCodeString(initializer) : '';
    // Add const/let prefix for VariableDeclarationList
    if (initializer && initializer.kindName === 'VariableDeclarationList' && initStr) {
        initStr = DECLARATION_KINDS.CONST + ' ' + initStr;
    }
    const exprStr = expression ? jsonToCodeString(expression) : '';

    let result = KEYWORDS.FOR + ' (' + initStr + ' in ' + exprStr + ') ';

    if (statement) {
        result += '{\n' + jsonToCodeString(statement) + '\n}';
    } else {
        result += '{ }';
    }

    return result;
}

/**
 * Generate text representation for while statement
 */
function generateWhileStatementText(node) {
    const expression = node.expression ? convertAstToJson(node.expression) : null;
    const statement = node.statement ? convertAstToJson(node.statement) : null;

    const condStr = expression ? jsonToCodeString(expression) : '';

    let result = KEYWORDS.WHILE + ' (' + condStr + ') ';

    if (statement) {
        result += '{\n' + jsonToCodeString(statement) + '\n}';
    } else {
        result += '{ }';
    }

    return result;
}

/**
 * Generate text representation for do...while statement
 */
function generateDoStatementText(node) {
    const expression = node.expression ? convertAstToJson(node.expression) : null;
    const statement = node.statement ? convertAstToJson(node.statement) : null;

    const condStr = expression ? jsonToCodeString(expression) : '';

    let result = KEYWORDS.DO + ' ';

    if (statement) {
        result += '{\n' + jsonToCodeString(statement) + '\n} ';
    } else {
        result += '{ } ';
    }

    result += KEYWORDS.WHILE + ' (' + condStr + ')';

    return result;
}

/**
 * Generate text representation for for statement
 */
function generateForStatementText(node) {
    const initializer = node.initializer ? convertAstToJson(node.initializer) : null;
    const condition = node.condition ? convertAstToJson(node.condition) : null;
    const incrementor = node.incrementor ? convertAstToJson(node.incrementor) : null;
    const statement = node.statement ? convertAstToJson(node.statement) : null;

    let initStr = initializer ? jsonToCodeString(initializer) : '';
    // Add const/let prefix for VariableDeclarationList in initializer position
    if (initializer && initializer.kindName === 'VariableDeclarationList' && initStr) {
        // Use the declarationKind from the VariableDeclarationList
        const declKind = initializer.declarationKind || DECLARATION_KINDS.DEFAULT;
        initStr = declKind + ' ' + initStr;
    }
    const condStr = condition ? jsonToCodeString(condition) : '';
    const incrStr = incrementor ? jsonToCodeString(incrementor) : '';

    let result = KEYWORDS.FOR + ' (' + initStr + '; ' + condStr + '; ' + incrStr + ') ';

    if (statement) {
        result += '{\n' + jsonToCodeString(statement) + '\n}';
    } else {
        result += '{ }';
    }

    return result;
}

/**
 * Generate text representation for switch statement
 */
function generateSwitchStatementText(node) {
    const expression = node.expression ? convertAstToJson(node.expression) : null;
    const caseBlock = node.caseBlock ? convertAstToJson(node.caseBlock) : null;

    const exprStr = expression ? jsonToCodeString(expression) : '';

    let result = KEYWORDS.SWITCH + ' (' + exprStr + ') {\n';

    if (caseBlock && caseBlock.clauses) {
        for (const clause of caseBlock.clauses) {
            if (clause.kindName === 'CaseClause') {
                const caseExpr = clause.expression ? jsonToCodeString(clause.expression) : '';
                result += '  ' + KEYWORDS.CASE + ' ' + caseExpr + ':\n';

                if (clause.statements && clause.statements.length > 0) {
                    for (const stmt of clause.statements) {
                        result += '    ' + jsonToCodeString(stmt) + '\n';
                    }
                }
                result += '    ' + KEYWORDS.BREAK + ';\n';
            } else if (clause.kindName === 'DefaultClause') {
                result += '  ' + KEYWORDS.DEFAULT + ':\n';

                if (clause.statements && clause.statements.length > 0) {
                    for (const stmt of clause.statements) {
                        result += '    ' + jsonToCodeString(stmt) + '\n';
                    }
                }
            }
        }
    }

    result += '}';
    return result;
}

/**
 * Generate text representation for try-catch-finally statement
 */
function generateTryStatementText(node) {
    const tryBlock = node.tryBlock ? convertAstToJson(node.tryBlock) : null;
    const catchClause = node.catchClause ? convertAstToJson(node.catchClause) : null;
    const finallyBlock = node.finallyBlock ? convertAstToJson(node.finallyBlock) : null;

    let result = KEYWORDS.TRY + ' {\n' + jsonToCodeString(tryBlock) + '\n}';

    if (catchClause) {
        const varDecl = catchClause.variableDeclaration;
        const varName = varDecl ? jsonToCodeString(varDecl) : '';
        const catchBlock = catchClause.block;

        if (varName) {
            result += ' ' + KEYWORDS.CATCH + ' (' + varName + ') {\n' + jsonToCodeString(catchBlock) + '\n}';
        } else {
            result += ' ' + KEYWORDS.CATCH + ' {\n' + jsonToCodeString(catchBlock) + '\n}';
        }
    }

    if (finallyBlock) {
        result += ' ' + KEYWORDS.FINALLY + ' {\n' + jsonToCodeString(finallyBlock) + '\n}';
    }

    return result;
}

/**
 * Generate JavaScript-compatible text for arrow function (without type annotations)
 */
function generateArrowFunctionText(node) {
    const params = (node.parameters || []).map(param => {
        let paramText = '';
        // Handle rest parameter (...)
        if (param.dotDotDotToken) {
            paramText += '...';
        }
        // Handle binding patterns (destructuring in parameters)
        if (param.name && param.name.kind) {
            const bindingKind = getSyntaxKindName(param.name.kind);
            if (bindingKind === SYNTAX_KIND_NAMES.OBJECT_BINDING_PATTERN || bindingKind === SYNTAX_KIND_NAMES.ARRAY_BINDING_PATTERN) {
                // For binding patterns, use getText() to get the full pattern
                paramText += param.name.getText();
            } else {
                // For simple identifiers, use escapedText
                paramText += param.name?.escapedText || param.name?.text || '';
            }
        } else {
            // Fallback to escapedText or text
            paramText += param.name?.escapedText || param.name?.text || '';
        }
        // Handle default values
        if (param.initializer) {
            const initJson = convertAstToJson(param.initializer);
            paramText += ' = ' + jsonToCodeString(initJson);
        }
        return paramText;
    }).join(', ');

    // Check if the arrow function has async modifier
    const hasAsync = node.modifiers && node.modifiers.some(m =>
        m.kind === ts.SyntaxKind.AsyncKeyword
    );

    let result = '';
    if (hasAsync) {
        result += KEYWORDS.ASYNC + ' ';
    }
    result += '(' + params + ') => ';

    // Handle body - convert to JSON and use jsonToCodeString to strip TypeScript syntax
    if (node.body) {
        const bodyJson = convertAstToJson(node.body);
        const bodyCode = jsonToCodeString(bodyJson);

        if (node.body.kind === ts.SyntaxKind.Block) {
            // Block body: () => { statements }
            // jsonToCodeString already handles Block with proper indentation
            result += '{\n' + bodyCode + '\n}';
        } else {
            // Expression body: () => expression
            result += bodyCode;
        }
    }

    return result;
}

/**
 * Generate JavaScript-compatible text for function expression (without type annotations)
 */
function generateFunctionExpressionText(node) {
    const params = (node.parameters || []).map(param => {
        let paramText = '';
        // Handle rest parameter (...)
        if (param.dotDotDotToken) {
            paramText += '...';
        }
        // Handle binding patterns (destructuring in parameters)
        if (param.name && param.name.kind) {
            const bindingKind = getSyntaxKindName(param.name.kind);
            if (bindingKind === SYNTAX_KIND_NAMES.OBJECT_BINDING_PATTERN || bindingKind === SYNTAX_KIND_NAMES.ARRAY_BINDING_PATTERN) {
                // For binding patterns, use getText() to get the full pattern
                paramText += param.name.getText();
            } else {
                // For simple identifiers, use escapedText
                paramText += param.name?.escapedText || param.name?.text || '';
            }
        } else {
            // Fallback to escapedText or text
            paramText += param.name?.escapedText || param.name?.text || '';
        }
        // Handle default values
        if (param.initializer) {
            const initJson = convertAstToJson(param.initializer);
            paramText += ' = ' + jsonToCodeString(initJson);
        }
        return paramText;
    }).join(', ');

    // Check if the function has async modifier
    const hasAsync = node.modifiers && node.modifiers.some(m =>
        m.kind === ts.SyntaxKind.AsyncKeyword
    );

    // Get function name if present
    const name = node.name?.escapedText || node.name?.text || '';

    let result = '';
    if (hasAsync) {
        result += KEYWORDS.ASYNC + ' ';
    }
    result += KEYWORDS.FUNCTION + ' ' + name + '(' + params + ') ';

    // Handle body - convert to JSON and use jsonToCodeString to strip TypeScript syntax
    if (node.body) {
        const bodyJson = convertAstToJson(node.body);
        const bodyCode = jsonToCodeString(bodyJson);

        if (node.body.kind === ts.SyntaxKind.Block) {
            // Block body: function() { statements }
            result += '{\n' + bodyCode + '\n}';
        } else {
            // Expression body (rare but possible)
            result += '{ ' + bodyCode + ' }';
        }
    }

    return result;
}

/**
 * Generate text representation for get accessor (without type annotations)
 */
function generateGetAccessorText(node) {
    const name = node.name ? node.name.getText() : '';
    let result = KEYWORDS.GET + ' ' + name + '() ';
    if (node.body) {
        const bodyJson = convertAstToJson(node.body);
        const bodyCode = bodyJson ? jsonToCodeString(bodyJson) : '{}';
        result += '{ ' + bodyCode + ' }';
    } else {
        result += '{}';
    }
    return result;
}

/**
 * Generate text representation for set accessor (without type annotations)
 */
function generateSetAccessorText(node) {
    const name = node.name ? node.name.getText() : '';
    const params = (node.parameters || []).map(p => p.name?.getText() || '').join(', ');
    let result = KEYWORDS.SET + ' ' + name + '(' + params + ') ';
    if (node.body) {
        const bodyJson = convertAstToJson(node.body);
        const bodyCode = bodyJson ? jsonToCodeString(bodyJson) : '{}';
        result += '{ ' + bodyCode + ' }';
    } else {
        result += '{}';
    }
    return result;
}

/**
 * Generate text representation for class expression (without type annotations)
 */
function generateClassExpressionText(node) {
    const className = node.name ? node.name.getText() : '';

    let result = KEYWORDS.CLASS + ' ';
    if (className) {
        result += className + ' ';
    }

    // Handle extends clause
    if (node.heritageClauses) {
        for (const clause of node.heritageClauses) {
            const tokenName = getSyntaxKindName(clause.token);
            if (tokenName === 'ExtendsKeyword') {
                if (clause.types && clause.types.length > 0) {
                    result += KEYWORDS.EXTENDS + ' ' + clause.types[0].expression.getText() + ' ';
                }
            }
        }
    }

    result += '{ ';

    // Handle members - generate them directly
    if (node.members) {
        const memberTexts = [];
        for (const member of node.members) {
            const memberKind = getSyntaxKindName(member.kind);
            if (memberKind === 'Constructor') {
                // Generate constructor directly
                memberTexts.push(generateConstructorText(member));
            } else if (memberKind === 'MethodDeclaration') {
                // Generate method directly
                memberTexts.push(generateMethodDeclarationText(member));
            } else if (memberKind === 'PropertyDeclaration') {
                // Generate property directly
                memberTexts.push(generatePropertyDeclarationText(member));
            }
        }
        result += memberTexts.join(' ');
    }

    result += ' }';

    return result;
}

/**
 * Generate text representation for constructor (without type annotations)
 */
function generateConstructorText(node) {
    const params = (node.parameters || []).map(p => p.name?.getText() || '').join(', ');
    return `${KEYWORDS.CONSTRUCTOR}(${params}) { ${generateBlockBodyText(node.body)} }`;
}

/**
 * Generate text representation for method declaration (without type annotations)
 */
function generateMethodDeclarationText(node) {
    const name = node.name?.getText() || '';
    const params = (node.parameters || []).map(p => p.name?.getText() || '').join(', ');
    return `${name}(${params}) { ${generateBlockBodyText(node.body)} }`;
}

/**
 * Generate text representation for property declaration (without type annotations)
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
 * Generate text representation for block body
 */
function generateBlockBodyText(blockNode) {
    if (!blockNode || !blockNode.statements) {
        return '';
    }
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
 * Generate text representation for throw statement
 */
function generateThrowStatementText(node) {
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    return expr ? KEYWORDS.THROW + ' ' + jsonToCodeString(expr) : KEYWORDS.THROW;
}

/**
 * Generate text representation for property access expression (handles non-null assertion and optional chaining)
 */
function generatePropertyAccessExpressionText(node) {
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    const name = node.name ? node.name.escapedText : '';
    // Check for optional chaining (?.)
    const dotOperator = node.questionDotToken ? '?.' : '.';
    return expr ? jsonToCodeString(expr) + dotOperator + name : name;
}

/**
 * Generate text representation for non-null assertion (removes the ! operator)
 */
function generateNonNullExpressionText(node) {
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    return expr ? jsonToCodeString(expr) : '';
}

/**
 * Generate text representation for as-expression (without the type assertion)
 */
function generateAsExpressionText(node) {
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    return expr ? jsonToCodeString(expr) : '';
}

/**
 * Generate text representation for import expression
 */
function generateImportExpressionText(node) {
    // TypeScript ImportExpression uses 'expression' field
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    const modulePath = expr ? jsonToCodeString(expr) : '';
    return 'import(' + modulePath + ')';
}

/**
 * Generate text representation for object literal expression (strips TypeScript syntax)
 */
function generateObjectLiteralExpressionText(node) {
    if (!node.properties || node.properties.length === 0) {
        return '{}';
    }

    const props = node.properties.map(prop => {
        if (prop.kind === ts.SyntaxKind.PropertyAssignment) {
            const name = prop.name.getText();
            const valueJson = convertAstToJson(prop.initializer);
            // For object literals in code generation, we need to recursively process the value
            // Use jsonToCodeString with the correct field 'value' for PropertyAssignment JSON
            const valueStr = valueJson ? jsonToCodeString(valueJson) : '';
            return name + ': ' + valueStr;
        } else if (prop.kind === ts.SyntaxKind.ShorthandPropertyAssignment) {
            return prop.name.getText();
        } else if (prop.kind === ts.SyntaxKind.SpreadAssignment) {
            const exprJson = convertAstToJson(prop.expression);
            const exprStr = exprJson ? jsonToCodeString(exprJson) : '';
            return '...' + exprStr;
        } else if (prop.kind === ts.SyntaxKind.MethodDeclaration) {
            // Handle method shorthand: { methodName() { ... } }
            const methodJson = convertAstToJson(prop);
            return methodJson ? jsonToCodeString(methodJson) : '';
        } else if (prop.kind === ts.SyntaxKind.GetAccessor) {
            // Handle getter: { get propName() { ... } }
            const accessorJson = convertAstToJson(prop);
            return accessorJson ? jsonToCodeString(accessorJson) : '';
        } else if (prop.kind === ts.SyntaxKind.SetAccessor) {
            // Handle setter: { set propName(val) { ... } }
            const accessorJson = convertAstToJson(prop);
            return accessorJson ? jsonToCodeString(accessorJson) : '';
        }
        return '';
    }).join(', ');

    return '{' + props + '}';
}

/**
 * Generate text representation for template expression (strips TypeScript syntax)
 */
function generateTemplateExpressionText(node) {
    if (!node.templateSpans || node.templateSpans.length === 0) {
        return '`' + escapeTemplateLiteral(node.head ? node.head.text : '') + '`';
    }

    let result = '`';
    result += escapeTemplateLiteral(node.head ? node.head.text : '');

    for (const span of node.templateSpans) {
        const exprJson = convertAstToJson(span.expression);
        const exprStr = exprJson ? jsonToCodeString(exprJson) : '';
        result += '${' + exprStr + '}';
        result += escapeTemplateLiteral(span.literal ? span.literal.text : '');
    }

    result += '`';
    return result;
}

/**
 * Escape special characters in template literals
 */
function escapeTemplateLiteral(str) {
    if (!str) return '';
    return str.replace(/\\/g, '\\\\').replace(/`/g, '\\`').replace(/\$\{/g, '\\${');
}

/**
 * Generate text representation for statement
 */
function jsonToCodeString(json) {
    if (!json) return '';

    switch (json.kindName) {
        case 'Block': {
            if (json.statements && json.statements.length > 0) {
                return json.statements.map(s => '  ' + jsonToCodeString(s)).join('\n');
            }
            return '';
        }

        case 'ExpressionStatement': {
            const expr = json.expression;
            if (expr) {
                const exprStr = jsonToCodeString(expr);
                return exprStr + ';';
            }
            return '';
        }

        case 'ReturnStatement': {
            const retExpr = json.expression;
            if (retExpr) {
                const retExprStr = jsonToCodeString(retExpr);
                return KEYWORDS.RETURN + ' ' + retExprStr + ';';
            }
            return KEYWORDS.RETURN + ';';
        }

        case 'VariableStatement':
        case 'FirstStatement': {
            const declList = json.declarationList;
            if (declList && declList.declarations) {
                const decls = declList.declarations;
                if (decls && decls.length > 0) {
                    const decl = decls[0];
                    const name = decl.name;
                    const init = decl.initializer;
                    // Use the declarationKind (const/let/var) from the VariableDeclarationList
                    const declKind = declList.declarationKind || DECLARATION_KINDS.DEFAULT;
                    let result = declKind + ' ' + name;
                    if (init) {
                        result += ' = ' + jsonToCodeString(init);
                    } else if (declKind === DECLARATION_KINDS.CONST) {
                        // const declarations require an initializer in JavaScript
                        result += ' = ' + EMPTY_VALUES.UNDEFINED;
                    }
                    return result + ';';
                }
            }
            return '// variable';
        }

        case 'VariableDeclarationList': {
            if (Array.isArray(json.declarations) && json.declarations.length > 0) {
                const parts = json.declarations.map(decl => jsonToCodeString(decl));
                return parts.join(', ');
            }
            return jsonToCodeString(json.declarations);
        }

        case 'VariableDeclaration': {
            let result = json.name;
            if (json.initializer) {
                result += ' = ' + jsonToCodeString(json.initializer);
            }
            return result;
        }

        case 'Identifier':
            return json.text || json.name || '';

        case 'ThisKeyword':
            return 'this';

        case 'FirstLiteralToken':
            return json.text || '';

        case 'StringLiteral':
            // Always return with quotes for string literals
            // Check if text already has quotes
            if (json.text && (json.text.startsWith('"') || json.text.startsWith("'"))) {
                return json.text;
            }
            const val = json.text || json.value || '';
            return '"' + val + '"';

        case 'PropertyAccessExpression': {
            const expr = json.expression;
            const name = json.name;
            // Check for optional chaining (?.)
            const dotOperator = json.questionDotToken ? '?.' : '.';
            return jsonToCodeString(expr) + dotOperator + name;
        }

        case 'ElementAccessExpression': {
            const elemExpr = json.expression;
            // The argument can be named 'argument' or 'argumentExpression'
            const arg = json.argument || json.argumentExpression;
            // Check for optional chaining (?.[)
            const accessor = json.questionDotToken ? '?.' : '';
            return jsonToCodeString(elemExpr) + accessor + '[' + jsonToCodeString(arg) + ']';
        }

        case 'BinaryExpression': {
            const left = json.left;
            const op = json.operator;
            const right = json.right;
            return jsonToCodeString(left) + ' ' + op + ' ' + jsonToCodeString(right);
        }

        case 'PrefixUnaryExpression': {
            const operand = json.operand;
            const prefixOp = json.operator;
            return prefixOp + jsonToCodeString(operand);
        }

        case 'PostfixUnaryExpression': {
            const postOperand = json.operand;
            const postfixOp = json.operator;
            return jsonToCodeString(postOperand) + postfixOp;
        }

        case 'CallExpression': {
            const callExpr = json.expression;
            const args = json.arguments || [];
            const argStr = args.map(arg => {
                // If arg is a simple string that looks like an identifier (not JSON object), use it directly
                if (typeof arg === 'string' && !arg.startsWith('{')) {
                    return arg;
                }
                return jsonToCodeString(arg);
            }).join(', ');
            return jsonToCodeString(callExpr) + '(' + argStr + ')';
        }

        case 'IfStatement': {
            const ifExpr = json.expression;
            const thenStmt = json.thenStatement;
            const elseStmt = json.elseStatement;
            let result = KEYWORDS.IF + ' (' + jsonToCodeString(ifExpr) + ') {\n' + jsonToCodeString(thenStmt) + '\n}';
            if (elseStmt) {
                result += ' ' + KEYWORDS.ELSE + ' {\n' + jsonToCodeString(elseStmt) + '\n}';
            }
            return result;
        }

        case 'ConditionalExpression': {
            const cond = json.condition;
            const whenTrue = json.whenTrue;
            const whenFalse = json.whenFalse;
            return jsonToCodeString(cond) + ' ? ' + jsonToCodeString(whenTrue) + ' : ' + jsonToCodeString(whenFalse);
        }

        case 'TypeOfExpression': {
            return KEYWORDS.TYPEOF + ' ' + jsonToCodeString(json.expression);
        }

        case 'ArrayLiteralExpression': {
            if (json.elements) {
                const elemStr = json.elements.map(e => jsonToCodeString(e)).join(', ');
                return '[' + elemStr + ']';
            }
            return '[]';
        }

        case 'ParenthesizedExpression': {
            return '(' + jsonToCodeString(json.expression) + ')';
        }

        case 'AsExpression': {
            return jsonToCodeString(json.expression);
        }

        case 'NonNullExpression': {
            // Non-null assertion (!) - just return the expression without the !
            return jsonToCodeString(json.expression);
        }

        case 'AwaitExpression': {
            return KEYWORDS.AWAIT + ' ' + jsonToCodeString(json.expression);
        }

        case 'ThrowStatement': {
            const expr = json.expression ? jsonToCodeString(json.expression) : '';
            return KEYWORDS.THROW + ' ' + expr;
        }

        case 'ObjectLiteralExpression': {
            if (json.properties) {
                const propStr = json.properties.map(p => jsonToCodeString(p)).join(', ');
                return '{' + propStr + '}';
            }
            return '{}';
        }

        case 'PropertyAssignment': {
            return json.name + ': ' + jsonToCodeString(json.value);
        }

        case 'NewExpression': {
            const expr = json.expression;
            const args = json.arguments || [];
            const argStr = args.map(arg => jsonToCodeString(arg)).join(', ');
            return KEYWORDS.NEW + ' ' + jsonToCodeString(expr) + '(' + argStr + ')';
        }

        case 'ArrowFunction': {
            // Use pre-generated text if available
            if (json.text) return json.text;
            // Otherwise generate from parameters and body
            const params = (json.parameters || []).map(p => {
                let param = '';
                if (p.hasDotDotDotDot) param += '...';
                // Use bindingPattern if present (for destructuring parameters)
                if (p.bindingPattern) {
                    param += jsonToCodeString(p.bindingPattern);
                } else {
                    param += p.name;
                }
                if (p.initializerText || (p.initializer && p.initializer.text)) {
                    param += ' = ' + (p.initializerText || p.initializer.text);
                }
                return param;
            }).join(', ');
            const body = json.body ? jsonToCodeString(json.body) : '{}';
            // Check if body is a block
            if (body.startsWith('{')) {
                return '(' + params + ') => ' + body;
            } else {
                return '(' + params + ') => { ' + body + ' }';
            }
        }

        case 'FunctionExpression': {
            // Use pre-generated text if available
            if (json.text) return json.text;
            // Otherwise generate from parameters and body
            const name = json.name || '';
            const params = (json.parameters || []).map(p => {
                let param = '';
                if (p.hasDotDotDotDot) param += '...';
                // Use bindingPattern if present (for destructuring parameters)
                if (p.bindingPattern) {
                    param += jsonToCodeString(p.bindingPattern);
                } else {
                    param += p.name;
                }
                if (p.initializerText || (p.initializer && p.initializer.text)) {
                    param += ' = ' + (p.initializerText || p.initializer.text);
                }
                return param;
            }).join(', ');
            const body = json.body ? jsonToCodeString(json.body) : '{}';
            if (name) {
                return KEYWORDS.FUNCTION + ' ' + name + '(' + params + ') ' + body;
            } else {
                return KEYWORDS.FUNCTION + '(' + params + ') ' + body;
            }
        }

        case 'ClassExpression': {
            // Use pre-generated text if available
            if (json.text) return json.text;
            // Fallback to class placeholder
            return 'class {}';
        }

        case 'GetAccessor': {
            // Use pre-generated text if available
            if (json.text) return json.text;
            // Otherwise generate from name and body
            const name = json.name || '';
            const body = json.body ? jsonToCodeString(json.body) : '{}';
            return KEYWORDS.GET + ' ' + name + '() { ' + body + ' }';
        }

        case 'SetAccessor': {
            // Use pre-generated text if available
            if (json.text) return json.text;
            // Otherwise generate from name, parameters and body
            const name = json.name || '';
            const params = (json.parameters || []).map(p => p.name || '').join(', ');
            const body = json.body ? jsonToCodeString(json.body) : '{}';
            return KEYWORDS.SET + ' ' + name + '(' + params + ') { ' + body + ' }';
        }

        case 'SpreadElement': {
            return '...' + jsonToCodeString(json.expression);
        }

        case 'SpreadAssignment': {
            return '...' + jsonToCodeString(json.expression);
        }

        case 'DeleteExpression': {
            const expr = json.expression;
            if (expr) {
                return KEYWORDS.DELETE + ' ' + jsonToCodeString(expr);
            }
            return KEYWORDS.DELETE + ' ' + EMPTY_VALUES.UNDEFINED;
        }

        case 'ObjectBindingPattern': {
            const elements = json.elements || [];
            if (elements.length === 0) return '{}';
            const props = elements.map(elem => jsonToCodeString(elem)).join(', ');
            return '{' + props + '}';
        }

        case 'ArrayBindingPattern': {
            const elements = json.elements || [];
            const props = elements.map(elem => jsonToCodeString(elem)).join(', ');
            return '[' + props + ']';
        }

        case 'BindingElement': {
            let elem = '';
            if (json.dotDotDotToken) {
                elem += '...';
            }
            if (json.propertyName) {
                elem += json.propertyName + ': ';
            }
            elem += json.name || '';
            if (json.initializer) {
                const initStr = jsonToCodeString(json.initializer);
                elem += ' = ' + initStr;
            }
            return elem;
        }

        case 'TemplateExpression': {
            // Use pre-generated text
            if (json.text) return json.text;
            return '/* template expression */';
        }

        case 'NoSubstitutionTemplateLiteral': {
            const text = json.text || '';
            return '`' + escapeTemplateLiteral(text) + '`';
        }

        case 'ImportExpression': {
            // Use the expression field for the module specifier
            const expr = json.expression;
            const modulePath = expr ? jsonToCodeString(expr) : '';
            return 'import(' + modulePath + ')';
        }

        case 'BreakStatement': {
            if (json.label) {
                return KEYWORDS.BREAK + ' ' + json.label + ';';
            }
            return KEYWORDS.BREAK + ';';
        }

        case 'ContinueStatement': {
            if (json.label) {
                return KEYWORDS.CONTINUE + ' ' + json.label + ';';
            }
            return KEYWORDS.CONTINUE + ';';
        }

        case 'TryStatement': {
            // Use pre-generated text
            if (json.text) return json.text;
            return '/* try statement */';
        }

        case 'ResourceReferenceExpression': {
            // Handle resource reference conversion ($r and $rawfile)
            if (json.resourceRefType === RESOURCE_REF_TYPES.R) {
                // Convert $r('app.string.name') to __getResourceId__(type, bundle, module, name)
                if (json.arguments && json.arguments.length > 0) {
                    const resourcePath = json.arguments[0];
                    // Remove quotes from string literal
                    const pathStr = resourcePath.replace(REGEX_PATTERNS.QUOTES, '');
                    // Parse format: 'app.type.name' or 'module.type.name'
                    const parts = pathStr.split('.');
                    if (parts.length >= 3) {
                        const module = parts[0];
                        const type = parts[parts.length - 2];
                        const name = parts[parts.length - 1];
                        const typeId = RESOURCE_TYPE_IDS[type] || DEFAULT_RESOURCE_TYPE_ID;
                        return `__getResourceId__(${typeId}, ${EMPTY_VALUES.UNDEFINED}, "${module}", "${name}")`;
                    }
                }
                return `__getResourceId__(${DEFAULT_RESOURCE_TYPE_ID}, ${EMPTY_VALUES.UNDEFINED}, "${EMPTY_VALUES.EMPTY_STRING}", "${EMPTY_VALUES.EMPTY_STRING}")`;
            } else if (json.resourceRefType === RESOURCE_REF_TYPES.RAWFILE) {
                // Convert $rawfile('icon.png') to __getRawFileId__('icon.png')
                if (json.arguments && json.arguments.length > 0) {
                    const filename = json.arguments[0];
                    // Remove quotes if present
                    const filenameStr = filename.replace(REGEX_PATTERNS.QUOTES, '');
                    return `__getRawFileId__("${filenameStr}")`;
                }
                return '__getRawFileId__("")';
            }
            return json.text || '';
        }

        default:
            // For other expressions, try to use text if available
            if (json.text) return json.text;
            return JSON.stringify(json);
    }
}

main();
