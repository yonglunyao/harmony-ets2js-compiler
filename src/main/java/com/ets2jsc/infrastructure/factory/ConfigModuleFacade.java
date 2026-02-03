package com.ets2jsc.infrastructure.factory;

import com.ets2jsc.domain.service.ConfigService;
import com.ets2jsc.domain.model.config.CompilerConfig;

import java.nio.file.Files;

import java.nio.file.Path;
import java.util.Map;

/**
 * Facade for configuration management.
 * <p>
 * This class provides a single entry point for all configuration operations,
 * handling configuration loading, validation, and defaults.
 */
public class ConfigModuleFacade implements ConfigService, AutoCloseable {

    private CompilerConfig configuration;

    /**
     * Creates a new config module facade with default configuration.
     */
    public ConfigModuleFacade() {
        this.configuration = CompilerConfig.createDefault();
    }

    /**
     * Creates a new config module facade with a specific configuration.
     *
     * @param configuration the initial configuration
     */
    public ConfigModuleFacade(CompilerConfig configuration) {
        if (configuration == null) {
            this.configuration = CompilerConfig.createDefault();
        } else {
            this.configuration = configuration;
        }
    }

    @Override
    public CompilerConfig getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(CompilerConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        this.configuration = config;
    }

    @Override
    public CompilerConfig loadFromProject(Path projectDir) {
        if (projectDir == null) {
            throw new IllegalArgumentException("Project directory cannot be null");
        }
        if (!Files.isDirectory(projectDir)) {
            throw new IllegalArgumentException("Project directory is not a directory: " + projectDir);
        }

        // Try to load from module.json if it exists
        Path moduleJsonPath = projectDir.resolve("module.json");
        if (Files.exists(moduleJsonPath)) {
            return loadFromModuleJson(moduleJsonPath);
        }

        // Try to load from package.json if it exists
        Path packageJsonPath = projectDir.resolve("package.json");
        if (Files.exists(packageJsonPath)) {
            return loadFromPackageJson(packageJsonPath);
        }

        // No config file found, use defaults with project path
        CompilerConfig config = CompilerConfig.createDefault();
        config.setProjectPath(projectDir.toString());
        this.configuration = config;
        return config;
    }

    @Override
    public CompilerConfig loadFromProperties(Map<String, Object> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Properties cannot be null");
        }

        CompilerConfig config = new CompilerConfig();

        // Map properties to config fields
        if (properties.containsKey("projectPath")) {
            config.setProjectPath(properties.get("projectPath").toString());
        }
        if (properties.containsKey("buildPath")) {
            config.setBuildPath(properties.get("buildPath").toString());
        }
        if (properties.containsKey("sourcePath")) {
            config.setSourcePath(properties.get("sourcePath").toString());
        }
        if (properties.containsKey("compileMode")) {
            String modeStr = properties.get("compileMode").toString();
            config.setCompileMode(CompilerConfig.CompileMode.valueOf(modeStr));
        }
        if (properties.containsKey("partialUpdateMode")) {
            config.setPartialUpdateMode(Boolean.parseBoolean(properties.get("partialUpdateMode").toString()));
        }
        if (properties.containsKey("generateSourceMap")) {
            config.setGenerateSourceMap(Boolean.parseBoolean(properties.get("generateSourceMap").toString()));
        }
        if (properties.containsKey("pureJavaScript")) {
            config.setPureJavaScript(Boolean.parseBoolean(properties.get("pureJavaScript").toString()));
        }

        this.configuration = config;
        return config;
    }

    @Override
    public CompilerConfig createDefault() {
        CompilerConfig config = CompilerConfig.createDefault();
        this.configuration = config;
        return config;
    }

    @Override
    public boolean isValid() {
        if (configuration == null) {
            return false;
        }

        // Check required fields
        String projectPath = configuration.getProjectPath();
        if (projectPath == null || projectPath.isEmpty()) {
            return false;
        }

        // Check compile mode is set
        if (configuration.getCompileMode() == null) {
            return false;
        }

        return true;
    }

    /**
     * Loads configuration from a module.json file.
     *
     * @param moduleJsonPath the path to the module.json file
     * @return the loaded configuration
     */
    private CompilerConfig loadFromModuleJson(Path moduleJsonPath) {
        // For now, use default configuration
        // In production, this would parse the JSON file
        CompilerConfig config = CompilerConfig.createDefault();
        config.setProjectPath(moduleJsonPath.getParent().toString());
        this.configuration = config;
        return config;
    }

    /**
     * Loads configuration from a package.json file.
     *
     * @param packageJsonPath the path to the package.json file
     * @return the loaded configuration
     */
    private CompilerConfig loadFromPackageJson(Path packageJsonPath) {
        // For now, use default configuration
        // In production, this would parse the JSON file
        CompilerConfig config = CompilerConfig.createDefault();
        config.setProjectPath(packageJsonPath.getParent().toString());
        this.configuration = config;
        return config;
    }

    @Override
    public void close() {
        // ConfigModuleFacade doesn't hold resources that need cleanup
    }
}
