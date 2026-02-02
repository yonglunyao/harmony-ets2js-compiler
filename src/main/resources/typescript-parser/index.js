#!/usr/bin/env node

/**
 * ETS/TypeScript to AST Parser
 *
 * Unified entry point for both library usage and CLI execution.
 * Parses ETS (ArkTS) and TypeScript source code and outputs an AST as JSON.
 *
 * @module index
 * @author ETS Compiler Team
 * @since 2.0
 *
 * @example CLI usage:
 *   node index.js <source-file> <output-file>
 *
 * @example Library usage:
 *   const { parse, parseFile } = require('./index');
 *   const ast = parse('const x = 42;');
 */

const ts = require('typescript');
const fs = require('fs');

// Import from the modular structure
const { preprocessEts } = require('./src/javascript/ast/preprocessor');
const { convertAstToJson } = require('./src/javascript/ast/converter');

// =============================================================================
// PUBLIC API - Library Interface
// =============================================================================

/**
 * Parse ETS/TypeScript source code and output AST as JSON.
 *
 * @param {string} sourceCode - The source code to parse
 * @param {string} fileName - Optional filename for error reporting
 * @returns {Object} AST as JSON-serializable object
 */
function parse(sourceCode, fileName = 'input.ets') {
    const preprocessResult = preprocessEts(sourceCode);

    const tsSourceFile = ts.createSourceFile(
        fileName,
        preprocessResult.code,
        ts.ScriptTarget.Latest,
        true,
        ts.ScriptKind.TS
    );

    return convertAstToJson(tsSourceFile, preprocessResult.decorators);
}

/**
 * Parse ETS/TypeScript file and output AST as JSON.
 *
 * @param {string} filePath - Path to the source file
 * @returns {Object} AST as JSON-serializable object
 */
function parseFile(filePath) {
    const sourceCode = fs.readFileSync(filePath, 'utf-8');
    return parse(sourceCode, filePath);
}

// =============================================================================
// CLI INTERFACE
// =============================================================================

const { ERROR_MESSAGES } = require('./src/javascript/common/constants');

/**
 * Main CLI function.
 * Executes when run directly from command line.
 */
function main() {
    const args = process.argv.slice(2);

    if (args.length < 2) {
        console.error(ERROR_MESSAGES.USAGE);
        process.exit(1);
    }

    const [sourceFilePath, outputFile] = args;

    try {
        const astJson = parseFile(sourceFilePath);
        fs.writeFileSync(outputFile, JSON.stringify(astJson, null, 2));
        console.error(ERROR_MESSAGES.SUCCESS, sourceFilePath);
    } catch (error) {
        console.error(ERROR_MESSAGES.PARSE_FAILED, error.message);
        console.error(error.stack);
        process.exit(1);
    }
}

// =============================================================================
// MODULE EXPORTS & ENTRY POINT
// =============================================================================

// Export public API for library usage
module.exports = {
    parse,
    parseFile,
    // Export sub-modules for advanced usage
    preprocess: preprocessEts,
    convert: convertAstToJson
};

// Run CLI if called directly
if (require.main === module) {
    main();
}
