package com.ets2jsc.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SourceFileFinder.
 */
@DisplayName("SourceFileFinder Tests")
class SourceFileFinderTest {

    @Test
    @DisplayName("Test isEtsFile returns true for .ets files")
    void testIsEtsFile() {
        Path etsFile = Path.of("src/App.ets");
        assertTrue(SourceFileFinder.isEtsFile(etsFile));

        Path tsFile = Path.of("src/App.ts");
        assertFalse(SourceFileFinder.isEtsFile(tsFile));

        Path jsFile = Path.of("src/App.js");
        assertFalse(SourceFileFinder.isEtsFile(jsFile));
    }

    @Test
    @DisplayName("Test isTsFile returns true for .ts files")
    void testIsTsFile() {
        Path tsFile = Path.of("src/App.ts");
        assertTrue(SourceFileFinder.isTsFile(tsFile));

        Path etsFile = Path.of("src/App.ets");
        assertFalse(SourceFileFinder.isTsFile(etsFile));

        Path jsFile = Path.of("src/App.js");
        assertFalse(SourceFileFinder.isTsFile(jsFile));
    }

    @Test
    @DisplayName("Test isSourceFile returns true for both .ets and .ts files")
    void testIsSourceFile() {
        Path etsFile = Path.of("src/App.ets");
        assertTrue(SourceFileFinder.isSourceFile(etsFile));

        Path tsFile = Path.of("src/App.ts");
        assertTrue(SourceFileFinder.isSourceFile(tsFile));

        Path jsFile = Path.of("src/App.js");
        assertFalse(SourceFileFinder.isSourceFile(jsFile));

        Path txtFile = Path.of("README.txt");
        assertFalse(SourceFileFinder.isSourceFile(txtFile));
    }

    @Test
    @DisplayName("Test findSourceFiles in empty directory")
    void testFindSourceFilesInEmptyDirectory(@TempDir Path tempDir) throws IOException {
        List<Path> files = SourceFileFinder.findSourceFiles(tempDir);

        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    @DisplayName("Test findSourceFiles with mixed file types")
    void testFindSourceFilesWithMixedTypes(@TempDir Path tempDir) throws IOException {
        // Create test files
        Files.writeString(tempDir.resolve("App.ets"), "struct App {}");
        Files.writeString(tempDir.resolve("utils.ts"), "export function foo() {}");
        Files.writeString(tempDir.resolve("README.md"), "# README");
        Files.createDirectory(tempDir.resolve("subdir"));
        Files.writeString(tempDir.resolve("subdir/component.ets"), "struct Component {}");

        List<Path> files = SourceFileFinder.findSourceFiles(tempDir);

        assertNotNull(files);
        assertEquals(3, files.size());

        // Check that only .ets and .ts files are included
        assertTrue(files.stream().allMatch(SourceFileFinder::isSourceFile));
    }

    @Test
    @DisplayName("Test findEtsFiles returns only .ets files")
    void testFindEtsFiles(@TempDir Path tempDir) throws IOException {
        // Create test files
        Files.writeString(tempDir.resolve("App.ets"), "struct App {}");
        Files.writeString(tempDir.resolve("utils.ts"), "export function foo() {}");
        Files.writeString(tempDir.resolve("component.ets"), "struct Component {}");

        List<Path> files = SourceFileFinder.findEtsFiles(tempDir);

        assertNotNull(files);
        assertEquals(2, files.size());

        // All should be .ets files
        assertTrue(files.stream().allMatch(SourceFileFinder::isEtsFile));
    }

    @Test
    @DisplayName("Test findTsFiles returns only .ts files")
    void testFindTsFiles(@TempDir Path tempDir) throws IOException {
        // Create test files
        Files.writeString(tempDir.resolve("utils.ts"), "export function foo() {}");
        Files.writeString(tempDir.resolve("App.ets"), "struct App {}");
        Files.writeString(tempDir.resolve("helper.ts"), "export function bar() {}");

        List<Path> files = SourceFileFinder.findTsFiles(tempDir);

        assertNotNull(files);
        assertEquals(2, files.size());

        // All should be .ts files
        assertTrue(files.stream().allMatch(SourceFileFinder::isTsFile));
    }

    @Test
    @DisplayName("Test findSourceFiles with custom predicate")
    void testFindSourceFilesWithCustomPredicate(@TempDir Path tempDir) throws IOException {
        // Create test files
        Files.writeString(tempDir.resolve("test.ets"), "struct Test {}");
        Files.writeString(tempDir.resolve("test.ts"), "export function test() {}");
        Files.writeString(tempDir.resolve("other.txt"), "text");

        // Find only .ts files using custom predicate
        List<Path> files = SourceFileFinder.findSourceFiles(tempDir, SourceFileFinder::isTsFile);

        assertNotNull(files);
        assertEquals(1, files.size());
        assertTrue(SourceFileFinder.isTsFile(files.get(0)));
    }

    @Test
    @DisplayName("Test findSourceFiles with non-directory path")
    void testFindSourceFilesWithNonDirectoryPath(@TempDir Path tempDir) throws IOException {
        // Create a file instead of directory
        Path file = tempDir.resolve("test.ets");
        Files.writeString(file, "struct Test {}");

        // Should return empty list for non-directory path
        List<Path> files = SourceFileFinder.findSourceFiles(file);

        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    @DisplayName("Test isEtsFile with case sensitivity")
    void testIsEtsFileCaseSensitivity() {
        Path lowercase = Path.of("test.ets");
        assertTrue(SourceFileFinder.isEtsFile(lowercase));

        Path uppercase = Path.of("test.ETS");
        assertFalse(SourceFileFinder.isEtsFile(uppercase));

        Path mixed = Path.of("test.Ets");
        assertFalse(SourceFileFinder.isEtsFile(mixed));
    }

    @Test
    @DisplayName("Test isTsFile with case sensitivity")
    void testIsTsFileCaseSensitivity() {
        Path lowercase = Path.of("test.ts");
        assertTrue(SourceFileFinder.isTsFile(lowercase));

        Path uppercase = Path.of("test.TS");
        assertFalse(SourceFileFinder.isTsFile(uppercase));
    }

    @Test
    @DisplayName("Test findSourceFiles with nested directories")
    void testFindSourceFilesWithNestedDirectories(@TempDir Path tempDir) throws IOException {
        // Create nested directory structure
        Path level1 = tempDir.resolve("level1");
        Path level2 = level1.resolve("level2");
        Files.createDirectories(level2);

        Files.writeString(tempDir.resolve("root.ets"), "struct Root {}");
        Files.writeString(level1.resolve("level1.ets"), "struct Level1 {}");
        Files.writeString(level2.resolve("level2.ets"), "struct Level2 {}");

        List<Path> files = SourceFileFinder.findSourceFiles(tempDir);

        assertNotNull(files);
        assertEquals(3, files.size());
    }

    @Test
    @DisplayName("Test findSourceFiles returns absolute paths")
    void testFindSourceFilesReturnsAbsolutePath(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("test.ets"), "struct Test {}");

        List<Path> files = SourceFileFinder.findSourceFiles(tempDir);

        assertNotNull(files);
        assertFalse(files.isEmpty());
        assertTrue(files.get(0).isAbsolute());
    }
}
