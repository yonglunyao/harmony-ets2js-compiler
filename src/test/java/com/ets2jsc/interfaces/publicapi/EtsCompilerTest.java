package com.ets2jsc.interfaces.publicapi;

import com.ets2jsc.interfaces.publicapi.model.CompilationMode;
import com.ets2jsc.interfaces.publicapi.model.PublicCompilationResult;
import com.ets2jsc.shared.exception.CompilationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the public EtsCompiler API.
 * These tests verify that the public facade works correctly for external usage.
 */
@DisplayName("Public EtsCompiler API Tests")
class EtsCompilerTest {

    private static final String TEST_SOURCE_CODE = """
            @Component
            struct MyComponent {
                @State message: string = 'Hello';

                build() {
                    Text(this.message);
                }
            }
            """;

    @Test
    @DisplayName("EtsCompiler.create() should create a compiler with default settings")
    void testCreateWithDefaultSettings() {
        try (EtsCompiler compiler = EtsCompiler.create()) {
            assertNotNull(compiler);
            assertFalse(compiler.isClosed());
            assertEquals(CompilationMode.SEQUENTIAL, compiler.getMode());
            assertEquals(1, compiler.getThreadCount());
            assertNotNull(compiler.getConfig());
        }
    }

    @Test
    @DisplayName("EtsCompiler.builder() should create a configurable builder")
    void testBuilderCreatesConfigurableCompiler() {
        try (EtsCompiler compiler = EtsCompiler.builder()
                .parallelMode(true)
                .threadCount(4)
                .sourcePath("src/ets")
                .buildPath("build")
                .generateSourceMap(true)
                .build()) {
            assertNotNull(compiler);
            assertEquals(CompilationMode.PARALLEL, compiler.getMode());
            assertEquals(4, compiler.getThreadCount());
            assertEquals("src/ets", compiler.getConfig().getSourcePath());
            assertEquals("build", compiler.getConfig().getBuildPath());
            assertTrue(compiler.getConfig().isGenerateSourceMap());
        }
    }

    @Test
    @DisplayName("EtsCompiler should compile a single file")
    void testCompileSingleFile(@TempDir Path tempDir) throws Exception {
        Path sourceFile = tempDir.resolve("Test.ets");
        Files.writeString(sourceFile, TEST_SOURCE_CODE);

        Path outputFile = tempDir.resolve("Test.js");

        try (EtsCompiler compiler = EtsCompiler.create()) {
            PublicCompilationResult result = compiler.compileFile(sourceFile, outputFile);

            assertTrue(result.isSuccess());
            assertEquals(1, result.getTotalCount());
            assertEquals(1, result.getSuccessCount());
            assertEquals(0, result.getFailureCount());
            assertTrue(Files.exists(outputFile));
        }
    }

    @Test
    @DisplayName("EtsCompiler should compile multiple files in batch")
    void testCompileBatch(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("sources");
        Files.createDirectories(sourceDir);

        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(outputDir);

        for (int i = 1; i <= 3; i++) {
            Path file = sourceDir.resolve("Test" + i + ".ets");
            Files.writeString(file, TEST_SOURCE_CODE.replace("MyComponent", "MyComponent" + i));
        }

        try (EtsCompiler compiler = EtsCompiler.create()) {
            PublicCompilationResult result = compiler.compileBatch(
                    java.util.List.of(
                            sourceDir.resolve("Test1.ets"),
                            sourceDir.resolve("Test2.ets"),
                            sourceDir.resolve("Test3.ets")
                    ),
                    outputDir
            );

            assertTrue(result.isSuccess());
            assertEquals(3, result.getTotalCount());
            assertEquals(3, result.getSuccessCount());
        }
    }

    @Test
    @DisplayName("EtsCompiler should compile a project")
    void testCompileProject(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("sources");
        Files.createDirectories(sourceDir);

        Path outputDir = tempDir.resolve("output");

        Path file = sourceDir.resolve("App.ets");
        Files.writeString(file, TEST_SOURCE_CODE);

        try (EtsCompiler compiler = EtsCompiler.create()) {
            PublicCompilationResult result = compiler.compileProject(sourceDir, outputDir, false);

            assertTrue(result.isSuccess());
            assertEquals(1, result.getTotalCount());
        }
    }

    @Test
    @DisplayName("EtsCompiler should handle empty project directory")
    void testCompileEmptyProject(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("sources");
        Files.createDirectories(sourceDir);

        Path outputDir = tempDir.resolve("output");

        try (EtsCompiler compiler = EtsCompiler.create()) {
            PublicCompilationResult result = compiler.compileProject(sourceDir, outputDir, false);

            assertEquals(0, result.getTotalCount());
            assertTrue(result.isAllSuccess());
        }
    }

    @Test
    @DisplayName("EtsCompiler should close properly")
    void testClose() {
        EtsCompiler compiler = EtsCompiler.create();
        assertFalse(compiler.isClosed());

        compiler.close();
        assertTrue(compiler.isClosed());

        // Close should be idempotent
        compiler.close();
        assertTrue(compiler.isClosed());
    }

    @Test
    @DisplayName("EtsCompiler should throw exception when used after close")
    void testThrowsExceptionWhenUsedAfterClose() {
        EtsCompiler compiler = EtsCompiler.create();
        compiler.close();

        assertThrows(IllegalStateException.class, () -> {
            compiler.compileFile(Path.of("test.ets"), Path.of("test.js"));
        });
    }

    @Test
    @DisplayName("EtsCompilerBuilder should throw exception for invalid thread count")
    void testBuilderThrowsExceptionForInvalidThreadCount() {
        assertThrows(IllegalArgumentException.class, () -> {
            EtsCompiler.builder().threadCount(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            EtsCompiler.builder().threadCount(-1);
        });
    }

    @Test
    @DisplayName("EtsCompiler should provide result summary")
    void testProvidesResultSummary(@TempDir Path tempDir) throws Exception {
        Path sourceFile = tempDir.resolve("Test.ets");
        Files.writeString(sourceFile, TEST_SOURCE_CODE);

        Path outputFile = tempDir.resolve("Test.js");

        try (EtsCompiler compiler = EtsCompiler.create()) {
            PublicCompilationResult result = compiler.compileFile(sourceFile, outputFile);

            String summary = result.getSummary();
            assertNotNull(summary);
            assertTrue(summary.contains("Total"));
        }
    }

    @Test
    @DisplayName("PublicCompilationResult should expose file results")
    void testPublicCompilationResultExposesFileResults(@TempDir Path tempDir) throws Exception {
        Path sourceFile = tempDir.resolve("Test.ets");
        Files.writeString(sourceFile, TEST_SOURCE_CODE);

        Path outputFile = tempDir.resolve("Test.js");

        try (EtsCompiler compiler = EtsCompiler.create()) {
            PublicCompilationResult result = compiler.compileFile(sourceFile, outputFile);

            var fileResults = result.getFileResults();
            assertNotNull(fileResults);
            assertEquals(1, fileResults.size());

            var fileResult = fileResults.get(0);
            assertTrue(fileResult.isSuccess());
            assertEquals(sourceFile, fileResult.getSourcePath());
            assertEquals(outputFile, fileResult.getOutputPath());
        }
    }

    @Test
    @DisplayName("EtsCompiler with parallel mode should work correctly")
    void testParallelMode(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("sources");
        Files.createDirectories(sourceDir);

        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(outputDir);

        for (int i = 1; i <= 5; i++) {
            Path file = sourceDir.resolve("Test" + i + ".ets");
            Files.writeString(file, TEST_SOURCE_CODE.replace("MyComponent", "MyComponent" + i));
        }

        try (EtsCompiler compiler = EtsCompiler.builder()
                .parallelMode(true)
                .threadCount(2)
                .build()) {
            PublicCompilationResult result = compiler.compileProject(sourceDir, outputDir, false);

            assertTrue(result.isSuccess());
            assertEquals(5, result.getTotalCount());
            assertEquals(CompilationMode.PARALLEL, compiler.getMode());
        }
    }

    @Test
    @DisplayName("EtsCompilerBuilder fromConfig should copy settings")
    void testBuilderFromConfig() {
        com.ets2jsc.domain.model.config.CompilerConfig config =
                com.ets2jsc.domain.model.config.CompilerConfig.createDefault();
        config.setProjectPath("/test/project");
        config.setSourcePath("src/ets");
        config.setGenerateSourceMap(false);

        EtsCompiler compiler = EtsCompilerBuilder.fromConfig(config).build();

        assertEquals("/test/project", compiler.getConfig().getProjectPath());
        assertEquals("src/ets", compiler.getConfig().getSourcePath());
        assertFalse(compiler.getConfig().isGenerateSourceMap());
    }
}
