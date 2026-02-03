package com.ets2jsc.application.di;

import com.ets2jsc.domain.service.ParserService;
import com.ets2jsc.domain.service.TransformerService;
import com.ets2jsc.domain.service.GeneratorService;
import com.ets2jsc.domain.service.ConfigService;
import com.ets2jsc.domain.service.ModuleFactoryService;
import com.ets2jsc.domain.model.config.CompilerConfig;

/**
 * Service provider for compilation services.
 * <p>
 * This class provides a singleton access point to service instances,
 * implementing a simple service locator pattern. It manages the lifecycle
 * of the module factory and ensures proper cleanup.
 */
public final class ModuleServiceProvider {

    private static final ModuleServiceProvider INSTANCE = new ModuleServiceProvider();

    private final ModuleFactoryService moduleFactory;
    private final ConfigService config;
    private volatile boolean closed;

    /**
     * Private constructor for singleton.
     */
    private ModuleServiceProvider() {
        this.moduleFactory = new com.ets2jsc.infrastructure.factory.DefaultModuleFactory();
        this.config = moduleFactory.createConfig();
        this.closed = false;
    }

    /**
     * Gets the singleton instance of the service provider.
     *
     * @return the service provider instance
     */
    public static ModuleServiceProvider getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the module factory.
     *
     * @return the module factory
     * @throws IllegalStateException if the provider is closed
     */
    public ModuleFactoryService getModuleFactory() {
        checkNotClosed();
        return moduleFactory;
    }

    /**
     * Gets a parser service instance.
     *
     * @return a new parser service instance
     * @throws IllegalStateException if the provider is closed
     */
    public ParserService getParser() {
        checkNotClosed();
        return moduleFactory.createParser();
    }

    /**
     * Gets a transformer service instance with the given configuration.
     *
     * @param config the compiler configuration
     * @return a new transformer service instance
     * @throws IllegalStateException if the provider is closed
     */
    public TransformerService getTransformer(CompilerConfig config) {
        checkNotClosed();
        return moduleFactory.createTransformer(config);
    }

    /**
     * Gets a generator service instance with the given configuration.
     *
     * @param config the compiler configuration
     * @return a new generator service instance
     * @throws IllegalStateException if the provider is closed
     */
    public GeneratorService getGenerator(CompilerConfig config) {
        checkNotClosed();
        return moduleFactory.createGenerator(config);
    }

    /**
     * Gets the config service instance.
     *
     * @return the config service instance
     * @throws IllegalStateException if the provider is closed
     */
    public ConfigService getConfig() {
        checkNotClosed();
        return config;
    }

    /**
     * Checks if the provider has been closed.
     *
     * @return true if the provider is closed, false otherwise
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Closes the service provider and releases all resources.
     * After calling this method, the provider cannot be used again.
     */
    public void close() {
        if (closed) {
            return;
        }

        try {
            moduleFactory.close();
        } finally {
            closed = true;
        }
    }

    /**
     * Checks that the provider has not been closed.
     *
     * @throws IllegalStateException if the provider is closed
     */
    private void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("ModuleServiceProvider is closed");
        }
    }
}
