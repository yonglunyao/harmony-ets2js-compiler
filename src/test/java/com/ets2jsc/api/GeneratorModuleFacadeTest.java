package com.ets2jsc.api;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CodeGenerationException;
import com.ets2jsc.generator.internal.IJsWriter;
import com.ets2jsc.generator.internal.ISourceMapGenerator;
import com.ets2jsc.impl.GeneratorModuleFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GeneratorModuleFacade.
 */
@DisplayName("GeneratorModuleFacade Tests")
class GeneratorModuleFacadeTest {

    private static final String TEST_FILE_NAME = "Test.ets";

    @Test
    @DisplayName("generate should return JavaScript code for source file")
    void testGenerateReturnsCodeForSourceFile() throws CodeGenerationException {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config);
        SourceFile sourceFile = new SourceFile(TEST_FILE_NAME);

        // Act
        String result = facade.generate(sourceFile);

        // Assert
        assertNotNull(result);
        // Empty source file produces empty output
    }

    @Test
    @DisplayName("generate should throw exception for null source file")
    void testGenerateThrowsExceptionForNullSourceFile() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> facade.generate((SourceFile) null));
    }

    @Test
    @DisplayName("generateToFile should work with custom writer")
    void testGenerateToFileWritesCodeToFile() throws Exception {
        // Arrange
        AtomicBoolean writeCalled = new AtomicBoolean(false);

        IJsWriter customWriter = new IJsWriter() {
            @Override
            public void write(Path path, String content) throws Exception {
                writeCalled.set(true);
            }

            @Override
            public void writeWithSourceMap(Path path, String content, String sourceMapFileName) throws Exception {
                writeCalled.set(true);
            }

            @Override
            public void close() {
                // No-op
            }
        };

        ISourceMapGenerator mockGenerator = new ISourceMapGenerator() {
            @Override
            public String generate(SourceFile sourceFile) {
                return "{}";
            }

            @Override
            public void close() {
                // No-op
            }
        };

        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config, customWriter, mockGenerator);
        SourceFile sourceFile = new SourceFile(TEST_FILE_NAME);
        Path outputPath = Path.of("output.js");

        // Act
        facade.generateToFile(sourceFile, outputPath);

        // Assert
        assertTrue(writeCalled.get());
    }

    @Test
    @DisplayName("generateToFile should throw exception for null source file")
    void testGenerateToFileThrowsExceptionForNullSourceFile() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config);
        Path outputPath = Path.of("output.js");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> facade.generateToFile(null, outputPath));
    }

    @Test
    @DisplayName("generateToFile should throw exception for null output path")
    void testGenerateToFileThrowsExceptionForNullOutputPath() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config);
        SourceFile sourceFile = new SourceFile(TEST_FILE_NAME);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> facade.generateToFile(sourceFile, null));
    }

    @Test
    @DisplayName("generateWithSourceMap should work with custom writer and generator")
    void testGenerateWithSourceMapWorks() throws Exception {
        // Arrange
        AtomicBoolean writeCalled = new AtomicBoolean(false);
        AtomicBoolean sourceMapWriteCalled = new AtomicBoolean(false);

        IJsWriter customWriter = new IJsWriter() {
            @Override
            public void write(Path path, String content) throws Exception {
                sourceMapWriteCalled.set(true);
            }

            @Override
            public void writeWithSourceMap(Path path, String content, String sourceMapFileName) throws Exception {
                writeCalled.set(true);
            }

            @Override
            public void close() {
                // No-op
            }
        };

        ISourceMapGenerator mockGenerator = new ISourceMapGenerator() {
            @Override
            public String generate(SourceFile sourceFile) {
                return "{\"version\": 3}";
            }

            @Override
            public void close() {
                // No-op
            }
        };

        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config, customWriter, mockGenerator);
        SourceFile sourceFile = new SourceFile(TEST_FILE_NAME);
        Path outputPath = Path.of("output.js");
        Path sourceMapPath = Path.of("output.js.map");

        // Act
        facade.generateWithSourceMap(sourceFile, outputPath, sourceMapPath);

        // Assert
        assertTrue(writeCalled.get());
        assertTrue(sourceMapWriteCalled.get());
    }

    @Test
    @DisplayName("generateWithSourceMap should throw exception for null source file")
    void testGenerateWithSourceMapThrowsExceptionForNullSourceFile() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config);
        Path outputPath = Path.of("output.js");
        Path sourceMapPath = Path.of("output.js.map");

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> facade.generateWithSourceMap(null, outputPath, sourceMapPath));
    }

    @Test
    @DisplayName("generateWithSourceMap should throw exception for null output path")
    void testGenerateWithSourceMapThrowsExceptionForNullOutputPath() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config);
        SourceFile sourceFile = new SourceFile(TEST_FILE_NAME);
        Path sourceMapPath = Path.of("output.js.map");

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> facade.generateWithSourceMap(sourceFile, null, sourceMapPath));
    }

    @Test
    @DisplayName("generateWithSourceMap should throw exception for null source map path")
    void testGenerateWithSourceMapThrowsExceptionForNullSourceMapPath() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config);
        SourceFile sourceFile = new SourceFile(TEST_FILE_NAME);
        Path outputPath = Path.of("output.js");

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> facade.generateWithSourceMap(sourceFile, outputPath, null));
    }

    @Test
    @DisplayName("reconfigure should throw exception for null config")
    void testReconfigureThrowsExceptionForNullConfig() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> facade.reconfigure(null));
    }

    @Test
    @DisplayName("close should not throw exception")
    void testCloseDoesNotThrowException() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        GeneratorModuleFacade facade = new GeneratorModuleFacade(config);

        // Act & Assert
        assertDoesNotThrow(() -> facade.close());
    }
}
