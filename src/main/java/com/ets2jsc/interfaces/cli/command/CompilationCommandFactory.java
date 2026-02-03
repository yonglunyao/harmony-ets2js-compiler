package com.ets2jsc.interfaces.cli.command;

import com.ets2jsc.application.compile.BatchCompilationService;
import com.ets2jsc.application.compile.CompilationPipeline;

import java.nio.file.Path;
import java.util.List;

/**
 * Factory for creating compilation commands.
 * <p>
 * This factory provides a convenient API for creating different types
 * of compilation commands, encapsulating the complexity of command
 * instantiation.
 * </p>
 *
 * @since 1.0
 */
public final class CompilationCommandFactory {

    private CompilationCommandFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a command for compiling a single source file.
     *
     * @param pipeline the compilation pipeline to use
     * @param sourcePath the source file path
     * @param outputPath the output file path
     * @return a new single file compilation command
     * @throws IllegalArgumentException if any parameter is null
     */
    public static CompilationCommand createSingleFileCommand(
            CompilationPipeline pipeline, Path sourcePath, Path outputPath) {
        return new SingleFileCompilationCommand(pipeline, sourcePath, outputPath);
    }

    /**
     * Creates a command for compiling multiple source files.
     *
     * @param batchService the batch compilation service to use
     * @param sourceFiles the list of source files to compile
     * @param outputDir the output directory
     * @return a new batch compilation command
     * @throws IllegalArgumentException if any parameter is null or sourceFiles is empty
     */
    public static CompilationCommand createBatchCommand(
            BatchCompilationService batchService, List<Path> sourceFiles, Path outputDir) {
        return new BatchCompilationCommand(batchService, sourceFiles, outputDir);
    }

    /**
     * Creates a command for compiling an entire project.
     *
     * @param batchService the batch compilation service to use
     * @param sourceDir the source project directory
     * @param outputDir the output directory
     * @param copyResources whether to copy non-source resource files
     * @return a new project compilation command
     * @throws IllegalArgumentException if any parameter is null
     */
    public static CompilationCommand createProjectCommand(
            BatchCompilationService batchService, Path sourceDir, Path outputDir, boolean copyResources) {
        return new ProjectCompilationCommand(batchService, sourceDir, outputDir, copyResources);
    }

    /**
     * Creates a command for compiling an entire project without copying resources.
     *
     * @param batchService the batch compilation service to use
     * @param sourceDir the source project directory
     * @param outputDir the output directory
     * @return a new project compilation command
     * @throws IllegalArgumentException if any parameter is null
     */
    public static CompilationCommand createProjectCommand(
            BatchCompilationService batchService, Path sourceDir, Path outputDir) {
        return createProjectCommand(batchService, sourceDir, outputDir, false);
    }
}
