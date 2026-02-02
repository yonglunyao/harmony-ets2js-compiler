package com.ets2jsc.domain.model.compilation;

import java.nio.file.Path;
import java.util.List;

/**
 * Represents the result of a compilation operation.
 * Contains success/failure status and optional error information.
 */
public class CompilationResult {

    private final boolean success;
    private final Path sourcePath;
    private final Path outputPath;
    private final String errorMessage;
    private final long durationMs;
    private final Exception exception;

    private CompilationResult(boolean success, Path sourcePath, Path outputPath,
                             String errorMessage, long durationMs, Exception exception) {
        this.success = success;
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
        this.errorMessage = errorMessage;
        this.durationMs = durationMs;
        this.exception = exception;
    }

    /**
     * Creates a successful compilation result.
     *
     * @param sourcePath the source file path
     * @param outputPath the output file path
     * @param durationMs the compilation duration in milliseconds
     * @return a successful result
     */
    public static CompilationResult success(Path sourcePath, Path outputPath, long durationMs) {
        return new CompilationResult(true, sourcePath, outputPath, null, durationMs, null);
    }

    /**
     * Creates a failed compilation result.
     *
     * @param sourcePath the source file path
     * @param outputPath the output file path
     * @param errorMessage the error message
     * @param durationMs the compilation duration in milliseconds
     * @return a failed result
     */
    public static CompilationResult failure(Path sourcePath, Path outputPath, String errorMessage, long durationMs) {
        return new CompilationResult(false, sourcePath, outputPath, errorMessage, durationMs, null);
    }

    /**
     * Creates a failed compilation result with an exception.
     *
     * @param sourcePath the source file path
     * @param outputPath the output file path
     * @param exception the exception that caused the failure
     * @param durationMs the compilation duration in milliseconds
     * @return a failed result
     */
    public static CompilationResult failure(Path sourcePath, Path outputPath, Exception exception, long durationMs) {
        return new CompilationResult(false, sourcePath, outputPath, exception.getMessage(), durationMs, exception);
    }

    public boolean isSuccess() {
        return success;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public Exception getException() {
        return exception;
    }
}
