package com.ets2jsc.infrastructure.factory;

import com.ets2jsc.domain.service.ParserService;
import com.ets2jsc.domain.service.TransformerService;
import com.ets2jsc.domain.service.GeneratorService;
import com.ets2jsc.domain.service.ConfigService;
import com.ets2jsc.domain.service.ModuleFactoryService;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.infrastructure.parser.ParserModuleFacade;
import com.ets2jsc.infrastructure.transformer.TransformerModuleFacade;
import com.ets2jsc.infrastructure.generator.GeneratorModuleFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of ModuleFactoryService.
 * <p>
 * This factory creates service instances with proper initialization and
 * lifecycle management. All created services are tracked and can be closed
 * when the factory is closed.
 */
public class DefaultModuleFactory implements ModuleFactoryService {

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
    public ParserService createParser() {
        checkNotClosed();
        ParserModuleFacade parser = new ParserModuleFacade();
        trackModule(parser);
        return parser;
    }

    @Override
    public TransformerService createTransformer(CompilerConfig config) {
        checkNotClosed();

        CompilerConfig effectiveConfig = (config != null) ? config : CompilerConfig.createDefault();
        TransformerModuleFacade transformer = new TransformerModuleFacade(effectiveConfig);
        trackModule(transformer);
        return transformer;
    }

    @Override
    public GeneratorService createGenerator(CompilerConfig config) {
        checkNotClosed();

        CompilerConfig effectiveConfig = (config != null) ? config : CompilerConfig.createDefault();
        GeneratorModuleFacade generator = new GeneratorModuleFacade(effectiveConfig);
        trackModule(generator);
        return generator;
    }

    @Override
    public ConfigService createConfig() {
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
