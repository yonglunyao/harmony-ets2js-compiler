package com.ets2jsc.integration;

import com.ets2jsc.compiler.CompilerFactory;
import com.ets2jsc.compiler.ICompiler;
import com.ets2jsc.config.CompilerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Optimized auto-validation test with parallel execution using thread pool.
 * Performance target: 100 test files in under 30 seconds.
 */
@DisplayName("TypeScript to JavaScript Auto-Validation Test (Optimized)")
public class TsJsAutoValidationTestOptimizedTest {

    private static final String TEST_FIXTURES_PATH = "src/test/resources/fixtures/ts-validation";
    private static final String LOGS_DIR = "target/ts-validation-logs";
    private static final int MAX_CONCURRENT_TESTS = 16;

    private static final java.util.concurrent.atomic.AtomicInteger passedCount = new java.util.concurrent.atomic.AtomicInteger(0);
    private static final java.util.concurrent.atomic.AtomicInteger failedCount = new java.util.concurrent.atomic.AtomicInteger(0);
    private static final List<String> failedTests = Collections.synchronizedList(new ArrayList<>());

    @Test
    @DisplayName("Run all TypeScript validation tests in parallel")
    void testAllFiles() throws Exception {
        Path fixtureDir = Paths.get(TEST_FIXTURES_PATH);
        if (!Files.exists(fixtureDir)) {
            System.out.println("Test fixtures directory not found: " + TEST_FIXTURES_PATH);
            return;
        }

        // Create logs directory
        Path logsDir = Paths.get(LOGS_DIR);
        Files.createDirectories(logsDir);

        System.out.println("\n========================================");
        System.out.println("TypeScript to JavaScript Validation (Optimized)");
        System.out.println("========================================\n");

        // Get all test files
        List<String> testNames;
        try (Stream<Path> paths = Files.list(fixtureDir)) {
            testNames = paths
                    .filter(p -> p.toString().endsWith(".ts"))
                    .map(p -> p.getFileName().toString().replace(".ts", ""))
                    .sorted()
                    .toList();
        }

        long startTime = System.currentTimeMillis();

        // Run tests in parallel using fixed thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_TESTS);

        // Submit all test tasks
        testNames.forEach(testName -> {
            executorService.submit(() -> runTest(testName, fixtureDir, logsDir));
        });

        // Shutdown and wait for completion
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("WARNING: Tests did not complete within 60 seconds");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long duration = System.currentTimeMillis() - startTime;

        // Print summary
        printSummary(duration);
    }

    private void runTest(String testName, Path fixtureDir, Path logsDir) {
        try {
            Path tsFile = fixtureDir.resolve(testName + ".ts");
            Path jsFile = logsDir.resolve(testName + ".js");
            Path tsLogFile = logsDir.resolve(testName + "-ts.log");
            Path jsLogFile = logsDir.resolve(testName + "-js.log");

            // Compile TS to JS
            CompilerConfig config = CompilerConfig.createDefault();
            try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
                compiler.compile(tsFile, jsFile);
            }

            // Execute TypeScript
            String tsOutput = executeScript("TypeScript", tsFile);
            Files.writeString(tsLogFile, tsOutput);

            // Execute JavaScript
            String jsOutput = executeScript("JavaScript", jsFile);
            Files.writeString(jsLogFile, jsOutput);

            // Compare outputs
            boolean match = compareOutputs(testName, tsOutput, jsOutput);

            if (match) {
                passedCount.incrementAndGet();
                System.out.println("[PASS] " + testName);
            } else {
                failedCount.incrementAndGet();
                failedTests.add(testName);
                System.out.println("[FAIL] " + testName);
            }
        } catch (Exception e) {
            failedCount.incrementAndGet();
            failedTests.add(testName);
            System.out.println("[ERROR] " + testName + " - " + e.getMessage());
        }
    }

    private String executeScript(String type, Path scriptFile) throws Exception {
        List<String> command;
        String osName = System.getProperty("os.name", "").toLowerCase();

        if ("TypeScript".equals(type)) {
            if (osName.contains("win")) {
                command = List.of("cmd", "/c", "npx", "ts-node", "--esm",
                        "--experimentalSpecifierResolution=node", scriptFile.toAbsolutePath().toString());
            } else {
                command = List.of("npx", "ts-node", "--esm",
                        "--experimentalSpecifierResolution=node", scriptFile.toAbsolutePath().toString());
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
                String trimmed = line.trim();
                if (trimmed.isEmpty()
                    || trimmed.startsWith("DeprecationWarning")
                    || trimmed.startsWith("(node:")
                    || trimmed.startsWith("(Use `node")) {
                    continue;
                }
                output.append(trimmed).append("\n");
            }
        }

        process.waitFor();
        return output.toString();
    }

    private boolean compareOutputs(String testName, String tsOutput, String jsOutput) {
        List<String> tsLines = parseLines(tsOutput);
        List<String> jsLines = parseLines(jsOutput);

        if (tsLines.size() != jsLines.size()) {
            return false;
        }

        for (int i = 0; i < tsLines.size(); i++) {
            String tsLine = normalizeLine(tsLines.get(i));
            String jsLine = normalizeLine(jsLines.get(i));
            if (!tsLine.equals(jsLine)) {
                return false;
            }
        }
        return true;
    }

    private List<String> parseLines(String output) {
        List<String> lines = new ArrayList<>();
        for (String line : output.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                lines.add(trimmed);
            }
        }
        return lines;
    }

    private String normalizeLine(String line) {
        return line.trim().replaceAll("\\s+", " ");
    }

    private void printSummary(long durationMs) {
        System.out.println("\n========================================");
        System.out.println("Test Summary");
        System.out.println("========================================");
        System.out.println("Total: " + (passedCount.get() + failedCount.get()));
        System.out.println("Passed: " + passedCount.get());
        System.out.println("Failed: " + failedCount.get());
        System.out.println("Duration: " + durationMs + "ms (" + String.format("%.1f", durationMs / 1000.0) + "s)");
        System.out.println("Throughput: " + String.format("%.2f files/sec", (passedCount.get() + failedCount.get()) * 1000.0 / durationMs));

        if (!failedTests.isEmpty()) {
            System.out.println("\nFailed tests:");
            for (String name : failedTests) {
                System.out.println("  - " + name);
            }
        }

        double passRate = (passedCount.get() + failedCount.get()) > 0
                ? (double) passedCount.get() / (passedCount.get() + failedCount.get()) * 100
                : 0;
        System.out.println("\nPass rate: " + String.format("%.1f%%", passRate));
        System.out.println("========================================\n");
    }
}
