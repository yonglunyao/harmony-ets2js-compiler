package com.ets2jsc;

import com.ets2jsc.compiler.CompilerFactory;
import com.ets2jsc.compiler.CompilationResult;
import com.ets2jsc.compiler.ICompiler;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.exception.CompilationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for project compilation functionality.
 */
@DisplayName("Project Compilation Tests")
class EtsCompilerProjectTest {

    @Test
    @DisplayName("Test compileProject creates output directory")
    void testCompileProjectCreatesOutputDirectory(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        Path outputDir = tempDir.resolve("output");

        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            CompilationResult result = compiler.compileProject(sourceDir, outputDir, false);

            assertTrue(Files.exists(outputDir), "Output directory should be created");
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Test compileProject throws exception for non-directory source")
    void testCompileProjectThrowsExceptionForNonDirectorySource(@TempDir Path tempDir) throws Exception {
        Path sourceFile = tempDir.resolve("source.ets");
        Files.writeString(sourceFile, "@Component struct App {}");

        Path outputDir = tempDir.resolve("output");

        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            assertThrows(CompilationException.class, () -> {
                compiler.compileProject(sourceFile, outputDir, false);
            });
        }
    }

    @Test
    @DisplayName("Test compileProject handles empty source directory")
    void testCompileProjectHandlesEmptySourceDirectory(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        Path outputDir = tempDir.resolve("output");

        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            CompilationResult result = compiler.compileProject(sourceDir, outputDir, false);

            assertEquals(0, result.getTotalCount());
            assertTrue(result.isAllSuccess());
        }
    }

    @Test
    @DisplayName("Test compileProject with copyResources=false does not copy resources")
    void testCompileProjectWithoutCopyResources(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        // Create a resource file
        Path jsonFile = sourceDir.resolve("config.json");
        Files.writeString(jsonFile, "{}");

        Path outputDir = tempDir.resolve("output");

        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            CompilationResult result = compiler.compileProject(sourceDir, outputDir, false);

            assertEquals(0, result.getCopiedResourceCount());
        }
    }

    @Test
    @DisplayName("Test compileProject with copyResources=true copies resources")
    void testCompileProjectWithCopyResources(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        // Create a resource file
        Path jsonFile = sourceDir.resolve("config.json");
        Files.writeString(jsonFile, "{}");

        Path outputDir = tempDir.resolve("output");

        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            CompilationResult result = compiler.compileProject(sourceDir, outputDir, true);

            assertTrue(result.getCopiedResourceCount() > 0, "Should copy resource files");
        }
    }

    @Test
    @DisplayName("Test compileProject preserves directory structure")
    void testCompileProjectPreservesDirectoryStructure(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Path pagesDir = sourceDir.resolve("pages");
        Files.createDirectories(pagesDir);

        // Create source file in subdirectory
        Path etsFile = pagesDir.resolve("Index.ets");
        Files.writeString(etsFile, "@Component struct Index { build() { Text('Hello') } }");

        Path outputDir = tempDir.resolve("output");

        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            CompilationResult result = compiler.compileProject(sourceDir, outputDir, false);

            assertTrue(result.getTotalCount() > 0);
            // Check that output file is in corresponding subdirectory
            Path outputFile = outputDir.resolve("pages/Index.js");
            assertTrue(Files.exists(outputFile), "Output should preserve directory structure");
        }
    }

    @Test
    @DisplayName("Test compileBatchWithStructure preserves relative paths")
    void testCompileBatchWithStructurePreservesRelativePaths(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Path subDir = sourceDir.resolve("components");
        Files.createDirectories(subDir);

        Path etsFile = subDir.resolve("Button.ets");
        Files.writeString(etsFile, "@Component struct Button { build() { Text('Click') } }");

        Path outputDir = tempDir.resolve("output");

        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            CompilationResult result = compiler.compileBatchWithStructure(
                    java.util.List.of(etsFile), sourceDir, outputDir);

            assertTrue(result.getSuccessCount() > 0);
            // Verify output file is in subdirectory
            Path outputFile = outputDir.resolve("components/Button.js");
            assertTrue(Files.exists(outputFile), "Output file should maintain relative path");
        }
    }

    @Test
    @DisplayName("Test compileProject copies resource files to correct locations")
    void testCompileProjectCopiesResourcesToCorrectLocations(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Path assetsDir = sourceDir.resolve("assets");
        Files.createDirectories(assetsDir);

        Path imageFile = assetsDir.resolve("logo.png");
        Files.writeString(imageFile, "fake-image-data");

        Path outputDir = tempDir.resolve("output");

        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            CompilationResult result = compiler.compileProject(sourceDir, outputDir, true);

            // Check resource file is copied with correct path
            Path outputImage = outputDir.resolve("assets/logo.png");
            assertTrue(Files.exists(outputImage), "Resource file should be copied to correct location");
        }
    }

    @Test
    @DisplayName("Test compileProject result summary contains correct information")
    void testCompileProjectResultSummaryContainsCorrectInformation(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        Path etsFile = sourceDir.resolve("App.ets");
        Files.writeString(etsFile, "@Component struct App { build() { Text('Hello') } }");

        Path jsonFile = sourceDir.resolve("config.json");
        Files.writeString(jsonFile, "{}");

        Path outputDir = tempDir.resolve("output");

        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            CompilationResult result = compiler.compileProject(sourceDir, outputDir, true);

            String summary = result.getSummary();
            assertTrue(summary.contains("Total"));
            assertTrue(summary.contains("Success"));
        }
    }

    @Test
    @DisplayName("Test compileProject creates parent directories for output files")
    void testCompileProjectCreatesParentDirectories(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Path deepDir = sourceDir.resolve("a/b/c/d");
        Files.createDirectories(deepDir);

        Path etsFile = deepDir.resolve("Deep.ets");
        Files.writeString(etsFile, "@Component struct Deep { build() { Text('Deep') } }");

        Path outputDir = tempDir.resolve("output");

        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            CompilationResult result = compiler.compileProject(sourceDir, outputDir, false);

            // Verify all parent directories were created
            Path outputFile = outputDir.resolve("a/b/c/d/Deep.js");
            assertTrue(Files.exists(outputFile), "All parent directories should be created");
        }
    }
}
