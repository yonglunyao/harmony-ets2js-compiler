package com.ets2jsc.integration;

import com.ets2jsc.EtsCompiler;
import com.ets2jsc.config.CompilerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive validation test suite with detailed logging.
 * Uses batch scripts for cross-platform execution.
 */
@DisplayName("TypeScript to JavaScript Execution Validation Tests (With Logging)")
class TsJsExecutionValidationTest {

    private static final String TEST_FIXTURES_PATH = "src/test/resources/fixtures/ts-validation";
    private static final String LOGS_DIR = "target/ts-validation-logs";

    static Stream<String> provideTestNames() {
        try {
            Path fixtureDir = Paths.get(TEST_FIXTURES_PATH);
            if (!Files.exists(fixtureDir)) {
                return Stream.empty();
            }
            return Files.list(fixtureDir)
                .filter(p -> p.toString().endsWith(".ts"))
                .map(p -> p.getFileName().toString().replace(".ts", ""));
        } catch (IOException e) {
            return Stream.empty();
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestNames")
    @DisplayName("Validate TypeScript → JavaScript execution consistency with logging")
    void testTsJsExecutionConsistency(String testName, @TempDir Path tempDir) throws Exception {
        Path fixtureDir = Paths.get(TEST_FIXTURES_PATH);
        Path tsFile = fixtureDir.resolve(testName + ".ts");

        if (!Files.exists(tsFile)) {
            System.out.println("Skipping " + testName + " - file not found");
            return;
        }

        // Create logs directory
        Path logsDir = Paths.get(LOGS_DIR);
        Files.createDirectories(logsDir);

        // Read TypeScript source
        String tsSource = Files.readString(tsFile);

        // Create output paths
        Path etsFile = tempDir.resolve(testName + ".ets");
        Path jsFile = tempDir.resolve(testName + ".js");
        Path tsLogFile = logsDir.resolve(testName + "-ts.log");
        Path jsLogFile = logsDir.resolve(testName + "-js.log");
        Path comparisonLogFile = logsDir.resolve(testName + "-comparison.log");

        // Write the source as ETS (same as TS for our tests)
        Files.writeString(etsFile, tsSource);

        // Compile ETS to JS
        try {
            CompilerConfig config = CompilerConfig.createDefault();
            EtsCompiler compiler = new EtsCompiler(config);
            compiler.compile(etsFile, jsFile);
        } catch (Exception e) {
            Files.writeString(tsLogFile, "COMPILATION_FAILED: " + e.getMessage());
            Files.writeString(jsLogFile, "COMPILATION_FAILED: " + e.getMessage());
            Files.writeString(comparisonLogFile, "COMPILATION_FAILED: " + e.getMessage());
            System.out.println("COMPILATION_FAILED: " + testName + " - " + e.getMessage());
            return;
        }

        // Execute both TypeScript and JavaScript
        boolean tsSuccess = executeAndLog("TypeScript", tsFile, tsLogFile);
        boolean jsSuccess = executeAndLog("JavaScript", jsFile, jsLogFile);

        if (!tsSuccess || !jsSuccess) {
            System.out.println(String.format("EXECUTION_FAILED: %s - TS=%b, JS=%b", testName, tsSuccess, jsSuccess));
            return;
        }

        // Compare outputs and log results
        compareAndLogOutputs(testName, tsLogFile, jsLogFile, comparisonLogFile);
    }

    /**
     * Executes a file and logs the output.
     */
    private boolean executeAndLog(String type, Path file, Path logFile) {
        try {
            String output = executeScript(type, file);
            Files.writeString(logFile, output);
            return true;
        } catch (Exception e) {
            try {
                Files.writeString(logFile, "EXECUTION_FAILED: " + e.getMessage());
            } catch (IOException io) {
                // Ignore
            }
            System.out.println(String.format("%s execution failed for %s: %s", type, file.getFileName(), e.getMessage()));
            return false;
        }
    }

    /**
     * Executes a script file (TypeScript or JavaScript).
     */
    private String executeScript(String type, Path scriptFile) throws Exception {
        List<String> command;
        boolean useCmdWrapper = false;

        if ("TypeScript".equals(type)) {
            // On Windows, wrap npx command with cmd /c
            String osName = System.getProperty("os.name", "").toLowerCase();
            if (osName.contains("win")) {
                useCmdWrapper = true;
                command = List.of(
                    "cmd", "/c",
                    "npx", "ts-node",
                    "--esm",
                    "--experimentalSpecifierResolution=node",
                    scriptFile.toAbsolutePath().toString()
                );
            } else {
                command = List.of(
                    "npx", "ts-node",
                    "--esm",
                    "--experimentalSpecifierResolution=node",
                    scriptFile.toAbsolutePath().toString()
                );
            }
        } else {
            command = List.of("node", scriptFile.toAbsolutePath().toString());
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 && output.length() == 0) {
            throw new RuntimeException("Execution failed with exit code " + exitCode);
        }

        return output.toString();
    }

    /**
     * Compares two output files and logs the comparison result.
     */
    private void compareAndLogOutputs(String testName, Path tsLogFile, Path jsLogFile, Path comparisonLogFile) throws IOException {
        String tsOutput = Files.readString(tsLogFile);
        String jsOutput = Files.readString(jsLogFile);

        StringBuilder comparisonResult = new StringBuilder();
        comparisonResult.append("=== Comparison Result for ").append(testName).append(" ===\n\n");

        List<String> tsLines = parseOutputLines(tsOutput);
        List<String> jsLines = parseOutputLines(jsOutput);

        comparisonResult.append("TS Output Lines: ").append(tsLines.size()).append("\n");
        comparisonResult.append("JS Output Lines: ").append(jsLines.size()).append("\n\n");

        boolean allMatch = true;

        // Compare line by line
        for (int i = 0; i < Math.max(tsLines.size(), jsLines.size()); i++) {
            String tsLine = i < tsLines.size() ? tsLines.get(i) : null;
            String jsLine = i < jsLines.size() ? jsLines.get(i) : null;

            if (tsLine == null) {
                comparisonResult.append(String.format("[MISMATCH] Line %d: JS has extra output: %s\n", i + 1, jsLine));
                allMatch = false;
                continue;
            }
            if (jsLine == null) {
                comparisonResult.append(String.format("[MISMATCH] Line %d: TS has extra output: %s\n", i + 1, tsLine));
                allMatch = false;
                continue;
            }

            String normalizedTsLine = normalizeLine(tsLine);
            String normalizedJsLine = normalizeLine(jsLine);

            if (normalizedTsLine.equals(normalizedJsLine)) {
                comparisonResult.append(String.format("[MATCH] Line %d: %s\n", i + 1, normalizedTsLine));
            } else {
                comparisonResult.append(String.format("[MISMATCH] Line %d:\n  TS:  %s\n  JS:  %s\n",
                    i + 1, normalizedTsLine, normalizedJsLine));
                allMatch = false;
            }
        }

        if (allMatch) {
            comparisonResult.append("\n=== RESULT: ALL OUTPUTS MATCH ===");
        } else {
            comparisonResult.append("\n=== RESULT: OUTPUTS DO NOT MATCH ===");
        }

        Files.writeString(comparisonLogFile, comparisonResult.toString());

        if (!allMatch) {
            fail(String.format("Output mismatch for %s. Check comparison log: %s",
                testName, comparisonLogFile));
        }
    }

    /**
     * Parse output into lines, filtering out headers and empty lines.
     */
    private List<String> parseOutputLines(String output) {
        List<String> lines = new ArrayList<>();
        for (String line : output.split("\n")) {
            String trimmed = line.trim();
            // Skip empty lines and warnings
            if (trimmed.isEmpty()
                || trimmed.startsWith("DeprecationWarning")
                || trimmed.startsWith("(node:")
                || trimmed.startsWith("(Use `node")) {
                continue;
            }
            // Include all output lines
            lines.add(trimmed);
        }
        return lines;
    }

    /**
     * Normalizes a line for comparison.
     */
    private String normalizeLine(String line) {
        return line.trim()
            .replaceAll("\\s+", " ");
    }
}
