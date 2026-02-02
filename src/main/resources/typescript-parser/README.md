# @ets2jsc/typescript-parser

ETS/TypeScript to AST parser with npm SDK style package structure.

## Overview

This parser converts ETS (ArkTS) and TypeScript source code into an Abstract Syntax Tree (AST) represented as JSON. It handles ETS-specific syntax like `struct` by preprocessing it to `class`.

## Package Structure

```
typescript-parser/
├── index.js                          # ⭐ UNIFIED ENTRY POINT (CLI + Library)
└── src/
    └── javascript/                   # JavaScript source modules
        ├── ast/                      # AST processing module
        │   ├── preprocessor.js       # ETS preprocessing
        │   ├── converter.js          # Main AST converter
        │   └── converters/           # Node type converters
        │       ├── literals.js       # Literal nodes
        │       ├── statements.js     # Statement nodes
        │       ├── declarations.js   # Declaration nodes
        │       └── expressions.js    # Expression nodes
        ├── codegen/                  # Code generation module
        │   └── index.js              # JSON to code generator
        └── common/                   # Shared utilities
            ├── constants.js          # All constants
            └── utils.js              # Utility functions
```

## Architecture

The module is organized by **language and functional domain** with clear separation of concerns:

1. **src/javascript/ast/** - AST conversion logic
   - Uses parameter-passing pattern to avoid circular dependencies
   - Each converter receives `(result, node, convert)` parameters

2. **src/javascript/codegen/** - Code string generation
   - Converts JSON AST back to executable code

3. **src/javascript/common/** - Shared utilities
   - Constants and functions used across modules

## Usage

### As a Library

```javascript
const { parse, parseFile } = require('./index');

// Parse source code string
const ast = parse('const x = 42;');

// Parse file
const ast = parseFile('./input.ets');
```

### As a CLI Tool

```bash
node index.js input.ets output.json
```

### Using Sub-modules

```javascript
const { preprocess, convert } = require('./index');

// Advanced usage with separate preprocessing and conversion
const preprocessResult = preprocess(sourceCode);
const ast = convert(tsSourceFile, preprocessResult.decorators);
```

## Modules

### index.js (Unified Entry Point)
- **CLI Interface**: `node index.js <input> <output>`
- **Library Interface**: `require('./index')`
- Exports: `parse()`, `parseFile()`, `preprocess`, `convert`
- Lines: 111

### src/javascript/ast/ - AST Processing Module

#### src/javascript/ast/preprocessor.js (76 lines)
ETS-specific preprocessing:
- `preprocessEts()` - Main preprocessing function
- `extractDecorators()` - Extract decorators from struct
- `ensureExportForEntryClasses()` - Add export to @Entry classes

#### src/javascript/ast/converter.js (267 lines)
Main AST converter orchestrator:
- `convertAstToJson()` - Convert TypeScript AST to JSON
- Delegates to specialized converters for each node type
- Uses parameter-passing pattern to avoid circular dependencies

#### src/javascript/ast/converters/ (1,844 lines)
Specialized converters for different node types:
- **literals.js** (123 lines) - String, number, template literals, boolean, null, undefined
- **statements.js** (464 lines) - Block, variable, return, if, loops, switch, try/catch
- **declarations.js** (597 lines) - Class, function, method, property, decorator declarations
- **expressions.js** (660 lines) - Call, identifier, property access, binary, unary, etc.

### src/javascript/codegen/ - Code Generation Module

#### src/javascript/codegen/index.js (456 lines)
JSON to code string generation:
- `jsonToCodeString()` - Convert JSON AST back to code
- Separate generator for each node type

### src/javascript/common/ - Shared Utilities Module

#### src/javascript/common/constants.js (152 lines)
All magic numbers and strings extracted to named constants:
- `RESOURCE_TYPE_IDS` - Resource type mappings
- `KEYWORDS` - JavaScript/TypeScript keywords
- `OPERATORS` - Operators and punctuation
- `REGEX_PATTERNS` - ETS preprocessing patterns

#### src/javascript/common/utils.js (165 lines)
Utility functions:
- `getSyntaxKindName()` - Map syntax kind to name
- `hasExportModifier()` - Check for export modifier
- `isBindingPattern()` - Check for binding pattern
- `getDeclarationKind()` - Get declaration kind from flags
- `hasAsyncModifier()` - Check for async modifier
- `escapeTemplateLiteral()` - Escape template strings

## Clean Code Principles

1. **No Magic Numbers** - All constants in `src/javascript/common/constants.js`
2. **Functional Domain Separation** - `ast/`, `codegen/`, `common/`
3. **Language Layer** - `src/javascript/` for multi-language support
4. **Single Responsibility** - Each module has one clear purpose
5. **DRY** - Shared utilities in `common/`
6. **KISS** - Simple parameter-passing for recursion
7. **YAGNI** - No unnecessary abstractions
8. **English Documentation** - All comments and naming in English
9. **Standard Entry Point** - `index.js` follows npm convention

## Code Statistics

| Module | Lines | Purpose |
|--------|-------|---------|
| **index.js** | 111 | Entry point |
| **src/javascript/ast/** | 2,187 | AST processing |
| **src/javascript/codegen/** | 456 | Code generation |
| **src/javascript/common/** | 317 | Shared utilities |
| **Total** | 3,071 | Full implementation |

## Test Results

- **Pass Rate: 75.0%** (75/100 tests)
- All tests that pass with the original version also pass
- No regression in functionality

## License

ISC
