package com.ets2jsc.cli;

import com.ets2jsc.CompilationResult;
import com.ets2jsc.EtsCompiler;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.util.SourceFileFinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Command-line interface launcher for ETS to JS compiler.
 * Handles argument parsing, validation, and execution orchestration.
 */
public class EtsCompilerLauncher {

    // Command line constants
    private static final String MODE_BATCH = "--batch";
    private static final String MODE_PARALLEL = "--parallel";
    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_ERROR = 1;
    private static final int REQUIRED_ARGS_MIN = 2;
    private static final int ARG_INDEX_INPUT = 0;
    private static final int ARG_INDEX_OUTPUT = 1;
    private static final int ARG_INDEX_MODE = 2;
    private static final int ARG_INDEX_THREADS = 3;

    /**
     * Main entry point for command-line usage.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        int exitCode = execute(args);
        System.exit(exitCode);
    }

    /**
     * Executes the compiler with the given arguments.
     *
     * @param args command line arguments
     * @return exit code (0 for success, 1 for failure)
     */
    public static int execute(String[] args) {
        // Validate arguments
        if (args.length < REQUIRED_ARGS_MIN) {
            printUsage();
            return EXIT_ERROR;
        }

        CompilerConfig config = CompilerConfig.createDefault();
        EtsCompiler compiler = new EtsCompiler(config);

        try {
            Path inputPath = Path.of(args[ARG_INDEX_INPUT]);
            Path outputPath = Path.of(args[ARG_INDEX_OUTPUT]);

            if (args.length > ARG_INDEX_MODE) {
                return executeBatchCompilation(compiler, inputPath, outputPath, args);
            } else {
                return executeSingleFileCompilation(compiler, inputPath, outputPath);
            }
        } catch (EtsCompiler.CompilationException e) {
            System.err.println("编译失败: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("原因: " + e.getCause().getMessage());
            }
            return EXIT_ERROR;
        } catch (Exception e) {
            System.err.println("编译失败: " + e.getMessage());
            e.printStackTrace();
            return EXIT_ERROR;
        }
    }

    /**
     * Executes batch compilation (sequential or parallel).
     *
     * @param compiler the compiler instance
     * @param inputPath the input directory path
     * @param outputPath the output directory path
     * @param args command line arguments
     * @return exit code
     */
    private static int executeBatchCompilation(EtsCompiler compiler, Path inputPath,
            Path outputPath, String[] args) throws EtsCompiler.CompilationException, IOException {
        String mode = args[ARG_INDEX_MODE];

        if (!MODE_BATCH.equals(mode) && !MODE_PARALLEL.equals(mode)) {
            System.err.println("未知选项: " + mode);
            printUsage();
            return EXIT_ERROR;
        }

        // Validate input is a directory
        if (!Files.isDirectory(inputPath)) {
            System.err.println("错误: " + inputPath + " 不是目录");
            return EXIT_ERROR;
        }

        List<Path> sourceFiles = SourceFileFinder.findSourceFiles(inputPath);
        if (sourceFiles.isEmpty()) {
            System.out.println("未找到 ETS/TS 文件");
            return EXIT_SUCCESS;
        }

        System.out.println("找到 " + sourceFiles.size() + " 个文件");

        if (MODE_PARALLEL.equals(mode)) {
            return executeParallelCompilation(compiler, sourceFiles, outputPath, args);
        } else {
            return executeSequentialBatchCompilation(compiler, sourceFiles, outputPath);
        }
    }

    /**
     * Executes parallel batch compilation.
     *
     * @param compiler the compiler instance
     * @param sourceFiles list of source files to compile
     * @param outputPath the output directory path
     * @param args command line arguments
     * @return exit code
     */
    private static int executeParallelCompilation(EtsCompiler compiler, List<Path> sourceFiles,
            Path outputPath, String[] args) {
        int threads = parseThreadCount(args);
        System.out.println("使用并行编译模式，线程数: " + threads);

        long startTime = System.currentTimeMillis();
        CompilationResult result = compiler.compileBatchParallel(sourceFiles, outputPath, threads);
        long duration = System.currentTimeMillis() - startTime;

        printCompilationResults(result, duration);
        return result.isAllSuccess() ? EXIT_SUCCESS : EXIT_ERROR;
    }

    /**
     * Executes sequential batch compilation.
     *
     * @param compiler the compiler instance
     * @param sourceFiles list of source files to compile
     * @param outputPath the output directory path
     * @return exit code
     */
    private static int executeSequentialBatchCompilation(EtsCompiler compiler,
            List<Path> sourceFiles, Path outputPath) throws EtsCompiler.CompilationException, IOException {
        long startTime = System.currentTimeMillis();
        compiler.compileBatch(sourceFiles, outputPath);
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Compiled " + sourceFiles.size() + " files to " + outputPath);
        System.out.println("耗时: " + duration + "ms");
        return EXIT_SUCCESS;
    }

    /**
     * Executes single file compilation.
     *
     * @param compiler the compiler instance
     * @param inputPath the input file path
     * @param outputPath the output file path
     * @return exit code
     */
    private static int executeSingleFileCompilation(EtsCompiler compiler,
            Path inputPath, Path outputPath) throws EtsCompiler.CompilationException {
        compiler.compile(inputPath, outputPath);
        System.out.println("编译完成: " + inputPath + " -> " + outputPath);
        return EXIT_SUCCESS;
    }

    /**
     * Parses thread count from command line arguments.
     *
     * @param args command line arguments
     * @return thread count (defaults to available processors)
     */
    private static int parseThreadCount(String[] args) {
        int defaultThreads = Runtime.getRuntime().availableProcessors();
        if (args.length > ARG_INDEX_THREADS) {
            try {
                int parsed = Integer.parseInt(args[ARG_INDEX_THREADS]);
                if (parsed > 0) {
                    return parsed;
                }
            } catch (NumberFormatException e) {
                System.err.println("无效的线程数: " + args[ARG_INDEX_THREADS] + "，使用默认值: " + defaultThreads);
            }
        }
        return defaultThreads;
    }

    /**
     * Prints compilation results summary.
     *
     * @param result the compilation result
     * @param duration the compilation duration in milliseconds
     */
    private static void printCompilationResults(CompilationResult result, long duration) {
        System.out.println();
        System.out.println("=== 编译结果 ===");
        System.out.println(result.getSummary());
        System.out.println("吞吐量: " + (result.getTotalCount() * 1000.0 / duration) + " 文件/秒");

        if (!result.isAllSuccess()) {
            System.out.println();
            System.out.println("失败的文件:");
            for (CompilationResult.FileResult failure : result.getFailures()) {
                System.out.println("  - " + failure.getSourcePath());
                System.out.println("    错误: " + failure.getMessage());
            }
        }
    }

    /**
     * Prints usage information.
     */
    private static void printUsage() {
        System.err.println("用法: EtsCompiler <input> <output> [mode] [threads]");
        System.err.println();
        System.err.println("模式:");
        System.err.println("  (无)        - 编译单个文件");
        System.err.println("  " + MODE_BATCH + "   - 批量编译目录（顺序）");
        System.err.println("  " + MODE_PARALLEL + " - 批量编译目录（并行）");
        System.err.println();
        System.err.println("示例:");
        System.err.println("  EtsCompiler src/App.ets build/App.js");
        System.err.println("  EtsCompiler src/main/ets build/dist " + MODE_BATCH);
        System.err.println("  EtsCompiler src/main/ets build/dist " + MODE_PARALLEL);
        System.err.println("  EtsCompiler src/main/ets build/dist " + MODE_PARALLEL + " 8");
    }
}
