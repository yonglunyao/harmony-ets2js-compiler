package com.ets2jsc.api;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.shared.exception.ParserException;
import com.ets2jsc.impl.ParserModuleFacade;
import com.ets2jsc.parser.internal.IAstBuilder;
import com.ets2jsc.parser.internal.ITypeScriptParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ParserModuleFacade.
 */
@DisplayName("ParserModuleFacade Tests")
class ParserModuleFacadeTest {

    private static final String TEST_FILE_NAME = "Test.ets";
    private static final String TEST_SOURCE_CODE = "const x: number = 42;";

    @Test
    @DisplayName("parseString should return valid SourceFile")
    void testParseStringReturnsValidSourceFile() {
        // Arrange
        ParserModuleFacade facade = new ParserModuleFacade();

        // Act & Assert
        // Note: This test requires a working TypeScript parser
        // In a real scenario, we'd use a mock, but since Mockito is not available,
        // we test the facade with the real parser
        assertDoesNotThrow(() -> {
            SourceFile result = facade.parseString(TEST_FILE_NAME, TEST_SOURCE_CODE);
            assertNotNull(result);
            assertEquals(TEST_FILE_NAME, result.getFileName());
        });
    }

    @Test
    @DisplayName("parseString should throw exception for null fileName")
    void testParseStringThrowsExceptionForNullFileName() {
        // Arrange
        ParserModuleFacade facade = new ParserModuleFacade();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> facade.parseString(null, TEST_SOURCE_CODE));
    }

    @Test
    @DisplayName("parseString should throw exception for empty fileName")
    void testParseStringThrowsExceptionForEmptyFileName() {
        // Arrange
        ParserModuleFacade facade = new ParserModuleFacade();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> facade.parseString("", TEST_SOURCE_CODE));
    }

    @Test
    @DisplayName("parseString should throw exception for null sourceCode")
    void testParseStringThrowsExceptionForNullSourceCode() {
        // Arrange
        ParserModuleFacade facade = new ParserModuleFacade();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> facade.parseString(TEST_FILE_NAME, null));
    }

    @Test
    @DisplayName("canParse should return false for non-existent .ets files")
    void testCanParseReturnsFalseForNonExistentEtsFiles() {
        // Arrange
        ParserModuleFacade facade = new ParserModuleFacade();
        Path etsFile = Path.of("test.ets");

        // Act
        boolean result = facade.canParse(etsFile);

        // Assert
        // canParse returns false for non-existent files (checks isRegularFile)
        assertFalse(result);
    }

    @Test
    @DisplayName("canParse should return false for non-existent .ts files")
    void testCanParseReturnsFalseForNonExistentTsFiles() {
        // Arrange
        ParserModuleFacade facade = new ParserModuleFacade();
        Path tsFile = Path.of("test.ts");

        // Act
        boolean result = facade.canParse(tsFile);

        // Assert
        // canParse returns false for non-existent files (checks isRegularFile)
        assertFalse(result);
    }

    @Test
    @DisplayName("canParse should return false for unsupported extensions")
    void testCanParseReturnsFalseForUnsupportedExtensions() {
        // Arrange
        ParserModuleFacade facade = new ParserModuleFacade();
        Path javaFile = Path.of("test.java");

        // Act
        boolean result = facade.canParse(javaFile);

        // Assert
        // Even if the file doesn't exist, it checks extension
        assertFalse(result);
    }

    @Test
    @DisplayName("canParse should return false for null path")
    void testCanParseReturnsFalseForNullPath() {
        // Arrange
        ParserModuleFacade facade = new ParserModuleFacade();

        // Act
        boolean result = facade.canParse(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("close should not throw exception")
    void testCloseDoesNotThrowException() {
        // Arrange
        ParserModuleFacade facade = new ParserModuleFacade();

        // Act & Assert
        assertDoesNotThrow(() -> facade.close());
    }
}
