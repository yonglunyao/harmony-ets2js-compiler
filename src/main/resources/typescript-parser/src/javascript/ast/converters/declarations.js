/**
 * Declaration Node Converters
 * Converts declaration AST nodes to JSON format
 * @module lib/converters/declarations
 */

const ts = require('typescript');
const { KEYWORDS } = require('../../common/constants');
const { hasExportModifier, getSyntaxKindName } = require('../../common/utils');

/**
 * Convert SourceFile node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 * @param {Array} extractedDecorators - Decorators extracted during preprocessing
 */
function convertSourceFile(result, node, convert, extractedDecorators) {
    result.fileName = node.fileName;
    result.statements = convertStatements(node.statements, convert, extractedDecorators);
}

/**
 * Convert ClassDeclaration node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 * @param {Array} extractedDecorators - Decorators extracted during preprocessing
 */
function convertClassDeclaration(result, node, convert, extractedDecorators) {
    result.name = node.name?.escapedText || '';
    result.isExport = hasExportModifier(node.modifiers);
    result.decorators = combineExtractedAndInlineDecorators(node.decorators, extractedDecorators, convert);
    result.members = convertStatements(node.members, convert);
    result.heritageClauses = convertHeritageClauses(node.heritageClauses);
}

/**
 * Convert ClassExpression node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertClassExpression(result, node, convert) {
    result.name = node.name?.escapedText || '';
    result.decorators = convertDecorators(node.decorators, convert);
    result.members = convertStatements(node.members, convert);
    result.heritageClauses = convertHeritageClauses(node.heritageClauses);
    result.text = generateClassExpressionText(node, convert);
}

/**
 * Convert MethodDeclaration node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertMethodDeclaration(result, node, convert) {
    result.name = node.name?.escapedText || '';
    result.decorators = convertDecoratorsWithModifierCheck(node.decorators, node.modifiers, convert);
    result.modifiers = convertModifiers(node.modifiers);
    result.parameters = convertParameters(node.parameters, convert);
    result.body = node.body ? convert(node.body) : null;
    result.asteriskToken = !!node.asteriskToken;
}

/**
 * Convert GetAccessor node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertGetAccessor(result, node, convert) {
    result.name = node.name?.escapedText || '';
    result.body = node.body ? convert(node.body) : null;
    result.text = generateGetAccessorText(node, convert);
}

/**
 * Convert SetAccessor node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertSetAccessor(result, node, convert) {
    result.name = node.name?.escapedText || '';
    result.parameters = convertParametersSimple(node.parameters);
    result.body = node.body ? convert(node.body) : null;
    result.text = generateSetAccessorText(node, convert);
}

/**
 * Convert FunctionDeclaration node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertFunctionDeclaration(result, node, convert) {
    result.name = node.name?.escapedText || '';
    result.modifiers = convertModifiers(node.modifiers);
    result.parameters = convertParameters(node.parameters, convert);
    result.body = node.body ? convert(node.body) : null;
    result.asteriskToken = !!node.asteriskToken;
}

/**
 * Convert Constructor node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertConstructor(result, node, convert) {
    result.name = KEYWORDS.CONSTRUCTOR;
    result.parameters = convertParameters(node.parameters, convert);
    result.body = node.body ? convert(node.body) : null;
}

/**
 * Convert PropertyDeclaration node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 * @param {Function} convert - Recursive convert function
 */
function convertPropertyDeclaration(result, node, convert) {
    result.name = node.name?.escapedText || '';
    result.type = node.type?.getText() || '';
    result.initializer = node.initializer ? convert(node.initializer) : null;

    if (node.initializer) {
        const initJson = result.initializer;
        result.initializerText = initJson?.text || node.initializer?.getText() || '';
    }

    result.decorators = convertDecoratorsWithModifierCheck(node.decorators, node.modifiers, convert);
    result.modifiers = convertModifiers(node.modifiers);
}

/**
 * Convert Decorator node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
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

/**
 * Convert InterfaceDeclaration node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
function convertInterfaceDeclaration(result, node) {
    result.name = node.name?.escapedText || '';
    result.isTypeOnly = true;
}

/**
 * Convert ImportDeclaration node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
function convertImportDeclaration(result, node) {
    result.moduleSpecifier = node.moduleSpecifier.getText();
    result.importClause = convertImportClause(node.importClause);
}

/**
 * Convert ExportDeclaration node.
 * @param {Object} result - Result object to populate
 * @param {Object} node - TypeScript AST node
 */
function convertExportDeclaration(result, node) {
    result.isTypeOnly = node.isTypeOnly || false;
    result.exportClause = convertExportClause(node.exportClause);
    result.moduleSpecifier = node.moduleSpecifier ? node.moduleSpecifier.getText() : null;
}

/**
 * Convert statements array.
 * @param {Array} statements - Array of statement nodes
 * @param {Function} convert - Recursive convert function
 * @param {Array} extractedDecorators - Decorators extracted during preprocessing
 * @returns {Array} Converted statements
 */
function convertStatements(statements, convert, extractedDecorators = []) {
    const result = [];
    for (const stmt of statements) {
        const stmtJson = convert(stmt, extractedDecorators);
        if (stmtJson) {
            result.push(stmtJson);
        }
    }
    return result;
}

/**
 * Convert heritage clauses (extends, implements).
 * @param {Array} heritageClauses - Array of heritage clauses
 * @returns {Array} Converted heritage clauses
 */
function convertHeritageClauses(heritageClauses) {
    const { getSyntaxKindName, SYNTAX_KIND_NAMES } = require('../../common/utils');

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
 * @param {Function} convert - Recursive convert function
 * @returns {Array} Converted decorators
 */
function convertDecorators(decorators, convert) {
    const result = [];
    for (const dec of decorators || []) {
        const decJson = convert(dec);
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
 * @param {Function} convert - Recursive convert function
 * @returns {Array} Converted decorators
 */
function convertDecoratorsWithModifierCheck(decorators, modifiers, convert) {
    const { SYNTAX_KIND_NAMES, REGEX_PATTERNS } = require('../../common/constants');

    const result = convertDecorators(decorators, convert);

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
 * @param {Function} convert - Recursive convert function
 * @returns {Array} Combined decorators
 */
function combineExtractedAndInlineDecorators(decorators, extractedDecorators, convert) {
    const result = [];

    // Add extracted decorators (for @Component on struct)
    for (const dec of extractedDecorators || []) {
        result.push({
            name: dec.name
        });
    }

    // Add inline decorators
    for (const dec of decorators || []) {
        const decJson = convert(dec);
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
    const { REGEX_PATTERNS } = require('../../common/constants');

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
    const { getSyntaxKindName } = require('../../common/utils');

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
 * @param {Function} convert - Recursive convert function
 * @returns {Array} Converted parameters
 */
function convertParameters(parameters, convert) {
    const { getSyntaxKindName, isBindingPattern } = require('../../common/utils');

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

// Text generation helpers
function generateGetAccessorText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const name = node.name ? node.name.getText() : '';
    const bodyCode = node.body ? jsonToCodeString(convert(node.body)) : '';

    return `${KEYWORDS.GET} ${name}() {${bodyCode}}`;
}

function generateSetAccessorText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const name = node.name ? node.name.getText() : '';
    const params = (node.parameters || []).map(p => p.name?.getText() || '').join(', ');
    const bodyCode = node.body ? jsonToCodeString(convert(node.body)) : '';

    return `${KEYWORDS.SET} ${name}(${params}) {${bodyCode}}`;
}

function generateClassExpressionText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');
    const { SYNTAX_KIND_NAMES, OPERATORS } = require('../../common/constants');

    const className = node.name ? node.name.getText() : '';
    let result = KEYWORDS.CLASS + ' ';

    if (className) {
        result += className + ' ';
    }

    result += generateExtendsClause(node);
    result += OPERATORS.OPEN_BRACE + ' ';
    result += generateClassMembers(node, convert);
    result += ' ' + OPERATORS.CLOSE_BRACE;

    return result;
}

function generateExtendsClause(node) {
    const { getSyntaxKindName } = require('../../common/utils');

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

function generateClassMembers(node, convert) {
    const { getSyntaxKindName, jsonToCodeString } = require('../../common/utils');
    const { convert: conv } = require('../converter');

    if (!node.members) return '';

    const memberTexts = [];
    for (const member of node.members) {
        const memberKind = getSyntaxKindName(member.kind);
        if (memberKind === 'Constructor') {
            memberTexts.push(generateConstructorText(member, convert));
        } else if (memberKind === 'MethodDeclaration') {
            memberTexts.push(generateMethodDeclarationText(member, convert));
        } else if (memberKind === 'PropertyDeclaration') {
            memberTexts.push(generatePropertyDeclarationText(member));
        }
    }

    return memberTexts.join(' ');
}

function generateConstructorText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const params = (node.parameters || []).map(p => p.name?.getText() || '').join(', ');
    const bodyCode = node.body ? generateBlockBodyText(node.body, convert) : '';

    return `${KEYWORDS.CONSTRUCTOR}(${params}) {${bodyCode}}`;
}

function generateMethodDeclarationText(node, convert) {
    const { jsonToCodeString } = require('../../codegen');

    const name = node.name?.getText() || '';
    const params = (node.parameters || []).map(p => p.name?.getText() || '').join(', ');
    const bodyCode = node.body ? generateBlockBodyText(node.body, convert) : '';

    return `${name}(${params}) {${bodyCode}}`;
}

function generatePropertyDeclarationText(node) {
    const name = node.name?.getText() || '';

    if (node.initializer) {
        const initText = node.initializer.getText();
        return `${name} = ${initText};`;
    }

    return `${name};`;
}

function generateBlockBodyText(blockNode, convert) {
    const { jsonToCodeString } = require('../../codegen');

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

module.exports = {
    convertSourceFile,
    convertClassDeclaration,
    convertClassExpression,
    convertMethodDeclaration,
    convertGetAccessor,
    convertSetAccessor,
    convertFunctionDeclaration,
    convertConstructor,
    convertPropertyDeclaration,
    convertDecorator,
    convertInterfaceDeclaration,
    convertImportDeclaration,
    convertExportDeclaration,
    convertStatements,
    convertHeritageClauses,
    convertDecorators,
    convertDecoratorsWithModifierCheck,
    combineExtractedAndInlineDecorators,
    convertModifiers,
    convertParameters,
    convertParametersSimple,
    convertImportClause,
    convertExportClause
};
