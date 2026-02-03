package com.ets2jsc.interfaces.publicapi;

import com.ets2jsc.application.compile.BatchCompilationService;
import com.ets2jsc.application.compile.BatchCompilationServiceFactory;
import com.ets2jsc.application.compile.CompilationPipeline;
import com.ets2jsc.application.compile.CompilationPipelineFactory;
import com.ets2jsc.application.compile.EtsCompilerBuilder;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.domain.model.compilation.CompilationResult;
import com.ets2jsc.domain.service.CompilationMode;
import com.ets2jsc.interfaces.publicapi.model.CompilationMode;
import com.ets2jsc.interfaces.publicapi.model.PublicCompilationResult;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Public facade for ETS to JS Compiler.
 * <p>
 * This class provides a simple, stable API for external applications
 * to compile ETS/ArkTS source code to JavaScript. It hides away
 * internal complexity while providing access to all compilation features.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Compile a single file
 * EtsCompiler compiler = EtsCompiler.builder()
 *         .projectPath(Path.of("/my/project"))
 *         .build()
 *         .compileFile(Path.of("src/Main.ets"));
 * }</pre>
 *
 * @see EtsCompilerBuilder
 * @see PublicCompilationResult
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
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
     * @param config  compiler configuration
     * @param mode   compilation mode
     * @param threadCount  thread count for parallel compilation
     */
    EtsCompiler(CompilerConfig config, CompilationMode mode, int threadCount) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
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
     * Creates a new builder for configuring the compiler.
     *
     * @return a new builder instance
     */
    public static EtsCompilerBuilder builder() {
        return new EtsCompilerBuilder();
    }

    /**
     * Compiles a single source file to an output file.
     *
     * @param sourcePath path to the source file (.ets, .ts, .tsx)
     * @param outputPath  path to the output file (.js)
     * @return a compilation result
     * @throws CompilationException if compilation fails
     * @throws IllegalStateException if compiler is closed
     */
    public PublicCompilationResult compileFile(Path sourcePath, Path outputPath) throws CompilationException {
        checkNotClosed();
        CompilationResult internalResult = pipeline.execute(sourcePath, outputPath);
        return new PublicCompilationResult(internalResult);
    }

    /**
     * Compiles multiple source files to an output directory.
     *
     * @param sourceFiles list of source file paths
     * @param outputDir  output directory
     * @return a compilation result
     * @throws CompilationException if compilation fails
     * @throws IllegalStateException if compiler is closed
     */
    public PublicCompilationResult compileBatch(List<Path> sourceFiles, Path outputDir) throws CompilationException {
        checkNotClosed();
        CompilationResult internalResult = batchService.compileBatch(sourceFiles, outputDir);
        return new PublicCompilationResult(internalResult);
    }

    /**
     * Compiles multiple source files while preserving directory structure.
     *
     * @param sourceFiles list of source file paths
     * @param baseDir     base directory for calculating relative paths
     * @param outputDir  output directory
     * @return a compilation result
     * @throws CompilationException if compilation fails
     * @throws IllegalStateException if compiler is closed
     */
    public PublicCompilationResult compileBatchWithStructure(List<Path> sourceFiles, Path baseDir, Path outputDir) throws CompilationException {
        checkNotClosed();
        CompilationResult internalResult = batchService.compileBatchWithStructure(sourceFiles, baseDir, outputDir);
        return new PublicCompilationResult(internalResult);
    }

    /**
     * Compiles a project directory to an output directory.
     *
     * @param sourceDir  source project directory
     * @param outputDir  output directory
     * @param copyResources whether to copy non-source files
     * @return a compilation result
     * @throws CompilationException if compilation fails
     * @throws IllegalStateException if compiler is closed
     */
    public PublicCompilationResult compileProject(Path sourceDir, Path outputDir, boolean copyResources) throws CompilationException {
        checkNotClosed();
        CompilationResult internalResult = batchService.compileProject(sourceDir, outputDir, copyResources);
        return new PublicCompilationResult(internalResult);
    }

    /**
     * Closes the compiler and releases all resources.
     *
     * @throws IllegalStateException if compiler is closed
     */
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
     * @throws IllegalStateException if pipeline is closed
     */
    private void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("EtsCompiler is closed");
        }
    }
}
