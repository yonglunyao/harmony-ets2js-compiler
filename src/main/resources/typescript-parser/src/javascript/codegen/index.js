/**
 * JSON to Code String Generator
 * Converts JSON AST representation back to code strings
 * @module lib/codegen
 */

const {
    KEYWORDS,
    EMPTY_VALUES,
    OPERATORS,
    DECLARATION_KINDS,
    RESOURCE_REF_TYPES,
    SYNTAX_KIND_NAMES,
    REGEX_PATTERNS,
    RESOURCE_TYPE_IDS,
    DEFAULT_RESOURCE_TYPE_ID,
    RESOURCE_REF_FUNCTIONS
} = require('../common/constants');

/**
 * Map of kind names to code generator functions.
 * @constant
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

// Code generator functions
function generateBlockCode(json) {
    if (json.statements?.length > 0) {
        return json.statements.map(s => '  ' + jsonToCodeString(s)).join('\n');
    }
    return '';
}

function generateExpressionStatementCode(json) {
    const expr = json.expression;
    if (!expr) return '';
    return jsonToCodeString(expr) + OPERATORS.SEMICOLON;
}

function generateReturnStatementCode(json) {
    const retExpr = json.expression;
    if (retExpr) {
        return KEYWORDS.RETURN + ' ' + jsonToCodeString(retExpr) + OPERATORS.SEMICOLON;
    }
    return KEYWORDS.RETURN + OPERATORS.SEMICOLON;
}

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

function generateVariableDeclarationListCode(json) {
    if (Array.isArray(json.declarations) && json.declarations.length > 0) {
        return json.declarations.map(decl => jsonToCodeString(decl)).join(', ');
    }
    return jsonToCodeString(json.declarations);
}

function generateVariableDeclarationCode(json) {
    let result = json.name;
    if (json.initializer) {
        result += OPERATORS.EQUALS + jsonToCodeString(json.initializer);
    }
    return result;
}

function generateIdentifierCode(json) {
    return json.text || json.name || '';
}

function generateStringLiteralCode(json) {
    if (json.text && (json.text.startsWith('"') || json.text.startsWith("'"))) {
        return json.text;
    }
    const val = json.text || json.value || '';
    return OPERATORS.DOUBLE_QUOTE + val + OPERATORS.DOUBLE_QUOTE;
}

function generatePropertyAccessExpressionCode(json) {
    const expr = json.expression;
    const name = json.name;
    const dotOperator = json.questionDotToken ? OPERATORS.QUESTION_DOT : OPERATORS.DOT;
    return jsonToCodeString(expr) + dotOperator + name;
}

function generateElementAccessExpressionCode(json) {
    const elemExpr = json.expression;
    const arg = json.argument || json.argumentExpression;
    const accessor = json.questionDotToken ? OPERATORS.QUESTION_DOT : '';

    return jsonToCodeString(elemExpr) + accessor + OPERATORS.OPEN_BRACKET + jsonToCodeString(arg) + OPERATORS.CLOSE_BRACKET;
}

function generateBinaryExpressionCode(json) {
    const left = json.left;
    const op = json.operator;
    const right = json.right;
    return jsonToCodeString(left) + ' ' + op + ' ' + jsonToCodeString(right);
}

function generatePrefixUnaryExpressionCode(json) {
    return json.operator + jsonToCodeString(json.operand);
}

function generatePostfixUnaryExpressionCode(json) {
    return jsonToCodeString(json.operand) + json.operator;
}

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

function generateConditionalExpressionCode(json) {
    const cond = json.condition;
    const whenTrue = json.whenTrue;
    const whenFalse = json.whenFalse;
    return jsonToCodeString(cond) + ' ? ' + jsonToCodeString(whenTrue) + ' : ' + jsonToCodeString(whenFalse);
}

function generateTypeOfExpressionCode(json) {
    return KEYWORDS.TYPEOF + ' ' + jsonToCodeString(json.expression);
}

function generateArrayLiteralExpressionCode(json) {
    if (json.elements) {
        const elemStr = json.elements.map(e => jsonToCodeString(e)).join(', ');
        return OPERATORS.OPEN_BRACKET + elemStr + OPERATORS.CLOSE_BRACKET;
    }
    return '[]';
}

function generateParenthesizedExpressionCode(json) {
    return OPERATORS.OPEN_PAREN + jsonToCodeString(json.expression) + OPERATORS.CLOSE_PAREN;
}

function generateAsExpressionCode(json) {
    return jsonToCodeString(json.expression);
}

function generateNonNullExpressionCode(json) {
    return jsonToCodeString(json.expression);
}

function generateAwaitExpressionCode(json) {
    return KEYWORDS.AWAIT + ' ' + jsonToCodeString(json.expression);
}

function generateThrowStatementCode(json) {
    const expr = json.expression ? jsonToCodeString(json.expression) : '';
    return KEYWORDS.THROW + ' ' + expr;
}

function generateObjectLiteralExpressionCode(json) {
    if (json.properties) {
        const propStr = json.properties.map(p => jsonToCodeString(p)).join(', ');
        return OPERATORS.OPEN_BRACE + propStr + OPERATORS.CLOSE_BRACE;
    }
    return '{}';
}

function generatePropertyAssignmentCode(json) {
    return json.name + ': ' + jsonToCodeString(json.value);
}

function generateNewExpressionCode(json) {
    const expr = json.expression;
    const args = json.arguments || [];
    const argStr = args.map(arg => jsonToCodeString(arg)).join(', ');
    return KEYWORDS.NEW + ' ' + jsonToCodeString(expr) + OPERATORS.OPEN_PAREN + argStr + OPERATORS.CLOSE_PAREN;
}

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

function generateClassExpressionCode(json) {
    if (json.text) return json.text;
    return 'class {}';
}

function generateGetAccessorCode(json) {
    if (json.text) return json.text;

    const name = json.name || '';
    const body = json.body ? jsonToCodeString(json.body) : '{}';
    return `${KEYWORDS.GET} ${name}() { ${body} }`;
}

function generateSetAccessorCode(json) {
    if (json.text) return json.text;

    const name = json.name || '';
    const params = (json.parameters || []).map(p => p.name || '').join(', ');
    const body = json.body ? jsonToCodeString(json.body) : '{}';
    return `${KEYWORDS.SET} ${name}(${params}) { ${body} }`;
}

function generateSpreadElementCode(json) {
    return OPERATORS.SPREAD + jsonToCodeString(json.expression);
}

function generateSpreadAssignmentCode(json) {
    return OPERATORS.SPREAD + jsonToCodeString(json.expression);
}

function generateDeleteExpressionCode(json) {
    const expr = json.expression;
    if (expr) {
        return KEYWORDS.DELETE + ' ' + jsonToCodeString(expr);
    }
    return KEYWORDS.DELETE + ' ' + EMPTY_VALUES.UNDEFINED;
}

function generateObjectBindingPatternCode(json) {
    const elements = json.elements || [];
    if (elements.length === 0) return '{}';

    const props = elements.map(elem => jsonToCodeString(elem)).join(', ');
    return OPERATORS.OPEN_BRACE + props + OPERATORS.CLOSE_BRACE;
}

function generateArrayBindingPatternCode(json) {
    const elements = json.elements || [];
    const props = elements.map(elem => jsonToCodeString(elem)).join(', ');
    return OPERATORS.OPEN_BRACKET + props + OPERATORS.CLOSE_BRACKET;
}

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

function generateTemplateExpressionCode(json) {
    if (json.text) return json.text;
    return '/* template expression */';
}

function generateNoSubstitutionTemplateLiteralCode(json) {
    const { escapeTemplateLiteral } = require('../common/utils');
    const text = json.text || '';
    return OPERATORS.BACKTICK + escapeTemplateLiteral(text) + OPERATORS.BACKTICK;
}

function generateImportExpressionCode(json) {
    const expr = json.expression;
    const modulePath = expr ? jsonToCodeString(expr) : '';
    return 'import(' + modulePath + ')';
}

function generateBreakStatementCode(json) {
    if (json.label) {
        return KEYWORDS.BREAK + ' ' + json.label + OPERATORS.SEMICOLON;
    }
    return KEYWORDS.BREAK + OPERATORS.SEMICOLON;
}

function generateContinueStatementCode(json) {
    if (json.label) {
        return KEYWORDS.CONTINUE + ' ' + json.label + OPERATORS.SEMICOLON;
    }
    return KEYWORDS.CONTINUE + OPERATORS.SEMICOLON;
}

function generateTryStatementCode(json) {
    if (json.text) return json.text;
    return '/* try statement */';
}

function generateResourceReferenceExpressionCode(json) {
    if (json.resourceRefType === RESOURCE_REF_TYPES.R) {
        return generateRResourceReference(json);
    }
    if (json.resourceRefType === RESOURCE_REF_TYPES.RAWFILE) {
        return generateRawfileResourceReference(json);
    }
    return json.text || '';
}

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

function generateRawfileResourceReference(json) {
    if (json.arguments?.length > 0) {
        const filename = json.arguments[0].replace(REGEX_PATTERNS.QUOTES, '');
        return `__getRawFileId__("${filename}")`;
    }
    return '__getRawFileId__("")';
}

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

module.exports = {
    jsonToCodeString
};
