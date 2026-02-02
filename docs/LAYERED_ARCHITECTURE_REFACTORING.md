# Layered Architecture Refactoring Plan

## Overview

This document describes the migration plan for refactoring the ETS to JS Compiler
from a mixed-dimension module structure to a clean **Layered Architecture**.

---

## Problem Statement

### Current Issues

The current codebase mixes multiple dimensions of organization:

| Dimension | Packages | Problem |
|-----------|----------|---------|
| **Pipeline** | parser/, transformer/, generator/, compiler/ | Mixed with technical layers |
| **Layer** | api/, impl/, di/ | Incomplete implementation |
| **Component** | cli/, command/, config/, util/ | Unclear boundaries |
| **Model** | ast/, context/, events/ | Scattered across layers |
| **Sub-module** | parser/converters/expr, generator/strategy/ | Inconsistent nesting |

This leads to:
- Unclear module boundaries
- Complex dependency graphs
- Difficult onboarding for new developers
- Hard to test in isolation

---

## Target Architecture

```
src/main/java/com/ets2jsc/
├── domain/                    # 【Domain Layer】Core business models
├── application/              # 【Application Layer】Use case orchestration
├── infrastructure/           # 【Infrastructure Layer】Technical implementations
├── interfaces/               # 【Interface Layer】External interactions
└── shared/                   # 【Shared Layer】Cross-cutting utilities
```

---

## Layer Responsibilities

### Domain Layer (`domain/`)

**Responsibility**: Core business models and business rules, completely independent.

```
domain/
├── model/
│   ├── ast/              # AST node models (SourceFile, ClassDeclaration, etc.)
│   ├── compilation/      # Compilation models (CompilationRequest, CompilationResult)
│   ├── config/           # Configuration models (CompilerConfig, OutputConfiguration)
│   └── source/           # Source code representation (SourceCode, SourceLocation)
├── service/              # Domain service interfaces
│   ├── ParserService.java
│   ├── TransformerService.java
│   └── GeneratorService.java
└── repository/           # (Optional) Persistence abstractions
```

**Dependency Rule**: Can only depend on `shared/`. No dependencies on other layers.

---

### Application Layer (`application/`)

**Responsibility**: Use case orchestration, compilation pipeline management.

```
application/
├── compile/              # Compilation orchestration
│   ├── CompilationOrchestrator.java    # Main orchestrator
│   ├── CompilationPipeline.java        # Pipeline definition
│   ├── CompilationStage.java           # Stage abstraction
│   └── CompilationContext.java         # Pipeline context
├── dto/                  # Data Transfer Objects
│   ├── CompilationRequestDTO.java
│   └── CompilationResultDTO.java
├── facade/               # Application facades
│   └── CompilerFacade.java
└── exception/            # Application-specific exceptions
    └── CompilationWorkflowException.java
```

**Dependency Rule**: Can depend on `domain/` and `shared/`. Can use interfaces from `infrastructure/`.

---

### Infrastructure Layer (`infrastructure/`)

**Responsibility**: Technical implementations, external system integration.

```
infrastructure/
├── parser/
│   ├── TypeScriptScriptParser.java
│   ├── AstBuilder.java
│   ├── converters/
│   │   ├── expr/        # Expression converters
│   │   └── stmt/        # Statement converters
│   └── internal/
│       ├── ITypeScriptParser.java
│       └── IAstBuilder.java
├── transformer/
│   ├── chain/           # Transformation chain
│   ├── decorators/
│   │   └── impl/        # Decorator implementations
│   └── impl/            # Transformer implementations
├── generator/
│   ├── CodeGenerator.java
│   ├── strategy/        # Generation strategies
│   ├── writer/          # Output writers
│   └── internal/
│       ├── IJsWriter.java
│       └── ISourceMapGenerator.java
├── config/
│   ├── CompilerConfigLoader.java
│   └── CompilerConfigValidator.java
└── factory/             # Infrastructure factories
    └── TransformerFactory.java
```

**Dependency Rule**: Can depend on `domain/` and `shared/`. Implements interfaces defined in `domain/`.

---

### Interface Layer (`interfaces/`)

**Responsibility**: External interaction, user input handling.

```
interfaces/
├── cli/                  # Command-line interface
│   ├── EtsCompilerLauncher.java
│   ├── command/
│   │   ├── CliCommand.java
│   │   └── CommandLineParser.java
│   └── output/
│       └── OutputFormatter.java
├── api/                  # (Future) REST API
└── spi/                  # Service Provider Interface
    └── CompilerExtension.java
```

**Dependency Rule**: Can only depend on `application/` and `shared/`.

---

### Shared Layer (`shared/`)

**Responsibility**: Cross-cutting utilities, common to all layers.

```
shared/
├── exception/            # Custom exceptions
│   ├── CompilationException.java
│   ├── ParserException.java
│   └── CodeGenerationException.java
├── constant/             # Constants
│   ├── Components.java
│   ├── Decorators.java
│   └── RuntimeFunctions.java
├── util/                 # Utilities
│   ├── FileUtil.java
│   └── StringUtil.java
└── events/               # Event definitions
    ├── CompilationEvent.java
    └── CompilationListener.java
```

**Dependency Rule**: No dependencies on other layers.

---

## Migration Phases

### Phase 1: Foundation Setup (Week 1)

**Objective**: Create new layer structure and move shared components.

**Tasks**:
1. Create layer directories
2. Move `exception/` → `shared/exception/`
3. Move `constant/` → `shared/constant/`
4. Move `util/` → `shared/util/`
5. Move `events/` → `shared/events/`
6. Update all import statements

**Migration Script**:
```bash
# Create directories
mkdir -p src/main/java/com/ets2jsc/{domain,application,infrastructure,interfaces,shared}

# Move shared components
git mv src/main/java/com/ets2jsc/exception src/main/java/com/ets2jsc/shared/exception
git mv src/main/java/com/ets2jsc/constant src/main/java/com/ets2jsc/shared/constant
git mv src/main/java/com/ets2jsc/util src/main/java/com/ets2jsc/shared/util
git mv src/main/java/com/ets2jsc/events src/main/java/com/ets2jsc/shared/events

# Update imports
find src -name "*.java" -exec sed -i 's/com\.ets2jsc\.exception\./com.ets2jsc.shared.exception./g' {} \;
find src -name "*.java" -exec sed -i 's/com\.ets2jsc\.constant\./com.ets2jsc.shared.constant./g' {} \;
find src -name "*.java" -exec sed -i 's/com\.ets2jsc\.util\./com.ets2jsc.shared.util./g' {} \;
find src -name "*.java" -exec sed -i 's/com\.ets2jsc\.events\./com.ets2jsc.shared.events./g' {} \;
```

**Verification**:
```bash
mvn clean compile
mvn test
mvn pmd:check
```

---

### Phase 2: Domain Layer Extraction (Week 2)

**Objective**: Extract domain models and service interfaces.

**Tasks**:
1. Move `ast/` → `domain/model/ast/`
2. Create `domain/model/compilation/`
3. Create `domain/model/config/`
4. Create domain service interfaces

**Files to Move**:
```bash
git mv src/main/java/com/ets2jsc/ast src/main/java/com/ets2jsc/domain/model/ast
```

**Files to Create**:

```java
// domain/service/ParserService.java
package com.ets2jsc.domain.service;

import com.ets2jsc.domain.model.source.SourceCode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.shared.exception.ParserException;

/**
 * Domain service for parsing source code into AST.
 */
public interface ParserService {
    /**
     * Parses source code and produces an AST.
     *
     * @param sourceCode the source code to parse
     * @return the parsed AST
     * @throws ParserException if parsing fails
     */
    SourceFile parse(SourceCode sourceCode) throws ParserException;

    /**
     * Checks if this parser can handle the given source code.
     *
     * @param sourceCode the source code to check
     * @return true if this parser can handle the source code
     */
    boolean canParse(SourceCode sourceCode);
}

// domain/service/TransformerService.java
package com.ets2jsc.domain.service;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CompilationException;

/**
 * Domain service for transforming AST nodes.
 */
public interface TransformerService {
    /**
     * Transforms a source file through all applicable transformers.
     *
     * @param sourceFile the source file to transform
     * @param config the compiler configuration
     * @return the transformed source file
     * @throws CompilationException if transformation fails
     */
    SourceFile transform(SourceFile sourceFile, CompilerConfig config)
            throws CompilationException;

    /**
     * Transforms a single AST node.
     *
     * @param node the node to transform
     * @param config the compiler configuration
     * @return the transformed node
     */
    AstNode transformNode(AstNode node, CompilerConfig config);
}

// domain/service/GeneratorService.java
package com.ets2jsc.domain.service;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.compilation.CompilationOutput;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CodeGenerationException;

/**
 * Domain service for generating JavaScript code from AST.
 */
public interface GeneratorService {
    /**
     * Generates JavaScript code from a source file AST.
     *
     * @param sourceFile the source file AST
     * @param config the compiler configuration
     * @return the generated compilation output
     * @throws CodeGenerationException if generation fails
     */
    CompilationOutput generate(SourceFile sourceFile, CompilerConfig config)
            throws CodeGenerationException;
}
```

**Verification**:
```bash
mvn clean compile
mvn test
mvn pmd:check
```

---

### Phase 3: Infrastructure Layer Migration (Week 3-4)

**Objective**: Move technical implementations to infrastructure layer.

**Tasks**:
1. Move `parser/` → `infrastructure/parser/`
2. Move `transformer/` → `infrastructure/transformer/`
3. Move `generator/` → `infrastructure/generator/`
4. Implement domain service interfaces

**Migration Script**:
```bash
git mv src/main/java/com/ets2jsc/parser src/main/java/com/ets2jsc/infrastructure/parser
git mv src/main/java/com/ets2jsc/transformer src/main/java/com/ets2jsc/infrastructure/transformer
git mv src/main/java/com/ets2jsc/generator src/main/java/com/ets2jsc/infrastructure/generator
```

**Files to Create**:

```java
// infrastructure/parser/TypeScriptParserService.java
package com.ets2jsc.infrastructure.parser;

import com.ets2jsc.domain.service.ParserService;
import com.ets2jsc.domain.model.source.SourceCode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.shared.exception.ParserException;
import com.ets2jsc.infrastructure.parser.TypeScriptScriptParser;
import com.ets2jsc.infrastructure.parser.AstBuilder;

/**
 * Infrastructure implementation of ParserService.
 */
public class TypeScriptParserService implements ParserService, AutoCloseable {

    private final TypeScriptScriptParser parser;

    public TypeScriptParserService() {
        this.parser = new TypeScriptScriptParser();
    }

    @Override
    public SourceFile parse(SourceCode sourceCode) throws ParserException {
        AstBuilder builder = new AstBuilder();
        return builder.build(sourceCode.getFileName(), sourceCode.getContent());
    }

    @Override
    public boolean canParse(SourceCode sourceCode) {
        String fileName = sourceCode.getFileName().toLowerCase();
        return fileName.endsWith(".ets")
                || fileName.endsWith(".ts")
                || fileName.endsWith(".tsx");
    }

    @Override
    public void close() {
        // Cleanup if needed
    }
}
```

**Verification**:
```bash
mvn clean compile
mvn test
mvn pmd:check
```

---

### Phase 4: Application Layer Creation (Week 5)

**Objective**: Create compilation pipeline orchestration.

**Key Classes to Create**:

```java
// application/compile/CompilationPipeline.java
package com.ets2jsc.application.compile;

import com.ets2jsc.domain.service.ParserService;
import com.ets2jsc.domain.service.TransformerService;
import com.ets2jsc.domain.service.GeneratorService;
import com.ets2jsc.domain.model.compilation.CompilationRequest;
import com.ets2jsc.domain.model.compilation.CompilationResult;
import com.ets2jsc.shared.exception.CompilationException;

/**
 * Compilation pipeline orchestrator.
 * Manages the flow: Parse → Transform → Generate
 */
public class CompilationPipeline implements AutoCloseable {

    private final ParserService parser;
    private final TransformerService transformer;
    private final GeneratorService generator;

    public CompilationPipeline(
            ParserService parser,
            TransformerService transformer,
            GeneratorService generator) {
        this.parser = parser;
        this.transformer = transformer;
        this.generator = generator;
    }

    public CompilationResult execute(CompilationRequest request) throws CompilationException {
        long startTime = System.currentTimeMillis();

        try {
            // Stage 1: Parse
            var sourceFile = parser.parse(request.getSourceCode());

            // Stage 2: Transform
            var transformed = transformer.transform(sourceFile, request.getConfig());

            // Stage 3: Generate
            var output = generator.generate(transformed, request.getConfig());

            long duration = System.currentTimeMillis() - startTime;
            return CompilationResult.success(request, output, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            return CompilationResult.failure(request, e, duration);
        }
    }

    @Override
    public void close() {
        // Cleanup resources
    }
}

// application/facade/CompilerFacade.java
package com.ets2jsc.application.facade;

import com.ets2jsc.application.compile.CompilationPipeline;
import com.ets2jsc.domain.model.compilation.CompilationRequest;
import com.ets2jsc.domain.model.compilation.CompilationResult;
import com.ets2jsc.shared.exception.CompilationException;

/**
 * Application facade for compilation operations.
 * Provides a simplified interface to the compilation system.
 */
public class CompilerFacade implements AutoCloseable {

    private final CompilationPipeline pipeline;

    public CompilerFacade(CompilationPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public CompilationResult compile(CompilationRequest request) throws CompilationException {
        return pipeline.execute(request);
    }

    @Override
    public void close() {
        pipeline.close();
    }
}
```

**Verification**:
```bash
mvn clean compile
mvn test
mvn pmd:check
```

---

### Phase 5: Interface Layer Refactoring (Week 6)

**Objective**: Update CLI to use the new architecture.

**Tasks**:
1. Move `cli/` → `interfaces/cli/`
2. Move `command/` → `interfaces/cli/command/`
3. Update `EtsCompilerLauncher` to use `CompilerFacade`
4. Remove old `api/`, `impl/`, `di/` packages

**Key Changes**:

```java
// interfaces/cli/EtsCompilerLauncher.java (Updated)
package com.ets2jsc.interfaces.cli;

import com.ets2jsc.application.facade.CompilerFacade;
import com.ets2jsc.application.compile.CompilationPipeline;
import com.ets2jsc.domain.service.ParserService;
import com.ets2jsc.domain.service.TransformerService;
import com.ets2jsc.domain.service.GeneratorService;
import com.ets2jsc.infrastructure.parser.TypeScriptParserService;
// ... other imports

public class EtsCompilerLauncher {
    public static int execute(String[] args) {
        // Create services
        ParserService parser = new TypeScriptParserService();
        TransformerService transformer = new /* infrastructure impl */;
        GeneratorService generator = new /* infrastructure impl */;

        // Create pipeline and facade
        CompilationPipeline pipeline = new CompilationPipeline(parser, transformer, generator);
        CompilerFacade facade = new CompilerFacade(pipeline);

        // Execute compilation
        try (facade) {
            CompilationRequest request = parseRequest(args);
            CompilationResult result = facade.compile(request);
            return result.isSuccess() ? 0 : 1;
        }
    }
}
```

**Cleanup**:
```bash
# Remove old packages
rm -rf src/main/java/com/ets2jsc/api
rm -rf src/main/java/com/ets2jsc/impl
rm -rf src/main/java/com/ets2jsc/di
rm -rf src/main/java/com/ets2jsc/compiler  # logic moved to application layer
```

**Verification**:
```bash
mvn clean compile
mvn test
mvn pmd:check
```

---

### Phase 6: Final Cleanup (Week 7)

**Objective**: Remove remnants and optimize.

**Tasks**:
1. Consolidate `context/` package into appropriate layers
2. Update all documentation
3. Run final quality checks
4. Performance optimization

**Verification**:
```bash
mvn clean compile
mvn test
mvn pmd:check
mvn spotbugs:check
```

---

## Dependency Rules Summary

| From Layer | Can Depend On |
|------------|---------------|
| interfaces | application, shared |
| application | domain, infrastructure, shared |
| domain | shared only |
| infrastructure | domain, shared |
| shared | none |

---

## Rollback Strategy

Each phase creates a git commit:
```bash
git commit -m "refactor: layered architecture - phase N - description"
```

If issues arise:
```bash
# Rollback specific phase
git revert HEAD~1

# Or rollback to before refactoring
git revert HEAD~N
```

---

## Success Criteria

- [ ] All 633 JUnit tests pass
- [ ] 0 PMD violations
- [ ] 0 SpotBugs violations
- [ ] Clean dependency graph (no circular dependencies)
- [ ] Documentation updated
- [ ] Build time < 2 minutes
- [ ] Test coverage ≥ 70%

---

## Migration Time Estimate

| Phase | Duration | Risk Level |
|-------|----------|------------|
| Phase 1 | 1 week | Low |
| Phase 2 | 1 week | Medium |
| Phase 3 | 2 weeks | High |
| Phase 4 | 1 week | Medium |
| Phase 5 | 1 week | Medium |
| Phase 6 | 1 week | Low |
| **Total** | **7 weeks** | - |

---

## References

- DDD (Domain-Driven Design) by Eric Evans
- Clean Architecture by Robert C. Martin
- Patterns of Enterprise Application Architecture by Martin Fowler
