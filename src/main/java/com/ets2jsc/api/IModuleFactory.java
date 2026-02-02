package com.ets2jsc.api;

import com.ets2jsc.config.CompilerConfig;

/**
 * Factory interface for creating module instances.
 * <p>
 * This interface provides a single entry point for creating all module facades.
 * Implementations should handle proper initialization, dependency injection,
 * and lifecycle management of module instances.
 */
public interface IModuleFactory extends AutoCloseable {

    /**
     * Creates a parser module instance.
     *
     * @return a new parser instance
     */
    IParser createParser();

    /**
     * Creates a transformer module instance.
     *
     * @param config the compiler configuration
     * @return a new transformer instance
     */
    ITransformer createTransformer(CompilerConfig config);

    /**
     * Creates a code generator module instance.
     *
     * @param config the compiler configuration
     * @return a new code generator instance
     */
    ICodeGenerator createCodeGenerator(CompilerConfig config);

    /**
     * Creates a config module instance.
     *
     * @return a new config instance
     */
    IConfig createConfig();

    /**
     * Closes all resources held by the factory.
     */
    @Override
    void close();
}
