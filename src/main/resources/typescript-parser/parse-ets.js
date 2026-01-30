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

        case ts.SyntaxKind.VariableStatement:
            result.declarationList = convertAstToJson(node.declarationList);
            break;

        case ts.SyntaxKind.VariableDeclarationList:
            result.declarations = [];
            // Capture variable declaration keyword (let, const, var)
            if (node.flags & ts.NodeFlags.Let) {
                result.declarationKind = 'let';
            } else if (node.flags & ts.NodeFlags.Const) {
                result.declarationKind = 'const';
            } else {
                result.declarationKind = 'var';
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

            // Check if this is a special component (ForEach) that needs JSON objects as arguments
            const isForEach = result.expression && result.expression.kindName === 'Identifier' &&
                            result.expression.name === 'ForEach';

            for (const arg of (node.arguments || [])) {
                if (isForEach) {
                    // ForEach needs JSON objects for proper argument processing
                    result.arguments.push(convertAstToJson(arg));
                } else {
                    // For regular calls, always convert to JSON to strip TypeScript syntax
                    const argJson = convertAstToJson(arg);
                    // Use text property if available (it's been processed to remove TS syntax)
                    // Otherwise fall back to getText() for simple cases
                    if (argJson && argJson.text) {
                        // For StringLiteral, ensure quotes are included
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
                if (componentName === 'ForEach' || componentName === 'If') {
                    result.isSpecialComponent = true;
                    result.componentName = componentName;
                }
            }
            break;

        case ts.SyntaxKind.Identifier:
            result.name = node.escapedText;
            result.text = node.getText();

            // Check for resource references: $r() and $rawfile()
            if (result.name.startsWith('$r(') || result.name.startsWith('$rawfile(')) {
                // Convert to resource function call
                const match = result.name.match(/^\$(r|rawfile)\(([^)]+)\)$/);
                if (match) {
                    const resourceType = match[1]; // 'r' or 'rawfile'
                    const resourceId = match[2];
                    if (resourceType === 'r') {
                        result.name = RuntimeFunctions.GET_RESOURCE_ID;
                        result.text = `this.${RuntimeFunctions.GET_RESOURCE_ID}('${resourceId}')`;
                    } else if (resourceType === 'rawfile') {
                        result.name = RuntimeFunctions.GET_RAW_FILE_ID;
                        result.text = `this.${RuntimeFunctions.GET_RAW_FILE_ID}('${resourceId}')`;
                    }
                }
            }

            break;

        case ts.SyntaxKind.PropertyAccessExpression:
            result.expression = convertAstToJson(node.expression);
            result.name = node.name.escapedText;
            // Note: Don't extract arguments here - they're handled by the parent CallExpression
            // to avoid duplication
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
            break;

        case ts.SyntaxKind.AsExpression:
        case ts.SyntaxKind.TypeAssertion:
            result.expression = convertAstToJson(node.expression);
            result.type = node.type ? node.type.getText() : null;
            // Add text representation without the type assertion (just the expression)
            result.text = generateAsExpressionText(node);
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
            // Generate JavaScript-compatible text without type annotations
            result.text = generateArrowFunctionText(node);
            break;

        case ts.SyntaxKind.StringLiteral:
            result.text = node.text;
            break;

        case ts.SyntaxKind.NumericLiteral:
            result.text = node.text;
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
        initStr = 'const ' + initStr;
    }
    const exprStr = expression ? jsonToCodeString(expression) : '';

    let result = awaitModifier ? 'for await (' : 'for (';
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
        initStr = 'const ' + initStr;
    }
    const exprStr = expression ? jsonToCodeString(expression) : '';

    let result = 'for (' + initStr + ' in ' + exprStr + ') ';

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

    let result = 'while (' + condStr + ') ';

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

    let result = 'do ';

    if (statement) {
        result += '{\n' + jsonToCodeString(statement) + '\n} ';
    } else {
        result += '{ } ';
    }

    result += 'while (' + condStr + ')';

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
        const declKind = initializer.declarationKind || 'const';
        initStr = declKind + ' ' + initStr;
    }
    const condStr = condition ? jsonToCodeString(condition) : '';
    const incrStr = incrementor ? jsonToCodeString(incrementor) : '';

    let result = 'for (' + initStr + '; ' + condStr + '; ' + incrStr + ') ';

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

    let result = 'switch (' + exprStr + ') {\n';

    if (caseBlock && caseBlock.clauses) {
        for (const clause of caseBlock.clauses) {
            if (clause.kindName === 'CaseClause') {
                const caseExpr = clause.expression ? jsonToCodeString(clause.expression) : '';
                result += '  case ' + caseExpr + ':\n';

                if (clause.statements && clause.statements.length > 0) {
                    for (const stmt of clause.statements) {
                        result += '    ' + jsonToCodeString(stmt) + '\n';
                    }
                }
                result += '    break;\n';
            } else if (clause.kindName === 'DefaultClause') {
                result += '  default:\n';

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

    let result = 'try {\n' + jsonToCodeString(tryBlock) + '\n}';

    if (catchClause) {
        const varDecl = catchClause.variableDeclaration;
        const varName = varDecl ? jsonToCodeString(varDecl) : '';
        const catchBlock = catchClause.block;

        if (varName) {
            result += ' catch (' + varName + ') {\n' + jsonToCodeString(catchBlock) + '\n}';
        } else {
            result += ' catch {\n' + jsonToCodeString(catchBlock) + '\n}';
        }
    }

    if (finallyBlock) {
        result += ' finally {\n' + jsonToCodeString(finallyBlock) + '\n}';
    }

    return result;
}

/**
 * Generate JavaScript-compatible text for arrow function (without type annotations)
 */
function generateArrowFunctionText(node) {
    const params = (node.parameters || []).map(param => {
        // Get parameter name without type annotation
        const name = param.name?.escapedText || param.name?.text || '';
        return name;
    }).join(', ');

    let result = '(' + params + ') => ';

    // Handle body - convert to JSON and use jsonToCodeString to strip TypeScript syntax
    if (node.body) {
        const bodyJson = convertAstToJson(node.body);
        const bodyCode = jsonToCodeString(bodyJson);

        if (node.body.kind === ts.SyntaxKind.Block) {
            // Block body: () => { ... }
            // jsonToCodeString returns statements with newlines, we need to format them
            const statements = bodyCode.trim().split('\n').map(s => '    ' + s.trim()).join('\n');
            result += '{\n' + statements + '\n  }';
        } else {
            // Expression body: () => expression
            result += bodyCode;
        }
    }

    return result;
}

/**
 * Generate text representation for as-expression (without the type assertion)
 */
function generateAsExpressionText(node) {
    const expr = node.expression ? convertAstToJson(node.expression) : null;
    return expr ? jsonToCodeString(expr) : '';
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
            const valueStr = valueJson ? jsonToCodeString(valueJson) : '';
            return name + ': ' + valueStr;
        } else if (prop.kind === ts.SyntaxKind.ShorthandPropertyAssignment) {
            return prop.name.getText();
        } else if (prop.kind === ts.SyntaxKind.SpreadAssignment) {
            const exprJson = convertAstToJson(prop.expression);
            const exprStr = exprJson ? jsonToCodeString(exprJson) : '';
            return '...' + exprStr;
        }
        return '';
    }).join(', ');

    return '{' + props + '}';
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
                return 'return ' + retExprStr + ';';
            }
            return 'return;';
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
                    let result = 'const ' + name;
                    if (init) {
                        result += ' = ' + jsonToCodeString(init);
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
            return jsonToCodeString(expr) + '.' + name;
        }

        case 'ElementAccessExpression': {
            const elemExpr = json.expression;
            // The argument can be named 'argument' or 'argumentExpression'
            const arg = json.argument || json.argumentExpression;
            return jsonToCodeString(elemExpr) + '[' + jsonToCodeString(arg) + ']';
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
            let result = 'if (' + jsonToCodeString(ifExpr) + ') {\n' + jsonToCodeString(thenStmt) + '\n}';
            if (elseStmt) {
                result += ' else {\n' + jsonToCodeString(elseStmt) + '\n}';
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
            return 'typeof ' + jsonToCodeString(json.expression);
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
            return jsonToCodeString(json.expression);
        }

        case 'AwaitExpression': {
            return 'await ' + jsonToCodeString(json.expression);
        }

        case 'ObjectLiteralExpression': {
            if (json.properties) {
                const propStr = json.properties.map(p => jsonToCodeString(p)).join(', ');
                return '{' + propStr + '}';
            }
            return '{}';
        }

        case 'PropertyAssignment': {
            return json.name + ': ' + jsonToCodeString(json.initializer);
        }

        case 'ArrowFunction': {
            return '/* arrow function */';
        }

        case 'FunctionExpression': {
            return '/* function expression */';
        }

        case 'SpreadElement': {
            return '...' + jsonToCodeString(json.expression);
        }

        case 'SpreadAssignment': {
            return '...' + jsonToCodeString(json.expression);
        }

        case 'BreakStatement': {
            if (json.label) {
                return 'break ' + json.label + ';';
            }
            return 'break;';
        }

        case 'ContinueStatement': {
            if (json.label) {
                return 'continue ' + json.label + ';';
            }
            return 'continue;';
        }

        case 'TryStatement': {
            // Use pre-generated text
            if (json.text) return json.text;
            return '/* try statement */';
        }

        default:
            // For other expressions, try to use text if available
            if (json.text) return json.text;
            return JSON.stringify(json);
    }
}

main();
