package com.ets2jsc.domain.service;

import com.ets2jsc.domain.model.config.CompilerConfig;

/**
 * Domain service for creating module instances.
 * <p>
 * This service provides a single entry point for creating all compilation services.
 * Implementations should handle proper initialization, dependency injection,
 * and lifecycle management of service instances.
 */
public interface ModuleFactoryService extends AutoCloseable {

    /**
     * Creates a parser service instance.
     *
     * @return a new parser service instance
     */
    ParserService createParser();

    /**
     * Creates a transformer service instance.
     *
     * @param config the compiler configuration
     * @return a new transformer service instance
     */
    TransformerService createTransformer(CompilerConfig config);

    /**
     * Creates a generator service instance.
     *
     * @param config the compiler configuration
     * @return a new generator service instance
     */
    GeneratorService createGenerator(CompilerConfig config);

    /**
     * Creates a config service instance.
     *
     * @return a new config service instance
     */
    ConfigService createConfig();

    /**
     * Closes all resources held by the factory.
     */
    @Override
    void close();
}
