package com.ets2jsc.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ResourceFileCopier.
 */
@DisplayName("ResourceFileCopier Tests")
class ResourceFileCopierTest {

    @Test
    @DisplayName("Test copyResourceFiles creates target directory")
    void testCopyResourceFilesCreatesTargetDirectory(@TempDir Path tempDir) throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        Path targetDir = tempDir.resolve("target");

        ResourceFileCopier.copyResourceFiles(sourceDir, targetDir);

        assertTrue(Files.exists(targetDir), "Target directory should be created");
        assertTrue(Files.isDirectory(targetDir), "Target should be a directory");
    }

    @Test
    @DisplayName("Test copyResourceFiles copies non-source files")
    void testCopyResourceFilesCopiesNonSourceFiles(@TempDir Path tempDir) throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        // Create a non-source file
        Path imageFile = sourceDir.resolve("logo.png");
        Files.writeString(imageFile, "fake-image-data");

        Path targetDir = tempDir.resolve("target");

        int copiedCount = ResourceFileCopier.copyResourceFiles(sourceDir, targetDir);

        assertEquals(1, copiedCount, "Should copy 1 file");
        Path targetFile = targetDir.resolve("logo.png");
        assertTrue(Files.exists(targetFile), "Target file should exist");
    }

    @Test
    @DisplayName("Test copyResourceFiles preserves directory structure")
    void testCopyResourceFilesPreservesDirectoryStructure(@TempDir Path tempDir) throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Path subDir = sourceDir.resolve("assets/images");
        Files.createDirectories(subDir);

        Path imageFile = subDir.resolve("icon.png");
        Files.writeString(imageFile, "fake-image-data");

        Path targetDir = tempDir.resolve("target");

        ResourceFileCopier.copyResourceFiles(sourceDir, targetDir);

        Path targetFile = targetDir.resolve("assets/images/icon.png");
        assertTrue(Files.exists(targetFile), "Target file should maintain directory structure");
    }

    @Test
    @DisplayName("Test copyResourceFiles skips .ets source files")
    void testCopyResourceFilesSkipsEtsFiles(@TempDir Path tempDir) throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        // Create source files
        Path etsFile = sourceDir.resolve("App.ets");
        Files.writeString(etsFile, "@Component struct App {}");

        Path jsonFile = sourceDir.resolve("config.json");
        Files.writeString(jsonFile, "{}");

        Path targetDir = tempDir.resolve("target");

        int copiedCount = ResourceFileCopier.copyResourceFiles(sourceDir, targetDir);

        assertEquals(1, copiedCount, "Should only copy JSON file, not .ets file");
        assertTrue(Files.exists(targetDir.resolve("config.json")), "JSON file should be copied");
        assertFalse(Files.exists(targetDir.resolve("App.js")), ".ets file should not be copied");
    }

    @Test
    @DisplayName("Test copyResourceFiles skips .ts source files")
    void testCopyResourceFilesSkipsTsFiles(@TempDir Path tempDir) throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        Path tsFile = sourceDir.resolve("utils.ts");
        Files.writeString(tsFile, "export function foo() {}");

        Path txtFile = sourceDir.resolve("readme.txt");
        Files.writeString(txtFile, "readme content");

        Path targetDir = tempDir.resolve("target");

        int copiedCount = ResourceFileCopier.copyResourceFiles(sourceDir, targetDir);

        assertEquals(1, copiedCount, "Should only copy txt file, not .ts file");
    }

    @Test
    @DisplayName("Test copyResourceFiles skips excluded directories")
    void testCopyResourceFilesSkipsExcludedDirectories(@TempDir Path tempDir) throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Path nodeModules = sourceDir.resolve("node_modules");
        Files.createDirectories(nodeModules);

        Path npmFile = nodeModules.resolve("package.json");
        Files.writeString(npmFile, "{}");

        Path targetDir = tempDir.resolve("target");

        int copiedCount = ResourceFileCopier.copyResourceFiles(sourceDir, targetDir);

        assertEquals(0, copiedCount, "Should not copy files from excluded directories");
    }

    @Test
    @DisplayName("Test isExcludedDirectory returns true for node_modules")
    void testIsExcludedDirectoryForNodeModules() {
        assertTrue(ResourceFileCopier.isExcludedDirectory("node_modules"));
    }

    @Test
    @DisplayName("Test isExcludedDirectory returns true for .git")
    void testIsExcludedDirectoryForGit() {
        assertTrue(ResourceFileCopier.isExcludedDirectory(".git"));
    }

    @Test
    @DisplayName("Test isExcludedDirectory returns false for normal directories")
    void testIsExcludedDirectoryForNormalDirectories() {
        assertFalse(ResourceFileCopier.isExcludedDirectory("src"));
        assertFalse(ResourceFileCopier.isExcludedDirectory("assets"));
    }

    @Test
    @DisplayName("Test isSourceExtension returns true for .ets")
    void testIsSourceExtensionForEts() {
        assertTrue(ResourceFileCopier.isSourceExtension(".ets"));
    }

    @Test
    @DisplayName("Test isSourceExtension returns true for .ts")
    void testIsSourceExtensionForTs() {
        assertTrue(ResourceFileCopier.isSourceExtension(".ts"));
    }

    @Test
    @DisplayName("Test isSourceExtension returns false for non-source extensions")
    void testIsSourceExtensionForNonSourceExtensions() {
        assertFalse(ResourceFileCopier.isSourceExtension(".json"));
        assertFalse(ResourceFileCopier.isSourceExtension(".png"));
    }

    @Test
    @DisplayName("Test addSourceExtension adds custom extension")
    void testAddSourceExtensionAddsCustomExtension(@TempDir Path tempDir) throws IOException {
        ResourceFileCopier.addSourceExtension(".custom");

        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        Path customFile = sourceDir.resolve("file.custom");
        Files.writeString(customFile, "content");

        Path otherFile = sourceDir.resolve("file.txt");
        Files.writeString(otherFile, "content");

        Path targetDir = tempDir.resolve("target");

        int copiedCount = ResourceFileCopier.copyResourceFiles(sourceDir, targetDir);

        assertEquals(1, copiedCount, "Should only copy txt file, custom extension should be skipped");
    }

    @Test
    @DisplayName("Test addExcludedDirectory adds custom directory")
    void testAddExcludedDirectoryAddsCustomDirectory(@TempDir Path tempDir) throws IOException {
        ResourceFileCopier.addExcludedDirectory("custom_exclude");

        Path sourceDir = tempDir.resolve("source");
        Path customDir = sourceDir.resolve("custom_exclude");
        Files.createDirectories(customDir);

        Path file = customDir.resolve("file.txt");
        Files.writeString(file, "content");

        Path targetDir = tempDir.resolve("target");

        int copiedCount = ResourceFileCopier.copyResourceFiles(sourceDir, targetDir);

        assertEquals(0, copiedCount, "Should not copy files from custom excluded directory");
    }

    @Test
    @DisplayName("Test copyResourceFiles with empty source directory")
    void testCopyResourceFilesWithEmptySourceDirectory(@TempDir Path tempDir) throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        Path targetDir = tempDir.resolve("target");

        int copiedCount = ResourceFileCopier.copyResourceFiles(sourceDir, targetDir);

        assertEquals(0, copiedCount, "Should copy 0 files from empty directory");
    }

    @Test
    @DisplayName("Test copyResourceFiles skips .map files")
    void testCopyResourceFilesSkipsMapFiles(@TempDir Path tempDir) throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);

        Path mapFile = sourceDir.resolve("app.js.map");
        Files.writeString(mapFile, "{}");

        Path txtFile = sourceDir.resolve("readme.txt");
        Files.writeString(txtFile, "readme");

        Path targetDir = tempDir.resolve("target");

        int copiedCount = ResourceFileCopier.copyResourceFiles(sourceDir, targetDir);

        assertEquals(1, copiedCount, "Should skip .map files");
    }
}
