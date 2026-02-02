package com.ets2jsc.cli;

import com.ets2jsc.compiler.CompilerFactory;
import com.ets2jsc.compiler.ICompiler;
import com.ets2jsc.compiler.CompilationResult;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.exception.CompilationException;
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
    private static final String MODE_PROJECT = "--project";
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

        try {
            Path inputPath = Path.of(args[ARG_INDEX_INPUT]);
            Path outputPath = Path.of(args[ARG_INDEX_OUTPUT]);

            if (args.length > ARG_INDEX_MODE) {
                return executeBatchCompilation(inputPath, outputPath, args);
            } else {
                return executeSingleFileCompilation(inputPath, outputPath, config);
            }
        } catch (CompilationException e) {
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
     * Executes single file compilation.
     *
     * @param inputPath the input file path
     * @param outputPath the output file path
     * @param config the compiler configuration
     * @return exit code
     */
    private static int executeSingleFileCompilation(Path inputPath, Path outputPath, CompilerConfig config)
            throws CompilationException {
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            compiler.compile(inputPath, outputPath);
            System.out.println("Compilation completed: " + inputPath + " -> " + outputPath);
            return EXIT_SUCCESS;
        }
    }

    /**
     * Executes batch compilation (sequential, parallel, or project mode).
     *
     * @param inputPath the input directory path
     * @param outputPath the output directory path
     * @param args command line arguments
     * @return exit code
     */
    private static int executeBatchCompilation(Path inputPath, Path outputPath, String[] args)
            throws CompilationException {
        String mode = args[ARG_INDEX_MODE];

        if (!MODE_BATCH.equals(mode) && !MODE_PARALLEL.equals(mode) && !MODE_PROJECT.equals(mode)) {
            LOGGER.error("Unknown option: {}", mode);
            printUsage();
            return EXIT_ERROR;
        }

        // Validate input is a directory
        if (!Files.isDirectory(inputPath)) {
            LOGGER.error("Input path is not a directory: {}", inputPath);
            return EXIT_ERROR;
        }

        List<Path> sourceFiles;
        try {
            sourceFiles = SourceFileFinder.findSourceFiles(inputPath);
        } catch (IOException e) {
            throw new CompilationException("Failed to find source files: " + inputPath, e);
        }
        if (sourceFiles.isEmpty()) {
            System.out.println("No ETS/TS files found");
            return EXIT_SUCCESS;
        }

        System.out.println("Found " + sourceFiles.size() + " files");

        // Determine compilation mode
        ICompiler.CompilationMode compilationMode;
        if (MODE_PROJECT.equals(mode)) {
            compilationMode = ICompiler.CompilationMode.SEQUENTIAL;
            return executeProjectCompilation(inputPath, outputPath, compilationMode);
        }

        compilationMode = MODE_PARALLEL.equals(mode)
                ? ICompiler.CompilationMode.PARALLEL
                : ICompiler.CompilationMode.SEQUENTIAL;

        return executeDirectoryCompilation(inputPath, outputPath, sourceFiles, compilationMode, args);
    }

    /**
     * Executes directory compilation with specified mode.
     *
     * @param inputPath the input directory path
     * @param outputPath the output directory path
     * @param sourceFiles list of source files to compile
     * @param mode the compilation mode
     * @param args command line arguments
     * @return exit code
     */
    private static int executeDirectoryCompilation(Path inputPath, Path outputPath,
            List<Path> sourceFiles, ICompiler.CompilationMode mode, String[] args)
            throws CompilationException {
        try (ICompiler compiler = createCompilerForMode(mode, args)) {
            long startTime = System.currentTimeMillis();
            CompilationResult result = compiler.compileBatch(sourceFiles, outputPath);
            long duration = System.currentTimeMillis() - startTime;

            printCompilationResults(result, duration, mode);
            return result.isAllSuccess() ? EXIT_SUCCESS : EXIT_ERROR;
        }
    }

    /**
     * Executes project compilation with directory structure preservation.
     *
     * @param inputPath the input project directory
     * @param outputPath the output directory
     * @param mode the compilation mode
     * @return exit code
     */
    private static int executeProjectCompilation(Path inputPath, Path outputPath,
            ICompiler.CompilationMode mode) throws CompilationException {
        System.out.println("Compiling project: " + inputPath);
        System.out.println("Output directory: " + outputPath);
        System.out.println("Mode: " + mode + ", preserve directory structure, copy resources");

        try (ICompiler compiler = CompilerFactory.createCompiler(CompilerConfig.createDefault(), mode)) {
            long startTime = System.currentTimeMillis();
            CompilationResult result = compiler.compileProject(inputPath, outputPath, true);
            long duration = System.currentTimeMillis() - startTime;

            System.out.println();
            System.out.println("=== Project Compilation Results ===");
            System.out.println(result.getSummary());
            System.out.println("Duration: " + duration + "ms");

            if (!result.isAllSuccess()) {
                System.out.println();
                System.out.println("Some files failed to compile.");
                return EXIT_ERROR;
            }

            return EXIT_SUCCESS;
        }
    }

    /**
     * Creates a compiler for the specified mode with optional thread count.
     *
     * @param mode the compilation mode
     * @param args command line arguments (may contain thread count)
     * @return a new compiler instance
     */
    private static ICompiler createCompilerForMode(ICompiler.CompilationMode mode, String[] args) {
        CompilerConfig config = CompilerConfig.createDefault();

        if (mode == ICompiler.CompilationMode.PARALLEL && args.length > ARG_INDEX_THREADS) {
            int threads = parseThreadCount(args);
            return CompilerFactory.createParallelCompiler(config, threads);
        }

        return CompilerFactory.createCompiler(config, mode);
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
     * @param mode the compilation mode
     */
    private static void printCompilationResults(CompilationResult result, long duration, ICompiler.CompilationMode mode) {
        System.out.println();
        System.out.println("=== Compilation Results ===");
        System.out.println("Mode: " + mode);
        System.out.println(result.getSummary());
        System.out.println("Throughput: " + (result.getTotalCount() * 1000.0 / duration) + " files/sec");

        if (!result.isAllSuccess()) {
            System.out.println();
            System.out.println("Failed files:");
            for (CompilationResult.FileResult failure : result.getFailures()) {
                System.out.println("  - " + failure.getSourcePathAsString());
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
        System.err.println("  " + MODE_BATCH + "    - Batch compile directory (sequential, flat output)");
        System.err.println("  " + MODE_PARALLEL + " - Batch compile directory (parallel, flat output)");
        System.err.println("  " + MODE_PROJECT + "  - Compile project (preserve structure, copy resources)");
        System.err.println();
        System.err.println("Examples:");
        System.err.println("  EtsCompiler src/App.ets build/App.js");
        System.err.println("  EtsCompiler src/main/ets build/dist " + MODE_BATCH);
        System.err.println("  EtsCompiler src/main/ets build/dist " + MODE_PARALLEL);
        System.err.println("  EtsCompiler src/main/ets build/dist " + MODE_PARALLEL + " 8");
        System.err.println("  EtsCompiler src/Project build/Project " + MODE_PROJECT);
    }
}
