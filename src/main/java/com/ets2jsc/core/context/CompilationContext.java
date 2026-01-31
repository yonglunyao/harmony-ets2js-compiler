package com.ets2jsc.core.context;

import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.constant.Symbols;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context object for the compilation process.
 * Holds configuration, state, and shared utilities across the compilation pipeline.
 * <p>
 * This context is created at the start of compilation and passed through all stages
 * of the compilation process (parsing, transformation, code generation).
 */
public class CompilationContext {

    private final CompilerConfig config;
    private final Path sourcePath;
    private final Path outputPath;
    private final Map<String, Object> attributes;
    private final List<String> warnings;
    private final List<String> errors;
    private final long startTime;

    /**
     * Creates a new compilation context.
     *
     * @param config the compiler configuration
     * @param sourcePath the source file path
     * @param outputPath the output file path
     */
    public CompilationContext(CompilerConfig config, Path sourcePath, Path outputPath) {
        this.config = config != null ? config : CompilerConfig.createDefault();
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
        this.attributes = new HashMap<>();
        this.warnings = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Creates a new compilation context with default configuration.
     *
     * @param sourcePath the source file path
     * @param outputPath the output file path
     */
    public CompilationContext(Path sourcePath, Path outputPath) {
        this(CompilerConfig.createDefault(), sourcePath, outputPath);
    }

    /**
     * Gets the compiler configuration.
     *
     * @return the compiler configuration
     */
    public CompilerConfig getConfig() {
        return config;
    }

    /**
     * Gets the source file path.
     *
     * @return the source file path
     */
    public Path getSourcePath() {
        return sourcePath;
    }

    /**
     * Gets the output file path.
     *
     * @return the output file path
     */
    public Path getOutputPath() {
        return outputPath;
    }

    /**
     * Gets the source file name.
     *
     * @return the source file name
     */
    public String getSourceFileName() {
        if (sourcePath != null) {
            String fileName = sourcePath.getFileName().toString();
            // Remove extension
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                return fileName.substring(0, dotIndex);
            }
            return fileName;
        }
        return Symbols.DEFAULT_KEYWORD;
    }

    /**
     * Checks if pure JavaScript mode is enabled.
     *
     * @return true if pure JavaScript mode is enabled
     */
    public boolean isPureJavaScript() {
        return config.isPureJavaScript();
    }

    /**
     * Checks if partial update mode is enabled.
     *
     * @return true if partial update mode is enabled
     */
    public boolean isPartialUpdateMode() {
        return config.isPartialUpdateMode();
    }

    /**
     * Checks if source map generation is enabled.
     *
     * @return true if source map generation is enabled
     */
    public boolean isGenerateSourceMap() {
        return config.isGenerateSourceMap();
    }

    /**
     * Sets an attribute value in the context.
     *
     * @param key the attribute key
     * @param value the attribute value
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Gets an attribute value from the context.
     *
     * @param key the attribute key
     * @return the attribute value, or null if not found
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Gets an attribute value from the context with a default value.
     *
     * @param key the attribute key
     * @param defaultValue the default value
     * @return the attribute value, or the default if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, T defaultValue) {
        Object value = attributes.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    /**
     * Checks if an attribute exists in the context.
     *
     * @param key the attribute key
     * @return true if the attribute exists
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * Removes an attribute from the context.
     *
     * @param key the attribute key
     * @return the removed value, or null if not found
     */
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    /**
     * Adds a warning message.
     *
     * @param warning the warning message
     */
    public void addWarning(String warning) {
        warnings.add(warning);
    }

    /**
     * Adds an error message.
     *
     * @param error the error message
     */
    public void addError(String error) {
        errors.add(error);
    }

    /**
     * Gets all warnings.
     *
     * @return a list of warnings
     */
    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }

    /**
     * Gets all errors.
     *
     * @return a list of errors
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Checks if there are any warnings.
     *
     * @return true if there are warnings
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Checks if there are any errors.
     *
     * @return true if there are errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Clears all warnings and errors.
     */
    public void clearDiagnostics() {
        warnings.clear();
        errors.clear();
    }

    /**
     * Gets the elapsed time since context creation in milliseconds.
     *
     * @return the elapsed time in milliseconds
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Creates a summary of this compilation context.
     *
     * @return a summary string
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Source: ").append(sourcePath != null ? sourcePath : "N/A").append("\n");
        sb.append("Output: ").append(outputPath != null ? outputPath : "N/A").append("\n");
        sb.append("Warnings: ").append(warnings.size()).append("\n");
        sb.append("Errors: ").append(errors.size()).append("\n");
        sb.append("Time: ").append(getElapsedTime()).append("ms");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "CompilationContext{" +
                "sourcePath=" + sourcePath +
                ", outputPath=" + outputPath +
                ", warnings=" + warnings.size() +
                ", errors=" + errors.size() +
                '}';
    }
}
