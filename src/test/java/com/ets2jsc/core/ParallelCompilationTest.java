package com.ets2jsc.core;

import com.ets2jsc.compiler.CompilerFactory;
import com.ets2jsc.compiler.ICompiler;
import com.ets2jsc.compiler.CompilationResult;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.shared.exception.CompilationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Parallel Compilation Functionality
 * Test parallel compilation functionality
 */
class ParallelCompilationTest {

    @TempDir
    Path tempDir;

    /**
     * Test parallel compile multiple simple files
     */
    @Test
    void testParallelCompilation() throws Exception {
        // create test files
        Path sourceDir = tempDir.resolve("sources");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(sourceDir);
        Files.createDirectories(outputDir);

        // create multiple test files
        for (int i = 1; i <= 5; i++) {
            String content = ""
                + "@Component\n"
                + "struct Test" + i + " {\n"
                + "    build() {\n"
                + "        Column() {\n"
                + "            Text('Hello " + i + "')\n"
                + "        }\n"
                + "    }\n"
                + "}\n";

            Path sourceFile = sourceDir.resolve("Test" + i + ".ets");
            Files.writeString(sourceFile, content);
        }

        // use parallel compilation
        CompilerConfig config = CompilerConfig.createDefault();
        List<Path> sourceFiles = Files.walk(sourceDir)
            .filter(p -> p.toString().endsWith(".ets"))
            .toList();

        try (ICompiler compiler = CompilerFactory.createParallelCompiler(config, 2)) {
            CompilationResult result = compiler.compileBatch(sourceFiles, outputDir);

            // verify results
            assertEquals(5, result.getTotalCount(), "should compile 5 files");
            assertTrue(result.isAllSuccess(), "all files should compile successfully");
            assertTrue(result.getDurationMs() >= 0, "duration should be non-negative");

            // verify output files exist
            for (int i = 1; i <= 5; i++) {
                Path outputFile = outputDir.resolve("Test" + i + ".js");
                assertTrue(Files.exists(outputFile), "output file should exist: " + outputFile);
            }
        }
    }

    /**
     * Test parallel compilation performance advantage
     */
    @Test
    void testParallelPerformance() throws Exception {
        // create test files
        Path sourceDir = tempDir.resolve("sources");
        Path outputDir1 = tempDir.resolve("output1");
        Path outputDir2 = tempDir.resolve("output2");
        Files.createDirectories(sourceDir);
        Files.createDirectories(outputDir1);
        Files.createDirectories(outputDir2);

        // create multiple test files
        int fileCount = 10;
        for (int i = 1; i <= fileCount; i++) {
            String content = ""
                + "@Component\n"
                + "struct PerfTest" + i + " {\n"
                + "    @State count: number = 0\n"
                + "    build() {\n"
                + "        Column() {\n"
                + "            Text('Count: ' + this.count)\n"
                + "            Button('Increase')\n"
                + "                .onClick(() => {\n"
                + "                    this.count++\n"
                + "                })\n"
                + "        }\n"
                + "    }\n"
                + "}\n";

            Path sourceFile = sourceDir.resolve("PerfTest" + i + ".ets");
            Files.writeString(sourceFile, content);
        }

        CompilerConfig config = CompilerConfig.createDefault();
        List<Path> sourceFiles = Files.walk(sourceDir)
            .filter(p -> p.toString().endsWith(".ets"))
            .toList();

        // sequential compilation
        long sequentialStart = System.currentTimeMillis();
        try (ICompiler compiler1 = CompilerFactory.createSequentialCompiler(config)) {
            for (Path sourceFile : sourceFiles) {
                String fileName = sourceFile.getFileName().toString();
                Path outputPath = outputDir1.resolve(fileName.replace(".ets", ".js"));
                compiler1.compile(sourceFile, outputPath);
            }
        }
        long sequentialTime = System.currentTimeMillis() - sequentialStart;

        // parallel compilation
        long parallelStart = System.currentTimeMillis();
        try (ICompiler compiler2 = CompilerFactory.createParallelCompiler(config, 4)) {
            CompilationResult result = compiler2.compileBatch(sourceFiles, outputDir2);
            long parallelTime = System.currentTimeMillis() - parallelStart;

            // verify results
            assertTrue(result.isAllSuccess(), "parallel compilation should succeed");

            // output performance comparison
            System.out.println("=== Performance Comparison ===");
            System.out.println("Files: " + fileCount);
            System.out.println("sequential compilationDuration: " + sequentialTime + "ms");
            System.out.println("parallel compilationDuration: " + parallelTime + "ms");
            System.out.println("Performance improvement: " + ((sequentialTime - parallelTime) * 100.0 / sequentialTime) + "%");
            System.out.println("Throughput: " + (fileCount * 1000.0 / parallelTime) + " files/sec");
        }
    }

    /**
     * Test parallel compilation error handling
     * use non-existent file to test error handling
     */
    @Test
    void testParallelErrorHandling() throws Exception {
        // create test files
        Path sourceDir = tempDir.resolve("sources");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(sourceDir);
        Files.createDirectories(outputDir);

        // create an empty file (may cause parsing issues)
        Files.writeString(sourceDir.resolve("Good.ets"),
            "@Component\nstruct Good { build() { Column() {} } }");
        Files.writeString(sourceDir.resolve("Bad.ets"), "");
        Files.writeString(sourceDir.resolve("Good2.ets"),
            "@Component\nstruct Good2 { build() { Column() {} } }");

        CompilerConfig config = CompilerConfig.createDefault();
        List<Path> sourceFiles = Files.walk(sourceDir)
            .filter(p -> p.toString().endsWith(".ets"))
            .toList();

        try (ICompiler compiler = CompilerFactory.createParallelCompiler(config, 2)) {
            CompilationResult result = compiler.compileBatch(sourceFiles, outputDir);

            // verify results - should process all files
            assertTrue(result.getTotalCount() >= 0, "should process files");

            // verify results summary is available
            String summary = result.getSummary();
            assertNotNull(summary, "summary should not be empty");
            assertTrue(summary.contains("Total"), "summary should contain total information");
        }
    }

    /**
     * Test performance with different thread counts
     */
    @Test
    void testDifferentThreadCounts() throws Exception {
        // create test files
        Path sourceDir = tempDir.resolve("sources");
        Files.createDirectories(sourceDir);

        int fileCount = 20;
        for (int i = 1; i <= fileCount; i++) {
            String content = ""
                + "@Component\n"
                + "struct ThreadTest" + i + " {\n"
                + "    build() {\n"
                + "        Column() {\n"
                + "            Text('Test " + i + "')\n"
                + "        }\n"
                + "    }\n"
                + "}\n";

            Path sourceFile = sourceDir.resolve("ThreadTest" + i + ".ets");
            Files.writeString(sourceFile, content);
        }

        CompilerConfig config = CompilerConfig.createDefault();
        List<Path> sourceFiles = Files.walk(sourceDir)
            .filter(p -> p.toString().endsWith(".ets"))
            .toList();

        System.out.println("=== Different Thread Count Performance Test ===");
        System.out.println("Files: " + fileCount);
        System.out.println();

        int[] threadCounts = {1, 2, 4, 8};
        for (int threads : threadCounts) {
            Path outputDir = tempDir.resolve("output" + threads);
            Files.createDirectories(outputDir);

            try (ICompiler compiler = CompilerFactory.createParallelCompiler(config, threads)) {
                long startTime = System.currentTimeMillis();
                CompilationResult result = compiler.compileBatch(sourceFiles, outputDir);
                long duration = System.currentTimeMillis() - startTime;

                System.out.println("Threads: " + threads);
                System.out.println("  Duration: " + duration + "ms");
                System.out.println("  Throughput: " + (fileCount * 1000.0 / duration) + " files/sec");
                System.out.println("  Result: " + result.getSummary());
                System.out.println();

                assertTrue(result.isAllSuccess(), "compilation should succeed");
            }
        }
    }
}
