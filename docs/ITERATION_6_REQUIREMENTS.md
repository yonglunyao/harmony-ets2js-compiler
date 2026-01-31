# Iteration 6 Requirements

## Overview
This document tracks requirements for Iteration 6 development based on issues found during testing of the harmony-utils project compilation.

## Critical Issues Found

### Issue 1: Empty Class Declarations for Plain TypeScript Classes
**Priority**: P0 (Critical)
**Status**: BLOCKING - Root cause identified but not yet fixed

**Description**:
When compiling certain TypeScript utility classes (like LogUtil.ets, ObjectUtil.ets, etc.), the generated JavaScript output contains only import statements and is missing the complete class definition with methods and properties.

**Examples**:
- `LogUtil.js` - Only has 4 import statements, class is completely missing
- `ObjectUtil.js` - Only has 4 import statements, class is completely missing
- `NotificationOptions.js` - Only has 3 import statements, class is completely missing

**Root Cause**:
The issue is in `TypeScriptScriptParser.convertJsonToAst()`. When an exception occurs during statement conversion:
1. The exception is caught in `convertJsonNode()`
2. Falls back to `convertJsonNodeLegacy()`
3. Legacy method returns `null` for ClassDeclaration (doesn't end with "Expression")
4. `null` statements are not added to SourceFile
5. Result: Class declarations are silently dropped

**Current Code Location**:
```java
// TypeScriptScriptParser.java:244-249
try {
    return conversionContext.convertStatement(json);
} catch (Exception e) {
    // Fallback to legacy methods if converter fails
    return convertJsonNodeLegacy(json, kindName);
}

// Legacy method returns null for ClassDeclaration
private AstNode convertJsonNodeLegacy(JsonObject json, String kindName) {
    if (kindName.endsWith("Expression")) {
        return new ExpressionStatement(convertExpressionToString(json));
    }
    return null;  // <-- ClassDeclaration is lost here
}
```

**Possible Solutions**:
1. **Option A**: Add proper error logging and handling in conversion methods
2. **Option B**: Fix the root cause of exceptions in statement converters
3. **Option C**: Improve legacy fallback to handle ClassDeclaration
4. **Option D**: Remove the try-catch that swallows exceptions, let them propagate

**Acceptance Criteria**:
- All TypeScript class declarations are successfully converted
- Generated JS contains complete class definitions with methods and properties
- No silent dropping of statements during parsing
- Proper error messages when conversion fails

**Test Case**:
```java
@Test
void testLogUtilGeneratesCompleteClass() {
    compile("LogUtil.ets");
    assertOutputContains("export class LogUtil");
    assertOutputContains("static init(");
    assertOutputContains("static debug(");
}
```

---

### Issue 2: Raw AST JSON in JavaScript Output
**Priority**: P0 (Critical)
**Status**: IDENTIFIED

**Description**:
Method declarations inside object literals (shorthand method syntax) generate raw AST JSON instead of JavaScript code.

**Example**:
```typescript
// Source
AuthUtil.userAuthInstance.on('result', {
  onResult(result) { ... }
});

// Generated (INCORRECT)
AuthUtil.userAuthInstance.on("result", {{"kind":175,"kindName":"MethodDeclaration",...}})
```

**Root Cause**:
`MethodDeclaration` inside object literals is not handled by `ExpressionConverterRegistry`. The expression converter throws an exception or returns the raw JSON toString().

**Files Affected**:
- AuthUtil.js (line 53)
- CrashUtil.js (line 20)
- DialogUtil.js (line 123)
- RegexUtil.js (line 22)
- NotificationUtil.js (line 202)

**Possible Solutions**:
1. Add `MethodDeclarationConverter` to `ExpressionConverterRegistry` for handling method shorthand in object literals
2. Create proper JavaScript method syntax: `{ methodName() { body } }`

**Acceptance Criteria**:
- Object literal method shorthand is properly converted to JavaScript
- No raw AST JSON in generated output
- es2abc compilation succeeds for all affected files

---

### Issue 3: Corrupted Export Statements
**Priority**: P1 (High)
**Status**: IDENTIFIED

**Description**:
Some generated JS files have corrupted export statements with mixed content and garbage text.

**Examples**:
- `Index.js` - Has incomplete exports and corrupted text like "buffer } from '@ki"
- `ToastUtil.js` - Similar corruption

**Root Cause**:
Unknown - needs investigation. Possibly related to how ExportStatement is processed.

---

## Requirements Summary

| Issue | Priority | Type | Status |
|-------|----------|------|--------|
| Empty class declarations | P0 | Bug | Root cause found |
| Raw AST JSON in output | P0 | Bug | Solution identified |
| Corrupted export statements | P1 | Bug | Investigation needed |

---

## Notes
- These issues affect the compilation of real-world TypeScript projects (harmony-utils)
- 64 out of 71 files compile successfully with es2abc
- 7 files have syntax errors that need to be fixed
- The fix should not break existing functionality for @Component struct classes
