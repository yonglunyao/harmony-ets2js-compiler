package com.ets2jsc.domain.model.compilation;

import lombok.Getter;

import java.nio.file.Path;

/**
 * Represents the output of a compilation operation.
 * Contains the generated JavaScript code and optionally the source map.
 */
@Getter
public class CompilationOutput {

    private final String javaScriptCode;
    private final String sourceMap;
    private final Path outputPath;
    private final Path sourceMapPath;

    /**
     * Creates a new compilation output.
     *
     * @param javaScriptCode the generated JavaScript code
     * @param outputPath the path where the output was written
     */
    public CompilationOutput(String javaScriptCode, Path outputPath) {
        this(javaScriptCode, null, outputPath, null);
    }

    /**
     * Creates a new compilation output with source map.
     *
     * @param javaScriptCode the generated JavaScript code
     * @param sourceMap the generated source map
     * @param outputPath the path where the output was written
     * @param sourceMapPath the path where the source map was written
     */
    public CompilationOutput(String javaScriptCode, String sourceMap, Path outputPath, Path sourceMapPath) {
        this.javaScriptCode = javaScriptCode;
        this.sourceMap = sourceMap;
        this.outputPath = outputPath;
        this.sourceMapPath = sourceMapPath;
    }

    /**
     * Checks if a source map was generated.
     *
     * @return true if a source map was generated, false otherwise
     */
    public boolean hasSourceMap() {
        return sourceMap != null;
    }
}
