package com.ets2jsc.interfaces.cli.command;

import com.ets2jsc.application.compile.CompilationPipeline;
import com.ets2jsc.domain.model.compilation.CompilationResult;
import com.ets2jsc.shared.exception.CompilationException;

import java.nio.file.Path;

/**
 * Command for compiling a single source file.
 * <p>
 * This command encapsulates the compilation of a single ETS source file
 * to a JavaScript output file.
 * </p>
 *
 * @since 1.0
 */
public class SingleFileCompilationCommand implements CompilationCommand {

    private final CompilationPipeline pipeline;
    private final Path sourcePath;
    private final Path outputPath;

    /**
     * Creates a new single file compilation command.
     *
     * @param pipeline the compilation pipeline to use
     * @param sourcePath the source file path
     * @param outputPath the output file path
     * @throws IllegalArgumentException if any parameter is null
     */
    public SingleFileCompilationCommand(CompilationPipeline pipeline, Path sourcePath, Path outputPath) {
        if (pipeline == null) {
            throw new IllegalArgumentException("Compilation pipeline cannot be null");
        }
        if (sourcePath == null) {
            throw new IllegalArgumentException("Source path cannot be null");
        }
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path cannot be null");
        }

        this.pipeline = pipeline;
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
    }

    @Override
    public CompilationResult execute() throws CompilationException {
        return pipeline.execute(sourcePath, outputPath);
    }

    @Override
    public Path getSourcePath() {
        return sourcePath;
    }

    @Override
    public Path getOutputPath() {
        return outputPath;
    }

    @Override
    public String getDescription() {
        return String.format("SingleFileCompilation: %s -> %s", sourcePath, outputPath);
    }
}
