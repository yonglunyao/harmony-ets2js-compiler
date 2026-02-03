# ETS to JS Compiler

A compiler that translates HarmonyOS ArkTS/ETS source code to JavaScript. The compiler converts ArkTS (Extended TypeScript) code to standard JavaScript that can be executed by the HarmonyOS runtime.

English | [简体中文](README.md)

## Overview

This compiler translates ArkTS/ETS source code to JavaScript, enabling developers to use modern declarative UI syntax and decorators while maintaining compatibility with the HarmonyOS runtime environment.

## Features

### Decorator Support

- `@Component` - Converts struct to class View
- `@State` - State management using ObservedPropertySimple
- `@Prop` - Component property decorator
- `@Link` - Two-way data binding
- `@Provide/@Consume` - State dependency injection
- `@Builder` - Builder function transformation

### UI Components

- ForEach - List rendering
- If - Conditional rendering
- Declarative UI to create/pop pattern conversion

### Expression Support

- Object literals
- Array literals
- Arrow functions
- Template strings
- Resource references (`$r()`, `$rawfile()`)

### Advanced Features

- Parallel compilation support
- Pure JavaScript mode (without ArkUI runtime dependencies)
- SourceMap generation
- Project-level compilation with resource copying

## Technology Stack

- **Java 17** - Primary development language
- **Maven 3.8.3+** - Build tool and dependency management
- **GraalVM JavaScript Engine** - Embedded JavaScript execution engine
- **TypeScript 5.9.3** - AST parsing

## Requirements

- Java 17
- Maven 3.8.3+
- Node.js 14+ (for TypeScript parser)

## Installation

```bash
# Clone the repository
git clone https://github.com/yonglunyao/harmony-ets2js-compiler.git
cd harmony-ets2js-compiler

# Build the project
mvn clean package
```

## Usage

### CLI Usage

```bash
# Compile a single file
java -jar target/ets2jsc-1.0-SNAPSHOT.jar <input.ets> <output.js>

# Compile a project directory
java -jar target/ets2jsc-1.0-SNAPSHOT.jar --project <source-dir> <output-dir>

# Compile with resources
java -jar target/ets2jsc-1.0-SNAPSHOT.jar --project --resources <source-dir> <output-dir>
```

### Java API

#### Basic Usage

```java
import com.ets2jsc.interfaces.publicapi.EtsCompiler;
import com.ets2jsc.interfaces.publicapi.model.PublicCompilationResult;
import java.nio.file.Paths;

// Create compiler with default configuration
try (EtsCompiler compiler = EtsCompiler.create()) {
    // Compile a single file
    PublicCompilationResult result = compiler.compileFile(
        Paths.get("src/main/ets/MyComponent.ets"),
        Paths.get("output/MyComponent.js")
    );

    if (result.isSuccess()) {
        System.out.println("Compilation successful");
    }
}
```

#### Using Builder Pattern

```java
import com.ets2jsc.interfaces.publicapi.EtsCompiler;
import java.nio.file.Paths;

// Create compiler with custom configuration
try (EtsCompiler compiler = EtsCompiler.builder()
        .projectPath(Paths.get("/my/project"))
        .sourcePath("src/main/ets")
        .buildPath("build")
        .parallelMode(true)
        .threadCount(4)
        .generateSourceMap(true)
        .build()) {

    // Compile a project
    PublicCompilationResult result = compiler.compileProject(
        Paths.get("src"),
        Paths.get("output"),
        true  // copy resources
    );
}
```

#### Batch Compilation

```java
import com.ets2jsc.interfaces.publicapi.EtsCompiler;
import java.nio.file.Paths;
import java.util.List;

try (EtsCompiler compiler = EtsCompiler.builder()
        .parallelMode(true)
        .threadCount(4)
        .build()) {

    List<Path> sourceFiles = List.of(
        Paths.get("src/Component1.ets"),
        Paths.get("src/Component2.ets")
    );

    // Compile batch while preserving directory structure
    PublicCompilationResult result = compiler.compileBatchWithStructure(
        sourceFiles,
        Paths.get("src"),
        Paths.get("output")
    );
}
```

### Configuration Options

```java
import com.ets2jsc.domain.model.config.CompilerConfig;

CompilerConfig config = CompilerConfig.createDefault();

// Compilation mode
config.setCompileMode(CompilerConfig.CompileMode.MODULE_JSON);

// Output options
config.setGenerateSourceMap(true);
config.setMinifyOutput(false);

// Feature flags
config.setPureJavaScript(true);  // Generate pure JS without ArkUI runtime
config.setValidateApi(true);
config.setProcessTs(true);
config.setEnableLazyImport(false);

// Paths
config.setProjectPath("/path/to/project");
config.setSourcePath("src/main/ets");
config.setBuildPath("build");
```

## Architecture

The project follows **Domain-Driven Design (DDD)** layered architecture:

```
src/main/java/com/ets2jsc/
├── interfaces/                    # Interface Layer - API and CLI
│   ├── publicapi/                # Public API for external usage
│   │   ├── EtsCompiler.java      # Main compiler facade
│   │   ├── EtsCompilerBuilder.java
│   │   └── model/                # Public API models
│   └── cli/                      # Command-line interface
│       └── command/
│
├── application/                  # Application Layer - Use cases
│   ├── compile/                  # Compilation orchestration
│   │   ├── CompilationPipeline.java
│   │   ├── CompilationPipelineFactory.java
│   │   ├── BatchCompilationService.java
│   │   ├── BatchCompilationServiceFactory.java
│   │   ├── SequentialBatchCompilationService.java
│   │   └── ParallelBatchCompilationService.java
│   └── di/                       # Dependency injection
│
├── domain/                       # Domain Layer - Core business logic
│   ├── model/                    # Domain models
│   │   ├── ast/                  # AST node definitions
│   │   │   ├── AstNode.java
│   │   │   ├── AstVisitor.java
│   │   │   ├── BuiltInComponents.java
│   │   │   ├── ComponentRegistry.java
│   │   │   └── ...
│   │   ├── config/               # Configuration models
│   │   │   ├── CompilerConfig.java
│   │   │   ├── CompilationMode.java
│   │   │   └── OutputConfiguration.java
│   │   ├── compilation/          # Compilation models
│   │   │   ├── CompilationRequest.java
│   │   │   ├── CompilationResult.java
│   │   │   └── FileResult.java
│   │   └── source/               # Source file models
│   └── service/                  # Domain services
│       └── ParserService.java
│
├── infrastructure/               # Infrastructure Layer - Technical details
│   ├── parser/                   # TypeScript parser implementation
│   │   ├── AstBuilder.java
│   │   ├── ConversionContext.java
│   │   ├── NodeConverterRegistry.java
│   │   ├── converters/
│   │   │   ├── expr/             # Expression converters (40+)
│   │   │   └── stmt/             # Statement converters (40+)
│   │   └── internal/
│   ├── transformer/              # AST transformation
│   │   ├── AstTransformer.java
│   │   ├── BuildMethodTransformer.java
│   │   ├── chain/                # Transformation chain
│   │   └── decorators/           # Decorator transformers
│   │       └── impl/
│   ├── generator/                # Code generation
│   │   ├── ComponentCodeGenerator.java
│   │   ├── PropertyGenerator.java
│   │   ├── BlockGenerator.java
│   │   ├── JsWriter.java
│   │   ├── SourceMapGenerator.java
│   │   ├── strategy/            # Generation strategies
│   │   ├── writer/              # Code writers
│   │   ├── context/             # Generation context
│   │   └── internal/
│   └── factory/                 # Factory implementations
│       ├── GeneratorFactory.java
│       └── TransformerFactory.java
│
└── shared/                      # Shared Kernel - Cross-cutting concerns
    ├── constant/                # Constants (Symbols, RuntimeFunctions, etc.)
    ├── exception/               # Custom exceptions
    ├── events/                  # Domain events
    └── util/                    # Utilities
        ├── ResourceFileCopier.java
        ├── SourceFileFinder.java
        └── ...
```

### Layer Responsibilities

| Layer | Responsibility |
|-------|---------------|
| **interfaces** | Public API, CLI, external integration points |
| **application** | Use case orchestration, workflow coordination |
| **domain** | Core business logic, domain models, domain services |
| **infrastructure** | Technical implementations (parser, transformer, generator) |
| **shared** | Cross-cutting utilities, constants, exceptions |

## TypeScript Parser Architecture

The TypeScript parser uses an **npm SDK style modular architecture**:

```
src/main/resources/typescript-parser/
├── index.js                          # CLI + library unified entry
└── src/javascript/
    ├── ast/                          # AST processing module
    │   ├── preprocessor.js           # ETS preprocessing
    │   ├── converter.js              # Main AST converter
    │   └── converters/               # Node type converters
    │       ├── literals.js           # Literal nodes
    │       ├── statements.js         # Statement nodes
    │       ├── declarations.js       # Declaration nodes
    │       └── expressions.js        # Expression nodes
    ├── codegen/                      # Code generation module
    │   └── index.js                  # JSON → code
    └── common/                       # Common utilities
        ├── constants.js              # Constants
        └── utils.js                  # Utility functions
```

### Architecture Principles

1. **Layered by functional domain** - `ast/`, `codegen/`, `common/`
2. **Language layering** - `src/javascript/` supports multi-language extensions
3. **Single responsibility** - Each module has a single, clear purpose
4. **Parameter passing pattern** - Avoid circular dependencies
5. **Zero magic numbers** - All constants extracted to `common/constants.js`

## Maven Commands

```bash
# Compile
mvn compile

# Run tests
mvn test

# Run specific test
mvn test -Dtest=SimpleComponentTest

# Run tests with verbose output
mvn test -X

# Clean build artifacts
mvn clean

# Full build
mvn clean package
```

## Documentation

Detailed documentation is available in the [docs](docs) directory:

- [01-Technical-Documentation.md](docs/01-技术文档.md) - Technical documentation
- [Iteration2-Implementation-Plan-part1.md](docs/迭代2-技术实现方案-part1.md) - Iteration 2 design (part 1)
- [Iteration2-Implementation-Plan-part2.md](docs/迭代2-技术实现方案-part2.md) - Iteration 2 design (part 2)
- [Iteration3-Requirements.md](docs/迭代3-需求说明书.md) - Iteration 3 requirements
- [Iteration3-Implementation-Plan.md](docs/迭代3-技术实现方案.md) - Iteration 3 design
- [Iteration4-Implementation-Plan.md](docs/迭代4-技术实现方案.md) - Iteration 4 design
- [CODING_STANDARDS.md](docs/CODING_STANDARDS.md) - Coding standards and guidelines

## Development Guide

### Adding New Features

1. Define AST nodes in `domain/model/ast/`
2. Implement parser logic in `infrastructure/parser/converters/`
3. Create transformer in `infrastructure/transformer/`
4. Generate code in `infrastructure/generator/`
5. Add tests in `src/test/java/com/ets2jsc/`

### Extending TypeScript Parser

1. Add node converters in `src/javascript/ast/converters/`
2. Add code generators in `src/javascript/codegen/index.js`
3. Add constants in `src/javascript/common/constants.js`
4. Add utilities in `src/javascript/common/utils.js`

## Testing

### Test Organization

| Target Package | Test Package Location |
|----------------|----------------------|
| `domain/model/ast` | `src/test/java/com/ets2jsc/domain/model/ast/` |
| `domain/model/config` | `src/test/java/com/ets2jsc/domain/model/config/` |
| `infrastructure/generator` | `src/test/java/com/ets2jsc/infrastructure/generator/` |
| `infrastructure/transformer` | `src/test/java/com/ets2jsc/infrastructure/transformer/` |
| Integration tests | `src/test/java/com/ets2jsc/integration/` |

## Contributing

Contributions are welcome! Please submit Pull Requests.

### Code Standards

Before submitting code, ensure:

- Follow [CODING_STANDARDS.md](docs/CODING_STANDARDS.md)
- No magic numbers (all constants extracted)
- Avoid returning null (prefer exceptions or Optional)
- Use custom exception classes
- Use SLF4J for logging
- Control cyclomatic complexity ≤ 10
- All comments and naming in English

## License

This project is licensed under the MIT License.

## Author

- yonglunyao - Initial work

## Acknowledgments

- GraalVM - JavaScript engine
- TypeScript - Parser foundation
- HarmonyOS community - Platform support
