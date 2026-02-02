package com.ets2jsc.compiler;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unified result class for recording compilation results.
 * Supports both sequential and parallel compilation modes.
 */
public class CompilationResult {

    private final Instant startTime;
    private volatile Instant endTime;
    protected final ConcurrentMap<Path, FileResult> fileResults;
    protected final AtomicInteger successCount;
    protected final AtomicInteger failureCount;
    protected final AtomicInteger skippedCount;
    private final int copiedResourceCount;

    public CompilationResult() {
        this.startTime = Instant.now();
        this.fileResults = new ConcurrentHashMap<>();
        this.successCount = new AtomicInteger(0);
        this.failureCount = new AtomicInteger(0);
        this.skippedCount = new AtomicInteger(0);
        this.copiedResourceCount = 0;
    }

    /**
     * Constructor with pre-populated results and resource count.
     * Used by compileProject to return results with resource file information.
     *
     * @param results list of file results
     * @param totalCount total count
     * @param successCount success count
     * @param failureCount failure count
     * @param copiedResourceCount number of resource files copied
     */
    public CompilationResult(List<FileResult> results, int totalCount, int successCount,
                             int failureCount, int copiedResourceCount) {
        this.startTime = Instant.now();
        this.fileResults = new ConcurrentHashMap<>();
        this.successCount = new AtomicInteger(successCount);
        this.failureCount = new AtomicInteger(failureCount);
        this.skippedCount = new AtomicInteger(0);
        this.copiedResourceCount = copiedResourceCount;

        // Convert String-based results to Path-based for internal storage
        for (FileResult result : results) {
            String sourcePathStr = result.getSourcePathAsString();
            if (sourcePathStr != null) {
                Path sourcePath = Path.of(sourcePathStr);
                fileResults.put(sourcePath, result);
            }
        }
    }

    /**
     * Add file compilation result
     */
    public void addFileResult(Path sourcePath, FileResult result) {
        fileResults.put(sourcePath, result);
        if (result.getStatus() == Status.SUCCESS) {
            successCount.incrementAndGet();
        } else if (result.getStatus() == Status.FAILURE) {
            failureCount.incrementAndGet();
        } else {
            skippedCount.incrementAndGet();
        }
    }

    /**
     * Add file compilation result with String path key.
     * Used for compatibility with String-based FileResult implementations.
     */
    public void addFileResult(String sourcePathStr, FileResult result) {
        Path sourcePath = Path.of(sourcePathStr);
        addFileResult(sourcePath, result);
    }

    /**
     * Mark compilation as completed
     */
    public void markCompleted() {
        this.endTime = Instant.now();
    }

    /**
     * Get total duration in milliseconds
     */
    public long getDurationMs() {
        Instant end = endTime != null ? endTime : Instant.now();
        return Duration.between(startTime, end).toMillis();
    }

    /**
     * Get success count
     */
    public int getSuccessCount() {
        return successCount.get();
    }

    /**
     * Get failure count
     */
    public int getFailureCount() {
        return failureCount.get();
    }

    /**
     * Get skipped count
     */
    public int getSkippedCount() {
        return skippedCount.get();
    }

    /**
     * Get total file count
     */
    public int getTotalCount() {
        return fileResults.size();
    }

    /**
     * Get copied resource file count (for project compilation)
     */
    public int getCopiedResourceCount() {
        return copiedResourceCount;
    }

    /**
     * Get all failed files
     */
    public List<FileResult> getFailures() {
        List<FileResult> failures = new ArrayList<>();
        for (FileResult result : fileResults.values()) {
            if (result.getStatus() == Status.FAILURE) {
                failures.add(result);
            }
        }
        return failures;
    }

    /**
     * Check if all compilations succeeded
     */
    public boolean isAllSuccess() {
        return failureCount.get() == 0;
    }

    /**
     * Get summary information
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Total: %d | Success: %d | Failure: %d | Duration: %dms",
            getTotalCount(), getSuccessCount(), getFailureCount(), getDurationMs()));
        if (copiedResourceCount > 0) {
            sb.append(String.format(" | Resources copied: %d", copiedResourceCount));
        }
        return sb.toString();
    }

    /**
     * File compilation result
     */
    public static class FileResult {
        private final String sourcePath;
        private final String outputPath;
        private final Status status;
        private final String message;
        private final Throwable error;
        private final long durationMs;

        public FileResult(String sourcePath, String outputPath, String message, boolean success) {
            this.sourcePath = sourcePath;
            this.outputPath = outputPath;
            this.status = success ? Status.SUCCESS : Status.FAILURE;
            this.message = message;
            this.error = null;
            this.durationMs = 0;
        }

        public FileResult(Path sourcePath, Path outputPath, Status status, String message,
                         Throwable error, long durationMs) {
            this.sourcePath = sourcePath != null ? sourcePath.toString() : null;
            this.outputPath = outputPath != null ? outputPath.toString() : null;
            this.status = status;
            this.message = message;
            this.error = error;
            this.durationMs = durationMs;
        }

        /**
         * Constructor for Path-based results with status.
         */
        public FileResult(Path sourcePath, Path outputPath, String message, boolean success) {
            this.sourcePath = sourcePath != null ? sourcePath.toString() : null;
            this.outputPath = outputPath != null ? outputPath.toString() : null;
            this.status = success ? Status.SUCCESS : Status.FAILURE;
            this.message = message;
            this.error = null;
            this.durationMs = 0;
        }

        public static FileResult success(Path sourcePath, Path outputPath, long durationMs) {
            return new FileResult(sourcePath, outputPath, Status.SUCCESS,
                    "Compilation succeeded", null, durationMs);
        }

        public static FileResult failure(Path sourcePath, Path outputPath, String message,
                                       Throwable error, long durationMs) {
            return new FileResult(sourcePath, outputPath, Status.FAILURE, message, error, durationMs);
        }

        public static FileResult skipped(Path sourcePath, String reason) {
            return new FileResult(sourcePath, null, Status.SKIPPED, reason, null, 0);
        }

        /**
         * Gets the source path as a Path object.
         * Converts the stored String path back to Path for backward compatibility.
         *
         * @return the source path as Path, or null if not set
         */
        public Path getSourcePath() {
            return sourcePath != null ? Path.of(sourcePath) : null;
        }

        /**
         * Gets the source path as a String.
         *
         * @return the source path as String, or null if not set
         */
        public String getSourcePathAsString() {
            return sourcePath;
        }

        /**
         * Gets the output path as a Path object.
         * Converts the stored String path back to Path for backward compatibility.
         *
         * @return the output path as Path, or null if not set
         */
        public Path getOutputPath() {
            return outputPath != null ? Path.of(outputPath) : null;
        }

        /**
         * Gets the output path as a String.
         *
         * @return the output path as String, or null if not set
         */
        public String getOutputPathAsString() {
            return outputPath;
        }

        public Status getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getError() {
            return error;
        }

        public long getDurationMs() {
            return durationMs;
        }
    }

    /**
     * Compilation status
     */
    public enum Status {
        SUCCESS,
        FAILURE,
        SKIPPED
    }
}
