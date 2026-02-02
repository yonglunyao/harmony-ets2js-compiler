package com.ets2jsc.compiler;

import com.ets2jsc.config.CompilerConfig;

/**
 * Factory for creating compiler instances.
 * Provides a unified entry point for obtaining compilers with different execution strategies.
 */
public final class CompilerFactory {

    private CompilerFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a compiler with the specified configuration and mode.
     *
     * @param config the compiler configuration
     * @param mode the compilation mode (sequential or parallel)
     * @return a new compiler instance
     */
    public static ICompiler createCompiler(CompilerConfig config, ICompiler.CompilationMode mode) {
        switch (mode) {
            case SEQUENTIAL:
                return new SequentialCompiler(config);
            case PARALLEL:
                return new ParallelCompiler(config);
            default:
                throw new IllegalArgumentException("Unknown compilation mode: " + mode);
        }
    }

    /**
     * Creates a sequential compiler.
     *
     * @param config the compiler configuration
     * @return a new sequential compiler instance
     */
    public static ICompiler createSequentialCompiler(CompilerConfig config) {
        return new SequentialCompiler(config);
    }

    /**
     * Creates a parallel compiler with default thread pool size.
     *
     * @param config the compiler configuration
     * @return a new parallel compiler instance
     */
    public static ICompiler createParallelCompiler(CompilerConfig config) {
        return new ParallelCompiler(config);
    }

    /**
     * Creates a parallel compiler with specified thread pool size.
     *
     * @param config the compiler configuration
     * @param threadPoolSize the number of threads to use (0 or negative uses CPU core count)
     * @return a new parallel compiler instance
     */
    public static ICompiler createParallelCompiler(CompilerConfig config, int threadPoolSize) {
        return new ParallelCompiler(config, threadPoolSize);
    }

    /**
     * Creates a compiler with the specified configuration.
     * Defaults to parallel mode for better performance.
     *
     * @param config the compiler configuration
     * @return a new compiler instance
     */
    public static ICompiler createCompiler(CompilerConfig config) {
        return createParallelCompiler(config);
    }
}
