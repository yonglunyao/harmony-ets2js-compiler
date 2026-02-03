package com.ets2jsc.domain.service;

import com.ets2jsc.domain.model.config.CompilerConfig;

import java.nio.file.Path;
import java.util.Map;

/**
 * Domain service for configuration management.
 * <p>
 * This service provides configuration operations including loading,
 * validation, and defaults for the compiler configuration.
 */
public interface ConfigService {

    /**
     * Gets the current compiler configuration.
     *
     * @return the current configuration
     */
    CompilerConfig getConfiguration();

    /**
     * Sets a new compiler configuration.
     *
     * @param config the configuration to set
     */
    void setConfiguration(CompilerConfig config);

    /**
     * Loads configuration from a project directory.
     * Looks for configuration files like module.json, package.json, etc.
     *
     * @param projectDir the project directory to load from
     * @return the loaded configuration
     */
    CompilerConfig loadFromProject(Path projectDir);

    /**
     * Creates configuration from a properties map.
     *
     * @param properties the properties to convert to configuration
     * @return the created configuration
     */
    CompilerConfig loadFromProperties(Map<String, Object> properties);

    /**
     * Creates a default configuration.
     *
     * @return the default configuration
     */
    CompilerConfig createDefault();

    /**
     * Validates the current configuration.
     *
     * @return true if the configuration is valid, false otherwise
     */
    boolean isValid();
}
