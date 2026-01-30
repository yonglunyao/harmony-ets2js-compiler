# ETS to JS Compiler

ArkTS/ETS to JavaScript compiler for HarmonyOS. This compiler converts ArkTS (Extended TypeScript) code into standard JavaScript that can be executed by HarmonyOS runtime.

## Project Overview

This is a compiler that translates ArkTS/ETS source code into JavaScript, enabling developers to use modern declarative UI syntax and decorators while maintaining compatibility with HarmonyOS runtime environments.

### Key Features

- **Decorator Support**
  - `@Component` - Converts struct to class View
  - `@State` - State management with ObservedPropertySimple
  - `@Prop` - Property decorator for component props
  - `@Link` - Two-way data binding
  - `@Provide/@Consume` - State dependency injection
  - `@Builder` - Builder function transformation

- **UI Components**
  - ForEach - List rendering
  - If - Conditional rendering
  - Declarative UI to create/pop pattern transformation

- **Expression Support**
  - Object literals
  - Array literals
  - Arrow functions
  - Template literals
  - Resource references (`$r()`, `$rawfile()`)

## Requirements

- Java 17
- Maven 3.8.3+
- GraalVM JavaScript Engine (included as dependency)

## Installation

```bash
# Clone the repository
git clone https://github.com/yonglunyao/harmony-ets2js-compiler.git
cd harmony-ets2js-compiler

# Build the project
mvn clean package
```

## Usage

### Command Line

```bash
# Compile a single ETS file
java -jar target/ets2jsc-1.0-SNAPSHOT.jar <input.ets> <output.js>

# Or using Maven exec
mvn exec:java -Dexec.mainClass="com.ets2jsc.EtsCompiler" -Dexec.args="<input.ets> <output.js>"
```

### Maven Usage

```bash
# Compile
mvn compile

# Run tests
mvn test

# Run specific test
mvn test -Dtest=SimpleComponentTest

# Build JAR
mvn clean package
```

## Project Structure

```
src/main/java/com/ets2jsc/
├── EtsCompiler.java          # Main compiler entry point
├── ast/                       # AST node definitions
│   ├── AstNode.java
│   ├── SourceFile.java
│   ├── ClassDeclaration.java
│   └── ...
├── parser/                    # Parser implementation
│   └── TypeScriptScriptParser.java
├── transformer/               # AST transformers
│   ├── DecoratorTransformer.java
│   ├── ComponentTransformer.java
│   └── BuildMethodTransformer.java
├── generator/                 # Code generator
│   └── CodeGenerator.java
├── config/                    # Configuration
│   └── CompilerConfig.java
└── constant/                  # Constants
    ├── Decorators.java
    └── Components.java
```

## Documentation

Detailed documentation is available in the [docs](docs) directory:

- [01-技术文档.md](docs/01-技术文档.md) - Technical documentation
- [迭代2-技术实现方案-part1.md](docs/迭代2-技术实现方案-part1.md) - Iteration 2 technical design (Part 1)
- [迭代2-技术实现方案-part2.md](docs/迭代2-技术实现方案-part2.md) - Iteration 2 technical design (Part 2)
- [迭代3-需求说明书.md](docs/迭代3-需求说明书.md) - Iteration 3 requirements
- [迭代3-技术实现方案.md](docs/迭代3-技术实现方案.md) - Iteration 3 technical design
- [迭代4-技术实现方案.md](docs/迭代4-技术实现方案.md) - Iteration 4 technical design

## Development

### Adding New Features

1. Define AST nodes in `ast/` package
2. Implement parsing logic in `parser/`
3. Create transformer in `transformer/`
4. Generate code in `generator/`
5. Add tests in `src/test/java/`

### Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SimpleComponentTest

# Run with verbose output
mvn test -X
```

## License

This project is licensed under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Authors

- yonglunyao - Initial work

## Acknowledgments

- GraalVM for the JavaScript engine
- TypeScript for the parser foundation
- HarmonyOS team for the platform
