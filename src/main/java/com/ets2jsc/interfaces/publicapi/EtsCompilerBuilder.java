package com.ets2jsc.interfaces.publicapi;

import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.interfaces.publicapi.model.CompilationMode;

import java.nio.file.Path;

/**
 * Builder for creating configured {@link EtsCompiler} instances.
 * <p>
 * This builder provides a fluent API for configuring the compiler
 * with custom settings. All configuration parameters are optional
 * and will use sensible defaults if not specified.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * EtsCompiler compiler = EtsCompiler.builder()
 *         .parallelMode(true)
 *         .threadCount(4)
 *         .projectPath(Path.of("/my/project"))
 *         .sourcePath("src/main/ets")
 *         .buildPath("build")
 *         .generateSourceMap(true)
 *         .build();
 * }</pre>
 *
 * @see EtsCompiler
 */
public class EtsCompilerBuilder {

    private final CompilerConfig config;
    private int threadCount = 1;
    private Boolean parallelMode;

    /**
     * Creates a new builder with default configuration.
     */
    public EtsCompilerBuilder() {
        this.config = CompilerConfig.createDefault();
    }

    /**
     * Sets the project path.
     *
     * @param projectPath the project root directory
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder projectPath(Path projectPath) {
        config.setProjectPath(projectPath != null ? projectPath.toString() : null);
        return this;
    }

    /**
     * Sets the project path.
     *
     * @param projectPath the project root directory as a string
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder projectPath(String projectPath) {
        config.setProjectPath(projectPath);
        return this;
    }

    /**
     * Sets the build output path.
     *
     * @param buildPath the build output directory
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder buildPath(String buildPath) {
        config.setBuildPath(buildPath);
        return this;
    }

    /**
     * Sets the source path.
     *
     * @param sourcePath the source directory
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder sourcePath(String sourcePath) {
        config.setSourcePath(sourcePath);
        return this;
    }

    /**
     * Enables or disables parallel compilation mode.
     * <p>
     * When enabled, multiple files are compiled concurrently
     * using the configured thread count.
     *
     * @param parallel true to enable parallel mode, false for sequential
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder parallelMode(boolean parallel) {
        this.parallelMode = parallel;
        return this;
    }

    /**
     * Sets the thread count for parallel compilation.
     * <p>
     * This setting only has effect when parallel mode is enabled.
     * The default value is 1.
     *
     * @param threadCount the number of threads to use (must be positive)
     * @return this builder for method chaining
     * @throws IllegalArgumentException if threadCount is less than 1
     */
    public EtsCompilerBuilder threadCount(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Thread count must be at least 1");
        }
        this.threadCount = threadCount;
        return this;
    }

    /**
     * Enables or disables source map generation.
     * <p>
     * When enabled, a .map file will be generated alongside each
     * output file for debugging purposes.
     *
     * @param generate true to generate source maps, false otherwise
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder generateSourceMap(boolean generate) {
        config.setGenerateSourceMap(generate);
        return this;
    }

    /**
     * Enables or disables partial update mode.
     * <p>
     * Partial update mode generates code optimized for
     * incremental UI updates.
     *
     * @param partialUpdate true to enable partial update mode
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder partialUpdateMode(boolean partialUpdate) {
        config.setPartialUpdateMode(partialUpdate);
        return this;
    }

    /**
     * Enables or disables TypeScript processing.
     *
     * @param process true to process TypeScript files
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder processTypeScript(boolean process) {
        config.setProcessTs(process);
        return this;
    }

    /**
     * Enables or disables lazy import.
     *
     * @param enable true to enable lazy import
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder enableLazyImport(boolean enable) {
        config.setEnableLazyImport(enable);
        return this;
    }

    /**
     * Enables or disables API validation.
     *
     * @param validate true to validate APIs
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder validateApi(boolean validate) {
        config.setValidateApi(validate);
        return this;
    }

    /**
     * Enables or disables preview mode.
     *
     * @param preview true to enable preview mode
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder previewMode(boolean preview) {
        config.setPreview(preview);
        return this;
    }

    /**
     * Enables or disables minification.
     *
     * @param minify true to minify output
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder minifyOutput(boolean minify) {
        config.setMinifyOutput(minify);
        return this;
    }

    /**
     * Enables or disables pure JavaScript mode.
     * In pure JS mode, no ArkUI runtime dependencies are generated.
     *
     * @param pure true for pure JS mode
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder pureJavaScript(boolean pure) {
        config.setPureJavaScript(pure);
        return this;
    }

    /**
     * Adds an entry point to the configuration.
     *
     * @param key the entry point key
     * @param value the entry point value
     * @return this builder for method chaining
     */
    public EtsCompilerBuilder addEntry(String key, String value) {
        config.addEntry(key, value);
        return this;
    }

    /**
     * Builds the configured compiler instance.
     *
     * @return a new {@link EtsCompiler} with the configured settings
     */
    public EtsCompiler build() {
        // Determine compilation mode
        CompilationMode effectiveMode = (parallelMode != null && parallelMode)
                || (parallelMode == null && threadCount > 1)
                ? CompilationMode.PARALLEL
                : CompilationMode.SEQUENTIAL;

        // Ensure thread count is at least 1
        int effectiveThreadCount = Math.max(1, threadCount);

        return new EtsCompiler(config, effectiveMode, effectiveThreadCount);
    }

    /**
     * Creates a builder pre-configured with settings from an existing configuration.
     *
     * @param config the base configuration
     * @return a new builder with pre-configured settings
     */
    public static EtsCompilerBuilder fromConfig(CompilerConfig config) {
        EtsCompilerBuilder builder = new EtsCompilerBuilder();
        // Copy all properties from the provided config
        if (config.getProjectPath() != null) {
            builder.config.setProjectPath(config.getProjectPath());
        }
        if (config.getBuildPath() != null) {
            builder.config.setBuildPath(config.getBuildPath());
        }
        if (config.getSourcePath() != null) {
            builder.config.setSourcePath(config.getSourcePath());
        }
        builder.config.setCompileMode(config.getCompileMode());
        builder.config.setPartialUpdateMode(config.isPartialUpdateMode());
        builder.config.setPreview(config.isPreview());
        builder.config.setGenerateSourceMap(config.isGenerateSourceMap());
        builder.config.setGenerateDeclarations(config.isGenerateDeclarations());
        builder.config.setMinifyOutput(config.isMinifyOutput());
        builder.config.setProcessTs(config.isProcessTs());
        builder.config.setEnableLazyImport(config.isEnableLazyImport());
        builder.config.setValidateApi(config.isValidateApi());
        builder.config.setPureJavaScript(config.isPureJavaScript());
        return builder;
    }
}
