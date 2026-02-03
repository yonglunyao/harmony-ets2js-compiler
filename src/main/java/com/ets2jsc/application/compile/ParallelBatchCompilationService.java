package com.ets2jsc.application.compile;

import com.ets2jsc.domain.model.compilation.CompilationResult.FileResult;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.domain.service.GeneratorService;
import com.ets2jsc.shared.exception.CompilationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parallel batch compilation service implementation.
 * Compiles source files concurrently using a thread pool.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class ParallelBatchCompilationService implements BatchCompilationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelBatchCompilationService.class);
    private static final int DEFAULT_THREAD_MULTIPLIER = 1;

    private final CompilationPipeline pipeline;
    private final ExecutorService executorService;
    private final int threadPoolSize;
    private final CompilerConfig config;
    private volatile boolean closed;

    /**
     * Creates a new parallel batch compilation service.
     *
     * @param pipeline  compilation pipeline to use for each file
     */
    public ParallelBatchCompilationService(CompilationPipeline pipeline) {
        this(pipeline, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Creates a new parallel batch compilation service with given configuration and thread pool size.
     *
     * @param pipeline  compilation pipeline to use for each file
     * @param threadPoolSize number of threads in pool (0 or negative uses CPU core count)
     */
    public ParallelBatchCompilationService(CompilationPipeline pipeline, int threadPoolSize) {
        if (pipeline == null) {
            throw new IllegalArgumentException("Pipeline cannot be null");
        }
        this.pipeline = pipeline;
        this.config = pipeline.getConfig();
        // Determine actual thread pool size
        int cpuCores = Runtime.getRuntime().availableProcessors();
        if (threadPoolSize > 0) {
            this.threadPoolSize = Math.min(threadPoolSize, cpuCores * DEFAULT_THREAD_MULTIPLIER);
        } else {
            this.threadPoolSize = cpuCores;
        }
        // Create thread pool with custom thread factory
        this.executorService = Executors.newFixedThreadPool(this.threadPoolSize,
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "ETS-Compiler-" + threadNumber.getAndIncrement());
                        t.setDaemon(false);
                        return t;
                    }
                });
        this.closed = false;
        LOGGER.info("Parallel batch compilation service initialized with {} threads", this.threadPoolSize);
    }

    @Override
    public com.ets2jsc.domain.model.compilation.CompilationResult compileBatch(List<Path> sourceFiles, Path outputDir) throws CompilationException {
        LOGGER.info("Compiling {} files in parallel mode, threads: {}", sourceFiles.size(), threadPoolSize);
        com.ets2jsc.domain.model.compilation.CompilationResult result =
                new com.ets2jsc.domain.model.compilation.CompilationResult();
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            result.markCompleted();
            for (Path file : sourceFiles) {
                result.addFileResult(file, null, "Failed to create output directory: " + e.getMessage(), e, 0);
            }
            return result;
        }
        // Submit compilation tasks
        List<Future<com.ets2jsc.domain.model.compilation.CompilationResult.FileResult>> futures = new ArrayList<>();
        for (Path sourceFile : sourceFiles) {
            Future<com.ets2jsc.domain.model.compilation.CompilationResult.FileResult> future = executorService.submit(
                    new CompilationTask(sourceFile, outputDir));
            futures.add(future);
        }
        // Collect results
        for (int i = 0; i < futures.size(); i++) {
            try {
                com.ets2jsc.domain.model.compilation.CompilationResult.FileResult fileResult = futures.get(i).get();
                result.addFileResult(file, null, "Failed to create output directory: " + e.getMessage(), e, 0));
            } catch (InterruptedException | ExecutionException e) {
                Path sourceFile = sourceFiles.get(i);
                result.addFileResult(sourceFile, null, "Task execution error: " + e.getMessage(), e, 0));
            }
        }
        result.markCompleted();
        return result;
    }

    @Override
    public com.ets2jsc.domain.model.compilation.CompilationResult compileBatchWithStructure(List<Path> sourceFiles, Path baseDir, Path outputDir) {
        com.ets2jsc.domain.model.compilation.CompilationResult result =
                new com.ets2jsc.domain.model.compilation.CompilationResult();
        int successCount = 0;
        int failureCount = 0;
        for (Path sourceFile : sourceFiles) {
            try {
                // Calculate relative path from base directory
                Path relativePath = baseDir.relativize(sourceFile);
                // Transform source file extension to .js
                String relativePathStr = relativePath.toString();
                String outputPathStr = relativePathStr
                        .replace(".ets", ".js")
                        .replace(".ts", ".js")
                        .replace(".tsx", ".js");
                Path outputPath = outputDir.resolve(outputPathStr);
                // Create parent directories if needed
                Path parentDir = outputPath.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                // Compile file using pipeline
                pipeline.execute(sourceFile, outputPath);
                result.addFileResult(sourceFile, com.ets2jsc.domain.model.compilation.CompilationResult.FileResult.success(
                        sourceFile, outputPath, 0));
                successCount++;
            } catch (Exception e) {
                result.addFileResult(sourceFile, null, "Compilation failed: " + e.getMessage(), e, 0);
                failureCount++;
            }
        }
        result.markCompleted();
        return new com.ets2jsc.domain.model.compilation.CompilationResult(
                result.getFileResults(),
                result.getTotalCount(),
                result.getSuccessCount(),
                result.getFailureCount());
    }

    @Override
    public CompilationMode getMode() {
        return CompilationMode.PARALLEL;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(Symbols.THREAD_TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        closed = true;
    }

    /**
     * Inner class for single file compilation task.
     */
    private class CompilationTask implements Callable<com.ets2jsc.domain.model.compilation.CompilationResult.FileResult> {

        private final Path sourceFile;
        private final Path outputDir;

        public CompilationTask(Path sourceFile, Path outputDir) {
            this.sourceFile = sourceFile;
            this.outputDir = outputDir;
        }

        @Override
        public com.ets2jsc.domain.model.compilation.CompilationResult.FileResult call() {
            long startTime = System.currentTimeMillis();
            String fileName = sourceFile.getFileName().toString();
            String outputName = fileName.replace(".ets", ".js").replace(".ts", ".js");
            Path outputPath = outputDir.resolve(outputName);
            try {
                pipeline.execute(sourceFile, outputPath);
                long duration = System.currentTimeMillis() - startTime;
                return com.ets2jsc.domain.model.compilation.CompilationResult.FileResult.success(
                        sourceFile, outputPath, duration);
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                return com.ets2jsc.domain.model.compilation.CompilationResult.FileResult.failure(
                        sourceFile, outputPath, "Compilation failed: " + e.getMessage(), duration);
            }
        }
    }
}
