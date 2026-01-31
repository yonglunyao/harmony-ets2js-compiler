# ETS to JS Compiler - Coding Standards

## Overview

This document defines the coding standards and best practices for the ETS to JS Compiler project. All contributors must follow these guidelines to maintain code quality and consistency.

## Table of Contents

1. [General Principles](#general-principles)
2. [Code Quality Standards](#code-quality-standards)
3. [Naming Conventions](#naming-conventions)
4. [Code Organization](#code-organization)
5. [Error Handling](#error-handling)
6. [Logging](#logging)
7. [Testing Standards](#testing-standards)
8. [Documentation](#documentation)
9. [Java Specific Guidelines](#java-specific-guidelines)

---

## General Principles

### Clean Code Principles

- **Single Responsibility**: Each class/method should have one reason to change
- **DRY (Don't Repeat Yourself)**: Avoid code duplication
- **KISS (Keep It Simple, Stupid)**: Keep code simple and readable
- **YAGNI (You Aren't Gonna Need It)**: Don't add functionality until needed
- **Readability over Cleverness**: Code should be easy to read and understand

### Huawei Java Coding Standards (10 Army Rules)

1. **No Magic Numbers**: All constants must be defined with meaningful names
2. **Proper Naming**: Use clear, descriptive names for variables, methods, and classes
3. **Exception Handling**: Use exceptions instead of returning error codes
4. **Resource Management**: Properly close/release resources (try-with-resources)
5. **Null Safety**: Avoid NPE by proper null checks and using Optional
6. **Logging**: Use appropriate logging levels (SLF4J)
7. **Code Comments**: Comments should explain "why", not "what"
8. **Code Format**: Follow consistent formatting (indentation, line length)
9. **Complexity Control**: Keep cyclomatic complexity ≤ 10 per method
10. **Security**: Avoid security vulnerabilities (SQL injection, XSS, etc.)

---

## Code Quality Standards

### Cyclomatic Complexity

| Range | Rating | Risk | Action Required |
|-------|--------|------|-----------------|
| 1-5   | Excellent | Low | None |
| 6-10  | Acceptable | Medium | Consider optimizing |
| 11-20 | High | High | Must refactor |
| 20+   | Very High | Very High | Immediate refactor |

**Rule**: All methods must have cyclomatic complexity ≤ 10.

### Code Review Checklist

- [ ] No magic numbers (extract to constants)
- [ ] No null returns where exceptions are more appropriate
- [ ] All exceptions are specific (not generic RuntimeException)
- [ ] Proper error handling and logging
- [ ] Cyclomatic complexity ≤ 10
- [ ] Meaningful variable and method names
- [ ] Proper resource management (try-with-resources)
- [ ] No code duplication
- [ ] Unit tests written for new functionality
- [ ] Documentation updated

---

## Naming Conventions

### Java Naming Standards

```java
// Classes: PascalCase
public class EtsCompiler { }

// Interfaces: PascalCase, preferably with 'I' prefix or descriptive name
public interface AstVisitor<T> { }

// Methods: camelCase, verb-noun pattern
public String getSummary() { }
public void compile(Path source) { }

// Variables: camelCase
private String fileName;
private final int maxThreads;

// Constants: UPPER_SNAKE_CASE
public static final String MAX_THREADS = "maxThreads";
public static final int DEFAULT_TIMEOUT = 60;

// Package names: lowercase
package com.ets2jsc.parser;

// Enum names: PascalCase
public enum Status { SUCCESS, FAILURE, SKIPPED }
```

### File Naming

- Java source files: `PascalCase.java`
- Test files: `PascalCaseTest.java`
- Resource files: lowercase with hyphens `parse-ets.js`

---

## Code Organization

### Package Structure

```
com.ets2jsc/
├── ast/                    # AST node definitions
├── cli/                    # Command-line interface
├── config/                 # Configuration classes
├── constant/               # Constants and enums
├── core/                   # Core compiler functionality
├── exception/              # Custom exceptions
├── generator/              # Code generation
│   └── writer/            # Code writers
├── parser/                 # Parsing functionality
│   └── internal/          # Internal parser implementation
├── transformer/            # AST transformations
│   └── decorators/        # Decorator transformations
├── util/                   # Utility classes
└── EtsCompiler.java       # Main compiler class
```

### Class Organization

```java
1. Package declaration
2. Imports (grouped and sorted)
3. Class documentation
4. Static fields
5. Instance fields
6. Constructors
7. Static methods
8. Instance methods
9. Private helper methods
10. Inner classes
```

---

## Error Handling

### Custom Exceptions

All custom exceptions must be in the `com.ets2jsc.exception` package:

```java
// Base exception
public class ParserException extends RuntimeException {
    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Specific exception
public class ParserInitializationException extends ParserException {
    // Implementation
}
```

### Exception Handling Guidelines

```java
// ✅ GOOD: Use specific exceptions
throw new ParserInitializationException("Failed to initialize parser", cause);

// ❌ BAD: Use generic exceptions
throw new RuntimeException("Failed to initialize parser");

// ✅ GOOD: Throw exception for invalid input
public String generate(AstNode node) {
    if (node == null) {
        throw new IllegalArgumentException("AST node cannot be null");
    }
    return node.accept(this);
}

// ❌ BAD: Return null for invalid input
public String generate(AstNode node) {
    if (node == null) {
        return null;
    }
    return node.accept(this);
}
```

### Null Safety

```java
// ✅ GOOD: Use Optional for potentially absent values
public Optional<Decorator> getDecorator(String decoratorName) {
    return decorators.stream()
            .filter(d -> d.getName().equals(decoratorName))
            .findFirst();
}

// ✅ GOOD: Check null before setting
private PropertyDeclaration createPrivateProperty(...) {
    PropertyDeclaration privateProp = new PropertyDeclaration(privateName);
    privateProp.setTypeAnnotation(getObservedPropertyType() + "<" + propType + ">");

    String initializer = getInitializer(originalProp);
    if (initializer != null) {
        privateProp.setInitializer(initializer);
    }

    return privateProp;
}
```

---

## Logging

### Logging Framework

Use SLF4J with Simple binding:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);

    public void doSomething() {
        LOGGER.info("Processing file: {}", fileName);
        LOGGER.debug("Detailed debug info: {}", details);
        LOGGER.warn("Unexpected value: {}", value);
        LOGGER.error("Failed to process: {}", error, exception);
    }
}
```

### Logging Levels

| Level | Usage | Example |
|-------|--------|---------|
| ERROR | Errors that prevent normal operation | Compilation failed, file not found |
| WARN | Unexpected but recoverable situations | Invalid decorator usage, fallback to default |
| INFO | Important informational messages | Compilation started, files processed |
| DEBUG | Detailed diagnostic information | Variable values, method entry/exit |

### Logging Guidelines

```java
// ✅ GOOD: Use parameterized logging
LOGGER.info("Compiled {} files in {}ms", fileCount, duration);

// ❌ BAD: String concatenation
LOGGER.info("Compiled " + fileCount + " files in " + duration + "ms");

// ✅ GOOD: Include exception when logging errors
LOGGER.error("Failed to parse file: {}", fileName, exception);

// ❌ BAD: Log to System.err
System.err.println("Error: " + message);
```

---

## Testing Standards

### Test Naming

```java
@DisplayName("Property Declaration Tests")
class PropertyDeclarationTest {

    @Test
    @DisplayName("Test property creation and basic properties")
    void testPropertyCreation() {
        // Test implementation
    }
}
```

### Test Organization

```java
1. Given-When-Then pattern
2. Arrange-Act-Assert pattern
3. Descriptive test names
4. One assertion per test (when practical)
5. Test edge cases
```

### Test Coverage Requirements

- Unit tests: ≥ 80% coverage
- Integration tests: Critical paths must be covered
- All public APIs must have tests

---

## Documentation

### JavaDoc Standards

```java
/**
 * Compiles ETS source files to JavaScript.
 *
 * <p>This compiler orchestrates the entire compilation process including
 * parsing, transformation, code generation, and output writing.</p>
 *
 * @since 1.0
 * @author ETS Compiler Team
 */
public class EtsCompiler {

    /**
     * Compiles a single ETS source file to JavaScript.
     *
     * @param sourcePath path to the ETS source file
     * @param outputPath path to the output JavaScript file
     * @throws CompilationException if compilation fails
     */
    public void compile(Path sourcePath, Path outputPath) throws CompilationException {
        // Implementation
    }
}
```

### Comment Guidelines

```java
// ✅ GOOD: Comment explains why
// Use PIPELINE_SIZE power of 2 for efficient CPU cache utilization
private static final int PIPELINE_SIZE = 32;

// ❌ BAD: Comment repeats what code says
// Set pipeline size to 32
private static final int PIPELINE_SIZE = 32;

// ✅ GOOD: Comment for complex logic
// Handle Windows paths with spaces (remove leading slash)
if (jarPath.startsWith("/") && jarPath.contains(":")) {
    jarPath = jarPath.substring(1);
}

// ✅ GOOD: TODO comment with owner
// TODO(john): Add support for ES modules
```

---

## Java Specific Guidelines

### Constants

All constants must be defined in dedicated constant classes:

```java
// ✅ GOOD: Centralized constants
public final class RuntimeFunctions {
    public static final String COMPONENT_CREATE = "create";
    public static final String COMPONENT_POP = "pop";
    public static final String CREATE_STATE = "createState";

    private RuntimeFunctions() {
        // Prevent instantiation
    }
}

// ❌ BAD: Magic numbers
sb.append(componentName).append(".").append("create(");
```

### Lombok Usage

```java
// Use Lombok to reduce boilerplate code
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilerConfig {
    private CompileMode compileMode;
    private boolean partialUpdateMode;
}
```

### Resource Management

```java
// ✅ GOOD: Use try-with-resources
try (InputStream is = jarFile.getInputStream(entry)) {
    Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
}

// ✅ GOOD: Finally block for cleanup
BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
try {
    // Read output
} finally {
    if (reader != null) {
        try {
            reader.close();
        } catch (IOException ignored) {
            // Ignore close exception
        }
    }
    if (process != null) {
        process.destroyForcibly();
    }
}
```

---

## Refactoring History

### Completed Refactorings

#### 1. Magic Numbers Elimination (2024-01-31)
- Extracted all magic numbers to constant classes
- Files modified: `TypeScriptScriptParser.java`, `JsWriter.java`
- Constants added: `PROTOCOL_FILE`, `UNICODE_REPLACEMENT_CHARACTER`, `BMP_MAX_VALUE`, etc.

#### 2. Null Safety Improvements (2024-01-31)
- Replaced null returns with Optional where appropriate
- Added proper null checks
- Files modified: `PropertyDeclaration.java`, `PropertyTransformer.java`
- Changed: `getDecorator()` returns `Optional<Decorator>`

#### 3. Custom Exception Classes (2024-01-31)
- Created dedicated `exception` package
- Added `ParserException` and `ParserInitializationException`
- Replaced generic `RuntimeException` with specific exceptions

#### 4. SLF4J Logging Integration (2024-01-31)
- Added SLF4J dependency to pom.xml
- Replaced `System.err` with proper logging
- Files modified: `JsWriter.java`, `DecoratorTransformer.java`, `ParallelEtsCompiler.java`

#### 5. Code Internationalization (2024-01-31)
- Replaced all Chinese comments with English
- Updated test DisplayName annotations
- Improved code maintainability for international teams

### Technical Debt Tracking

| Item | Priority | Status | Notes |
|------|----------|--------|-------|
| Replace remaining Chinese in test DisplayName | Low | Pending | 144 chars in 9 files, tests pass |
| Add more unit tests for edge cases | Medium | Ongoing | Target: 90% coverage |
| Performance profiling | Low | Pending | Profile before optimization |

---

## Code Review Checklist

Before submitting any code, verify:

### Mandatory Checks
- [ ] No magic numbers (all constants defined)
- [ ] No null returns where exceptions are better
- [ ] Specific exceptions used (not RuntimeException)
- [ ] SLF4J logging used (not System.err/out)
- [ ] Cyclomatic complexity ≤ 10
- [ ] Unit tests added/updated
- [ ] JavaDoc comments written
- [ ] Code formatted consistently
- [ ] No TODO or FIXME left in committed code

### Recommended Checks
- [ ] Code duplication ≤ 5%
- [ ] Method length ≤ 50 lines
- [ ] Class length ≤ 500 lines
- [ ] Parameter count ≤ 5
- [ ] Nesting depth ≤ 4

---

## Build and Test Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Run specific test
mvn test -Dtest=ClassName

# Generate test coverage report
mvn jacoco:report

# Build JAR
mvn package
```

---

## Related Documents

- [README.md](../README.md) - Project overview
- [CLAUDE.md](../CLAUDE.md) - Project instructions
- [pom.xml](../pom.xml) - Maven configuration

---

## Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2024-01-31 | Code Review | Initial version - Established coding standards |
