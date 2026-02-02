package com.ets2jsc.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ParserException and its subclasses.
 */
@DisplayName("Parser Exception Tests")
class ParserExceptionTest {

    @Test
    @DisplayName("Test ParserException with message")
    void testParserExceptionMessage() {
        String message = "Parser failed";
        ParserException exception = new ParserException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Test ParserException with message and cause")
    void testParserExceptionMessageAndCause() {
        String message = "Parser failed";
        Throwable cause = new RuntimeException("Root cause");
        ParserException exception = new ParserException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Test ParserInitializationException")
    void testParserInitializationException() {
        String message = "Failed to initialize parser";
        Throwable cause = new IllegalStateException("TypeScript not found");
        ParserInitializationException exception = new ParserInitializationException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(exception instanceof ParserException);
    }

    @Test
    @DisplayName("Test SourceReadException")
    void testSourceReadException() {
        String message = "Cannot read source file";
        Throwable cause = new java.io.IOException("File not found");
        SourceReadException exception = new SourceReadException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(exception instanceof ParserException);
    }

    @Test
    @DisplayName("Test AstConversionException")
    void testAstConversionException() {
        String message = "Failed to convert AST";
        Throwable cause = new NullPointerException("AST node is null");
        AstConversionException exception = new AstConversionException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(exception instanceof ParserException);
    }

    @Test
    @DisplayName("Test CodeGenerationException")
    void testCodeGenerationException() {
        String message = "Failed to generate code";
        Throwable cause = new IllegalStateException("Invalid AST structure");
        CodeGenerationException exception = new CodeGenerationException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(exception instanceof ParserException);
    }

    @Test
    @DisplayName("Test exception without cause")
    void testExceptionWithoutCause() {
        ParserException exception = new ParserException("Error");

        assertNotNull(exception);
        assertEquals("Error", exception.getMessage());
        assertNull(exception.getCause());
    }
}
