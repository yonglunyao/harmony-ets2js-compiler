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
 * 结果类，用于记录多线程编译的结果。
 * Compilation result class for recording multi-threaded compilation results.
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
     * 获取总耗时（毫秒）
     * Get total duration in milliseconds
     */
    public long getDurationMs() {
        Instant end = endTime != null ? endTime : Instant.now();
        return Duration.between(startTime, end).toMillis();
    }

    /**
     * 获取成功数量
     */
    public int getSuccessCount() {
        return successCount.get();
    }

    /**
     * 获取失败数量
     */
    public int getFailureCount() {
        return failureCount.get();
    }

    /**
     * 获取跳过数量
     */
    public int getSkippedCount() {
        return skippedCount.get();
    }

    /**
     * 获取总文件数
     */
    public int getTotalCount() {
        return fileResults.size();
    }

    /**
     * 获取所有失败的文件
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
     * 是否全部成功
     */
    public boolean isAllSuccess() {
        return failureCount.get() == 0 && getTotalCount() > 0;
    }

    /**
     * 获取摘要信息
     */
    public String getSummary() {
        return String.format("编译完成 | 总计: %d | 成功: %d | 失败: %d | 跳过: %d | 耗时: %dms",
            getTotalCount(), getSuccessCount(), getFailureCount(), getSkippedCount(), getDurationMs());
    }

    /**
     * 文件编译结果
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
            return new FileResult(sourcePath, outputPath, Status.SUCCESS, "编译成功", null, durationMs);
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
     * 编译状态
     */
    public enum Status {
        SUCCESS,   // 成功
        FAILURE,   // 失败
        SKIPPED    // 跳过
    }
}
