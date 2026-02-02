package com.ets2jsc.parser;

import com.ets2jsc.parser.ProcessExecutor;
import com.ets2jsc.exception.ParserInitializationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProcessExecutor.
 */
@DisplayName("ProcessExecutor Tests")
class ProcessExecutorTest {

    @Test
    @DisplayName("Test ProcessExecutor construction with valid script path")
    void testConstructorWithValidPath(@TempDir Path tempDir) throws Exception {
        // Create a temporary JavaScript file
        Path scriptPath = tempDir.resolve("test-script.js");
        Files.writeString(scriptPath, "console.log('test');");

        ProcessExecutor executor = new ProcessExecutor(scriptPath.toString());

        assertNotNull(executor);
        assertEquals(scriptPath.toString(), executor.getScriptPath());
    }

    @Test
    @DisplayName("Test ProcessExecutor with null path stores the value")
    void testConstructorWithNullPath() {
        // The constructor accepts null and stores it
        // Validation happens later when execute() is called
        ProcessExecutor executor = new ProcessExecutor((String) null);
        assertNotNull(executor);
        assertNull(executor.getScriptPath());
    }

    @Test
    @DisplayName("Test ProcessExecutor with non-existent file should fail gracefully")
    void testConstructorWithNonExistentFile() {
        // This should not throw immediately during construction,
        // but will throw when trying to execute
        ProcessExecutor executor = new ProcessExecutor("non-existent-script.js");
        assertNotNull(executor);
    }

    @Test
    @DisplayName("Test ProcessExecutor.Result class")
    void testProcessResult() {
        ProcessExecutor.ProcessResult result = new ProcessExecutor.ProcessResult(0, "output", "errors");

        assertEquals(0, result.getExitCode());
        assertEquals("output", result.getOutput());
        assertEquals("errors", result.getErrorOutput());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("Test ProcessExecutor.Result with non-zero exit code")
    void testProcessResultFailure() {
        ProcessExecutor.ProcessResult result = new ProcessExecutor.ProcessResult(1, "error output", "");

        assertEquals(1, result.getExitCode());
        assertFalse(result.isSuccess());
    }
}
