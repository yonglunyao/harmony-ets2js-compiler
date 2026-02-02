/**
 * Main AST to JSON Converter
 * Orchestrates all node type converters
 * @module lib/converter
 */

const ts = require('typescript');
const { getSyntaxKindName } = require('../common/utils');

// Import all converters
const literals = require('./converters/literals');
const statements = require('./converters/statements');
const declarations = require('./converters/declarations');
const expressions = require('./converters/expressions');

/**
 * Map of syntax kind to converter function.
 * Each converter processes a specific AST node type.
 * @constant
 */
const AST_CONVERTERS = {
    [ts.SyntaxKind.SourceFile]: (result, node, convert, extracted) =>
        declarations.convertSourceFile(result, node, convert, extracted),

    [ts.SyntaxKind.ClassDeclaration]: (result, node, convert, extracted) =>
        declarations.convertClassDeclaration(result, node, convert, extracted),

    [ts.SyntaxKind.ClassExpression]: (result, node, convert) =>
        declarations.convertClassExpression(result, node, convert),

    [ts.SyntaxKind.MethodDeclaration]: (result, node, convert) =>
        declarations.convertMethodDeclaration(result, node, convert),

    [ts.SyntaxKind.GetAccessor]: (result, node, convert) =>
        declarations.convertGetAccessor(result, node, convert),

    [ts.SyntaxKind.SetAccessor]: (result, node, convert) =>
        declarations.convertSetAccessor(result, node, convert),

    [ts.SyntaxKind.FunctionDeclaration]: (result, node, convert) =>
        declarations.convertFunctionDeclaration(result, node, convert),

    [ts.SyntaxKind.Constructor]: (result, node, convert) =>
        declarations.convertConstructor(result, node, convert),

    [ts.SyntaxKind.PropertyDeclaration]: (result, node, convert) =>
        declarations.convertPropertyDeclaration(result, node, convert),

    [ts.SyntaxKind.Decorator]: (result, node) =>
        declarations.convertDecorator(result, node),

    [ts.SyntaxKind.Block]: (result, node, convert) =>
        statements.convertBlock(result, node, convert),

    [ts.SyntaxKind.VariableStatement]: (result, node, convert) =>
        statements.convertVariableStatement(result, node, convert),

    [ts.SyntaxKind.VariableDeclarationList]: (result, node, convert) =>
        statements.convertVariableDeclarationList(result, node, convert),

    [ts.SyntaxKind.VariableDeclaration]: (result, node, convert) =>
        statements.convertVariableDeclaration(result, node, convert),

    [ts.SyntaxKind.ExpressionStatement]: (result, node, convert) =>
        statements.convertExpressionStatement(result, node, convert),

    [ts.SyntaxKind.CallExpression]: (result, node, convert) =>
        expressions.convertCallExpression(result, node, convert),

    [ts.SyntaxKind.Identifier]: (result, node) =>
        expressions.convertIdentifier(result, node),

    [ts.SyntaxKind.ImportKeyword]: (result) =>
        expressions.convertImportKeyword(result),

    [ts.SyntaxKind.PropertyAccessExpression]: (result, node, convert) =>
        expressions.convertPropertyAccessExpression(result, node, convert),

    [ts.SyntaxKind.NewExpression]: (result, node, convert) =>
        expressions.convertNewExpression(result, node, convert),

    [ts.SyntaxKind.ElementAccessExpression]: (result, node, convert) =>
        expressions.convertElementAccessExpression(result, node, convert),

    [ts.SyntaxKind.ParenthesizedExpression]: (result, node, convert) =>
        expressions.convertParenthesizedExpression(result, node, convert),

    [ts.SyntaxKind.TypeOfExpression]: (result, node, convert) =>
        expressions.convertTypeOfExpression(result, node, convert),

    [ts.SyntaxKind.PrefixUnaryExpression]: (result, node, convert) =>
        expressions.convertPrefixUnaryExpression(result, node, convert),

    [ts.SyntaxKind.PostfixUnaryExpression]: (result, node, convert) =>
        expressions.convertPostfixUnaryExpression(result, node, convert),

    [ts.SyntaxKind.NonNullExpression]: (result, node, convert) =>
        expressions.convertNonNullExpression(result, node, convert),

    [ts.SyntaxKind.AsExpression]: (result, node, convert) =>
        expressions.convertAsExpression(result, node, convert),

    [ts.SyntaxKind.TypeAssertion]: (result, node, convert) =>
        expressions.convertTypeAssertion(result, node, convert),

    [ts.SyntaxKind.ImportExpression]: (result, node, convert) =>
        expressions.convertImportExpression(result, node, convert),

    [ts.SyntaxKind.InterfaceDeclaration]: (result, node) =>
        declarations.convertInterfaceDeclaration(result, node),

    [ts.SyntaxKind.ImportDeclaration]: (result, node) =>
        declarations.convertImportDeclaration(result, node),

    [ts.SyntaxKind.ExportDeclaration]: (result, node) =>
        declarations.convertExportDeclaration(result, node),

    [ts.SyntaxKind.ArrowFunction]: (result, node, convert) =>
        expressions.convertArrowFunction(result, node, convert),

    [ts.SyntaxKind.FunctionExpression]: (result, node, convert) =>
        expressions.convertFunctionExpression(result, node, convert),

    [ts.SyntaxKind.StringLiteral]: (result, node) =>
        literals.convertStringLiteral(result, node),

    [ts.SyntaxKind.NumericLiteral]: (result, node) =>
        literals.convertNumericLiteral(result, node),

    [ts.SyntaxKind.TemplateExpression]: (result, node, convert) =>
        literals.convertTemplateExpression(result, node, convert),

    [ts.SyntaxKind.NoSubstitutionTemplateLiteral]: (result, node) =>
        literals.convertNoSubstitutionTemplateLiteral(result, node),

    [ts.SyntaxKind.TrueKeyword]: (result) =>
        literals.convertTrueKeyword(result),

    [ts.SyntaxKind.FalseKeyword]: (result) =>
        literals.convertFalseKeyword(result),

    [ts.SyntaxKind.NullKeyword]: (result) =>
        literals.convertNullKeyword(result),

    [ts.SyntaxKind.UndefinedKeyword]: (result) =>
        literals.convertUndefinedKeyword(result),

    [ts.SyntaxKind.AwaitExpression]: (result, node, convert) =>
        expressions.convertAwaitExpression(result, node, convert),

    [ts.SyntaxKind.ThrowStatement]: (result, node, convert) =>
        statements.convertThrowStatement(result, node, convert),

    [ts.SyntaxKind.ReturnStatement]: (result, node, convert) =>
        statements.convertReturnStatement(result, node, convert),

    [ts.SyntaxKind.IfStatement]: (result, node, convert) =>
        statements.convertIfStatement(result, node, convert),

    [ts.SyntaxKind.ForOfStatement]: (result, node, convert) =>
        statements.convertForOfStatement(result, node, convert),

    [ts.SyntaxKind.ForInStatement]: (result, node, convert) =>
        statements.convertForInStatement(result, node, convert),

    [ts.SyntaxKind.WhileStatement]: (result, node, convert) =>
        statements.convertWhileStatement(result, node, convert),

    [ts.SyntaxKind.DoStatement]: (result, node, convert) =>
        statements.convertDoStatement(result, node, convert),

    [ts.SyntaxKind.ForStatement]: (result, node, convert) =>
        statements.convertForStatement(result, node, convert),

    [ts.SyntaxKind.SwitchStatement]: (result, node, convert) =>
        statements.convertSwitchStatement(result, node, convert),

    [ts.SyntaxKind.CaseBlock]: (result, node, convert) =>
        statements.convertCaseBlock(result, node, convert),

    [ts.SyntaxKind.CaseClause]: (result, node, convert) =>
        statements.convertCaseClause(result, node, convert),

    [ts.SyntaxKind.DefaultClause]: (result, node, convert) =>
        statements.convertDefaultClause(result, node, convert),

    [ts.SyntaxKind.TryStatement]: (result, node, convert) =>
        statements.convertTryStatement(result, node, convert),

    [ts.SyntaxKind.CatchClause]: (result, node, convert) =>
        statements.convertCatchClause(result, node, convert),

    [ts.SyntaxKind.BreakStatement]: (result, node) =>
        statements.convertBreakStatement(result, node),

    [ts.SyntaxKind.ContinueStatement]: (result, node) =>
        statements.convertContinueStatement(result, node),

    [ts.SyntaxKind.BinaryExpression]: (result, node, convert) =>
        expressions.convertBinaryExpression(result, node, convert),

    [ts.SyntaxKind.ConditionalExpression]: (result, node, convert) =>
        expressions.convertConditionalExpression(result, node, convert),

    [ts.SyntaxKind.ArrayLiteralExpression]: (result, node, convert) =>
        expressions.convertArrayLiteralExpression(result, node, convert),

    [ts.SyntaxKind.SpreadElement]: (result, node, convert) =>
        expressions.convertSpreadElement(result, node, convert),

    [ts.SyntaxKind.ObjectLiteralExpression]: (result, node, convert) =>
        expressions.convertObjectLiteralExpression(result, node, convert),

    [ts.SyntaxKind.PropertyAssignment]: (result, node, convert) =>
        expressions.convertPropertyAssignment(result, node, convert),

    [ts.SyntaxKind.ShorthandPropertyAssignment]: (result, node) =>
        expressions.convertShorthandPropertyAssignment(result, node),

    [ts.SyntaxKind.SpreadAssignment]: (result, node, convert) =>
        expressions.convertSpreadAssignment(result, node, convert),

    [ts.SyntaxKind.ObjectBindingPattern]: (result, node, convert) =>
        expressions.convertObjectBindingPattern(result, node, convert),

    [ts.SyntaxKind.ArrayBindingPattern]: (result, node, convert) =>
        expressions.convertArrayBindingPattern(result, node, convert),

    [ts.SyntaxKind.BindingElement]: (result, node, convert) =>
        expressions.convertBindingElement(result, node, convert),

    [ts.SyntaxKind.DeleteExpression]: (result, node, convert) =>
        expressions.convertDeleteExpression(result, node, convert)
};

/**
 * Convert TypeScript AST to JSON-serializable format.
 * This function uses the parameter-passing pattern to avoid circular dependencies.
 *
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

    // Get the converter for this node type
    const converter = AST_CONVERTERS[node.kind];

    if (converter) {
        // Pass the convert function as a parameter to avoid circular dependency
        converter(result, node, convertAstToJson, extractedDecorators);
    }

    return result;
}

module.exports = {
    convertAstToJson
};
