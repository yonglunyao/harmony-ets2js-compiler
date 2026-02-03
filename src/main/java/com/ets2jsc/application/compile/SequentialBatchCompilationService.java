package com.ets2jsc.application.compile;

import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CompilationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Sequential batch compilation service implementation.
 * Compiles source files one by one in sequence.
 */
public class SequentialBatchCompilationService implements BatchCompilationService {

    private final CompilationPipeline pipeline;
    private final CompilerConfig config;
    private volatile boolean closed;

    /**
     * Creates a new sequential batch compilation service.
     *
     * @param pipeline the compilation pipeline to use for each file
     */
    public SequentialBatchCompilationService(CompilationPipeline pipeline) {
        if (pipeline == null) {
            throw new IllegalArgumentException("Pipeline cannot be null");
        }
        this.pipeline = pipeline;
        this.config = pipeline.getConfig();
        this.closed = false;
    }

    @Override
    public com.ets2jsc.domain.model.compilation.CompilationResult compileBatch(
            List<Path> sourceFiles, Path outputDir) throws CompilationException {
        com.ets2jsc.domain.model.compilation.CompilationResult result =
                new com.ets2jsc.domain.model.compilation.CompilationResult();

        try {
            // Create output directory if it doesn't exist
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new CompilationException("Failed to create output directory: " + outputDir, e);
        }

        for (Path sourceFile : sourceFiles) {
            String fileName = sourceFile.getFileName().toString();
            String outputName = fileName.replace(".ets", ".js").replace(".ts", ".js");
            Path outputPath = outputDir.resolve(outputName);

            try {
                pipeline.execute(sourceFile, outputPath);
                result.addFileResult(sourceFile, com.ets2jsc.domain.model.compilation.CompilationResult.FileResult.success(
                        sourceFile, outputPath, 0));
            } catch (Exception e) {
                result.addFileResult(sourceFile, com.ets2jsc.domain.model.compilation.CompilationResult.FileResult.failure(
                        sourceFile, outputPath, "Compilation failed: " + e.getMessage(), e, 0));
            }
        }

        result.markCompleted();
        return result;
    }

    @Override
    public com.ets2jsc.domain.model.compilation.CompilationResult compileBatchWithStructure(
            List<Path> sourceFiles, Path baseDir, Path outputDir) throws CompilationException {
        com.ets2jsc.domain.model.compilation.CompilationResult result =
                new com.ets2jsc.domain.model.compilation.CompilationResult();

        for (Path sourceFile : sourceFiles) {
            String relativePathStr = baseDir.relativize(sourceFile).toString();
            String outputPathStr = relativePathStr
                    .replace(".ets", ".js")
                    .replace(".ts", ".js")
                    .replace(".tsx", ".js")
                    .replace(".jsx", ".js");
            Path outputPath = outputDir.resolve(outputPathStr);

            try {
                // Create parent directories if needed
                Path parentDir = outputPath.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }

                pipeline.execute(sourceFile, outputPath);
                result.addFileResult(sourceFile, com.ets2jsc.domain.model.compilation.CompilationResult.FileResult.success(
                        sourceFile, outputPath, 0));
            } catch (Exception e) {
                result.addFileResult(sourceFile, com.ets2jsc.domain.model.compilation.CompilationResult.FileResult.failure(
                        sourceFile, outputPath, "Compilation failed: " + e.getMessage(), e, 0));
            }
        }

        result.markCompleted();
        return result;
    }

    @Override
    public com.ets2jsc.domain.model.compilation.CompilationResult compileProject(
            Path sourceDir, Path outputDir, boolean copyResources) throws CompilationException {
        if (!Files.isDirectory(sourceDir)) {
            throw new CompilationException("Source path is not a directory: " + sourceDir);
        }

        try {
            // Normalize paths
            sourceDir = sourceDir.normalize();
            outputDir = outputDir.normalize();

            // Create output directory if it doesn't exist
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // Find all source files in the project
            List<Path> sourceFiles = com.ets2jsc.shared.util.SourceFileFinder.findSourceFiles(sourceDir);

            // Compile source files while preserving directory structure
            com.ets2jsc.domain.model.compilation.CompilationResult compileResult;
            if (!sourceFiles.isEmpty()) {
                compileResult = compileBatchWithStructure(sourceFiles, sourceDir, outputDir);
            } else {
                compileResult = new com.ets2jsc.domain.model.compilation.CompilationResult();
            }

            // Copy resource files if requested
            int copiedResourceCount = 0;
            if (copyResources) {
                copiedResourceCount = com.ets2jsc.shared.util.ResourceFileCopier.copyResourceFiles(sourceDir, outputDir);
            }

            return new com.ets2jsc.domain.model.compilation.CompilationResult(
                    compileResult.getFileResults(),
                    compileResult.getTotalCount(),
                    compileResult.getSuccessCount(),
                    compileResult.getFailureCount(),
                    copiedResourceCount);

        } catch (IOException e) {
            throw new CompilationException("Failed to compile project: " + sourceDir, e);
        }
    }

    @Override
    public CompilerConfig getConfig() {
        return config;
    }

    @Override
    public CompilationMode getMode() {
        return CompilationMode.SEQUENTIAL;
    }

    @Override
    public void close() {
        closed = true;
    }

    /**
     * Checks if the service has been closed.
     *
     * @return true if the service is closed, false otherwise
     */
    public boolean isClosed() {
        return closed;
    }
}
