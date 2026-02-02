/**
 * Utility functions for ETS/TypeScript Parser
 * Helper functions that don't depend on converters
 * @module lib/utils
 */

const ts = require('typescript');
const {
    SYNTAX_KIND_NAMES,
    DECLARATION_KINDS,
    KEYWORDS,
    REGEX_PATTERNS,
    ERROR_MESSAGES
} = require('./constants');

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
 * Check if modifiers contain async keyword.
 * @param {Array} modifiers - Array of modifiers
 * @returns {boolean} True if has async modifier
 */
function hasAsyncModifier(modifiers) {
    return modifiers && modifiers.some(m => m.kind === ts.SyntaxKind.AsyncKeyword);
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

module.exports = {
    getSyntaxKindName,
    hasExportModifier,
    isBindingPattern,
    getDeclarationKind,
    hasAsyncModifier,
    escapeTemplateLiteral
};
