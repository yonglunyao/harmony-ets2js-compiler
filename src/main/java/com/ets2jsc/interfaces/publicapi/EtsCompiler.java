package com.ets2jsc.interfaces.publicapi;

import lombok.Getter;

import com.ets2jsc.application.compile.BatchCompilationService;
import com.ets2jsc.application.compile.BatchCompilationServiceFactory;
import com.ets2jsc.application.compile.CompilationPipeline;
import com.ets2jsc.application.compile.CompilationPipelineFactory;
import com.ets2jsc.domain.model.compilation.CompilationResult;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CompilationException;
import com.ets2jsc.interfaces.publicapi.model.PublicCompilationResult;
import com.ets2jsc.interfaces.publicapi.model.CompilationMode;

import java.nio.file.Path;
import java.util.List;

/**
 * Public facade for the ETS to JS Compiler.
 * <p>
 * This class provides a simple, stable API for external applications
 * to compile ETS/ArkTS source code to JavaScript. It hides the internal
 * complexity while providing access to all compilation features.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Compile a single file
 * try (EtsCompiler compiler = EtsCompiler.create()) {
 *     PublicCompilationResult result = compiler.compileFile(
 *         Path.of("src/Main.ets"),
 *         Path.of("build/Main.js")
 *     );
 *     if (result.isSuccess()) {
 *         System.out.println("Compilation succeeded!");
 *     }
 * }
 *
 * // Compile a project
 * try (EtsCompiler compiler = EtsCompiler.builder()
 *         .parallelMode(true)
 *         .threadCount(4)
 *         .build()) {
 *     PublicCompilationResult result = compiler.compileProject(
 *         Path.of("src"),
 *         Path.of("build"),
 *         false  // don't copy resources
 *     );
 *     System.out.println(result.getSummary());
 * }
 * }</pre>
 *
 * @see EtsCompilerBuilder
 * @see PublicCompilationResult
 */
@Getter
public class EtsCompiler implements AutoCloseable {

    private final CompilationPipeline pipeline;
    private final BatchCompilationService batchService;
    private final CompilerConfig config;
    private final CompilationMode mode;
    private final int threadCount;
    private volatile boolean closed;

    /**
     * Creates a new compiler instance with default configuration.
     *
     * @return a new compiler instance
     */
    public static EtsCompiler create() {
        return builder().build();
    }

    /**
     * Creates a new builder for configuring the compiler.
     *
     * @return a new builder instance
     */
    public static EtsCompilerBuilder builder() {
        return new EtsCompilerBuilder();
    }

    /**
     * Package-private constructor used by EtsCompilerBuilder.
     *
     * @param config the compiler configuration
     * @param mode the compilation mode
     * @param threadCount the thread count for parallel compilation
     */
    EtsCompiler(CompilerConfig config, CompilationMode mode, int threadCount) {
        this.config = config;
        this.mode = mode;
        this.threadCount = threadCount;
        this.pipeline = CompilationPipelineFactory.createPipeline(config);

        if (mode == CompilationMode.PARALLEL && threadCount > 1) {
            this.batchService = BatchCompilationServiceFactory.createParallelService(pipeline, threadCount);
        } else {
            this.batchService = BatchCompilationServiceFactory.createSequentialService(pipeline);
        }
        this.closed = false;
    }

    /**
     * Compiles a single source file to an output file.
     *
     * @param sourcePath the path to the source file (.ets, .ts, .tsx)
     * @param outputPath the path to the output file (.js)
     * @return the compilation result
     * @throws CompilationException if compilation fails
     * @throws IllegalStateException if the compiler is closed
     */
    public PublicCompilationResult compileFile(Path sourcePath, Path outputPath) throws CompilationException {
        checkNotClosed();
        CompilationResult internalResult = pipeline.execute(sourcePath, outputPath);
        return new PublicCompilationResult(internalResult);
    }

    /**
     * Compiles multiple source files to an output directory.
     * All output files are placed directly in the output directory.
     *
     * @param sourceFiles list of source file paths
     * @param outputDir output directory
     * @return the compilation result
     * @throws CompilationException if compilation fails
     * @throws IllegalStateException if the compiler is closed
     */
    public PublicCompilationResult compileBatch(List<Path> sourceFiles, Path outputDir) throws CompilationException {
        checkNotClosed();
        CompilationResult internalResult = batchService.compileBatch(sourceFiles, outputDir);
        return new PublicCompilationResult(internalResult);
    }

    /**
     * Compiles multiple source files while preserving directory structure.
     * Each source file's relative path from the base directory is maintained in the output.
     *
     * @param sourceFiles list of source file paths
     * @param baseDir the base directory for calculating relative paths
     * @param outputDir the output directory
     * @return the compilation result
     * @throws CompilationException if compilation fails
     * @throws IllegalStateException if the compiler is closed
     */
    public PublicCompilationResult compileBatchWithStructure(
            List<Path> sourceFiles, Path baseDir, Path outputDir) throws CompilationException {
        checkNotClosed();
        CompilationResult internalResult = batchService.compileBatchWithStructure(sourceFiles, baseDir, outputDir);
        return new PublicCompilationResult(internalResult);
    }

    /**
     * Compiles a project directory, preserving directory structure.
     * This method recursively finds all compilable source files in the source directory
     * and compiles them to the output directory while maintaining the directory hierarchy.
     *
     * @param sourceDir the source project directory
     * @param outputDir the output directory
     * @param copyResources whether to copy non-source files to output directory
     * @return the compilation result
     * @throws CompilationException if compilation fails
     * @throws IllegalStateException if the compiler is closed
     */
    public PublicCompilationResult compileProject(
            Path sourceDir, Path outputDir, boolean copyResources) throws CompilationException {
        checkNotClosed();
        CompilationResult internalResult = batchService.compileProject(sourceDir, outputDir, copyResources);
        return new PublicCompilationResult(internalResult);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        try {
            batchService.close();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
        try {
            pipeline.close();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
        closed = true;
    }

    /**
     * Checks that the compiler has not been closed.
     *
     * @throws IllegalStateException if the compiler is closed
     */
    private void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("EtsCompiler is closed");
        }
    }
}
