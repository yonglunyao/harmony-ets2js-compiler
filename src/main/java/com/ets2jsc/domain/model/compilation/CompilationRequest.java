package com.ets2jsc.domain.model.compilation;

import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CompilationException;

import java.nio.file.Path;
import java.util.List;

/**
 * Represents a request to compile source code.
 * Contains all information needed for a compilation operation.
 */
public class CompilationRequest {

    private final Path sourcePath;
    private final Path outputPath;
    private final CompilerConfig config;
    private final CompilationMode mode;

    /**
     * Compilation mode for the request.
     */
    public enum CompilationMode {
        SINGLE_FILE,
        BATCH,
        PROJECT
    }

    /**
     * Creates a new compilation request.
     *
     * @param sourcePath the path to the source file
     * @param outputPath the path to the output file
     * @param config the compiler configuration
     */
    public CompilationRequest(Path sourcePath, Path outputPath, CompilerConfig config) {
        this(sourcePath, outputPath, config, CompilationMode.SINGLE_FILE);
    }

    /**
     * Creates a new compilation request with a specific mode.
     *
     * @param sourcePath the path to the source file
     * @param outputPath the path to the output file
     * @param config the compiler configuration
     * @param mode the compilation mode
     */
    public CompilationRequest(Path sourcePath, Path outputPath, CompilerConfig config, CompilationMode mode) {
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
        this.config = config;
        this.mode = mode;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public CompilerConfig getConfig() {
        return config;
    }

    public CompilationMode getMode() {
        return mode;
    }
}
