/**
 * ETS Preprocessing Module
 * Handles ETS-specific syntax transformations
 * @module lib/preprocessor
 */

const { REGEX_PATTERNS, KEYWORDS } = require('../common/constants');

/**
 * Preprocess ETS code to convert struct to class and extract decorators.
 * @param {string} sourceCode - The source code to preprocess
 * @returns {Object} Object containing processed code and extracted decorators
 */
function preprocessEts(sourceCode) {
    let processedCode = sourceCode;
    const extractedDecorators = extractDecorators(sourceCode);

    processedCode = processedCode.replace(REGEX_PATTERNS.STRUCT_KEYWORD, 'class ');
    processedCode = ensureExportForEntryClasses(processedCode);

    return {
        code: processedCode,
        decorators: extractedDecorators
    };
}

/**
 * Extract decorators from struct declarations.
 * @param {string} sourceCode - The source code to search
 * @returns {Array} Array of extracted decorator objects
 */
function extractDecorators(sourceCode) {
    const extractedDecorators = [];
    let match;

    while ((match = REGEX_PATTERNS.STRUCT_DECORATOR.exec(sourceCode)) !== null) {
        const decoratorText = match[1];
        REGEX_PATTERNS.INDIVIDUAL_DECORATOR.lastIndex = 0;
        let decMatch;

        while ((decMatch = REGEX_PATTERNS.INDIVIDUAL_DECORATOR.exec(decoratorText)) !== null) {
            extractedDecorators.push({
                name: decMatch[1],
                fullText: decMatch[0]
            });
        }
    }

    return extractedDecorators;
}

/**
 * Ensure @Entry decorated classes have export keyword.
 * @param {string} processedCode - The processed source code
 * @returns {string} Code with export added to Entry classes
 */
function ensureExportForEntryClasses(processedCode) {
    let code = processedCode;
    let match;

    REGEX_PATTERNS.ENTRY_PATTERN.lastIndex = 0;
    while ((match = REGEX_PATTERNS.ENTRY_PATTERN.exec(code)) !== null) {
        const classDecl = match[0];
        if (!classDecl.includes(KEYWORDS.EXPORT)) {
            code = code.replace(classDecl, classDecl.replace('class ', 'export class '));
        }
    }

    return code;
}

module.exports = {
    preprocessEts,
    extractDecorators,
    ensureExportForEntryClasses
};
