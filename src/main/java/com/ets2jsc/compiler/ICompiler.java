package com.ets2jsc.compiler;

import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.shared.exception.CompilationException;

import java.nio.file.Path;
import java.util.List;

/**
 * Interface for ETS compiler implementations.
 * Defines the contract for compiling ETS source files to JavaScript.
 */
public interface ICompiler extends AutoCloseable {

    /**
     * Compilation mode for batch operations.
     */
    enum CompilationMode {
        SEQUENTIAL,
        PARALLEL
    }

    /**
     * Compiles a single ETS source file to JavaScript.
     *
     * @param sourcePath path to the ETS source file
     * @param outputPath path to the output JavaScript file
     * @throws CompilationException if compilation fails
     */
    void compile(Path sourcePath, Path outputPath) throws CompilationException;

    /**
     * Compiles multiple ETS source files.
     * The execution mode (sequential or parallel) depends on the implementation.
     *
     * @param sourceFiles list of source file paths
     * @param outputDir output directory
     * @return compilation result with statistics
     * @throws CompilationException if compilation fails
     */
    CompilationResult compileBatch(List<Path> sourceFiles, Path outputDir) throws CompilationException;

    /**
     * Compiles multiple source files while preserving directory structure.
     * Each source file's relative path from the base directory is maintained in the output.
     *
     * @param sourceFiles list of source file paths
     * @param baseDir the base directory for calculating relative paths
     * @param outputDir the output directory
     * @return compilation result with statistics
     * @throws CompilationException if compilation fails
     */
    CompilationResult compileBatchWithStructure(List<Path> sourceFiles, Path baseDir, Path outputDir)
            throws CompilationException;

    /**
     * Compiles a project directory, preserving directory structure.
     * Source files are compiled and output maintains the original directory hierarchy.
     *
     * @param sourceDir the source project directory
     * @param outputDir the output directory
     * @param copyResources whether to copy non-source files to output directory
     * @return compilation result with statistics
     * @throws CompilationException if compilation fails
     */
    CompilationResult compileProject(Path sourceDir, Path outputDir, boolean copyResources)
            throws CompilationException;

    /**
     * Gets the compiler configuration.
     *
     * @return the compiler configuration
     */
    CompilerConfig getConfig();

    /**
     * Gets the compilation mode of this compiler.
     *
     * @return the compilation mode
     */
    CompilationMode getMode();

    /**
     * Shuts down the compiler and releases any resources.
     * For parallel compilers, this shuts down the thread pool.
     */
    @Override
    void close();
}
