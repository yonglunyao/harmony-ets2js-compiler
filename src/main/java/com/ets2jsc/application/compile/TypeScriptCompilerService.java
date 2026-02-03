package com.ets2jsc.application.compile;

import com.ets2jsc.shared.exception.CompilationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service for compiling TypeScript files using tsc compiler.
 * <p>
 * This service wraps the TypeScript compiler (tsc) to compile .ts files
 * that are standard TypeScript and do not contain ArkTS-specific syntax.
 */
public class TypeScriptCompilerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeScriptCompilerService.class);

    private static final String TSC_COMMAND = "tsc";
    private static final long TSC_TIMEOUT_SECONDS = 60;

    /**
     * Compiles a single TypeScript file using tsc.
     *
     * @param sourceFile the source TypeScript file
     * @param outputFile the output JavaScript file path
     * @return the path to the compiled file
     * @throws CompilationException if compilation fails
     */
    public Path compileFile(Path sourceFile, Path outputFile) throws CompilationException {
        if (!Files.exists(sourceFile)) {
            throw new CompilationException("Source file does not exist: " + sourceFile);
        }

        LOGGER.debug("Compiling TypeScript file: {} -> {}", sourceFile, outputFile);

        // Create parent directory if needed
        Path parentDir = outputFile.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                throw new CompilationException("Failed to create output directory: " + parentDir, e);
            }
        }

        // Build tsc command
        List<String> command = buildTscCommand(sourceFile, outputFile);

        // Execute tsc
        try {
            ProcessResult result = executeProcess(command, sourceFile.getParent());

            if (result.exitCode != 0) {
                throw new CompilationException(
                    "TypeScript compilation failed for: " + sourceFile + "\n" + result.stderr);
            }

            // Check if output file was created
            if (!Files.exists(outputFile)) {
                throw new CompilationException(
                    "TypeScript compilation completed but output file not found: " + outputFile);
            }

            LOGGER.debug("Successfully compiled: {} -> {}", sourceFile, outputFile);
            return outputFile;

        } catch (IOException e) {
            throw new CompilationException("Failed to compile TypeScript file: " + sourceFile, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompilationException("TypeScript compilation interrupted: " + sourceFile, e);
        }
    }

    /**
     * Compiles multiple TypeScript files using tsc.
     *
     * @param sourceFiles the list of source TypeScript files
     * @param baseDir     the base directory for calculating relative paths
     * @param outputDir   the output directory
     * @return compilation result with success/failure information
     * @throws CompilationException if compilation setup fails
     */
    public com.ets2jsc.domain.model.compilation.CompilationResult compileBatch(
            List<Path> sourceFiles, Path baseDir, Path outputDir)
            throws CompilationException {
        com.ets2jsc.domain.model.compilation.CompilationResult result =
                new com.ets2jsc.domain.model.compilation.CompilationResult();

        for (Path sourceFile : sourceFiles) {
            try {
                // Calculate relative path and output path
                Path relativePath = baseDir.relativize(sourceFile);
                String outputPathStr = relativePath.toString()
                        .replace(".ts", ".js")
                        .replace(".tsx", ".js");
                Path outputPath = outputDir.resolve(outputPathStr);

                compileFile(sourceFile, outputPath);
                result.addFileResult(sourceFile,
                        com.ets2jsc.domain.model.compilation.CompilationResult.FileResult.success(
                                sourceFile, outputPath, 0));

            } catch (CompilationException e) {
                result.addFileResult(sourceFile,
                        com.ets2jsc.domain.model.compilation.CompilationResult.FileResult.failure(
                                sourceFile, null, e.getMessage(), e, 0));
            }
        }

        result.markCompleted();
        return result;
    }

    /**
     * Checks if tsc is available on the system.
     *
     * @return true if tsc is available, false otherwise
     */
    public boolean isTscAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder(TSC_COMMAND, "--version");
            Process process = pb.start();
            return process.waitFor(TSC_TIMEOUT_SECONDS, TimeUnit.SECONDS) &&
                   process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * Builds the tsc command for compiling a single file.
     *
     * @param sourceFile the source file to compile
     * @param outputFile the output file path
     * @return the command list
     */
    private List<String> buildTscCommand(Path sourceFile, Path outputFile) {
        List<String> command = new ArrayList<>();
        command.add(TSC_COMMAND);
        command.add(sourceFile.toString());
        command.add("--outFile");
        command.add(outputFile.toString());
        command.add("--module");
        command.add("ESNext");
        command.add("--target");
        command.add("ES2020");
        command.add("--moduleResolution");
        command.add("node");
        command.add("--esModuleInterop");
        command.add("--skipLibCheck");
        return command;
    }

    /**
     * Executes a process and captures its output.
     *
     * @param command      the command to execute
     * @param workingDir   the working directory
     * @return the process result
     * @throws IOException          if process execution fails
     * @throws InterruptedException if process is interrupted
     */
    private ProcessResult executeProcess(List<String> command, Path workingDir)
            throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        if (workingDir != null) {
            pb.directory(workingDir.toFile());
        }

        Process process = pb.start();

        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();

        // Read stdout
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stdout.append(line).append("\n");
            }
        }

        // Read stderr
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stderr.append(line).append("\n");
            }
        }

        boolean completed = process.waitFor(TSC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        int exitCode = completed ? process.exitValue() : -1;

        return new ProcessResult(exitCode, stdout.toString(), stderr.toString());
    }

    /**
     * Represents the result of a process execution.
     */
    private record ProcessResult(int exitCode, String stdout, String stderr) {
    }
}
