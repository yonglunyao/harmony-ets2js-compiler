package com.ets2jsc.domain.model.compilation;

import com.ets2jsc.domain.model.compilation.CompilationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CompilationResult
 */
@DisplayName("Compilation Result Tests")
class CompilationResultTest {

    @Test
    @DisplayName("Test compilation result creation")
    void testCompilationResultCreation() {
        CompilationResult result = new CompilationResult();

        assertEquals(0, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getSkippedCount());
        assertTrue(result.getDurationMs() >= 0);
    }

    @Test
    @DisplayName("Test add success result")
    void testAddSuccessResult() {
        CompilationResult result = new CompilationResult();
        java.nio.file.Path path = java.nio.file.Paths.get("test.ets");
        java.nio.file.Path outputPath = java.nio.file.Paths.get("test.js");

        CompilationResult.FileResult fileResult = CompilationResult.FileResult.success(path, outputPath, 100);
        result.addFileResult(path, fileResult);

        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertTrue(result.isAllSuccess());
    }

    @Test
    @DisplayName("Test add failure result")
    void testAddFailureResult() {
        CompilationResult result = new CompilationResult();
        java.nio.file.Path path = java.nio.file.Paths.get("error.ets");
        java.nio.file.Path outputPath = java.nio.file.Paths.get("error.js");

        CompilationResult.FileResult fileResult = CompilationResult.FileResult.failure(
            path, outputPath, "Syntax error", new Exception("Parse error"), 50);
        result.addFileResult(path, fileResult);

        assertEquals(1, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertFalse(result.isAllSuccess());
    }

    @Test
    @DisplayName("Test add skipped result")
    void testAddSkippedResult() {
        CompilationResult result = new CompilationResult();
        java.nio.file.Path path = java.nio.file.Paths.get("skip.ets");

        CompilationResult.FileResult fileResult = CompilationResult.FileResult.skipped(path, "File not found");
        result.addFileResult(path, fileResult);

        assertEquals(1, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(1, result.getSkippedCount());
    }

    @Test
    @DisplayName("Test get failures list")
    void testGetFailures() {
        CompilationResult result = new CompilationResult();
        java.nio.file.Path path1 = java.nio.file.Paths.get("error1.ets");
        java.nio.file.Path path2 = java.nio.file.Paths.get("error2.ets");

        CompilationResult.FileResult result1 = CompilationResult.FileResult.failure(
            path1, null, "Error 1", null, 10);
        CompilationResult.FileResult result2 = CompilationResult.FileResult.failure(
            path2, null, "Error 2", null, 20);

        result.addFileResult(path1, result1);
        result.addFileResult(path2, result2);

        var failures = result.getFailures();
        assertEquals(2, failures.size());
    }

    @Test
    @DisplayName("Test summary information")
    void testSummary() {
        CompilationResult result = new CompilationResult();
        java.nio.file.Path path = java.nio.file.Paths.get("test.ets");
        java.nio.file.Path outputPath = java.nio.file.Paths.get("test.js");

        CompilationResult.FileResult fileResult = CompilationResult.FileResult.success(path, outputPath, 100);
        result.addFileResult(path, fileResult);
        result.markCompleted();

        String summary = result.getSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("Total"));
        assertTrue(summary.contains("Success"));
        assertTrue(summary.contains("Duration"));
    }

    @Test
    @DisplayName("Test mark completed")
    void testMarkCompleted() {
        CompilationResult result = new CompilationResult();

        assertFalse(result.getDurationMs() < 0);

        result.markCompleted();

        long duration1 = result.getDurationMs();
        // Wait a moment to ensure time update
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // ignore
        }

        long duration2 = result.getDurationMs();
        assertTrue(duration2 >= duration1);
    }

    @Test
    @DisplayName("Test file result status")
    void testFileResultStatus() {
        java.nio.file.Path path = java.nio.file.Paths.get("test.ets");
        java.nio.file.Path outputPath = java.nio.file.Paths.get("test.js");

        CompilationResult.FileResult successResult = CompilationResult.FileResult.success(path, outputPath, 100);
        assertEquals(CompilationResult.Status.SUCCESS, successResult.getStatus());

        CompilationResult.FileResult failureResult = CompilationResult.FileResult.failure(
            path, outputPath, "Error", null, 50);
        assertEquals(CompilationResult.Status.FAILURE, failureResult.getStatus());

        CompilationResult.FileResult skippedResult = CompilationResult.FileResult.skipped(path, "Reason");
        assertEquals(CompilationResult.Status.SKIPPED, skippedResult.getStatus());
    }

    @Test
    @DisplayName("Test file result fields")
    void testFileResultFields() {
        java.nio.file.Path path = java.nio.file.Paths.get("test.ets");
        java.nio.file.Path outputPath = java.nio.file.Paths.get("test.js");

        CompilationResult.FileResult result = CompilationResult.FileResult.success(path, outputPath, 100);

        assertEquals(path, result.getSourcePath());
        assertEquals(outputPath, result.getOutputPath());
        assertEquals("Compilation succeeded", result.getMessage());
        assertNull(result.getError());
        assertEquals(100, result.getDurationMs());
    }
}
