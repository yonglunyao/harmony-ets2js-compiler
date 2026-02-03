package com.ets2jsc.integration;

import com.ets2jsc.interfaces.publicapi.EtsCompiler;
import com.ets2jsc.interfaces.publicapi.model.PublicCompilationResult;
import com.ets2jsc.shared.util.SourceFileFinder;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test parallel compilation of harmony-utils project.
 */
public class ParallelProjectCompilationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelProjectCompilationTest.class);

    @Test
    public void testParallelCompileHarmonyUtilsProject() throws Exception {
        Path projectDir = Paths.get("src/test/resources/fixtures/integration/projects/harmony-utils");
        Path outputDir = Paths.get("target/test-parallel-compile/harmony-utils");

        // Clean output directory
        if (Files.exists(outputDir)) {
            deleteDirectory(outputDir);
        }
        Files.createDirectories(outputDir);

        // Create compiler with parallel mode (4 threads)
        try (EtsCompiler compiler = EtsCompiler.builder()
                .parallelMode(true)
                .threadCount(4)
                .projectPath(projectDir)
                .build()) {

            LOGGER.info("Starting parallel compilation of harmony-utils project...");

            // Compile project
            long startTime = System.currentTimeMillis();
            PublicCompilationResult result = compiler.compileProject(
                projectDir,
                outputDir,
                false  // don't copy resources
            );
            long duration = System.currentTimeMillis() - startTime;

            // Log results
            LOGGER.info("Compilation completed in {} ms", duration);
            LOGGER.info("Total files: {}", result.getTotalCount());
            LOGGER.info("Success: {}", result.getSuccessCount());
            LOGGER.info("Failed: {}", result.getFailureCount());

            // Print detailed results
            result.getFailures().forEach(fileResult -> {
                LOGGER.error("Failed: {} - {}", fileResult.getSourcePath(),
                    fileResult.getInternalResult().getMessage());
            });

            // Assert basic results
            assertTrue(result.getTotalCount() > 0, "Should have compiled some files");
            assertEquals(result.getTotalCount(), result.getSuccessCount() + result.getFailureCount(),
                "Total should equal success + failure");

            // Check that output files were created
            List<Path> sourceFiles = SourceFileFinder.findSourceFiles(projectDir);
            assertEquals(sourceFiles.size(), result.getTotalCount(),
                "All source files should be processed");

            LOGGER.info("Parallel compilation test completed");
        }
    }

    @Test
    public void testSequentialVsParallelCompilation() throws Exception {
        Path projectDir = Paths.get("src/test/resources/fixtures/integration/projects/harmony-utils");
        Path outputDirParallel = Paths.get("target/test-compare/parallel");
        Path outputDirSequential = Paths.get("target/test-compare/sequential");

        // Clean output directories
        deleteDirectory(outputDirParallel);
        deleteDirectory(outputDirSequential);
        Files.createDirectories(outputDirParallel);
        Files.createDirectories(outputDirSequential);

        List<Path> sourceFiles = SourceFileFinder.findSourceFiles(projectDir);
        LOGGER.info("Found {} source files to compile", sourceFiles.size());

        // Sequential compilation
        try (EtsCompiler compilerSequential = EtsCompiler.builder()
                .parallelMode(false)
                .projectPath(projectDir)
                .build()) {

            LOGGER.info("Starting sequential compilation...");
            long seqStart = System.currentTimeMillis();
            PublicCompilationResult seqResult = compilerSequential.compileProject(
                projectDir,
                outputDirSequential,
                false
            );
            long seqDuration = System.currentTimeMillis() - seqStart;

            LOGGER.info("Sequential compilation: {} ms, Success: {}, Failed: {}",
                seqDuration, seqResult.getSuccessCount(), seqResult.getFailureCount());
        }

        // Parallel compilation
        try (EtsCompiler compilerParallel = EtsCompiler.builder()
                .parallelMode(true)
                .threadCount(4)
                .projectPath(projectDir)
                .build()) {

            LOGGER.info("Starting parallel compilation...");
            long parStart = System.currentTimeMillis();
            PublicCompilationResult parResult = compilerParallel.compileProject(
                projectDir,
                outputDirParallel,
                false
            );
            long parDuration = System.currentTimeMillis() - parStart;

            LOGGER.info("Parallel compilation: {} ms, Success: {}, Failed: {}",
                parDuration, parResult.getSuccessCount(), parResult.getFailureCount());
        }

        // Compare output files
        LOGGER.info("Comparing output files...");
        compareOutputDirectories(outputDirSequential, outputDirParallel);
    }

    @Test
    public void testAnalyzeCompilationResults() throws Exception {
        Path projectDir = Paths.get("src/test/resources/fixtures/integration/projects/harmony-utils");
        Path outputDir = Paths.get("target/test-analyze/harmony-utils");

        deleteDirectory(outputDir);
        Files.createDirectories(outputDir);

        try (EtsCompiler compiler = EtsCompiler.builder()
                .parallelMode(true)
                .threadCount(4)
                .projectPath(projectDir)
                .build()) {

            PublicCompilationResult result = compiler.compileProject(
                projectDir,
                outputDir,
                false
            );

            LOGGER.info("=== Compilation Result Analysis ===");
            LOGGER.info("Total files processed: {}", result.getTotalCount());
            LOGGER.info("Successful: {}", result.getSuccessCount());
            LOGGER.info("Failed: {}", result.getFailureCount());

            // Categorize results
            AtomicInteger utilsCount = new AtomicInteger();
            AtomicInteger cryptoCount = new AtomicInteger();
            AtomicInteger actionCount = new AtomicInteger();
            AtomicInteger entityCount = new AtomicInteger();
            AtomicInteger otherCount = new AtomicInteger();

            result.getFileResults().forEach(fileResult -> {
                String path = fileResult.getSourcePath().toString();
                if (path.contains("/utils/")) {
                    utilsCount.incrementAndGet();
                } else if (path.contains("/crypto/")) {
                    cryptoCount.incrementAndGet();
                } else if (path.contains("/action/")) {
                    actionCount.incrementAndGet();
                } else if (path.contains("/entity/")) {
                    entityCount.incrementAndGet();
                } else {
                    otherCount.incrementAndGet();
                }

                if (!fileResult.isSuccess()) {
                    LOGGER.warn("FAILED: {} - {}", path, fileResult.getInternalResult().getMessage());
                }
            });

            LOGGER.info("=== File Categories ===");
            LOGGER.info("Utils: {}", utilsCount.get());
            LOGGER.info("Crypto: {}", cryptoCount.get());
            LOGGER.info("Action: {}", actionCount.get());
            LOGGER.info("Entity: {}", entityCount.get());
            LOGGER.info("Other: {}", otherCount.get());

            // Check if compilation was successful
            if (result.getFailureCount() == 0) {
                LOGGER.info("=== SUCCESS: All files compiled successfully! ===");
            } else {
                LOGGER.warn("=== WARNING: {} files failed to compile ===", result.getFailureCount());
            }
        }
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                .sorted((a, b) -> b.compareTo(a)) // Reverse order to delete files before directories
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        LOGGER.warn("Failed to delete: {}", path);
                    }
                });
        }
    }

    private void compareOutputDirectories(Path dir1, Path dir2) throws IOException {
        LOGGER.info("Comparing output from {} and {}", dir1, dir2);

        // This is a simplified comparison - just check file counts
        long files1 = Files.walk(dir1)
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".js"))
            .count();

        long files2 = Files.walk(dir2)
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".js"))
            .count();

        LOGGER.info("Sequential output files: {}", files1);
        LOGGER.info("Parallel output files: {}", files2);

        assertEquals(files1, files2, "Both modes should produce same number of output files");
    }
}
