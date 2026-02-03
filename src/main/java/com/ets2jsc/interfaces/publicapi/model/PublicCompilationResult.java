package com.ets2jsc.interfaces.publicapi.model;

import lombok.Getter;

import com.ets2jsc.domain.model.compilation.CompilationResult;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Public API wrapper for compilation results.
 * <p>
 * This class provides a stable, simplified view of compilation results
 * for external API consumers. It wraps the internal {@link CompilationResult}
 * and exposes only the information needed by clients.
 *
 * @see com.ets2jsc.interfaces.publicapi.EtsCompiler
 */
public class PublicCompilationResult {

    private final CompilationResult internalResult;

    /**
     * Creates a new public compilation result wrapper.
     *
     * @param internalResult the internal compilation result
     */
    public PublicCompilationResult(CompilationResult internalResult) {
        this.internalResult = internalResult;
    }

    /**
     * Checks if the compilation was successful.
     * <p>
     * A compilation is considered successful if all source files
     * were compiled without errors.
     *
     * @return true if compilation succeeded, false otherwise
     */
    public boolean isSuccess() {
        return internalResult.isSuccess();
    }

    /**
     * Checks if all compilations were successful.
     * <p>
     * This is equivalent to {@link #isSuccess()}.
     *
     * @return true if all compilations succeeded, false otherwise
     */
    public boolean isAllSuccess() {
        return internalResult.isAllSuccess();
    }

    /**
     * Gets the total number of files processed.
     *
     * @return the total count of processed files
     */
    public int getTotalCount() {
        return internalResult.getTotalCount();
    }

    /**
     * Gets the number of successfully compiled files.
     *
     * @return the success count
     */
    public int getSuccessCount() {
        return internalResult.getSuccessCount();
    }

    /**
     * Gets the number of files that failed to compile.
     *
     * @return the failure count
     */
    public int getFailureCount() {
        return internalResult.getFailureCount();
    }

    /**
     * Gets the number of skipped files.
     *
     * @return the skipped count
     */
    public int getSkippedCount() {
        return internalResult.getSkippedCount();
    }

    /**
     * Gets the number of resource files copied.
     *
     * @return the copied resource count
     */
    public int getCopiedResourceCount() {
        return internalResult.getCopiedResourceCount();
    }

    /**
     * Gets the compilation duration in milliseconds.
     *
     * @return the duration in milliseconds
     */
    public long getDurationMs() {
        return internalResult.getDurationMs();
    }

    /**
     * Gets a list of all file results.
     *
     * @return an unmodifiable list of file results
     */
    public List<FileResult> getFileResults() {
        return internalResult.getFileResults().stream()
                .map(FileResult::new)
                .toList();
    }

    /**
     * Gets a list of failed compilations.
     *
     * @return an unmodifiable list of failure results
     */
    public List<FileResult> getFailures() {
        return internalResult.getFailures().stream()
                .map(FileResult::new)
                .toList();
    }

    /**
     * Gets a human-readable summary of the compilation result.
     *
     * @return a summary string
     */
    public String getSummary() {
        return internalResult.getSummary();
    }

    /**
     * Gets the internal compilation result.
     * <p>
     * This method provides access to the full internal result for advanced use cases.
     *
     * @return the internal compilation result
     */
    public CompilationResult getInternalResult() {
        return internalResult;
    }

    @Override
    public String toString() {
        return getSummary();
    }

    /**
     * Represents the result of compiling a single file.
     */
    @Getter
    public static class FileResult {
        private final CompilationResult.FileResult internalResult;

        FileResult(CompilationResult.FileResult internalResult) {
            this.internalResult = internalResult;
        }

        /**
         * Creates a file result from an internal result.
         *
         * @param internalResult the internal file result
         */
        public static FileResult fromInternal(CompilationResult.FileResult internalResult) {
            return new FileResult(internalResult);
        }

        /**
         * Gets the compilation status.
         *
         * @return the status
         */
        public Status getStatus() {
            return switch (internalResult.getStatus()) {
                case SUCCESS -> Status.SUCCESS;
                case FAILURE -> Status.FAILURE;
                case SKIPPED -> Status.SKIPPED;
            };
        }

        /**
         * Checks if this file compiled successfully.
         *
         * @return true if successful, false otherwise
         */
        public boolean isSuccess() {
            return internalResult.getStatus() == CompilationResult.Status.SUCCESS;
        }

        /**
         * Checks if this file compilation failed.
         *
         * @return true if failed, false otherwise
         */
        public boolean isFailure() {
            return internalResult.getStatus() == CompilationResult.Status.FAILURE;
        }

        /**
         * Checks if this file was skipped.
         *
         * @return true if skipped, false otherwise
         */
        public boolean isSkipped() {
            return internalResult.getStatus() == CompilationResult.Status.SKIPPED;
        }

        /**
         * Gets the source file path.
         *
         * @return source file path
         */
        public Path getSourcePath() {
            return internalResult.getSourcePath();
        }

        /**
         * Gets the output file path.
         *
         * @return output file path
         */
        public Path getOutputPath() {
            return internalResult.getOutputPath();
        }
    }

    /**
     * Compilation status for a single file.
     */
    public enum Status {
        /** Compilation completed successfully. */
        SUCCESS,
        /** Compilation failed. */
        FAILURE,
        /** File was skipped. */
        SKIPPED
    }
}
