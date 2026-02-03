package com.ets2jsc.application.compile;

import com.ets2jsc.domain.model.config.CompilerConfig;

/**
 * Factory for creating batch compilation services.
 * <p>
 * Provides a unified entry point for obtaining batch compilation services
 * with different execution strategies (parallel or sequential).
 */
public final class BatchCompilationServiceFactory {

    private BatchCompilationServiceFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a batch compilation service with the specified pipeline and mode.
     *
     * @param pipeline the compilation pipeline to use for each file
     * @param mode the compilation mode (sequential or parallel)
     * @return a new batch compilation service instance
     * @throws IllegalArgumentException if pipeline is null
     */
    public static BatchCompilationService createService(
            CompilationPipeline pipeline, BatchCompilationService.CompilationMode mode) {
        if (pipeline == null) {
            throw new IllegalArgumentException("Compilation pipeline cannot be null");
        }

        switch (mode) {
            case SEQUENTIAL:
                return new SequentialBatchCompilationService(pipeline);
            case PARALLEL:
                return new ParallelBatchCompilationService(pipeline);
            default:
                throw new IllegalArgumentException("Unknown compilation mode: " + mode);
        }
    }

    /**
     * Creates a batch compilation service with the specified configuration and mode.
     *
     * @param pipeline the compilation pipeline to use for each file
     * @param config the compiler configuration
     * @param mode the compilation mode (sequential or parallel)
     * @return a new batch compilation service instance
     * @throws IllegalArgumentException if pipeline or config is null
     */
    public static BatchCompilationService createService(
            CompilationPipeline pipeline, CompilerConfig config, BatchCompilationService.CompilationMode mode) {
        if (pipeline == null) {
            throw new IllegalArgumentException("Compilation pipeline cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("Compiler configuration cannot be null");
        }

        switch (mode) {
            case SEQUENTIAL:
                return new SequentialBatchCompilationService(pipeline);
            case PARALLEL:
                return new ParallelBatchCompilationService(pipeline);
            default:
                throw new IllegalArgumentException("Unknown compilation mode: " + mode);
        }
    }

    /**
     * Creates a parallel batch compilation service with default thread pool size.
     *
     * @param pipeline the compilation pipeline to use for each file
     * @return a new parallel batch compilation service instance
     */
    public static BatchCompilationService createParallelService(CompilationPipeline pipeline) {
        if (pipeline == null) {
            throw new IllegalArgumentException("Compilation pipeline cannot be null");
        }
        return new ParallelBatchCompilationService(pipeline);
    }

    /**
     * Creates a parallel batch compilation service with specified thread pool size.
     *
     * @param pipeline the compilation pipeline to use for each file
     * @param threadPoolSize the number of threads to use (0 or negative uses CPU core count)
     * @return a new parallel batch compilation service instance
     */
    public static BatchCompilationService createParallelService(CompilationPipeline pipeline, int threadPoolSize) {
        if (pipeline == null) {
            throw new IllegalArgumentException("Compilation pipeline cannot be null");
        }
        return new ParallelBatchCompilationService(pipeline, threadPoolSize);
    }

    /**
     * Creates a sequential batch compilation service.
     *
     * @param pipeline the compilation pipeline to use for each file
     * @return a new sequential batch compilation service instance
     */
    public static BatchCompilationService createSequentialService(CompilationPipeline pipeline) {
        if (pipeline == null) {
            throw new IllegalArgumentException("Compilation pipeline cannot be null");
        }
        return new SequentialBatchCompilationService(pipeline);
    }

    /**
     * Creates a batch compilation service with the specified pipeline.
     * Defaults to parallel mode for better performance.
     *
     * @param pipeline the compilation pipeline to use for each file
     * @return a new batch compilation service instance
     */
    public static BatchCompilationService createService(CompilationPipeline pipeline) {
        return createParallelService(pipeline);
    }
}
