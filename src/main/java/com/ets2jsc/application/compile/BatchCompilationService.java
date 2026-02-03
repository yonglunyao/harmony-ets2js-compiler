package com.ets2jsc.application.compile;

import com.ets2jsc.domain.model.config.CompilerConfig;

import java.nio.file.Path;
import java.util.List;

/**
 * Domain service for batch compilation operations.
 * <p>
 * This service provides batch compilation capabilities for compiling
 * multiple source files with support for different execution strategies
 * (sequential or parallel).
 */
public interface BatchCompilationService extends AutoCloseable {

    /**
     * Compiles multiple source files to an output directory.
     * The execution mode (sequential or parallel) depends on the implementation.
     *
     * @param sourceFiles list of source file paths
     * @param outputDir output directory
     * @return compilation result with statistics
     * @throws com.ets2jsc.shared.exception.CompilationException if compilation fails
     */
    com.ets2jsc.domain.model.compilation.CompilationResult compileBatch(
            List<Path> sourceFiles, Path outputDir) throws com.ets2jsc.shared.exception.CompilationException;

    /**
     * Compiles multiple source files while preserving directory structure.
     * Each source file's relative path from the base directory is maintained in the output.
     *
     * @param sourceFiles list of source file paths
     * @param baseDir the base directory for calculating relative paths
     * @param outputDir the output directory
     * @return compilation result with statistics
     * @throws com.ets2jsc.shared.exception.CompilationException if compilation fails
     */
    com.ets2jsc.domain.model.compilation.CompilationResult compileBatchWithStructure(
            List<Path> sourceFiles, Path baseDir, Path outputDir) throws com.ets2jsc.shared.exception.CompilationException;

    /**
     * Compiles a project directory, preserving directory structure.
     * Source files are compiled and output maintains the original directory hierarchy.
     *
     * @param sourceDir the source project directory
     * @param outputDir the output directory
     * @param copyResources whether to copy non-source files to output directory
     * @return compilation result with statistics
     * @throws com.ets2jsc.shared.exception.CompilationException if compilation fails
     */
    com.ets2jsc.domain.model.compilation.CompilationResult compileProject(
            Path sourceDir, Path outputDir, boolean copyResources) throws com.ets2jsc.shared.exception.CompilationException;

    /**
     * Gets the compiler configuration.
     *
     * @return the compiler configuration
     */
    CompilerConfig getConfig();

    /**
     * Gets the compilation mode of this service.
     *
     * @return the compilation mode
     */
    CompilationMode getMode();

    /**
     * Compilation mode for batch operations.
     */
    enum CompilationMode {
        SEQUENTIAL,
        PARALLEL
    }
}
