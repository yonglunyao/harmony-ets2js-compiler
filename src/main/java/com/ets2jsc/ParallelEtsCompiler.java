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
 * 并行 ETS 编译器，支持多线程编译多个 ETS 文件。
 * 优化版：共享编译器实例，减少资源创建开销
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
    private final EtsCompiler sharedCompiler;  // 共享编译器实例，避免每个任务重复创建资源

    /**
     * 创建并行编译器
     * @param config 编译器配置
     * @param threadPoolSize 线程池大小，如果为 null 或小于等于 0，则使用 CPU 核心数
     */
    public ParallelEtsCompiler(CompilerConfig config, Integer threadPoolSize) {
        this.config = config;
        this.transformers = new ArrayList<>();
        this.codeGenerator = new CodeGenerator(config);
        this.jsWriter = new JsWriter();

        // 创建共享的编译器实例，避免每个任务重复创建资源
        this.sharedCompiler = new EtsCompiler(config);

        // 框定线程池大小 - 对于小文件任务，限制最大线程数以避免过度线程创建开销
        int cpuCores = Runtime.getRuntime().availableProcessors();
        if (threadPoolSize != null && threadPoolSize > 0) {
            // 对于小文件编译，建议线程数不超过 CPU 核心数的倍数
            this.threadPoolSize = Math.min(threadPoolSize, cpuCores * Symbols.MAX_THREAD_MULTIPLIER);
        } else {
            this.threadPoolSize = cpuCores;
        }

        // 创建线程池
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

        // 初始化转换器
        initializeTransformers();
    }

    /**
     * 使用默认线程池大小（CPU 核心数）创建并行编译器
     */
    public ParallelEtsCompiler(CompilerConfig config) {
        this(config, null);
    }

    /**
     * 初始化转换器管道
     */
    private void initializeTransformers() {
        transformers.add(new DecoratorTransformer(config.isPartialUpdateMode()));
        transformers.add(new BuildMethodTransformer(config.isPartialUpdateMode()));
        transformers.add(new ComponentTransformer());
    }

    /**
     * 并行编译多个 ETS 文件
     * @param sourceFiles 源文件列表
     * @param outputDir 输出目录
     * @return 编译结果
     */
    public CompilationResult compileParallel(List<Path> sourceFiles, Path outputDir) {
        CompilationResult result = new CompilationResult();

        // 确保输出目录存在
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            result.markCompleted();
            for (Path file : sourceFiles) {
                result.addFileResult(file, CompilationResult.FileResult.failure(
                    file, null, "无法创建输出目录: " + e.getMessage(), e, 0
                ));
            }
            return result;
        }

        // 提交编译任务
        List<Future<CompilationResult.FileResult>> futures = new ArrayList<>();
        for (Path sourceFile : sourceFiles) {
            Future<CompilationResult.FileResult> future = executorService.submit(
                new CompilationTask(sourceFile, outputDir, config, sharedCompiler)
            );
            futures.add(future);
        }

        // 收集结果
        for (int i = Symbols.INDEX_ZERO; i < futures.size(); i++) {
            try {
                CompilationResult.FileResult fileResult = futures.get(i).get();
                result.addFileResult(fileResult.getSourcePath(), fileResult);
            } catch (InterruptedException | ExecutionException e) {
                Path sourceFile = sourceFiles.get(i);
                result.addFileResult(sourceFile, CompilationResult.FileResult.failure(
                    sourceFile, null, "任务执行异常: " + e.getMessage(), e, 0
                ));
            }
        }

        result.markCompleted();
        return result;
    }

    /**
     * 关闭编译器，释放资源
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
     * 获取线程池大小
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * 编译任务 - 使用共享编译器实例
     */
    private class CompilationTask implements Callable<CompilationResult.FileResult> {
        private final Path sourcePath;
        private final Path outputDir;
        private final CompilerConfig config;
        private final EtsCompiler compiler;  // 使用共享编译器实例

        public CompilationTask(Path sourcePath, Path outputDir, CompilerConfig config, EtsCompiler compiler) {
            this.sourcePath = sourcePath;
            this.outputDir = outputDir;
            this.config = config;
            this.compiler = compiler;
        }

        @Override
        public CompilationResult.FileResult call() {
            long startTime = System.currentTimeMillis();

            // 确定输出路径
            String fileName = sourcePath.getFileName().toString();
            String outputName = fileName.replace(".ets", ".js").replace(".ts", ".js");
            Path outputPath = outputDir.resolve(outputName);

            try {
                // 使用共享的编译器实例进行编译
                compiler.compile(sourcePath, outputPath);
                long duration = System.currentTimeMillis() - startTime;
                return CompilationResult.FileResult.success(sourcePath, outputPath, duration);
            } catch (EtsCompiler.CompilationException e) {
                long duration = System.currentTimeMillis() - startTime;
                return CompilationResult.FileResult.failure(sourcePath, outputPath, e.getMessage(), e, duration);
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                return CompilationResult.FileResult.failure(sourcePath, outputPath, "未知错误: " + e.getMessage(), e, duration);
            }
        }
    }

    /**
     * 主方法用于命令行使用
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("用法: ParallelEtsCompiler <input-dir> <output-dir> [threads]");
            System.err.println("示例: ParallelEtsCompiler ./src ./build 8");
            System.exit(1);
        }

        Path inputDir = Path.of(args[0]);
        Path outputDir = Path.of(args[1]);

        // 线程数（可选）
        Integer threads = null;
        if (args.length >= 3) {
            try {
                threads = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("无效的线程数: " + args[2]);
                System.exit(1);
            }
        }

        // 创建配置
        CompilerConfig config = CompilerConfig.createDefault();

        // 创建并行编译器
        ParallelEtsCompiler compiler = new ParallelEtsCompiler(config, threads);

        System.out.println("=== ETS 并行编译器 ===");
        System.out.println("输入目录: " + inputDir);
        System.out.println("输出目录: " + outputDir);
        System.out.println("线程数: " + compiler.getThreadPoolSize());
        System.out.println();

        try {
            // 查找所有源文件
            List<Path> sourceFiles = findSourceFiles(inputDir);

            if (sourceFiles.isEmpty()) {
                System.out.println("未找到 ETS/TS 文件");
                compiler.shutdown();
                return;
            }

            System.out.println("找到 " + sourceFiles.size() + " 个文件");
            System.out.println("开始编译...");
            System.out.println();

            long startTime = System.currentTimeMillis();

            // 并行编译
            CompilationResult result = compiler.compileParallel(sourceFiles, outputDir);

            long totalDuration = System.currentTimeMillis() - startTime;

            // 打印结果
            System.out.println();
            System.out.println("=== 编译结果 ===");
            System.out.println(result.getSummary());

            if (!result.isAllSuccess()) {
                System.out.println();
                System.out.println("失败的文件:");
                for (CompilationResult.FileResult failure : result.getFailures()) {
                    System.out.println("  - " + failure.getSourcePath());
                    System.out.println("    错误: " + failure.getMessage());
                    if (failure.getError() != null) {
                        System.out.println("    详情: " + failure.getError().getMessage());
                    }
                }
            }

            // 显示性能信息
            System.out.println();
            System.out.println("=== 性能统计 ===");
            System.out.println("总耗时: " + totalDuration + "ms");
            System.out.println("平均每个文件: " + (totalDuration / result.getTotalCount()) + "ms");
            System.out.println("吞吐量: " + (result.getTotalCount() * 1000.0 / totalDuration) + " 文件/秒");

            // 返回适当的退出码
            compiler.shutdown();
            System.exit(result.isAllSuccess() ? 0 : 1);
        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
            compiler.shutdown();
            System.exit(1);
        }
    }

    /**
     * 查找目录中的所有 ETS/TypeScript 源文件
     */
    private static List<Path> findSourceFiles(Path dir) throws IOException {
        List<Path> sourceFiles = new ArrayList<>();
        if (Files.isDirectory(dir)) {
            Files.walk(dir)
                .filter(path -> path.toString().endsWith(".ets") ||
                               path.toString().endsWith(".ts"))
                .forEach(sourceFiles::add);
        }
        return sourceFiles;
    }
}
