# ETS to JS Compiler - Design Patterns Refactoring Plan

## Overview

This plan refactors the ETS to JS Compiler to apply SOLID principles and design patterns:
- **Dependency Inversion**: Lightweight DI container for testability
- **Strategy Pattern**: Replace conditional logic with strategy objects
- **Chain of Responsibility**: Proper transformer pipeline
- **Command Pattern**: Unify compilation actions
- **State Pattern**: Replace boolean flags with state objects
- **Builder Pattern**: Fluent API for configuration
- **Observer Pattern**: Event system for compilation

---

## Phase 1: Dependency Injection Infrastructure

### Objective
Create a lightweight DI container without external dependencies.

### New Files to Create

#### 1. ServiceLocator Interface
**File**: `src/main/java/com/ets2jsc/core/di/ServiceLocator.java`

```java
package com.ets2jsc.core.di;

/**
 * Service locator interface for dependency resolution.
 */
public interface ServiceLocator {
    <T> void register(Class<T> serviceType, ServiceFactory<T> factory);
    <T> void registerSingleton(Class<T> serviceType, T instance);
    <T> T resolve(Class<T> serviceType);
    boolean isRegistered(Class<?> serviceType);
}
```

#### 2. ServiceFactory Interface
**File**: `src/main/java/com/ets2jsc/core/di/ServiceFactory.java`

```java
package com.ets2jsc.core.di;

@FunctionalInterface
public interface ServiceFactory<T> {
    T create(ServiceLocator locator);
}
```

#### 3. DefaultServiceLocator Implementation
**File**: `src/main/java/com/ets2jsc/core/di/DefaultServiceLocator.java`

```java
package com.ets2jsc.core.di;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceLocator implements ServiceLocator {
    private final Map<Class<?>, ServiceFactory<?>> factories = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

    @Override
    public <T> void register(Class<T> serviceType, ServiceFactory<T> factory) {
        factories.put(serviceType, factory);
    }

    @Override
    public <T> void registerSingleton(Class<T> serviceType, T instance) {
        singletons.put(serviceType, instance);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> serviceType) {
        Object singleton = singletons.get(serviceType);
        if (singleton != null) return (T) singleton;

        ServiceFactory<?> factory = factories.get(serviceType);
        if (factory == null) {
            throw new ServiceResolutionException("Service not registered: " + serviceType.getName());
        }
        return (T) factory.create(this);
    }

    @Override
    public boolean isRegistered(Class<?> serviceType) {
        return singletons.containsKey(serviceType) || factories.containsKey(serviceType);
    }
}
```

#### 4. ServiceResolutionException
**File**: `src/main/java/com/ets2jsc/core/di/ServiceResolutionException.java`

```java
package com.ets2jsc.core.di;

import com.ets2jsc.exception.CompilationException;

public class ServiceResolutionException extends CompilationException {
    public ServiceResolutionException(String message) {
        super(message);
    }

    public ServiceResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Files to Modify

#### BaseCompiler.java
**Changes**:
- Line 28-32: Add `ServiceLocator locator` field
- Line 38-45: Modify constructor to accept `ServiceLocator`
- Line 41-42: Use `locator.resolve(CodeGenerator.class)`
- Line 182-184: Use transformer factory from locator

---

## Phase 2: Command Pattern for Compilation Actions

### New Files to Create

#### 1. CompilationCommand Interface
**File**: `src/main/java/com/ets2jsc/compiler/command/CompilationCommand.java`

```java
package com.ets2jsc.compiler.command;

import com.ets2jsc.compiler.CompilationResult;
import com.ets2jsc.exception.CompilationException;

public interface CompilationCommand {
    CompilationResult execute() throws CompilationException;
    String getName();
    boolean canExecute();
}
```

#### 2. SingleFileCompilationCommand
**File**: `src/main/java/com/ets2jsc/compiler/command/SingleFileCompilationCommand.java`

```java
package com.ets2jsc.compiler.command;

import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.compiler.CompilationResult;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.exception.CompilationException;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.transformer.AstTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

public class SingleFileCompilationCommand implements CompilationCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleFileCompilationCommand.class);

    private final Path sourcePath;
    private final Path outputPath;
    private final CompilerConfig config;
    private final List<AstTransformer> transformers;
    private final CodeGenerator codeGenerator;

    public SingleFileCompilationCommand(Path sourcePath, Path outputPath,
                                        CompilerConfig config,
                                        List<AstTransformer> transformers,
                                        CodeGenerator codeGenerator) {
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
        this.config = config;
        this.transformers = transformers;
        this.codeGenerator = codeGenerator;
    }

    @Override
    public CompilationResult execute() throws CompilationException {
        // Implementation
        return null;
    }

    @Override
    public String getName() {
        return "SingleFileCompilation";
    }

    @Override
    public boolean canExecute() {
        return sourcePath != null && outputPath != null;
    }
}
```

#### 3. BatchCompilationCommand
**File**: `src/main/java/com/ets2jsc/compiler/command/BatchCompilationCommand.java`

#### 4. ProjectCompilationCommand
**File**: `src/main/java/com/ets2jsc/compiler/command/ProjectCompilationCommand.java`

#### 5. CompilationCommandFactory
**File**: `src/main/java/com/ets2jsc/compiler/command/CompilationCommandFactory.java`

```java
package com.ets2jsc.compiler.command;

import java.nio.file.Path;
import java.util.List;

public interface CompilationCommandFactory {
    CompilationCommand createSingleFileCommand(Path sourcePath, Path outputPath);
    CompilationCommand createBatchCommand(List<Path> sourceFiles, Path outputDir);
    CompilationCommand createProjectCommand(Path sourceDir, Path outputDir, boolean copyResources);
}
```

### Files to Modify

#### ICompiler.java
Simplify interface to use command pattern.

---

## Phase 3: Chain of Responsibility for Transformers

### New Files to Create

#### 1. TransformationChain Interface
**File**: `src/main/java/com/ets2jsc/transformer/chain/TransformationChain.java`

```java
package com.ets2jsc.transformer.chain;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.exception.CompilationException;

public interface TransformationChain {
    AstNode process(AstNode node) throws CompilationException;
    void addHandler(TransformationHandler handler);
    int getHandlerCount();
    void clear();
}
```

#### 2. TransformationHandler Interface
**File**: `src/main/java/com/ets2jsc/transformer/chain/TransformationHandler.java`

```java
package com.ets2jsc.transformer.chain;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.exception.CompilationException;

public interface TransformationHandler {
    AstNode handle(AstNode node, TransformationChain chain) throws CompilationException;
    boolean canHandle(AstNode node);
    String getName();
}
```

#### 3. DefaultTransformationChain
**File**: `src/main/java/com/ets2jsc/transformer/chain/DefaultTransformationChain.java`

#### 4. TransformerAdapter
**File**: `src/main/java/com/ets2jsc/transformer/chain/TransformerAdapter.java`

---

## Phase 4: State Pattern for Configuration

### New Files to Create

#### 1. CompilationMode Interface
**File**: `src/main/java/com/ets2jsc/config/state/CompilationMode.java`

```java
package com.ets2jsc.config.state;

public interface CompilationMode {
    String getRenderMethodName();
    boolean isPartialUpdate();
    String getModeName();
}
```

#### 2. PartialUpdateMode
**File**: `src/main/java/com/ets2jsc/config/state/PartialUpdateMode.java`

#### 3. FullRenderMode
**File**: `src/main/java/com/ets2jsc/config/state/FullRenderMode.java`

---

## Phase 5: Builder Pattern for Configuration

### New Files to Create

#### CompilerConfigBuilder
**File**: `src/main/java/com/ets2jsc/config/CompilerConfigBuilder.java`

```java
package com.ets2jsc.config;

public class CompilerConfigBuilder {
    private String projectPath;
    private String buildPath;
    private boolean partialUpdateMode = true;
    private boolean generateSourceMap = true;

    public CompilerConfigBuilder projectPath(String path) {
        this.projectPath = path;
        return this;
    }

    public CompilerConfigBuilder buildPath(String path) {
        this.buildPath = path;
        return this;
    }

    public CompilerConfigBuilder partialUpdateMode(boolean enabled) {
        this.partialUpdateMode = enabled;
        return this;
    }

    public CompilerConfigBuilder sourceMap(boolean enabled) {
        this.generateSourceMap = enabled;
        return this;
    }

    public CompilerConfig build() {
        CompilerConfig config = new CompilerConfig();
        config.setProjectPath(projectPath);
        config.setBuildPath(buildPath);
        config.setPartialUpdateMode(partialUpdateMode);
        config.setGenerateSourceMap(generateSourceMap);
        return config;
    }
}
```

---

## Phase 6: Observer Pattern for Events

### New Files to Create

#### 1. CompilationListener Interface
**File**: `src/main/java/com/ets2jsc/core/events/CompilationListener.java`

```java
package com.ets2jsc.core.events;

import java.nio.file.Path;

public interface CompilationListener {
    void onCompilationStart(Path source);
    void onCompilationSuccess(Path source, Path output, long durationMs);
    void onCompilationFailure(Path source, String error);
    void onCompilationComplete(int fileCount, int successCount, int failureCount, long durationMs);
}
```

#### 2. CompilationEventDispatcher
**File**: `src/main/java/com/ets2jsc/core/events/CompilationEventDispatcher.java`

#### 3. LoggingCompilationListener
**File**: `src/main/java/com/ets2jsc/core/events/LoggingCompilationListener.java`

---

## Implementation Order

1. **Phase 1**: DI Infrastructure (Foundation)
2. **Phase 2**: Command Pattern
3. **Phase 3**: Chain of Responsibility
4. **Phase 4**: State Pattern
5. **Phase 5**: Builder Pattern
6. **Phase 6**: Observer Pattern

---

## Verification & Quality Assurance Measures

### 1. Continuous Testing Strategy

#### Before Each Phase
```bash
# Run full test suite and save baseline
mvn clean test > test-baseline-phase-N.txt
# Capture test coverage
mvn jacoco:report
# Save coverage report
cp target/site/jacoco/index.html coverage-phase-N.html
```

#### After Each Phase
```bash
# Run full test suite
mvn clean test
# Compare with baseline
# Run specific tests for modified components
mvn test -Dtest=*Compiler*,*Transformer*
# Run PMD check
mvn pmd:check
# Run SpotBugs
mvn spotbugs:check
# Build package
mvn package
```

### 2. Regression Test Suite

Create dedicated regression tests:
```bash
# Create integration test that runs before/after comparison
src/test/java/com/ets2jsc/regression/CompilationRegressionTest.java
```

### 3. Feature Flags for Gradual Rollout

Add feature toggle system:
```java
// New file: src/main/java/com/ets2jsc/config/FeatureFlags.java
public class FeatureFlags {
    public static final boolean USE_NEW_DI_SYSTEM = true;
    public static final boolean USE_COMMAND_PATTERN = true;
    public static final boolean USE_CHAIN_OF_RESPONSIBILITY = false; // Phase 3
    // ... other flags
}
```

### 4. Backward Compatibility Layer

Keep old code paths during transition:
```java
// In BaseCompiler.java
if (FeatureFlags.USE_NEW_DI_SYSTEM) {
    this.codeGenerator = locator.resolve(CodeGenerator.class);
} else {
    this.codeGenerator = new CodeGenerator(config); // Old way
}
```

### 5. Performance Benchmarks

Create benchmark tests:
```bash
# New file: src/test/java/com/ets2jsc/benchmark/CompilationBenchmarkTest.java
```

Run benchmarks before/after:
```bash
mvn test -Dtest=CompilationBenchmarkTest -Dbenchmark.output=benchmark-results.txt
```

### 6. Code Quality Gates

Each phase must pass:
- [ ] All unit tests pass (100%)
- [ ] Test coverage does not decrease
- [ ] PMD violations = 0
- [ ] SpotBugs critical issues = 0
- [ ] Build succeeds
- [ ] Performance within 10% of baseline

### 7. Rollback Plan

Each phase is independently revertable:
```bash
# If Phase N fails, revert:
git revert HEAD~1
# Restore feature flag to false
# Re-run tests to verify stability
```

### 8. Documentation Updates

After each phase:
- Update CODING_STANDARDS.md with new patterns
- Add examples for new interfaces
- Update architecture diagrams

---

## Risk Mitigation Matrix

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Breaking existing tests | Medium | High | Feature flags, backward compatibility layer |
| Performance regression | Low | Medium | Benchmarks before/after each phase |
| Increased complexity | Medium | Medium | Keep patterns simple, document thoroughly |
| Integration issues | Low | High | Incremental integration, smoke tests |
| Test coverage decrease | Low | Medium | JaCoCo monitoring after each change |

---

## Verification

After each phase, run:
```bash
mvn clean test
mvn pmd:check
mvn package
```

---

## Critical Files Summary

| File | Lines | Changes |
|------|-------|---------|
| BaseCompiler.java | 38-45, 182-185, 207-211 | DI injection, transformer chain |
| CompilerConfig.java | 24-35 | State pattern for flags |
| ICompiler.java | 30, 41, 53, 66 | Command pattern |
| DecoratorTransformer.java | 88-102 | Strategy pattern |
| CompilerFactory.java | All | DI integration |

---

## Total New Files: 25
## Total Modified Files: 7
