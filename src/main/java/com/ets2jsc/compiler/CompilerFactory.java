package com.ets2jsc.compiler;

import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.infrastructure.factory.TransformerFactory;
import com.ets2jsc.infrastructure.factory.DefaultTransformerFactory;

/**
 * Factory for creating compiler instances.
 * Provides a unified entry point for obtaining compilers with different execution strategies.
 */
public final class CompilerFactory {

    private static TransformerFactory defaultTransformerFactory = new DefaultTransformerFactory();

    private CompilerFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Sets the default transformer factory for compiler creation.
     *
     * @param factory the transformer factory to use
     * @throws IllegalArgumentException if factory is null
     */
    public static void setDefaultTransformerFactory(TransformerFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Transformer factory cannot be null");
        }
        defaultTransformerFactory = factory;
    }

    /**
     * Returns the default transformer factory.
     *
     * @return the default transformer factory
     */
    public static TransformerFactory getDefaultTransformerFactory() {
        return defaultTransformerFactory;
    }

    /**
     * Creates a compiler with the specified configuration and mode.
     *
     * @param config the compiler configuration
     * @param mode the compilation mode (sequential or parallel)
     * @return a new compiler instance
     */
    public static ICompiler createCompiler(CompilerConfig config, ICompiler.CompilationMode mode) {
        return createCompiler(config, mode, defaultTransformerFactory);
    }

    /**
     * Creates a compiler with the specified configuration, mode, and transformer factory.
     *
     * @param config the compiler configuration
     * @param mode the compilation mode (sequential or parallel)
     * @param transformerFactory the factory for creating transformers
     * @return a new compiler instance
     * @throws IllegalArgumentException if any parameter is null
     */
    public static ICompiler createCompiler(CompilerConfig config, ICompiler.CompilationMode mode,
            TransformerFactory transformerFactory) {
        if (config == null) {
            throw new IllegalArgumentException("Compiler configuration cannot be null");
        }
        if (mode == null) {
            throw new IllegalArgumentException("Compilation mode cannot be null");
        }
        if (transformerFactory == null) {
            throw new IllegalArgumentException("Transformer factory cannot be null");
        }

        switch (mode) {
            case SEQUENTIAL:
                return new SequentialCompiler(config, transformerFactory);
            case PARALLEL:
                return new ParallelCompiler(config, transformerFactory);
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
        return createSequentialCompiler(config, defaultTransformerFactory);
    }

    /**
     * Creates a sequential compiler with a custom transformer factory.
     *
     * @param config the compiler configuration
     * @param transformerFactory the factory for creating transformers
     * @return a new sequential compiler instance
     */
    public static ICompiler createSequentialCompiler(CompilerConfig config, TransformerFactory transformerFactory) {
        if (config == null) {
            throw new IllegalArgumentException("Compiler configuration cannot be null");
        }
        if (transformerFactory == null) {
            throw new IllegalArgumentException("Transformer factory cannot be null");
        }
        return new SequentialCompiler(config, transformerFactory);
    }

    /**
     * Creates a parallel compiler with default thread pool size.
     *
     * @param config the compiler configuration
     * @return a new parallel compiler instance
     */
    public static ICompiler createParallelCompiler(CompilerConfig config) {
        return createParallelCompiler(config, defaultTransformerFactory);
    }

    /**
     * Creates a parallel compiler with a custom transformer factory.
     *
     * @param config the compiler configuration
     * @param transformerFactory the factory for creating transformers
     * @return a new parallel compiler instance
     */
    public static ICompiler createParallelCompiler(CompilerConfig config, TransformerFactory transformerFactory) {
        if (config == null) {
            throw new IllegalArgumentException("Compiler configuration cannot be null");
        }
        if (transformerFactory == null) {
            throw new IllegalArgumentException("Transformer factory cannot be null");
        }
        return new ParallelCompiler(config, transformerFactory);
    }

    /**
     * Creates a parallel compiler with specified thread pool size.
     *
     * @param config the compiler configuration
     * @param threadPoolSize the number of threads to use (0 or negative uses CPU core count)
     * @return a new parallel compiler instance
     */
    public static ICompiler createParallelCompiler(CompilerConfig config, int threadPoolSize) {
        return createParallelCompiler(config, threadPoolSize, defaultTransformerFactory);
    }

    /**
     * Creates a parallel compiler with specified thread pool size and custom transformer factory.
     *
     * @param config the compiler configuration
     * @param threadPoolSize the number of threads to use (0 or negative uses CPU core count)
     * @param transformerFactory the factory for creating transformers
     * @return a new parallel compiler instance
     */
    public static ICompiler createParallelCompiler(CompilerConfig config, int threadPoolSize,
            TransformerFactory transformerFactory) {
        if (config == null) {
            throw new IllegalArgumentException("Compiler configuration cannot be null");
        }
        if (transformerFactory == null) {
            throw new IllegalArgumentException("Transformer factory cannot be null");
        }
        return new ParallelCompiler(config, threadPoolSize, transformerFactory);
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
