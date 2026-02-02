package com.ets2jsc.impl;

import com.ets2jsc.api.ICodeGenerator;
import com.ets2jsc.api.IConfig;
import com.ets2jsc.api.IModuleFactory;
import com.ets2jsc.api.IParser;
import com.ets2jsc.api.ITransformer;
import com.ets2jsc.domain.model.config.CompilerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of IModuleFactory.
 * <p>
 * This factory creates module instances with proper initialization and
 * lifecycle management. All created modules are tracked and can be closed
 * when the factory is closed.
 */
public class DefaultModuleFactory implements IModuleFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultModuleFactory.class);

    private final List<AutoCloseable> createdModules;
    private boolean closed;

    /**
     * Creates a new default module factory.
     */
    public DefaultModuleFactory() {
        this.createdModules = new ArrayList<>();
        this.closed = false;
    }

    @Override
    public IParser createParser() {
        checkNotClosed();
        ParserModuleFacade parser = new ParserModuleFacade();
        trackModule(parser);
        return parser;
    }

    @Override
    public ITransformer createTransformer(CompilerConfig config) {
        checkNotClosed();

        CompilerConfig effectiveConfig = (config != null) ? config : CompilerConfig.createDefault();
        TransformerModuleFacade transformer = new TransformerModuleFacade(effectiveConfig);
        trackModule(transformer);
        return transformer;
    }

    @Override
    public ICodeGenerator createCodeGenerator(CompilerConfig config) {
        checkNotClosed();

        CompilerConfig effectiveConfig = (config != null) ? config : CompilerConfig.createDefault();
        GeneratorModuleFacade generator = new GeneratorModuleFacade(effectiveConfig);
        trackModule(generator);
        return generator;
    }

    @Override
    public IConfig createConfig() {
        checkNotClosed();
        ConfigModuleFacade config = new ConfigModuleFacade();
        trackModule(config);
        return config;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        // Close all created modules in reverse order
        for (int i = createdModules.size() - 1; i >= 0; i--) {
            AutoCloseable module = createdModules.get(i);
            try {
                module.close();
            } catch (Exception e) {
                LOGGER.warn("Failed to close module: {}", e.getMessage());
            }
        }

        createdModules.clear();
        closed = true;
    }

    /**
     * Tracks a created module for later cleanup.
     *
     * @param module the module to track
     */
    private void trackModule(AutoCloseable module) {
        createdModules.add(module);
    }

    /**
     * Checks that the factory has not been closed.
     *
     * @throws IllegalStateException if the factory is closed
     */
    private void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("ModuleFactory is closed");
        }
    }

    /**
     * Checks if the factory has been closed.
     *
     * @return true if the factory is closed, false otherwise
     */
    public boolean isClosed() {
        return closed;
    }
}
