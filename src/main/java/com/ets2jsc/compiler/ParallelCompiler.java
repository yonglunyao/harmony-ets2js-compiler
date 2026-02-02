package com.ets2jsc.compiler;

import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.shared.constant.Symbols;
import com.ets2jsc.factory.TransformerFactory;
import com.ets2jsc.shared.exception.CompilationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Parallel compiler implementation.
 * Compiles source files concurrently using a thread pool.
 */
public class ParallelCompiler extends BaseCompiler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelCompiler.class);
    private static final int DEFAULT_THREAD_MULTIPLIER = 1;

    private final ExecutorService executorService;
    private final int threadPoolSize;

    /**
     * Creates a new parallel compiler with the given configuration.
     * Uses CPU core count as thread pool size.
     *
     * @param config the compiler configuration
     */
    public ParallelCompiler(CompilerConfig config) {
        this(config, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Creates a new parallel compiler with the given configuration and thread pool size.
     *
     * @param config the compiler configuration
     * @param threadPoolSize number of threads in the pool (0 or negative uses CPU core count)
     */
    public ParallelCompiler(CompilerConfig config, int threadPoolSize) {
        this(config, threadPoolSize, new com.ets2jsc.factory.DefaultTransformerFactory());
    }

    /**
     * Creates a new parallel compiler with the given configuration and transformer factory.
     *
     * @param config the compiler configuration
     * @param transformerFactory the factory for creating transformers
     */
    public ParallelCompiler(CompilerConfig config, TransformerFactory transformerFactory) {
        this(config, Runtime.getRuntime().availableProcessors(), transformerFactory);
    }

    /**
     * Creates a new parallel compiler with the given configuration, thread pool size, and transformer factory.
     *
     * @param config the compiler configuration
     * @param threadPoolSize number of threads in the pool (0 or negative uses CPU core count)
     * @param transformerFactory the factory for creating transformers
     */
    public ParallelCompiler(CompilerConfig config, int threadPoolSize, TransformerFactory transformerFactory) {
        super(config, transformerFactory);

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

        LOGGER.info("Parallel compiler initialized with {} threads", this.threadPoolSize);
    }

    @Override
    public CompilationResult compileBatch(List<Path> sourceFiles, Path outputDir) throws CompilationException {
        LOGGER.info("Compiling {} files in parallel mode, threads: {}", sourceFiles.size(), threadPoolSize);
        CompilationResult result = new CompilationResult();

        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            result.markCompleted();
            for (Path file : sourceFiles) {
                result.addFileResult(file, CompilationResult.FileResult.failure(
                    file, null, "Failed to create output directory: " + e.getMessage(), e, 0
                ));
            }
            return result;
        }

        // Submit compilation tasks
        List<Future<CompilationResult.FileResult>> futures = new ArrayList<>();
        for (Path sourceFile : sourceFiles) {
            Future<CompilationResult.FileResult> future = executorService.submit(
                    new CompilationTask(sourceFile, outputDir));
            futures.add(future);
        }

        // Collect results
        for (int i = 0; i < futures.size(); i++) {
            try {
                CompilationResult.FileResult fileResult = futures.get(i).get();
                result.addFileResult(fileResult.getSourcePath(), fileResult);
            } catch (InterruptedException | ExecutionException e) {
                Path sourceFile = sourceFiles.get(i);
                result.addFileResult(sourceFile, CompilationResult.FileResult.failure(
                        sourceFile, null, "Task execution error: " + e.getMessage(), e, 0));
            }
        }

        result.markCompleted();
        return result;
    }

    @Override
    public CompilationMode getMode() {
        return CompilationMode.PARALLEL;
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(Symbols.THREAD_TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Gets the thread pool size.
     *
     * @return the thread pool size
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * Inner class for single file compilation task.
     */
    private class CompilationTask implements Callable<CompilationResult.FileResult> {

        private final Path sourceFile;
        private final Path outputDir;

        public CompilationTask(Path sourceFile, Path outputDir) {
            this.sourceFile = sourceFile;
            this.outputDir = outputDir;
        }

        @Override
        public CompilationResult.FileResult call() {
            long startTime = System.currentTimeMillis();
            String fileName = sourceFile.getFileName().toString();
            String outputName = fileName.replace(".ets", ".js").replace(".ts", ".js");
            Path outputPath = outputDir.resolve(outputName);

            try {
                compile(sourceFile, outputPath);
                long duration = System.currentTimeMillis() - startTime;
                return CompilationResult.FileResult.success(sourceFile, outputPath, duration);
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                return CompilationResult.FileResult.failure(sourceFile, outputPath,
                        "Compilation failed: " + e.getMessage(), e, duration);
            }
        }
    }
}
