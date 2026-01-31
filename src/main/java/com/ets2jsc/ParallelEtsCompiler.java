package com.ets2jsc;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.constant.Symbols;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.generator.JsWriter;
import com.ets2jsc.generator.SourceMapGenerator;
import com.ets2jsc.parser.AstBuilder;
import com.ets2jsc.transformer.AstTransformer;
import com.ets2jsc.transformer.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Parallel ETS compiler supporting multi-threaded compilation of multiple ETS files.
 * Optimized version: Shared compiler instance, reduced resource creation overhead.
 */
public class ParallelEtsCompiler {

    private final CompilerConfig config;
    private final List<AstTransformer> transformers;
    private final CodeGenerator codeGenerator;
    private final JsWriter jsWriter;
    private final ExecutorService executorService;
    private final int threadPoolSize;
    private final EtsCompiler sharedCompiler; // Shared compiler instance to avoid resource duplication

    /**
     * Create parallel compiler
     * @param config Compiler configuration
     * @param threadPoolSize Thread pool size, if null or <= 0, uses CPU core count
     */
    public ParallelEtsCompiler(CompilerConfig config, Integer threadPoolSize) {
        this.config = config;
        this.transformers = new ArrayList<>();
        this.codeGenerator = new CodeGenerator(config);
        this.jsWriter = new JsWriter();

        // Create shared compiler instance to avoid resource duplication
        this.sharedCompiler = new EtsCompiler(config);

        // Cap thread pool size - limit max threads for small file tasks
        int cpuCores = Runtime.getRuntime().availableProcessors();
        if (threadPoolSize != null && threadPoolSize > 0) {
            // For small file compilation, suggest threads not to exceed CPU core count multiplier
            this.threadPoolSize = Math.min(threadPoolSize, cpuCores * Symbols.MAX_THREAD_MULTIPLIER);
        } else {
            this.threadPoolSize = cpuCores;
        }

        /**
         * Create thread pool
         */
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
    }

    /**
     * Initialize transformers
     */
    private void initializeTransformers() {
        transformers.add(new DecoratorTransformer(config.isPartialUpdateMode()));
        transformers.add(new BuildMethodTransformer(config.isPartialUpdateMode()));
        transformers.add(new ComponentTransformer());
    }

    /**
     * Compile multiple ETS files in parallel
     * @param sourceFiles Source file list
     * @param outputDir Output directory
     * @return Compilation result
     */
    public CompilationResult compileParallel(List<Path> sourceFiles, Path outputDir) {
        System.out.println("Concurrent threads: " + threadPoolSize);
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
                new CompilationTask(sourceFile, outputDir, config, sharedCompiler)
            );
            futures.add(future);
        }

        // Collect results
        for (int i = Symbols.INDEX_ZERO; i < futures.size(); i++) {
            try {
                CompilationResult.FileResult fileResult = futures.get(i).get();
                result.addFileResult(fileResult.getSourcePath(), fileResult);
            } catch (InterruptedException | ExecutionException e) {
                Path sourceFile = sourceFiles.get(i);
                result.addFileResult(sourceFile, CompilationResult.FileResult.failure(
                    sourceFile, null, "Task execution error: " + e.getMessage(), e, 0
                ));
            }
        }

        result.markCompleted();
        return result;
    }

    /**
     * Shutdown compiler, release resources
     */
    public void shutdown() {
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
     * Get thread pool size
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * Inner class for single file compilation task
     */
    private static class CompilationTask implements Callable<CompilationResult.FileResult> {
        private final Path sourceFile;
        private final Path outputDir;
        private final CompilerConfig config;
        private final EtsCompiler compiler;

        public CompilationTask(Path sourceFile, Path outputDir, CompilerConfig config, EtsCompiler compiler) {
            this.sourceFile = sourceFile;
            this.outputDir = outputDir;
            this.config = config;
            this.compiler = compiler;
        }

        @Override
        public CompilationResult.FileResult call() {
            long startTime = System.currentTimeMillis();
            String fileName = sourceFile.getFileName().toString();
            String outputName = fileName.replace(".ets", ".js").replace(".ts", ".js");
            Path outputPath = outputDir.resolve(outputName);

            try {
                compiler.compile(sourceFile, outputPath);
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
