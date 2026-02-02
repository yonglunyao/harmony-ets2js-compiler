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
        final int exitCode = execute(args);
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

        final CompilerConfig config = CompilerConfig.createDefault();

        try {
            final Path inputPath = Path.of(args[ARG_INDEX_INPUT]);
            final Path outputPath = Path.of(args[ARG_INDEX_OUTPUT]);

            if (args.length > ARG_INDEX_MODE) {
                return executeBatchCompilation(inputPath, outputPath, args);
            } else {
                return executeSingleFileCompilation(inputPath, outputPath, config);
            }
        } catch (CompilationException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Compilation failed: {}", e.getMessage());
            }
            if (e.getCause() != null && LOGGER.isErrorEnabled()) {
                LOGGER.error("Cause: {}", e.getCause().getMessage());
            }
            return EXIT_ERROR;
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Compilation failed: {}", e.getMessage(), e);
            }
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
            LOGGER.info("Compilation completed: {} -> {}", inputPath, outputPath);
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
        final String mode = args[ARG_INDEX_MODE];

        if (!isValidMode(mode)) {
            LOGGER.error("Unknown option: {}", mode);
            printUsage();
            return EXIT_ERROR;
        }

        if (!Files.isDirectory(inputPath)) {
            LOGGER.error("Input path is not a directory: {}", inputPath);
            return EXIT_ERROR;
        }

        final List<Path> sourceFiles = findSourceFiles(inputPath);
        if (sourceFiles.isEmpty()) {
            LOGGER.info("No ETS/TS files found");
            return EXIT_SUCCESS;
        }

        LOGGER.info("Found {} files", sourceFiles.size());

        return executeWithMode(mode, inputPath, outputPath, sourceFiles, args);
    }

    /**
     * Validates if the given mode is a valid compilation mode.
     *
     * @param mode the mode to validate
     * @return true if mode is valid, false otherwise
     */
    private static boolean isValidMode(String mode) {
        return MODE_BATCH.equals(mode) || MODE_PARALLEL.equals(mode) || MODE_PROJECT.equals(mode);
    }

    /**
     * Finds source files in the given input path.
     *
     * @param inputPath the directory to search for source files
     * @return list of found source files
     * @throws CompilationException if source files cannot be found
     */
    private static List<Path> findSourceFiles(Path inputPath) throws CompilationException {
        try {
            return SourceFileFinder.findSourceFiles(inputPath);
        } catch (IOException e) {
            throw new CompilationException("Failed to find source files: " + inputPath, e);
        }
    }

    /**
     * Executes compilation based on the specified mode.
     *
     * @param mode the compilation mode
     * @param inputPath input directory path
     * @param outputPath output directory path
     * @param sourceFiles list of source files to compile
     * @param args command line arguments
     * @return exit code
     * @throws CompilationException if compilation fails
     */
    private static int executeWithMode(String mode, Path inputPath, Path outputPath,
                                     List<Path> sourceFiles, String[] args) throws CompilationException {
        if (MODE_PROJECT.equals(mode)) {
            return executeProjectCompilation(inputPath, outputPath, ICompiler.CompilationMode.SEQUENTIAL);
        }

        final ICompiler.CompilationMode compilationMode = MODE_PARALLEL.equals(mode)
                ? ICompiler.CompilationMode.PARALLEL
                : ICompiler.CompilationMode.SEQUENTIAL;

        return executeDirectoryCompilation(outputPath, sourceFiles, compilationMode, args);
    }

    /**
     * Executes directory compilation with specified mode.
     *
     * @param outputPath the output directory path
     * @param sourceFiles list of source files to compile
     * @param mode the compilation mode
     * @param args command line arguments
     * @return exit code
     */
    private static int executeDirectoryCompilation(Path outputPath,
            List<Path> sourceFiles, ICompiler.CompilationMode mode, String[] args)
            throws CompilationException {
        try (ICompiler compiler = createCompilerForMode(mode, args)) {
            final long startTime = System.currentTimeMillis();
            final CompilationResult result = compiler.compileBatch(sourceFiles, outputPath);
            final long duration = System.currentTimeMillis() - startTime;

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
        LOGGER.info("Compiling project: {}", inputPath);
        LOGGER.info("Output directory: {}", outputPath);
        LOGGER.info("Mode: {}, preserve directory structure, copy resources", mode);

        try (ICompiler compiler = CompilerFactory.createCompiler(CompilerConfig.createDefault(), mode)) {
            final long startTime = System.currentTimeMillis();
            final CompilationResult result = compiler.compileProject(inputPath, outputPath, true);
            final long duration = System.currentTimeMillis() - startTime;

            LOGGER.info("");
            LOGGER.info("=== Project Compilation Results ===");
            LOGGER.info("{}", result.getSummary());
            LOGGER.info("Duration: {}ms", duration);

            if (!result.isAllSuccess()) {
                LOGGER.info("");
                LOGGER.info("Some files failed to compile.");
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
        final CompilerConfig config = CompilerConfig.createDefault();

        if (mode == ICompiler.CompilationMode.PARALLEL && args.length > ARG_INDEX_THREADS) {
            final int threads = parseThreadCount(args);
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
        final int defaultThreads = Runtime.getRuntime().availableProcessors();
        if (args.length > ARG_INDEX_THREADS) {
            try {
                final int parsed = Integer.parseInt(args[ARG_INDEX_THREADS]);
                if (parsed > 0) {
                    return parsed;
                }
            } catch (NumberFormatException e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Invalid thread count: {}, using default: {}", args[ARG_INDEX_THREADS], defaultThreads);
                }
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
        LOGGER.info("");
        LOGGER.info("=== Compilation Results ===");
        LOGGER.info("Mode: {}", mode);
        LOGGER.info("{}", result.getSummary());
        LOGGER.info("Throughput: {} files/sec", result.getTotalCount() * 1000.0 / duration);

        if (!result.isAllSuccess()) {
            LOGGER.info("");
            LOGGER.info("Failed files:");
            for (final CompilationResult.FileResult failure : result.getFailures()) {
                LOGGER.info("  - {}", failure.getSourcePathAsString());
                LOGGER.info("    Error: {}", failure.getMessage());
            }
        }
    }

    /**
     * Prints usage information.
     */
    private static void printUsage() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Usage: EtsCompiler <input> <output> [mode] [threads]");
            LOGGER.info("");
            LOGGER.info("Modes:");
            LOGGER.info("  (none)      - Compile single file");
            LOGGER.info("  {}    - Batch compile directory (sequential, flat output)", MODE_BATCH);
            LOGGER.info("  {} - Batch compile directory (parallel, flat output)", MODE_PARALLEL);
            LOGGER.info("  {}  - Compile project (preserve structure, copy resources)", MODE_PROJECT);
            LOGGER.info("");
            LOGGER.info("Examples:");
            LOGGER.info("  EtsCompiler src/App.ets build/App.js");
            LOGGER.info("  EtsCompiler src/main/ets build/dist {}", MODE_BATCH);
            LOGGER.info("  EtsCompiler src/main/ets build/dist {}", MODE_PARALLEL);
            LOGGER.info("  EtsCompiler src/main/ets build/dist {} 8", MODE_PARALLEL);
            LOGGER.info("  EtsCompiler src/Project build/Project {}", MODE_PROJECT);
        }
    }
}
