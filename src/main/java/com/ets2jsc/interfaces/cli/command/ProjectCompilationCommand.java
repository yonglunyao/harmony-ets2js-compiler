package com.ets2jsc.interfaces.cli.command;

import com.ets2jsc.compiler.CompilationResult;
import com.ets2jsc.compiler.ICompiler;
import com.ets2jsc.shared.exception.CompilationException;

import java.nio.file.Path;

/**
 * Command for compiling an entire project directory.
 * <p>
 * This command encapsulates project compilation including source file
 * discovery, directory structure preservation, and optional resource copying.
 * </p>
 *
 * @since 1.0
 */
public class ProjectCompilationCommand implements CompilationCommand {

    private final ICompiler compiler;
    private final Path sourceDir;
    private final Path outputDir;
    private final boolean copyResources;

    /**
     * Creates a new project compilation command.
     *
     * @param compiler the compiler to use
     * @param sourceDir the source project directory
     * @param outputDir the output directory
     * @param copyResources whether to copy non-source resource files
     * @throws IllegalArgumentException if any parameter is null
     */
    public ProjectCompilationCommand(ICompiler compiler, Path sourceDir, Path outputDir, boolean copyResources) {
        if (compiler == null) {
            throw new IllegalArgumentException("Compiler cannot be null");
        }
        if (sourceDir == null) {
            throw new IllegalArgumentException("Source directory cannot be null");
        }
        if (outputDir == null) {
            throw new IllegalArgumentException("Output directory cannot be null");
        }

        this.compiler = compiler;
        this.sourceDir = sourceDir;
        this.outputDir = outputDir;
        this.copyResources = copyResources;
    }

    @Override
    public CompilationResult execute() throws CompilationException {
        return compiler.compileProject(sourceDir, outputDir, copyResources);
    }

    @Override
    public String getDescription() {
        return String.format("ProjectCompilation: %s -> %s (copyResources=%s)",
                sourceDir, outputDir, copyResources);
    }
}
