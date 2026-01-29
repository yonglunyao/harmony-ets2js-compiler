#!/usr/bin/env node

const ts = require('typescript');
const fs = require('fs');

/**
 * Parse ETS/TypeScript source code and output AST as JSON.
 * Usage: node parse.js <source-file> <output-file>
 */
function main() {
    const args = process.argv.slice(2);
    if (args.length < 2) {
        console.error('Usage: node parse.js <source-file> <output-file>');
        process.exit(1);
    }

    const [sourceFilePath, outputFile] = args;

    try {
        // Read source file
        const sourceCode = fs.readFileSync(sourceFilePath, 'utf-8');

        // Parse using TypeScript Compiler API
        const tsSourceFile = ts.createSourceFile(
            sourceFilePath,
            sourceCode,
            ts.ScriptTarget.Latest,
            true, // setParentNodes
            ts.ScriptKind.TS // Treat as TypeScript
        );

        // Convert AST to serializable format
        const astJson = convertAstToJson(tsSourceFile);

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
 * Convert TypeScript AST to JSON-serializable format.
 */
function convertAstToJson(node) {
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
                result.statements.push(convertAstToJson(stmt));
            }
            break;

        case ts.SyntaxKind.ClassDeclaration:
        case ts.SyntaxKind.StructDeclaration:
            result.name = node.name?.escapedText || '';
            result.decorators = [];
            for (const dec of (node.decorators || [])) {
                result.decorators.push(convertAstToJson(dec));
            }
            result.members = [];
            for (const member of (node.members || [])) {
                result.members.push(convertAstToJson(member));
            }
            break;

        case ts.SyntaxKind.MethodDeclaration:
            result.name = node.name?.escapedText || '';
            result.decorators = [];
            for (const dec of (node.decorators || [])) {
                result.decorators.push(convertAstToJson(dec));
            }
            result.parameters = [];
            for (const param of (node.parameters || [])) {
                result.parameters.push({
                    name: param.name?.escapedText || '',
                    type: param.type?.getText() || ''
                });
            }
            result.body = node.body ? convertAstToJson(node.body) : null;
            break;

        case ts.SyntaxKind.PropertyDeclaration:
            result.name = node.name?.escapedText || '';
            result.type = node.type?.getText() || '';
            result.initializer = node.initializer?.getText() || '';
            result.decorators = [];
            for (const dec of (node.decorators || [])) {
                result.decorators.push(convertAstToJson(dec));
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
                result.statements.push(convertAstToJson(stmt));
            }
            break;

        case ts.SyntaxKind.ExpressionStatement:
            result.expression = convertAstToJson(node.expression);
            break;

        case ts.SyntaxKind.CallExpression:
            result.expression = convertAstToJson(node.expression);
            result.arguments = [];
            for (const arg of (node.arguments || [])) {
                result.arguments.push(arg.getText());
            }
            break;

        case ts.SyntaxKind.Identifier:
            result.name = node.escapedText;
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
        [ts.SyntaxKind.StructDeclaration]: 'StructDeclaration',
        [ts.SyntaxKind.MethodDeclaration]: 'MethodDeclaration',
        [ts.SyntaxKind.PropertyDeclaration]: 'PropertyDeclaration',
        [ts.SyntaxKind.Decorator]: 'Decorator',
        [ts.SyntaxKind.Block]: 'Block',
        [ts.SyntaxKind.ExpressionStatement]: 'ExpressionStatement',
        [ts.SyntaxKind.CallExpression]: 'CallExpression',
        [ts.SyntaxKind.Identifier]: 'Identifier',
    };

    return kindMap[kind] || `Unknown_${kind}`;
}

main();
