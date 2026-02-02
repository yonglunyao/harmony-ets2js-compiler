package com.ets2jsc.di;

import com.ets2jsc.api.ICodeGenerator;
import com.ets2jsc.api.IConfig;
import com.ets2jsc.api.IModuleFactory;
import com.ets2jsc.api.IParser;
import com.ets2jsc.api.ITransformer;
import com.ets2jsc.config.CompilerConfig;

/**
 * Service provider for module instances.
 * <p>
 * This class provides a singleton access point to module instances,
 * implementing a simple service locator pattern. It manages the lifecycle
 * of the module factory and ensures proper cleanup.
 */
public final class ModuleServiceProvider {

    private static final ModuleServiceProvider INSTANCE = new ModuleServiceProvider();

    private final IModuleFactory moduleFactory;
    private final IConfig config;
    private volatile boolean closed;

    /**
     * Private constructor for singleton.
     */
    private ModuleServiceProvider() {
        this.moduleFactory = new com.ets2jsc.impl.DefaultModuleFactory();
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
    public IModuleFactory getModuleFactory() {
        checkNotClosed();
        return moduleFactory;
    }

    /**
     * Gets a parser instance.
     *
     * @return a new parser instance
     * @throws IllegalStateException if the provider is closed
     */
    public IParser getParser() {
        checkNotClosed();
        return moduleFactory.createParser();
    }

    /**
     * Gets a transformer instance with the given configuration.
     *
     * @param config the compiler configuration
     * @return a new transformer instance
     * @throws IllegalStateException if the provider is closed
     */
    public ITransformer getTransformer(CompilerConfig config) {
        checkNotClosed();
        return moduleFactory.createTransformer(config);
    }

    /**
     * Gets a code generator instance with the given configuration.
     *
     * @param config the compiler configuration
     * @return a new code generator instance
     * @throws IllegalStateException if the provider is closed
     */
    public ICodeGenerator getCodeGenerator(CompilerConfig config) {
        checkNotClosed();
        return moduleFactory.createCodeGenerator(config);
    }

    /**
     * Gets the config module instance.
     *
     * @return the config module instance
     * @throws IllegalStateException if the provider is closed
     */
    public IConfig getConfig() {
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
