package com.ets2jsc.config;

import com.ets2jsc.config.CompilationMode;
import com.ets2jsc.config.OutputConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder for creating {@link CompilerConfig} instances.
 * <p>
 * This builder provides a fluent API for configuring the compiler,
 * making configuration code more readable and maintainable.
 * </p>
 *
 * @since 1.0
 */
public class CompilerConfigBuilder {

    private String projectPath;
    private String buildPath;
    private String sourcePath;
    private CompilerConfig.CompileMode compileMode = CompilerConfig.CompileMode.MODULE_JSON;
    private CompilationMode compilationMode = CompilationMode.PARTIAL_UPDATE;
    private boolean isPreview = false;
    private OutputConfiguration outputConfiguration = OutputConfiguration.getDefault();
    private boolean processTs = true;
    private boolean enableLazyImport = false;
    private boolean validateApi = true;
    private boolean pureJavaScript = false;
    private Map<String, String> entryObj = new HashMap<>();

    private CompilerConfigBuilder() {
        // Private constructor
    }

    /**
     * Returns a new builder instance.
     *
     * @return a new builder
     */
    public static CompilerConfigBuilder builder() {
        return new CompilerConfigBuilder();
    }

    /**
     * Returns a new builder with default configuration.
     *
     * @return a new builder with defaults
     */
    public static CompilerConfigBuilder withDefaults() {
        CompilerConfig defaultConfig = CompilerConfig.createDefault();
        return new CompilerConfigBuilder()
                .projectPath(defaultConfig.getProjectPath())
                .buildPath(defaultConfig.getBuildPath())
                .sourcePath(defaultConfig.getSourcePath());
    }

    /**
     * Sets the project path.
     *
     * @param path the project path
     * @return this builder
     */
    public CompilerConfigBuilder projectPath(String path) {
        this.projectPath = path;
        return this;
    }

    /**
     * Sets the project path as a Path object.
     *
     * @param path the project path
     * @return this builder
     */
    public CompilerConfigBuilder projectPath(Path path) {
        this.projectPath = path != null ? path.toString() : null;
        return this;
    }

    /**
     * Sets the build path.
     *
     * @param path the build path
     * @return this builder
     */
    public CompilerConfigBuilder buildPath(String path) {
        this.buildPath = path;
        return this;
    }

    /**
     * Sets the build path as a Path object.
     *
     * @param path the build path
     * @return this builder
     */
    public CompilerConfigBuilder buildPath(Path path) {
        this.buildPath = path != null ? path.toString() : null;
        return this;
    }

    /**
     * Sets the source path.
     *
     * @param path the source path
     * @return this builder
     */
    public CompilerConfigBuilder sourcePath(String path) {
        this.sourcePath = path;
        return this;
    }

    /**
     * Sets the source path as a Path object.
     *
     * @param path the source path
     * @return this builder
     */
    public CompilerConfigBuilder sourcePath(Path path) {
        this.sourcePath = path != null ? path.toString() : null;
        return this;
    }

    /**
     * Sets the compile mode.
     *
     * @param mode the compile mode
     * @return this builder
     */
    public CompilerConfigBuilder compileMode(CompilerConfig.CompileMode mode) {
        this.compileMode = mode;
        return this;
    }

    /**
     * Sets the compilation mode (partial update or full render).
     *
     * @param mode the compilation mode
     * @return this builder
     */
    public CompilerConfigBuilder compilationMode(CompilationMode mode) {
        this.compilationMode = mode;
        return this;
    }

    /**
     * Sets whether partial update mode is enabled.
     *
     * @param partialUpdate true for partial update mode
     * @return this builder
     */
    public CompilerConfigBuilder partialUpdateMode(boolean partialUpdate) {
        this.compilationMode = CompilationMode.fromBoolean(partialUpdate);
        return this;
    }

    /**
     * Sets whether preview mode is enabled.
     *
     * @param preview true for preview mode
     * @return this builder
     */
    public CompilerConfigBuilder isPreview(boolean preview) {
        this.isPreview = preview;
        return this;
    }

    /**
     * Sets the output configuration.
     *
     * @param config the output configuration
     * @return this builder
     */
    public CompilerConfigBuilder outputConfiguration(OutputConfiguration config) {
        this.outputConfiguration = config;
        return this;
    }

    /**
     * Sets whether source maps should be generated.
     *
     * @param generate true to generate source maps
     * @return this builder
     */
    public CompilerConfigBuilder generateSourceMap(boolean generate) {
        if (this.outputConfiguration == null) {
            this.outputConfiguration = OutputConfiguration.getDefault();
        }
        this.outputConfiguration = OutputConfiguration.builder()
                .generateSourceMap(generate)
                .generateDeclarations(this.outputConfiguration.isGenerateDeclarations())
                .minifyOutput(this.outputConfiguration.isMinifyOutput())
                .build();
        return this;
    }

    /**
     * Sets whether TypeScript declarations should be generated.
     *
     * @param generate true to generate declarations
     * @return this builder
     */
    public CompilerConfigBuilder generateDeclarations(boolean generate) {
        if (this.outputConfiguration == null) {
            this.outputConfiguration = OutputConfiguration.getDefault();
        }
        this.outputConfiguration = OutputConfiguration.builder()
                .generateSourceMap(this.outputConfiguration.isGenerateSourceMap())
                .generateDeclarations(generate)
                .minifyOutput(this.outputConfiguration.isMinifyOutput())
                .build();
        return this;
    }

    /**
     * Sets whether output should be minified.
     *
     * @param minify true to minify output
     * @return this builder
     */
    public CompilerConfigBuilder minifyOutput(boolean minify) {
        if (this.outputConfiguration == null) {
            this.outputConfiguration = OutputConfiguration.getDefault();
        }
        this.outputConfiguration = OutputConfiguration.builder()
                .generateSourceMap(this.outputConfiguration.isGenerateSourceMap())
                .generateDeclarations(this.outputConfiguration.isGenerateDeclarations())
                .minifyOutput(minify)
                .build();
        return this;
    }

    /**
     * Sets whether TypeScript files should be processed.
     *
     * @param process true to process TypeScript files
     * @return this builder
     */
    public CompilerConfigBuilder processTs(boolean process) {
        this.processTs = process;
        return this;
    }

    /**
     * Sets whether lazy import is enabled.
     *
     * @param enable true to enable lazy import
     * @return this builder
     */
    public CompilerConfigBuilder enableLazyImport(boolean enable) {
        this.enableLazyImport = enable;
        return this;
    }

    /**
     * Sets whether API validation is enabled.
     *
     * @param enable true to enable API validation
     * @return this builder
     */
    public CompilerConfigBuilder validateApi(boolean enable) {
        this.validateApi = enable;
        return this;
    }

    /**
     * Sets whether pure JavaScript should be generated.
     *
     * @param pure true to generate pure JS
     * @return this builder
     */
    public CompilerConfigBuilder pureJavaScript(boolean pure) {
        this.pureJavaScript = pure;
        return this;
    }

    /**
     * Adds an entry point to the configuration.
     *
     * @param key the entry point key
     * @param value the entry point value
     * @return this builder
     */
    public CompilerConfigBuilder addEntry(String key, String value) {
        if (this.entryObj == null) {
            this.entryObj = new HashMap<>();
        }
        this.entryObj.put(key, value);
        return this;
    }

    /**
     * Sets all entry points for the configuration.
     *
     * @param entries the map of entry points
     * @return this builder
     */
    public CompilerConfigBuilder entries(Map<String, String> entries) {
        if (entries != null) {
            this.entryObj = new HashMap<>(entries);
        }
        return this;
    }

    /**
     * Builds the compiler configuration.
     *
     * @return a new CompilerConfig instance
     */
    public CompilerConfig build() {
        CompilerConfig config = new CompilerConfig();

        config.setProjectPath(projectPath);
        config.setBuildPath(buildPath);
        config.setSourcePath(sourcePath);
        config.setCompileMode(compileMode);
        config.setPartialUpdateMode(compilationMode.isPartialUpdateMode());
        config.setPreview(isPreview);

        if (outputConfiguration != null) {
            config.setGenerateSourceMap(outputConfiguration.isGenerateSourceMap());
            config.setGenerateDeclarations(outputConfiguration.isGenerateDeclarations());
            config.setMinifyOutput(outputConfiguration.isMinifyOutput());
        }

        config.setProcessTs(processTs);
        config.setEnableLazyImport(enableLazyImport);
        config.setValidateApi(validateApi);
        config.setPureJavaScript(pureJavaScript);

        if (entryObj != null && !entryObj.isEmpty()) {
            config.setEntryObj(new HashMap<>(entryObj));
        }

        return config;
    }
}
