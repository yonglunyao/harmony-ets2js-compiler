# ETS to JS Compiler - Public API

This document describes the public API for the ETS to JS Compiler. External applications can depend on this library and use the public API to compile ETS/ArkTS source code to JavaScript.

## Maven Dependency

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.ets2jsc</groupId>
    <artifactId>ets2jsc</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### Compile a Single File

```java
import com.ets2jsc.interfaces.publicapi.EtsCompiler;
import com.ets2jsc.interfaces.publicapi.model.PublicCompilationResult;

import java.nio.file.Path;

// Create a compiler with default settings
try (EtsCompiler compiler = EtsCompiler.create()) {
    PublicCompilationResult result = compiler.compileFile(
        Path.of("src/Main.ets"),
        Path.of("build/Main.js")
    );

    if (result.isSuccess()) {
        System.out.println("Compilation succeeded!");
    }
}
```

### Compile a Project

```java
import com.ets2jsc.interfaces.publicapi.EtsCompiler;
import com.ets2jsc.interfaces.publicapi.EtsCompilerBuilder;
import com.ets2jsc.interfaces.publicapi.model.PublicCompilationResult;

import java.nio.file.Path;

// Create a compiler with custom settings
try (EtsCompiler compiler = EtsCompiler.builder()
        .parallelMode(true)      // Enable parallel compilation
        .threadCount(4)          // Use 4 threads
        .sourcePath("src/ets")   // Source directory
        .buildPath("build")      // Output directory
        .generateSourceMap(true) // Generate source maps
        .build()) {

    PublicCompilationResult result = compiler.compileProject(
        Path.of("src/ets"),
        Path.of("build"),
        false  // Don't copy resource files
    );

    System.out.println(result.getSummary());
}
```

### Batch Compilation

```java
import com.ets2jsc.interfaces.publicapi.EtsCompiler;
import com.ets2jsc.interfaces.publicapi.model.PublicCompilationResult;

import java.nio.file.Path;
import java.util.List;

try (EtsCompiler compiler = EtsCompiler.builder()
        .parallelMode(true)
        .threadCount(8)
        .build()) {

    List<Path> sourceFiles = List.of(
        Path.of("src/App.ets"),
        Path.of("src/pages/Index.ets"),
        Path.of("src/components/Button.ets")
    );

    PublicCompilationResult result = compiler.compileBatch(
        sourceFiles,
        Path.of("build")
    );

    // Handle results
    for (var fileResult : result.getFileResults()) {
        if (fileResult.isFailure()) {
            System.err.println("Failed: " + fileResult.getSourcePath());
            System.err.println("Error: " + fileResult.getMessage());
        }
    }
}
```

## API Reference

### EtsCompiler

Main entry point for compilation operations.

| Method | Description |
|--------|-------------|
| `static EtsCompiler create()` | Create a compiler with default settings |
| `static EtsCompilerBuilder builder()` | Create a builder for custom configuration |
| `PublicCompilationResult compileFile(Path source, Path output)` | Compile a single file |
| `PublicCompilationResult compileBatch(List<Path> files, Path outputDir)` | Compile multiple files to a directory |
| `PublicCompilationResult compileProject(Path sourceDir, Path outputDir, boolean copyResources)` | Compile a project directory |
| `void close()` | Close the compiler and release resources |

### EtsCompilerBuilder

Builder for creating configured compiler instances.

| Method | Description |
|--------|-------------|
| `projectPath(Path)` | Set the project root directory |
| `buildPath(String)` | Set the build output path |
| `sourcePath(String)` | Set the source directory |
| `parallelMode(boolean)` | Enable/disable parallel compilation |
| `threadCount(int)` | Set the number of threads for parallel compilation |
| `generateSourceMap(boolean)` | Enable/disable source map generation |
| `partialUpdateMode(boolean)` | Enable/disable partial update mode |
| `minifyOutput(boolean)` | Enable/disable output minification |
| `EtsCompiler build()` | Build the compiler instance |

### PublicCompilationResult

Result of a compilation operation.

| Method | Description |
|--------|-------------|
| `boolean isSuccess()` | Check if compilation was successful |
| `int getTotalCount()` | Get total number of files processed |
| `int getSuccessCount()` | Get number of successful compilations |
| `int getFailureCount()` | Get number of failed compilations |
| `List<FileResult> getFileResults()` | Get all file results |
| `List<FileResult> getFailures()` | Get only failed compilations |
| `String getSummary()` | Get a human-readable summary |

### FileResult

Result of compiling a single file.

| Method | Description |
|--------|-------------|
| `Path getSourcePath()` | Get the source file path |
| `Path getOutputPath()` | Get the output file path |
| `Status getStatus()` | Get the compilation status |
| `String getMessage()` | Get the result message |
| `Throwable getError()` | Get the error (if failed) |
| `boolean isSuccess()` | Check if this file compiled successfully |

## Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `projectPath` | Path | Current directory | Project root directory |
| `buildPath` | String | "build" | Build output directory |
| `sourcePath` | String | "src/main/ets" | Source directory |
| `parallelMode` | boolean | false | Enable parallel compilation |
| `threadCount` | int | 1 | Number of threads (parallel mode) |
| `generateSourceMap` | boolean | true | Generate source maps |
| `partialUpdateMode` | boolean | true | Enable partial update mode |
| `minifyOutput` | boolean | false | Minify output JavaScript |
| `processTypeScript` | boolean | true | Process TypeScript files |
| `validateApi` | boolean | true | Validate API usage |

## Error Handling

```java
try (EtsCompiler compiler = EtsCompiler.create()) {
    PublicCompilationResult result = compiler.compileFile(
        Path.of("src/Test.ets"),
        Path.of("build/Test.js")
    );

    if (!result.isSuccess()) {
        for (var failure : result.getFailures()) {
            System.err.println("Failed: " + failure.getSourcePath());
            System.err.println("Message: " + failure.getMessage());
            if (failure.getError() != null) {
                failure.getError().printStackTrace();
            }
        }
    }
} catch (CompilationException e) {
    System.err.println("Compilation error: " + e.getMessage());
}
```

## Thread Safety

The `EtsCompiler` instance is **not thread-safe**. Create separate instances for concurrent use:

```java
// Correct: One compiler per thread
ExecutorService executor = Executors.newFixedThreadPool(4);
for (Path file : files) {
    executor.submit(() -> {
        try (EtsCompiler compiler = EtsCompiler.create()) {
            compiler.compileFile(file, getOutputPath(file));
        }
    });
}
```

## Resource Management

The `EtsCompiler` implements `AutoCloseable` and should be closed after use:

```java
// Prefer try-with-resources
try (EtsCompiler compiler = EtsCompiler.create()) {
    // Use compiler
} // Automatically closed

// Or manually close
EtsCompiler compiler = EtsCompiler.create();
try {
    // Use compiler
} finally {
    compiler.close();
}
```

## Support

For issues and questions, please refer to the main project documentation.
