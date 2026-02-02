package com.ets2jsc.compiler;

import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.exception.CompilationException;

import java.nio.file.Path;
import java.util.List;

/**
 * Sequential compiler implementation.
 * Compiles source files one by one in sequence.
 */
public class SequentialCompiler extends BaseCompiler {

    /**
     * Creates a new sequential compiler with the given configuration.
     *
     * @param config the compiler configuration
     */
    public SequentialCompiler(CompilerConfig config) {
        super(config);
    }

    @Override
    public CompilationResult compileBatch(List<Path> sourceFiles, Path outputDir) throws CompilationException {
        CompilationResult result = new CompilationResult();
        long startTime = System.currentTimeMillis();

        try {
            // Create output directory if it doesn't exist
            java.nio.file.Files.createDirectories(outputDir);
        } catch (java.io.IOException e) {
            throw new CompilationException("Failed to create output directory: " + outputDir, e);
        }

        for (Path sourceFile : sourceFiles) {
            String fileName = sourceFile.getFileName().toString();
            String outputName = fileName.replace(".ets", ".js").replace(".ts", ".js");
            Path outputPath = outputDir.resolve(outputName);

            try {
                compile(sourceFile, outputPath);
                result.addFileResult(sourceFile, CompilationResult.FileResult.success(
                    sourceFile, outputPath, System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                result.addFileResult(sourceFile, CompilationResult.FileResult.failure(
                    sourceFile, outputPath, "Compilation failed: " + e.getMessage(), e, 0));
            }
        }

        result.markCompleted();
        return result;
    }

    @Override
    public CompilationMode getMode() {
        return CompilationMode.SEQUENTIAL;
    }

    @Override
    public void close() {
        // No resources to release for sequential compiler
    }
}
