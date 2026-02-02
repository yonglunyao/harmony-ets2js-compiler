/**
 * Literal Node Converters
 * Converts literal AST nodes to JSON format
 * @module lib/converters/literals
 */

const { OPERATORS } = require('../../common/constants');

/**
 * Convert StringLiteral node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
function convertStringLiteral(result, node) {
    result.text = OPERATORS.DOUBLE_QUOTE + node.text + OPERATORS.DOUBLE_QUOTE;
}

/**
 * Convert NumericLiteral node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
function convertNumericLiteral(result, node) {
    result.text = node.text;
    result.value = node.numericLiteralValue;
}

/**
 * Convert TemplateExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertTemplateExpression(result, node, convert) {
    result.head = { text: node.head.text };
    result.templateSpans = [];
    for (const span of node.templateSpans) {
        result.templateSpans.push({
            expression: convert(span.expression),
            literal: { text: span.literal.text }
        });
    }
    result.text = generateTemplateExpressionText(node, convert);
}

/**
 * Convert NoSubstitutionTemplateLiteral node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
function convertNoSubstitutionTemplateLiteral(result, node) {
    result.text = OPERATORS.BACKTICK + node.text + OPERATORS.BACKTICK;
}

/**
 * Convert TrueKeyword node.
 * @param {Object} result - Result object to populate
 */
function convertTrueKeyword(result) {
    result.text = 'true';
}

/**
 * Convert FalseKeyword node.
 * @param {Object} result - Result object to populate
 */
function convertFalseKeyword(result) {
    result.text = 'false';
}

/**
 * Convert NullKeyword node.
 * @param {Object} result - Result object to populate
 */
function convertNullKeyword(result) {
    result.text = 'null';
}

/**
 * Convert UndefinedKeyword node.
 * @param {Object} result - Result object to populate
 */
function convertUndefinedKeyword(result) {
    result.text = 'undefined';
}

/**
 * Generate text representation for template expression.
 * @param {Object} node - TemplateExpression node
 * @param {Function} convert - Recursive convert function
 * @returns {string} Generated text
 */
function generateTemplateExpressionText(node, convert) {
    const { escapeTemplateLiteral } = require('../../common/utils');

    if (!node.templateSpans || node.templateSpans.length === 0) {
        return OPERATORS.BACKTICK + escapeTemplateLiteral(node.head?.text || '') + OPERATORS.BACKTICK;
    }

    let result = OPERATORS.BACKTICK;
    result += escapeTemplateLiteral(node.head?.text || '');

    for (const span of node.templateSpans) {
        const exprJson = convert(span.expression);
        const { jsonToCodeString } = require('../../codegen');
        const exprStr = exprJson ? jsonToCodeString(exprJson) : '';
        result += '${' + exprStr + '}';
        result += escapeTemplateLiteral(span.literal?.text || '');
    }

    return result + OPERATORS.BACKTICK;
}

module.exports = {
    convertStringLiteral,
    convertNumericLiteral,
    convertTemplateExpression,
    convertNoSubstitutionTemplateLiteral,
    convertTrueKeyword,
    convertFalseKeyword,
    convertNullKeyword,
    convertUndefinedKeyword
};
