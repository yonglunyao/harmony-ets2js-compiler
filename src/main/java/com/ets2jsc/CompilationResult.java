package com.ets2jsc;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Result class for recording multi-threaded compilation results.
 */
public class CompilationResult {

    private final Instant startTime;
    private volatile Instant endTime;
    private final ConcurrentMap<Path, FileResult> fileResults;
    private final AtomicInteger successCount;
    private final AtomicInteger failureCount;
    private final AtomicInteger skippedCount;

    public CompilationResult() {
        this.startTime = Instant.now();
        this.fileResults = new ConcurrentHashMap<>();
        this.successCount = new AtomicInteger(0);
        this.failureCount = new AtomicInteger(0);
        this.skippedCount = new AtomicInteger(0);
    }

    /**
     * Add file compilation result
     */
    public void addFileResult(Path sourcePath, FileResult result) {
        fileResults.put(sourcePath, result);
        if (result.status == Status.SUCCESS) {
            successCount.incrementAndGet();
        } else if (result.status == Status.FAILURE) {
            failureCount.incrementAndGet();
        } else {
            skippedCount.incrementAndGet();
        }
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
     * Get all failed files
     */
    public List<FileResult> getFailures() {
        List<FileResult> failures = new ArrayList<>();
        for (FileResult result : fileResults.values()) {
            if (result.status == Status.FAILURE) {
                failures.add(result);
            }
        }
        return failures;
    }

    /**
     * Check if all compilations succeeded
     */
    public boolean isAllSuccess() {
        return failureCount.get() == 0 && getTotalCount() > 0;
    }

    /**
     * Get summary information
     */
    public String getSummary() {
        return String.format("Compilation completed | Total: %d | Success: %d | Failure: %d | Skipped: %d | Duration: %dms",
            getTotalCount(), getSuccessCount(), getFailureCount(), getSkippedCount(), getDurationMs());
    }

    /**
     * File compilation result
     */
    public static class FileResult {
        private final Path sourcePath;
        private final Path outputPath;
        private final Status status;
        private final String message;
        private final Throwable error;
        private final long durationMs;

        public FileResult(Path sourcePath, Path outputPath, Status status, String message, Throwable error, long durationMs) {
            this.sourcePath = sourcePath;
            this.outputPath = outputPath;
            this.status = status;
            this.message = message;
            this.error = error;
            this.durationMs = durationMs;
        }

        public static FileResult success(Path sourcePath, Path outputPath, long durationMs) {
            return new FileResult(sourcePath, outputPath, Status.SUCCESS, "Compilation succeeded", null, durationMs);
        }

        public static FileResult failure(Path sourcePath, Path outputPath, String message, Throwable error, long durationMs) {
            return new FileResult(sourcePath, outputPath, Status.FAILURE, message, error, durationMs);
        }

        public static FileResult skipped(Path sourcePath, String reason) {
            return new FileResult(sourcePath, null, Status.SKIPPED, reason, null, 0);
        }

        public Path getSourcePath() {
            return sourcePath;
        }

        public Path getOutputPath() {
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
