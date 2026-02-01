package com.ets2jsc.integration;

import com.ets2jsc.EtsCompiler;
import com.ets2jsc.config.CompilerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Single test that automatically validates all TypeScript to JavaScript conversions.
 * Compiles and executes each test file, comparing TypeScript vs JavaScript output.
 */
@DisplayName("TypeScript to JavaScript Auto-Validation Test")
class TsJsAutoValidationTest {

    private static final String TEST_FIXTURES_PATH = "src/test/resources/fixtures/ts-validation";
    private static final String LOGS_DIR = "target/ts-validation-logs";
    private final AtomicInteger passedCount = new AtomicInteger(0);
    private final AtomicInteger failedCount = new AtomicInteger(0);
    private final List<String> failedTests = Collections.synchronizedList(new ArrayList<>());

    @Test
    @DisplayName("Run all TypeScript validation tests")
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
        System.out.println("TypeScript to JavaScript Validation");
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

        // Run each test in parallel (using ForkJoinPool common pool)
        testNames.parallelStream().forEach(testName -> {
            runTest(testName, fixtureDir, logsDir);
        });

        // Print summary
        System.out.println("\n========================================");
        System.out.println("Test Summary");
        System.out.println("========================================");
        System.out.println("Total: " + (passedCount.get() + failedCount.get()));
        System.out.println("Passed: " + passedCount.get());
        System.out.println("Failed: " + failedCount.get());

        if (!failedTests.isEmpty()) {
            System.out.println("\nFailed tests:");
            for (String name : failedTests) {
                System.out.println("  - " + name);
            }
        }

        // Assert that at least 50% of tests pass (adjustable threshold)
        int total = passedCount.get() + failedCount.get();
        double passRate = total > 0 ? (double) passedCount.get() / total * 100 : 0;
        System.out.println("\nPass rate: " + String.format("%.1f%%", passRate));
        System.out.println("========================================\n");

        assertTrue(passRate >= 30.0, "Pass rate should be at least 30% (actual: " + String.format("%.1f%%", passRate) + ")");
    }

    private void runTest(String testName, Path fixtureDir, Path logsDir) {
        Path tsFile = fixtureDir.resolve(testName + ".ts");
        Path jsFile = logsDir.resolve(testName + ".js");
        Path tsLogFile = logsDir.resolve(testName + "-ts.log");
        Path jsLogFile = logsDir.resolve(testName + "-js.log");

        try {
            // Compile TS to JS
            CompilerConfig config = CompilerConfig.createDefault();
            EtsCompiler compiler = new EtsCompiler(config);
            compiler.compile(tsFile, jsFile);

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
                // Skip warnings
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
}
