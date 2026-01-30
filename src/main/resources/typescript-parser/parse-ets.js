#!/usr/bin/env node

const ts = require('typescript');
const fs = require('fs');

/**
 * Parse ETS/TypeScript source code and output AST as JSON.
 * Handles ETS-specific syntax like struct by preprocessing.
 */
function main() {
    const args = process.argv.slice(2);
    if (args.length < 2) {
        console.error('Usage: node parse-ets.js <source-file> <output-file>');
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
        console.error('Successfully parsed:', sourceFilePath);

    } catch (error) {
        console.error('Error parsing file:', error.message);
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
    // Pattern: decorators followed by optional export keyword and struct/class
    const structDecoratorPattern = /(@\w+\s*(?:\([^)]*\))?\s*)\b(?:export\s+)?struct\s+/g;
    const extractedDecorators = [];

    // Find struct with preceding decorators
    let match;
    while ((match = structDecoratorPattern.exec(sourceCode)) !== null) {
        // Extract individual decorators from the matched text
        const decoratorText = match[1];
        const individualDecPattern = /@(\w+)(?:\s*\([^)]*\))?/g;
        let decMatch;
        while ((decMatch = individualDecPattern.exec(decoratorText)) !== null) {
            extractedDecorators.push({
                name: decMatch[1],
                fullText: decMatch[0]
            });
        }
    }

    // Replace struct with class
    processedCode = processedCode.replace(/\bstruct\s+/g, 'class ');

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
                    if (modKindName === 'ExportKeyword') {
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
                    result.modifiers.push({
                        kind: mod.kind,
                        kindName: getSyntaxKindName(mod.kind)
                    });
                }
            }
            result.parameters = [];
            for (const param of (node.parameters || [])) {
                result.parameters.push({
                    name: param.name?.escapedText || '',
                    type: param.type?.getText() || '',
                    hasDotDotDot: param.dotDotDotToken ? true : false,
                    questionToken: param.questionToken ? true : false
                });
            }
            result.body = node.body ? convertAstToJson(node.body) : null;
            result.asteriskToken = node.asteriskToken ? true : false;
            break;

        case ts.SyntaxKind.PropertyDeclaration:
            result.name = node.name?.escapedText || '';
            result.type = node.type?.getText() || '';
            result.initializer = node.initializer?.getText() || '';
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
                    if (modInfo.kindName === 'Decorator' && result.decorators.length === 0) {
                        // Try to get decorator name from the modifier's text
                        try {
                            const modText = mod.getText();
                            const decoratorMatch = modText.match(/@(\w+)/);
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

        case ts.SyntaxKind.ExpressionStatement:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.CallExpression:
            result.expression = convertAstToJson(node.expression);
            result.arguments = [];

            // Check if this is a special component (ForEach) that needs JSON objects as arguments
            const isForEach = result.expression && result.expression.kindName === 'Identifier' &&
                            result.expression.name === 'ForEach';

            for (const arg of (node.arguments || [])) {
                if (isForEach) {
                    // ForEach needs JSON objects for proper argument processing
                    result.arguments.push(convertAstToJson(arg));
                } else {
                    // Regular calls use text representation
                    try {
                        result.arguments.push(arg.getText());
                    } catch (e) {
                        // If getText fails, try recursive conversion
                        const argJson = convertAstToJson(arg);
                        if (argJson && argJson.text) {
                            result.arguments.push(argJson.text);
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
                if (componentName === 'ForEach' || componentName === 'If') {
                    result.isSpecialComponent = true;
                    result.componentName = componentName;
                }
            }
            break;

        case ts.SyntaxKind.Identifier:
            result.name = node.escapedText;
            result.text = node.getText();
            break;

        case ts.SyntaxKind.PropertyAccessExpression:
            result.expression = convertAstToJson(node.expression);
            result.name = node.name.escapedText;
            // Note: Don't extract arguments here - they're handled by the parent CallExpression
            // to avoid duplication
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
            result.parameters = [];
            for (const param of (node.parameters || [])) {
                result.parameters.push({
                    name: param.name?.escapedText || '',
                    type: param.type?.getText() || ''
                });
            }
            result.body = convertAstToJson(node.body);
            // Add text representation for arrow functions
            result.text = node.getText();
            break;

        case ts.SyntaxKind.StringLiteral:
            result.text = node.text;
            break;

        case ts.SyntaxKind.NumericLiteral:
            result.text = node.text;
            break;

        case ts.SyntaxKind.AwaitExpression:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.ReturnStatement:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.IfStatement:
            result.expression = convertAstToJson(node.expression);
            result.thenStatement = convertAstToJson(node.thenStatement);
            result.elseStatement = convertAstToJson(node.elseStatement);
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

        case ts.SyntaxKind.ObjectLiteralExpression:
            result.properties = [];
            if (node.properties) {
                for (const prop of node.properties) {
                    result.properties.push(convertAstToJson(prop));
                }
            }
            break;

        case ts.SyntaxKind.PropertyAssignment:
            result.name = node.name.getText();
            result.value = convertAstToJson(node.initializer);
            break;

        case ts.SyntaxKind.ShorthandPropertyAssignment:
            result.name = node.name.getText();
            result.value = { kindName: 'Identifier', name: node.name.getText(), text: node.name.getText() };
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
        [ts.SyntaxKind.MethodDeclaration]: 'MethodDeclaration',
        [ts.SyntaxKind.PropertyDeclaration]: 'PropertyDeclaration',
        [ts.SyntaxKind.Decorator]: 'Decorator',
        [ts.SyntaxKind.Block]: 'Block',
        [ts.SyntaxKind.ExpressionStatement]: 'ExpressionStatement',
        [ts.SyntaxKind.CallExpression]: 'CallExpression',
        [ts.SyntaxKind.Identifier]: 'Identifier',
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
    };

    return kindMap[kind] || `Unknown_${kind}`;
}

main();
