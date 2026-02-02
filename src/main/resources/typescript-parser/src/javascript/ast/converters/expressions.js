/**
 * Expression Node Converters
 * Converts expression AST nodes to JSON format
 * @module lib/converters/expressions
 */

const ts = require('typescript');
const { RESOURCE_REF_FUNCTIONS, SPECIAL_COMPONENTS, KEYWORDS, OPERATORS } = require('../../common/constants');
const { getSyntaxKindName, isBindingPattern, hasAsyncModifier } = require('../../common/utils');

/**
 * Convert CallExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertCallExpression(result, node, convert) {
    result.expression = convert(node.expression);
    result.arguments = convertCallArguments(node, result.expression, convert);
    result.isChainedCall = isChainedCall(result.expression);
    result.componentName = extractComponentName(result.expression);
    result.methodName = extractMethodName(result.expression);
    result.isSpecialComponent = isSpecialComponent(result.expression);
}

/**
 * Convert Identifier node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
function convertIdentifier(result, node) {
    result.name = node.escapedText;
    result.text = node.getText();

    const isResourceFunction = result.name === RESOURCE_REF_FUNCTIONS.R ||
                           result.name === RESOURCE_REF_FUNCTIONS.RAWFILE;
    if (isResourceFunction) {
        result.text = result.name;
    }
}

/**
 * Convert ImportKeyword node.
 * @param {Object} result - Result object to populate
 */
function convertImportKeyword(result) {
    result.name = KEYWORDS.IMPORT;
    result.text = KEYWORDS.IMPORT;
}

/**
 * Convert PropertyAccessExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertPropertyAccessExpression(result, node, convert) {
    result.expression = convert(node.expression);
    result.name = node.name.escapedText;
    result.questionDotToken = !!node.questionDotToken;
    result.text = generatePropertyAccessExpressionText(node, convert);
}

/**
 * Convert NewExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertNewExpression(result, node, convert) {
    result.expression = convert(node.expression);
    result.arguments = [];
    for (const arg of node.arguments || []) {
        result.arguments.push(convert(arg));
    }
}

/**
 * Convert ElementAccessExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertElementAccessExpression(result, node, convert) {
    result.expression = convert(node.expression);
    result.argumentExpression = convert(node.argumentExpression);
    result.questionDotToken = !!node.questionDotToken;
}

/**
 * Convert ParenthesizedExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertParenthesizedExpression(result, node, convert) {
    result.expression = convert(node.expression);
}

/**
 * Convert TypeOfExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertTypeOfExpression(result, node, convert) {
    result.expression = convert(node.expression);
}

/**
 * Convert PrefixUnaryExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertPrefixUnaryExpression(result, node, convert) {
    result.operator = ts.tokenToString(node.operator);
    result.operand = convert(node.operand);
}

/**
 * Convert PostfixUnaryExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertPostfixUnaryExpression(result, node, convert) {
    result.operator = ts.tokenToString(node.operator);
    result.operand = convert(node.operand);
}

/**
 * Convert NonNullExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertNonNullExpression(result, node, convert) {
    result.expression = convert(node.expression);
    result.text = generateNonNullExpressionText(node, convert);
}

/**
 * Convert AsExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertAsExpression(result, node, convert) {
    result.expression = convert(node.expression);
    result.type = node.type ? node.type.getText() : null;
    result.text = generateAsExpressionText(node, convert);
}

/**
 * Convert TypeAssertion node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertTypeAssertion(result, node, convert) {
    result.expression = convert(node.expression);
    result.type = node.type ? node.type.getText() : null;
    result.text = generateAsExpressionText(node, convert);
}

/**
 * Convert ImportExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertImportExpression(result, node, convert) {
    result.expression = convert(node.expression);
    result.text = generateImportExpressionText(node, convert);
}

/**
 * Convert ArrowFunction node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertArrowFunction(result, node, convert) {
    result.name = node.name?.escapedText || node.name?.text || '';
    result.parameters = convertParameters(node.parameters, convert);
    result.body = convert(node.body);
    result.text = generateArrowFunctionText(node, convert);
}

/**
 * Convert FunctionExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertFunctionExpression(result, node, convert) {
    result.name = node.name?.escapedText || node.name?.text || '';
    result.parameters = convertParameters(node.parameters, convert);
    result.body = convert(node.body);
    result.text = generateFunctionExpressionText(node, convert);
}

/**
 * Convert AwaitExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertAwaitExpression(result, node, convert) {
    result.expression = convert(node.expression);
}

/**
 * Convert BinaryExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertBinaryExpression(result, node, convert) {
    result.left = convert(node.left);
    result.operator = node.operatorToken.getText();
    result.right = convert(node.right);
}

/**
 * Convert ConditionalExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertConditionalExpression(result, node, convert) {
    result.condition = convert(node.condition);
    result.whenTrue = convert(node.whenTrue);
    result.whenFalse = convert(node.whenFalse);
}

/**
 * Convert ArrayLiteralExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertArrayLiteralExpression(result, node, convert) {
    result.elements = [];
    for (const elem of node.elements || []) {
        result.elements.push(convert(elem));
    }
}

/**
 * Convert SpreadElement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertSpreadElement(result, node, convert) {
    result.expression = convert(node.expression);
}

/**
 * Convert ObjectLiteralExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertObjectLiteralExpression(result, node, convert) {
    result.properties = [];
    for (const prop of node.properties || []) {
        result.properties.push(convert(prop));
    }
    result.text = generateObjectLiteralExpressionText(node, convert);
}

/**
 * Convert PropertyAssignment node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertPropertyAssignment(result, node, convert) {
    result.name = node.name.getText();
    result.value = convert(node.initializer);
}

/**
 * Convert ShorthandPropertyAssignment node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
function convertShorthandPropertyAssignment(result, node) {
    result.name = node.name.getText();
    result.value = {
        kindName: 'Identifier',
        name: node.name.getText(),
        text: node.name.getText()
    };
}

/**
 * Convert SpreadAssignment node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertSpreadAssignment(result, node, convert) {
    result.expression = convert(node.expression);
}

/**
 * Convert ObjectBindingPattern node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertObjectBindingPattern(result, node, convert) {
    result.elements = [];
    for (const elem of node.elements || []) {
        result.elements.push(convert(elem));
    }
}

/**
 * Convert ArrayBindingPattern node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertArrayBindingPattern(result, node, convert) {
    result.elements = [];
    for (const elem of node.elements || []) {
        result.elements.push(convert(elem));
    }
}

/**
 * Convert BindingElement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertBindingElement(result, node, convert) {
    result.name = node.name?.getText() || '';
    if (node.propertyName) {
        result.propertyName = node.propertyName.getText();
    }
    if (node.initializer) {
        result.initializer = convert(node.initializer);
    }
    if (node.dotDotDotToken) {
        result.dotDotDotToken = true;
    }
}

/**
 * Convert DeleteExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertDeleteExpression(result, node, convert) {
    result.expression = convert(node.expression);
}

/**
 * Convert parameters array to JSON with binding pattern support.
 * @param {Array} parameters - Array of parameter nodes
 * @param {Function} convert - Recursive convert function
 * @returns {Array} Converted parameters
 */
function convertParameters(parameters, convert) {
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
                paramJson.bindingPattern = convert(param.name);
                paramJson.name = param.name.getText() || '';
            }
        }

        if (param.initializer) {
            paramJson.initializer = convert(param.initializer);
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

// Helper functions for call expressions
function convertCallArguments(node, expressionJson, convert) {
    const isResourceCall = isResourceReferenceCall(expressionJson);
    const isForEach = isForEachCall(expressionJson);

    const result = [];
    for (const arg of node.arguments || []) {
        if (isForEach) {
            result.push(convert(arg));
        } else if (isResourceCall) {
            result.push(arg.getText());
        } else {
            result.push(convertCallArgument(arg, convert));
        }
    }
    return result;
}

function convertCallArgument(arg, convert) {
    const argJson = convert(arg);

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

function isResourceReferenceCall(expressionJson) {
    return expressionJson?.kindName === 'Identifier' &&
           (expressionJson.name === RESOURCE_REF_FUNCTIONS.R ||
            expressionJson.name === RESOURCE_REF_FUNCTIONS.RAWFILE);
}

function isForEachCall(expressionJson) {
    return expressionJson?.kindName === 'Identifier' &&
           expressionJson.name === SPECIAL_COMPONENTS.FOR_EACH;
}

function isChainedCall(expressionJson) {
    return expressionJson?.kindName === 'PropertyAccessExpression' &&
           expressionJson.expression?.kindName === 'CallExpression';
}

function extractComponentName(expressionJson) {
    if (expressionJson?.expression?.expression?.kindName === 'Identifier') {
        return expressionJson.expression.expression.name;
    }
    return null;
}

function extractMethodName(expressionJson) {
    return expressionJson?.name || null;
}

function isSpecialComponent(expressionJson) {
    return expressionJson?.kindName === 'Identifier' &&
           (expressionJson.name === SPECIAL_COMPONENTS.FOR_EACH ||
            expressionJson.name === SPECIAL_COMPONENTS.IF);
}

// Text generation helpers
function generatePropertyAccessExpressionText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const expr = node.expression ? convert(node.expression) : null;
    const name = node.name ? node.name.escapedText : '';
    const dotOperator = node.questionDotToken ? OPERATORS.QUESTION_DOT : OPERATORS.DOT;

    return expr ? jsonToCodeString(expr) + dotOperator + name : name;
}

function generateNonNullExpressionText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const expr = node.expression ? convert(node.expression) : null;
    return expr ? jsonToCodeString(expr) : '';
}

function generateAsExpressionText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const expr = node.expression ? convert(node.expression) : null;
    return expr ? jsonToCodeString(expr) : '';
}

function generateImportExpressionText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const expr = node.expression ? convert(node.expression) : null;
    const modulePath = expr ? jsonToCodeString(expr) : '';

    return 'import(' + modulePath + ')';
}

function generateArrowFunctionText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const params = generateFunctionParameters(node.parameters, convert);
    const hasAsync = hasAsyncModifier(node.modifiers);

    let result = hasAsync ? KEYWORDS.ASYNC + ' ' : '';
    result += OPERATORS.OPEN_PAREN + params + OPERATORS.CLOSE_PAREN + OPERATORS.ARROW;

    if (node.body) {
        const bodyJson = convert(node.body);
        const bodyCode = jsonToCodeString(bodyJson);

        if (node.body.kind === ts.SyntaxKind.Block) {
            result += OPERATORS.OPEN_BRACE + '\n' + bodyCode + '\n' + OPERATORS.CLOSE_BRACE;
        } else {
            result += bodyCode;
        }
    }

    return result;
}

function generateFunctionExpressionText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const params = generateFunctionParameters(node.parameters, convert);
    const name = node.name?.escapedText || node.name?.text || '';
    const hasAsync = hasAsyncModifier(node.modifiers);

    let result = hasAsync ? KEYWORDS.ASYNC + ' ' : '';
    result += KEYWORDS.FUNCTION + ' ' + name + OPERATORS.OPEN_PAREN + params + OPERATORS.CLOSE_PAREN + ' ';

    if (node.body) {
        const bodyJson = convert(node.body);
        const bodyCode = jsonToCodeString(bodyJson);

        if (node.body.kind === ts.SyntaxKind.Block) {
            result += OPERATORS.OPEN_BRACE + '\n' + bodyCode + '\n' + OPERATORS.CLOSE_BRACE;
        } else {
            result += OPERATORS.OPEN_BRACE + ' ' + bodyCode + ' ' + OPERATORS.CLOSE_BRACE;
        }
    }

    return result;
}

function generateFunctionParameters(parameters, convert) {
    const { isBindingPattern, getSyntaxKindName } = require('../../common/utils');

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
            const initJson = convert(param.initializer);
            const { jsonToCodeString } = require('../../codegen');
            paramText += OPERATORS.EQUALS + jsonToCodeString(initJson);
        }

        return paramText;
    }).join(OPERATORS.COMMA + ' ');
}

function generateObjectLiteralExpressionText(node, convert) {
    const ts = require('typescript');
    const { jsonToCodeString } = require('../../codegen');

    if (!node.properties || node.properties.length === 0) {
        return '{}';
    }

    const props = node.properties.map(prop => {
        if (prop.kind === ts.SyntaxKind.PropertyAssignment) {
            const name = prop.name.getText();
            const valueJson = convert(prop.initializer);
            const valueStr = valueJson ? jsonToCodeString(valueJson) : '';
            return name + ': ' + valueStr;
        } else if (prop.kind === ts.SyntaxKind.ShorthandPropertyAssignment) {
            return prop.name.getText();
        } else if (prop.kind === ts.SyntaxKind.SpreadAssignment) {
            const exprJson = convert(prop.expression);
            const exprStr = exprJson ? jsonToCodeString(exprJson) : '';
            return '...' + exprStr;
        } else if (prop.kind === ts.SyntaxKind.MethodDeclaration) {
            const methodJson = convert(prop);
            return methodJson ? jsonToCodeString(methodJson) : '';
        } else if (prop.kind === ts.SyntaxKind.GetAccessor) {
            const accessorJson = convert(prop);
            return accessorJson ? jsonToCodeString(accessorJson) : '';
        } else if (prop.kind === ts.SyntaxKind.SetAccessor) {
            const accessorJson = convert(prop);
            return accessorJson ? jsonToCodeString(accessorJson) : '';
        }
        return '';
    }).join(', ');

    return OPERATORS.OPEN_BRACE + props + OPERATORS.CLOSE_BRACE;
}

module.exports = {
    convertCallExpression,
    convertIdentifier,
    convertImportKeyword,
    convertPropertyAccessExpression,
    convertNewExpression,
    convertElementAccessExpression,
    convertParenthesizedExpression,
    convertTypeOfExpression,
    convertPrefixUnaryExpression,
    convertPostfixUnaryExpression,
    convertNonNullExpression,
    convertAsExpression,
    convertTypeAssertion,
    convertImportExpression,
    convertArrowFunction,
    convertFunctionExpression,
    convertAwaitExpression,
    convertBinaryExpression,
    convertConditionalExpression,
    convertArrayLiteralExpression,
    convertSpreadElement,
    convertObjectLiteralExpression,
    convertPropertyAssignment,
    convertShorthandPropertyAssignment,
    convertSpreadAssignment,
    convertObjectBindingPattern,
    convertArrayBindingPattern,
    convertBindingElement,
    convertDeleteExpression,
    convertParameters
};
