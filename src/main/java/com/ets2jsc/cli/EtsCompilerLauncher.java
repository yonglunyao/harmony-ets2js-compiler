package com.ets2jsc.cli;

import com.ets2jsc.CompilationResult;
import com.ets2jsc.EtsCompiler;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.util.SourceFileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Command-line interface launcher for ETS to JS compiler.
 * Handles argument parsing, validation, and execution orchestration.
 */
public class EtsCompilerLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(EtsCompilerLauncher.class);

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
            LOGGER.error("Compilation failed: {}", e.getMessage());
            if (e.getCause() != null) {
                LOGGER.error("Cause: {}", e.getCause().getMessage());
            }
            return EXIT_ERROR;
        } catch (Exception e) {
            LOGGER.error("Compilation failed: {}", e.getMessage(), e);
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
            LOGGER.error("Unknown option: {}", mode);
            printUsage();
            return EXIT_ERROR;
        }

        // Validate input is a directory
        if (!Files.isDirectory(inputPath)) {
            LOGGER.error("Input path is not a directory: {}", inputPath);
            return EXIT_ERROR;
        }

        List<Path> sourceFiles = SourceFileFinder.findSourceFiles(inputPath);
        if (sourceFiles.isEmpty()) {
            System.out.println("No ETS/TS files found");
            return EXIT_SUCCESS;
        }

        System.out.println("Found " + sourceFiles.size() + " files");

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
        System.out.println("Using parallel compilation mode, threads: " + threads);

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
        System.out.println("Duration: " + duration + "ms");
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
        System.out.println("Compilation completed: " + inputPath + " -> " + outputPath);
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
                LOGGER.warn("Invalid thread count: {}, using default: {}", args[ARG_INDEX_THREADS], defaultThreads);
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
        System.out.println("=== Compilation Results ===");
        System.out.println(result.getSummary());
        System.out.println("Throughput: " + (result.getTotalCount() * 1000.0 / duration) + " files/sec");

        if (!result.isAllSuccess()) {
            System.out.println();
            System.out.println("Failed files:");
            for (CompilationResult.FileResult failure : result.getFailures()) {
                System.out.println("  - " + failure.getSourcePath());
                System.out.println("    Error: " + failure.getMessage());
            }
        }
    }

    /**
     * Prints usage information.
     */
    private static void printUsage() {
        System.err.println("Usage: EtsCompiler <input> <output> [mode] [threads]");
        System.err.println();
        System.err.println("Modes:");
        System.err.println("  (none)      - Compile single file");
        System.err.println("  " + MODE_BATCH + "   - Batch compile directory (sequential)");
        System.err.println("  " + MODE_PARALLEL + " - Batch compile directory (parallel)");
        System.err.println();
        System.err.println("Examples:");
        System.err.println("  EtsCompiler src/App.ets build/App.js");
        System.err.println("  EtsCompiler src/main/ets build/dist " + MODE_BATCH);
        System.err.println("  EtsCompiler src/main/ets build/dist " + MODE_PARALLEL);
        System.err.println("  EtsCompiler src/main/ets build/dist " + MODE_PARALLEL + " 8");
    }
}
