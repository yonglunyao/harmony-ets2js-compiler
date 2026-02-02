/**
 * Statement Node Converters
 * Converts statement AST nodes to JSON format
 * @module lib/converters/statements
 */

const { KEYWORDS } = require('../../common/constants');

/**
 * Convert Block node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertBlock(result, node, convert) {
    result.statements = convertStatements(node.statements, convert);
}

/**
 * Convert VariableStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertVariableStatement(result, node, convert) {
    result.declarationList = convert(node.declarationList);
}

/**
 * Convert VariableDeclarationList node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertVariableDeclarationList(result, node, convert) {
    const { getDeclarationKind } = require('../../common/utils');

    result.declarations = [];
    result.declarationKind = getDeclarationKind(node.flags);
    for (const decl of node.declarations || []) {
        const declJson = convert(decl);
        if (declJson) {
            result.declarations.push(declJson);
        }
    }
}

/**
 * Convert VariableDeclaration node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertVariableDeclaration(result, node, convert) {
    result.name = node.name?.getText() || '';
    result.type = node.type?.getText() || '';
    result.initializer = node.initializer ? convert(node.initializer) : null;
}

/**
 * Convert ExpressionStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertExpressionStatement(result, node, convert) {
    result.expression = convert(node.expression);
}

/**
 * Convert ReturnStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertReturnStatement(result, node, convert) {
    result.expression = node.expression ? convert(node.expression) : null;
}

/**
 * Convert IfStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertIfStatement(result, node, convert) {
    result.expression = convert(node.expression);
    result.thenStatement = convert(node.thenStatement);
    result.elseStatement = node.elseStatement ? convert(node.elseStatement) : null;
}

/**
 * Convert ForOfStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertForOfStatement(result, node, convert) {
    result.initializer = convert(node.initializer);
    result.expression = convert(node.expression);
    result.statement = convert(node.statement);
    result.awaitModifier = !!node.awaitModifier;
    result.text = generateForOfStatementText(node, convert);
}

/**
 * Convert ForInStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertForInStatement(result, node, convert) {
    result.initializer = convert(node.initializer);
    result.expression = convert(node.expression);
    result.statement = convert(node.statement);
    result.text = generateForInStatementText(node, convert);
}

/**
 * Convert WhileStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertWhileStatement(result, node, convert) {
    result.expression = convert(node.expression);
    result.statement = convert(node.statement);
    result.text = generateWhileStatementText(node, convert);
}

/**
 * Convert DoStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertDoStatement(result, node, convert) {
    result.expression = convert(node.expression);
    result.statement = convert(node.statement);
    result.text = generateDoStatementText(node, convert);
}

/**
 * Convert ForStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertForStatement(result, node, convert) {
    result.initializer = node.initializer ? convert(node.initializer) : null;
    result.condition = node.condition ? convert(node.condition) : null;
    result.incrementor = node.incrementor ? convert(node.incrementor) : null;
    result.statement = convert(node.statement);
    result.text = generateForStatementText(node, convert);
}

/**
 * Convert SwitchStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertSwitchStatement(result, node, convert) {
    result.expression = convert(node.expression);
    result.caseBlock = convert(node.caseBlock);
    result.text = generateSwitchStatementText(node, convert);
}

/**
 * Convert CaseBlock node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertCaseBlock(result, node, convert) {
    result.clauses = [];
    for (const clause of node.clauses || []) {
        result.clauses.push(convert(clause));
    }
}

/**
 * Convert CaseClause node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertCaseClause(result, node, convert) {
    result.expression = convert(node.expression);
    result.statements = convertStatements(node.statements, convert);
}

/**
 * Convert DefaultClause node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertDefaultClause(result, node, convert) {
    result.statements = convertStatements(node.statements, convert);
}

/**
 * Convert TryStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertTryStatement(result, node, convert) {
    result.tryBlock = convert(node.tryBlock);
    result.catchClause = node.catchClause ? convert(node.catchClause) : null;
    result.finallyBlock = node.finallyBlock ? convert(node.finallyBlock) : null;
    result.text = generateTryStatementText(node, convert);
}

/**
 * Convert CatchClause node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertCatchClause(result, node, convert) {
    result.variableDeclaration = node.variableDeclaration ? convert(node.variableDeclaration) : null;
    result.block = convert(node.block);
}

/**
 * Convert BreakStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
function convertBreakStatement(result, node) {
    result.label = node.label ? node.label.getText() : null;
}

/**
 * Convert ContinueStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
function convertContinueStatement(result, node) {
    result.label = node.label ? node.label.getText() : null;
}

/**
 * Convert ThrowStatement node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertThrowStatement(result, node, convert) {
    result.expression = convert(node.expression);
    result.text = generateThrowStatementText(node, convert);
}

/**
 * Convert statements array.
 * @param {Array} statements - Array of statement nodes
 * @param {Function} convert - Recursive convert function
 * @returns {Array} Converted statements
 */
function convertStatements(statements, convert) {
    const result = [];
    for (const stmt of statements) {
        const stmtJson = convert(stmt);
        if (stmtJson) {
            result.push(stmtJson);
        }
    }
    return result;
}

// Text generation helpers
function generateForOfStatementText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');
    const { DECLARATION_KINDS, OPERATORS } = require('../../common/constants');

    const initStr = generateInitializerText(node.initializer, convert);
    const exprStr = node.expression ? jsonToCodeString(convert(node.expression)) : '';
    const stmtStr = node.statement ? jsonToCodeString(convert(node.statement)) : '';
    const awaitModifier = node.awaitModifier ? KEYWORDS.AWAIT + ' ' : '';

    return `${KEYWORDS.FOR} ${awaitModifier}(${initStr} of ${exprStr}) {${stmtStr}}`;
}

function generateForInStatementText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const initStr = generateInitializerText(node.initializer, convert);
    const exprStr = node.expression ? jsonToCodeString(convert(node.expression)) : '';
    const stmtStr = node.statement ? jsonToCodeString(convert(node.statement)) : '';

    return `${KEYWORDS.FOR} (${initStr} in ${exprStr}) {${stmtStr}}`;
}

function generateWhileStatementText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const condStr = node.expression ? jsonToCodeString(convert(node.expression)) : '';
    const stmtStr = node.statement ? jsonToCodeString(convert(node.statement)) : '';

    return `${KEYWORDS.WHILE} (${condStr}) {${stmtStr}}`;
}

function generateDoStatementText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const stmtStr = node.statement ? jsonToCodeString(convert(node.statement)) : '';
    const condStr = node.expression ? jsonToCodeString(convert(node.expression)) : '';

    return `${KEYWORDS.DO} {${stmtStr}} ${KEYWORDS.WHILE} (${condStr})`;
}

function generateForStatementText(node, convert) {
    const { jsonToCodeString, getDeclarationKind } = require('../../codegen');
    const { DECLARATION_KINDS, OPERATORS } = require('../../common/constants');

    const initStr = generateForInitializerText(node.initializer, convert);
    const condStr = node.condition ? jsonToCodeString(convert(node.condition)) : '';
    const incrStr = node.incrementor ? jsonToCodeString(convert(node.incrementor)) : '';
    const stmtStr = node.statement ? jsonToCodeString(convert(node.statement)) : '';

    return `${KEYWORDS.FOR} (${initStr}; ${condStr}; ${incrStr}) {${stmtStr}}`;
}

function generateSwitchStatementText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const exprStr = node.expression ? jsonToCodeString(convert(node.expression)) : '';
    const caseBlock = node.caseBlock ? convert(node.caseBlock) : null;

    let result = `${KEYWORDS.SWITCH} (${exprStr}) {`;

    if (caseBlock?.clauses) {
        for (const clause of caseBlock.clauses) {
            if (clause.kindName === 'CaseClause') {
                result += generateCaseClauseText(clause, jsonToCodeString);
            } else if (clause.kindName === 'DefaultClause') {
                result += generateDefaultClauseText(clause, jsonToCodeString);
            }
        }
    }

    return result + '}';
}

function generateCaseClauseText(clause, jsonToCodeString) {
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

function generateDefaultClauseText(clause, jsonToCodeString) {
    let result = `${KEYWORDS.DEFAULT}:`;

    if (clause.statements?.length > 0) {
        for (const stmt of clause.statements) {
            result += ' ' + jsonToCodeString(stmt);
        }
    }

    return result;
}

function generateTryStatementText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const tryBlock = node.tryBlock ? jsonToCodeString(convert(node.tryBlock)) : '';
    const catchClause = node.catchClause ? convert(node.catchClause) : null;
    const finallyBlock = node.finallyBlock ? jsonToCodeString(convert(node.finallyBlock)) : null;

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
        result += ` ${KEYWORDS.FINALLY} {${finallyBlock}}`;
    }

    return result;
}

function generateThrowStatementText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const expr = node.expression ? convert(node.expression) : null;
    return expr ? KEYWORDS.THROW + ' ' + jsonToCodeString(expr) : KEYWORDS.THROW;
}

function generateInitializerText(initializer, convert) {
    const { jsonToCodeString } = require('../../codegen');
    const { DECLARATION_KINDS } = require('../../common/constants');

    if (!initializer) return '';

    const initJson = convert(initializer);
    let initStr = jsonToCodeString(initJson);

    if (initJson?.kindName === 'VariableDeclarationList' && initStr) {
        initStr = DECLARATION_KINDS.CONST + ' ' + initStr;
    }

    return initStr;
}

function generateForInitializerText(initializer, convert) {
    const { jsonToCodeString } = require('../../codegen');
    const { DECLARATION_KINDS } = require('../../common/constants');

    if (!initializer) return '';

    const initJson = convert(initializer);
    let initStr = jsonToCodeString(initJson);

    if (initJson?.kindName === 'VariableDeclarationList' && initStr) {
        const declKind = initJson.declarationKind || DECLARATION_KINDS.DEFAULT;
        initStr = declKind + ' ' + initStr;
    }

    return initStr;
}

module.exports = {
    convertBlock,
    convertVariableStatement,
    convertVariableDeclarationList,
    convertVariableDeclaration,
    convertExpressionStatement,
    convertReturnStatement,
    convertIfStatement,
    convertForOfStatement,
    convertForInStatement,
    convertWhileStatement,
    convertDoStatement,
    convertForStatement,
    convertSwitchStatement,
    convertCaseBlock,
    convertCaseClause,
    convertDefaultClause,
    convertTryStatement,
    convertCatchClause,
    convertBreakStatement,
    convertContinueStatement,
    convertThrowStatement,
    convertStatements
};
