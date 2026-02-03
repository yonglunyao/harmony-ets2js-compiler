package com.ets2jsc.interfaces.cli.command;

import com.ets2jsc.application.compile.BatchCompilationService;
import com.ets2jsc.domain.model.compilation.CompilationResult;
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

    private final BatchCompilationService batchService;
    private final Path sourceDir;
    private final Path outputDir;
    private final boolean copyResources;

    /**
     * Creates a new project compilation command.
     *
     * @param batchService the batch compilation service to use
     * @param sourceDir the source project directory
     * @param outputDir the output directory
     * @param copyResources whether to copy non-source resource files
     * @throws IllegalArgumentException if any parameter is null
     */
    public ProjectCompilationCommand(BatchCompilationService batchService, Path sourceDir, Path outputDir, boolean copyResources) {
        if (batchService == null) {
            throw new IllegalArgumentException("Batch compilation service cannot be null");
        }
        if (sourceDir == null) {
            throw new IllegalArgumentException("Source directory cannot be null");
        }
        if (outputDir == null) {
            throw new IllegalArgumentException("Output directory cannot be null");
        }

        this.batchService = batchService;
        this.sourceDir = sourceDir;
        this.outputDir = outputDir;
        this.copyResources = copyResources;
    }

    @Override
    public CompilationResult execute() throws CompilationException {
        return batchService.compileProject(sourceDir, outputDir, copyResources);
    }

    @Override
    public String getDescription() {
        return String.format("ProjectCompilation: %s -> %s (copyResources=%s)",
                sourceDir, outputDir, copyResources);
    }
}
