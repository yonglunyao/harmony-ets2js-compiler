# ETS to JS Compiler - Module Architecture

## Overview

This document defines the module architecture for the ETS to JS Compiler project. The architecture follows SOLID principles with clear module boundaries and interface-based communication between modules.

## Module Principles

1. **Interface Segregation**: Modules communicate through well-defined interfaces
2. **Dependency Inversion**: High-level modules depend on abstractions, not concrete implementations
3. **Single Responsibility**: Each module has a single, well-defined purpose
4. **Open/Closed**: Modules are open for extension but closed for modification

## Module Structure

```
com.ets2jsc/
├── ast/                        # AST Model Module
├── cli/                        # Command Line Interface Module
├── compiler/                   # Compiler Module
│   └── command/               # Command Pattern Implementation
├── config/                     # Configuration Module
│   └── state/                 # State Pattern Implementation
├── constant/                   # Constants Module
├── core/                       # Core Infrastructure Module
│   ├── context/               # Context Management
│   ├── di/                    # Dependency Injection
│   ├── events/                # Event System
│   └── factory/               # Factory Pattern
├── exception/                  # Exception Module
├── generator/                  # Code Generator Module
│   ├── context/               # Generation Context
│   ├── strategy/              # Generation Strategies
│   └── writer/                # Code Writers
├── parser/                     # Parser Module
│   └── internal/              # Internal Implementation
│       └── converters/        # Converter Strategies
├── transformer/                # Transformer Module
│   ├── chain/                 # Chain of Responsibility
│   └── decorators/            # Decorator Handling
└── util/                       # Utility Module
```

## Module Descriptions

### 1. AST Model Module (`ast/`)

**Purpose**: Defines the Abstract Syntax Tree data model.

**Responsibilities**:
- AST node definitions
- AST visitor interfaces
- AST data structures

**Interfaces**:
- `AstNode` - Base interface for all AST nodes
- `AstVisitor<T>` - Visitor pattern interface

**Dependencies**: None (foundational module)

**Accessed By**: parser, transformer, generator

---

### 2. Command Line Interface Module (`cli/`)

**Purpose**: Provides command-line interface for the compiler.

**Responsibilities**:
- Command argument parsing
- User interaction
- Entry point for CLI execution

**Interfaces**:
- `CliLauncher` - Main entry point

**Dependencies**: compiler, config

---

### 3. Compiler Module (`compiler/`)

**Purpose**: Orchestrates the compilation pipeline.

**Responsibilities**:
- Compilation coordination
- Pipeline management
- Result aggregation

**Interfaces**:
- `ICompiler` - Compiler interface
- `CompilationCommand` - Command pattern interface

**Dependencies**: ast, config, core, generator, parser, transformer

---

### 4. Configuration Module (`config/`)

**Purpose**: Manages compiler configuration.

**Responsibilities**:
- Configuration storage
- Configuration building
- State management

**Interfaces**:
- `CompilerConfig` - Configuration data class
- `CompilerConfigBuilder` - Builder pattern interface
- `CompilationMode` - State pattern interface
- `OutputConfiguration` - Output settings state

**Dependencies**: core (for state pattern)

---

### 5. Constants Module (`constant/`)

**Purpose**: Defines all project constants.

**Responsibilities**:
- Constant definitions
- Symbol definitions
- Runtime function names

**Interfaces**:
- `Symbols` - Symbolic constants
- `RuntimeFunctions` - Runtime function names
- `Components` - Component constants
- `Decorators` - Decorator names

**Dependencies**: None (foundational module)

---

### 6. Core Infrastructure Module (`core/`)

**Purpose**: Provides foundational infrastructure for all modules.

**Responsibilities**:
- Dependency injection
- Event system
- Factory management
- Context management

**Interfaces**:
- `ServiceLocator<T>` - DI interface
- `ServiceFactory<T>` - Factory interface
- `CompilationListener` - Event listener interface
- `CompilationEventDispatcher` - Event dispatcher interface

**Dependencies**: exception

---

### 7. Exception Module (`exception/`)

**Purpose**: Defines all custom exceptions.

**Responsibilities**:
- Exception hierarchy
- Error types
- Exception utilities

**Interfaces**:
- `ParserException` - Base parser exception
- `CompilationException` - Compilation exception
- `CodeGenerationException` - Code generation exception
- `ServiceResolutionException` - DI exception

**Dependencies**: None (foundational module)

---

### 8. Code Generator Module (`generator/`)

**Purpose**: Generates JavaScript code from AST.

**Responsibilities**:
- Code generation strategies
- Output formatting
- Code writing

**Interfaces**:
- `CodeGenerationStrategy` - Strategy interface for code generation
- `CodeGenerationStrategyRegistry` - Registry for strategies
- `GenerationContext` - Context for generation operations

**Dependencies**: ast, config, constant, core

---

### 9. Parser Module (`parser/`)

**Purpose**: Parses TypeScript/ETS source code into AST.

**Responsibilities**:
- Source code parsing
- AST construction
- Node conversion

**Interfaces**:
- `AstBuilder` - Builder interface for AST construction
- `TypeScriptScriptParser` - Parser implementation

**Dependencies**: ast, core, exception

**Internal**:
- `internal/converters/` - Converter strategies (internal implementation)

---

### 10. Transformer Module (`transformer/`)

**Purpose**: Transforms AST nodes.

**Responsibilities**:
- AST transformation
- Decorator processing
- Component transformation

**Interfaces**:
- `AstTransformer` - Transformer interface
- `TransformationHandler` - Chain of Responsibility handler
- `TransformationChain` - Chain interface

**Dependencies**: ast, config, constant, core

**Internal**:
- `chain/` - Chain implementation
- `decorators/` - Decorator transformers (internal implementation)

---

### 11. Utility Module (`util/`)

**Purpose**: Provides utility functions.

**Responsibilities**:
- File operations
- String utilities
- Common helpers

**Dependencies**: None (utility module)

---

## Inter-Module Communication

### Interface-Based Communication Rules

1. **Module Access**: Modules should access other modules only through their public interfaces
2. **No Direct Dependencies**: Modules should not depend on concrete implementations of other modules
3. **Factory Pattern**: Use factories to obtain instances of interfaces
4. **DI Pattern**: Use dependency injection to manage dependencies

### Communication Patterns

```
┌─────────────┐
│     CLI     │
└──────┬──────┘
       │ uses
       ▼
┌─────────────┐
│  Compiler   │◄─────────────┐
└──────┬──────┘              │
       │                     │
       │ orchestrates        │
       ▼                     │
┌────────────────────────────┴──────┐
│         Core Infrastructure         │
│  (DI, Events, Factories, Context)  │
└────────────────────────────────────┘
       ▲        ▲        ▲        ▲
       │        │        │        │
       │ uses   │ uses   │ uses   │ uses
       │        │        │        │
┌──────┴──┐ ┌──┴─────┐ ┌┴──────┐ ┌┴──────┐
│ Parser  │ │Generator│ │Transformer│Config│
└─────┬───┘ └───┬────┘ └───────┘ └───────┘
      │          │
      │ produces │ consumes
      ▼          ▼
   ┌─────────────────┐
   │  AST Model      │
   └─────────────────┘
```

## Module Dependencies

### Dependency Matrix

| Module | AST | CLI | Compiler | Config | Constant | Core | Exception | Generator | Parser | Transformer | Util |
|--------|-----|-----|----------|--------|----------|------|-----------|-----------|--------|-------------|------|
| AST | - | | | | | | | | | | |
| CLI | | - | ✓ | ✓ | | ✓ | | | | | |
| Compiler | ✓ | | - | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Config | | | | - | | | | | | | |
| Constant | | | | | - | | | | | | |
| Core | | | | ✓ | | - | ✓ | | | | |
| Exception | | | | | | | - | | | | |
| Generator | ✓ | | | ✓ | ✓ | ✓ | | - | | | |
| Parser | ✓ | | | | | ✓ | ✓ | | - | | |
| Transformer | ✓ | | | ✓ | ✓ | ✓ | | | | - | |
| Util | | | | | | | | | | | - |

### Dependency Rules

1. **AST** - Foundational module, no dependencies
2. **Constant** - Foundational module, no dependencies
3. **Exception** - Foundational module, no dependencies
4. **Util** - Utility module, minimal dependencies
5. **Core** - Infrastructure module, depends only on Exception
6. **Config** - Configuration module, depends on Core (state pattern)
7. **Parser** - Parser module, depends on AST, Core, Exception
8. **Transformer** - Transformer module, depends on AST, Config, Constant, Core
9. **Generator** - Generator module, depends on AST, Config, Constant, Core
10. **Compiler** - Compiler module, orchestrates all other modules
11. **CLI** - CLI module, depends on Compiler, Config, Core

## Module Extension Points

### Adding New Transformers

1. Implement `AstTransformer` interface
2. Register with `TransformerFactory`
3. Add to transformation chain

### Adding New Code Generation Strategies

1. Implement `CodeGenerationStrategy` interface
2. Register with `CodeGenerationStrategyRegistry`
3. Strategy will be automatically selected based on node type

### Adding New Event Listeners

1. Implement `CompilationListener` interface
2. Register with `CompilationEventDispatcher`
3. Listener will receive all compilation events

### Adding New Services

1. Create service interface
2. Implement service
3. Create `ServiceFactory` for service
4. Register with `ServiceLocator`

## Best Practices

1. **Use Interfaces**: Always program to interfaces, not implementations
2. **Factory Pattern**: Use factories to obtain instances
3. **DI Pattern**: Use dependency injection for dependencies
4. **Strategy Pattern**: Use strategies for algorithms that may vary
5. **Chain of Responsibility**: Use chains for processing pipelines
6. **State Pattern**: Use states for mode-dependent behavior
7. **Builder Pattern**: Use builders for complex object construction
8. **Observer Pattern**: Use observers for event notification

## Module Testing

### Unit Testing

- Test each module in isolation
- Mock dependencies using interfaces
- Test all public interface methods

### Integration Testing

- Test module interactions through interfaces
- Test pipelines end-to-end
- Verify event propagation

### Module Coverage Requirements

- Core modules: ≥ 90% coverage
- Compiler module: ≥ 80% coverage
- Generator module: ≥ 80% coverage
- Parser module: ≥ 75% coverage
- Transformer module: ≥ 80% coverage
- Utility modules: ≥ 70% coverage

## Module Maintenance

### Adding New Features

1. Identify which module the feature belongs to
2. Define or update interfaces
3. Implement the feature
4. Update factory registrations if needed
5. Add unit tests
6. Update documentation

### Refactoring Modules

1. Ensure interfaces remain stable
2. Update implementations
3. Maintain backward compatibility
4. Run full test suite
5. Update documentation

---

## Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-02-02 | Architecture Team | Initial module architecture definition |
