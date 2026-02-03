package com.ets2jsc.domain.model.compilation;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Domain model for compilation results.
 * Supports both single file and batch compilation results.
 */
public class CompilationResult {

    private final Instant startTime;
    private volatile Instant endTime;
    protected final ConcurrentMap<Path, FileResult> fileResults;
    protected final AtomicInteger successCount;
    protected final AtomicInteger failureCount;
    protected final AtomicInteger skippedCount;
    private final int copiedResourceCount;

    /**
     * Creates a new empty compilation result.
     */
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
     * Add file compilation result.
     *
     * @param sourcePath the source file path
     * @param result the file result
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
     * Mark compilation as completed.
     */
    public void markCompleted() {
        this.endTime = Instant.now();
    }

    /**
     * Get total duration in milliseconds.
     *
     * @return duration in milliseconds
     */
    public long getDurationMs() {
        Instant end = endTime != null ? endTime : Instant.now();
        return Duration.between(startTime, end).toMillis();
    }

    /**
     * Get success count.
     *
     * @return number of successful compilations
     */
    public int getSuccessCount() {
        return successCount.get();
    }

    /**
     * Get failure count.
     *
     * @return number of failed compilations
     */
    public int getFailureCount() {
        return failureCount.get();
    }

    /**
     * Get skipped count.
     *
     * @return number of skipped compilations
     */
    public int getSkippedCount() {
        return skippedCount.get();
    }

    /**
     * Get total file count.
     *
     * @return total number of files
     */
    public int getTotalCount() {
        return fileResults.size();
    }

    /**
     * Get copied resource file count (for project compilation).
     *
     * @return number of copied resource files
     */
    public int getCopiedResourceCount() {
        return copiedResourceCount;
    }

    /**
     * Get all failed files.
     *
     * @return list of failed file results
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
     * Check if all compilations succeeded.
     *
     * @return true if all succeeded, false otherwise
     */
    public boolean isAllSuccess() {
        return failureCount.get() == 0;
    }

    /**
     * Get all file results as a list.
     *
     * @return list of all file results
     */
    public List<FileResult> getFileResults() {
        return new ArrayList<>(fileResults.values());
    }

    /**
     * Get summary information.
     *
     * @return summary string
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
     * Check if compilation was successful (for single file compilation).
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return isAllSuccess() && getTotalCount() > 0;
    }

    /**
     * Get source path (for single file compilation).
     *
     * @return source path or null
     */
    public Path getSourcePath() {
        if (getTotalCount() == 1) {
            FileResult result = fileResults.values().iterator().next();
            return result.getSourcePath();
        }
        return null;
    }

    /**
     * Get output path (for single file compilation).
     *
     * @return output path or null
     */
    public Path getOutputPath() {
        if (getTotalCount() == 1) {
            FileResult result = fileResults.values().iterator().next();
            return result.getOutputPath();
        }
        return null;
    }

    /**
     * Creates a successful compilation result for a single file.
     *
     * @param sourcePath the source file path
     * @param outputPath the output file path
     * @param durationMs the compilation duration in milliseconds
     * @return a successful result
     */
    public static CompilationResult success(Path sourcePath, Path outputPath, long durationMs) {
        CompilationResult result = new CompilationResult();
        result.addFileResult(sourcePath, FileResult.success(sourcePath, outputPath, durationMs));
        result.markCompleted();
        return result;
    }

    /**
     * Creates a failed compilation result for a single file.
     *
     * @param sourcePath the source file path
     * @param outputPath the output file path
     * @param errorMessage the error message
     * @param durationMs the compilation duration in milliseconds
     * @return a failed result
     */
    public static CompilationResult failure(Path sourcePath, Path outputPath, String errorMessage, long durationMs) {
        CompilationResult result = new CompilationResult();
        result.addFileResult(sourcePath, FileResult.failure(sourcePath, outputPath, errorMessage, null, durationMs));
        result.markCompleted();
        return result;
    }

    /**
     * Creates a failed compilation result for a single file with an exception.
     *
     * @param sourcePath the source file path
     * @param outputPath the output file path
     * @param exception the exception that caused the failure
     * @param durationMs the compilation duration in milliseconds
     * @return a failed result
     */
    public static CompilationResult failure(Path sourcePath, Path outputPath, Exception exception, long durationMs) {
        CompilationResult result = new CompilationResult();
        result.addFileResult(sourcePath, FileResult.failure(sourcePath, outputPath,
                exception.getMessage(), exception, durationMs));
        result.markCompleted();
        return result;
    }

    /**
     * File compilation result.
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
     * Compilation status.
     */
    public enum Status {
        SUCCESS,
        FAILURE,
        SKIPPED
    }
}
