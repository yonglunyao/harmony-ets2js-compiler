package com.ets2jsc.config;

/**
 * Output configuration state for compilation.
 * <p>
 * This class encapsulates all output-related settings using the State pattern,
 * replacing multiple boolean flags with a coherent configuration object.
 * </p>
 *
 * @since 1.0
 */
public class OutputConfiguration {

    private final boolean generateSourceMap;
    private final boolean generateDeclarations;
    private final boolean minifyOutput;

    private OutputConfiguration(Builder builder) {
        this.generateSourceMap = builder.generateSourceMap;
        this.generateDeclarations = builder.generateDeclarations;
        this.minifyOutput = builder.minifyOutput;
    }

    /**
     * Returns a new builder for creating output configuration instances.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the default output configuration.
     *
     * @return the default configuration with source maps enabled
     */
    public static OutputConfiguration getDefault() {
        return builder()
                .generateSourceMap(true)
                .generateDeclarations(false)
                .minifyOutput(false)
                .build();
    }

    /**
     * Returns true if source map generation is enabled.
     *
     * @return true if source maps will be generated
     */
    public boolean isGenerateSourceMap() {
        return generateSourceMap;
    }

    /**
     * Returns true if TypeScript declaration file generation is enabled.
     *
     * @return true if declarations will be generated
     */
    public boolean isGenerateDeclarations() {
        return generateDeclarations;
    }

    /**
     * Returns true if output minification is enabled.
     *
     * @return true if output will be minified
     */
    public boolean isMinifyOutput() {
        return minifyOutput;
    }

    /**
     * Builder for creating OutputConfiguration instances.
     */
    public static final class Builder {
        private boolean generateSourceMap = true;
        private boolean generateDeclarations = false;
        private boolean minifyOutput = false;

        private Builder() {
            // Private constructor
        }

        /**
         * Sets whether source maps should be generated.
         *
         * @param generate true to generate source maps
         * @return this builder
         */
        public Builder generateSourceMap(boolean generate) {
            this.generateSourceMap = generate;
            return this;
        }

        /**
         * Sets whether TypeScript declarations should be generated.
         *
         * @param generate true to generate declarations
         * @return this builder
         */
        public Builder generateDeclarations(boolean generate) {
            this.generateDeclarations = generate;
            return this;
        }

        /**
         * Sets whether output should be minified.
         *
         * @param minify true to minify output
         * @return this builder
         */
        public Builder minifyOutput(boolean minify) {
            this.minifyOutput = minify;
            return this;
        }

        /**
         * Builds the output configuration.
         *
         * @return a new OutputConfiguration instance
         */
        public OutputConfiguration build() {
            return new OutputConfiguration(this);
        }
    }
}
