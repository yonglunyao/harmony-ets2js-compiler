package com.ets2jsc.command;

import com.ets2jsc.compiler.CompilationResult;
import com.ets2jsc.compiler.ICompiler;
import com.ets2jsc.exception.CompilationException;

import java.nio.file.Path;
import java.util.Collections;

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

    private final ICompiler compiler;
    private final Path sourcePath;
    private final Path outputPath;

    /**
     * Creates a new single file compilation command.
     *
     * @param compiler the compiler to use
     * @param sourcePath the source file path
     * @param outputPath the output file path
     * @throws IllegalArgumentException if any parameter is null
     */
    public SingleFileCompilationCommand(ICompiler compiler, Path sourcePath, Path outputPath) {
        if (compiler == null) {
            throw new IllegalArgumentException("Compiler cannot be null");
        }
        if (sourcePath == null) {
            throw new IllegalArgumentException("Source path cannot be null");
        }
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path cannot be null");
        }

        this.compiler = compiler;
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
    }

    @Override
    public CompilationResult execute() throws CompilationException {
        compiler.compile(sourcePath, outputPath);

        CompilationResult result = new CompilationResult();
        result.addFileResult(sourcePath, CompilationResult.FileResult.success(sourcePath, outputPath, 0));
        result.markCompleted();
        return result;
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
