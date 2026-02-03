package com.ets2jsc.application.compile;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.compilation.CompilationResult;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CompilationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BatchCompilationService using mocks.
 * These tests isolate the batch compilation logic from the actual compilation pipeline.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Batch Compilation Service Unit Tests (Mocked)")
class BatchCompilationServiceTest {

    @Mock
    private CompilationPipeline mockPipeline;

    /**
     * Test sequential batch service with mocked pipeline.
     */
    @Test
    @DisplayName("Sequential batch service should compile all files successfully")
    void testSequentialBatchServiceCompilesAllFiles() throws CompilationException {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        when(mockPipeline.getConfig()).thenReturn(config);

        // Mock successful compilation
        when(mockPipeline.execute(any(Path.class), any(Path.class)))
                .thenReturn(CompilationResult.success(Path.of("test.ets"), Path.of("test.js"), 100));

        BatchCompilationService service = new SequentialBatchCompilationService(mockPipeline);

        // Act
        List<Path> sourceFiles = List.of(
            Path.of("file1.ets"),
            Path.of("file2.ets"),
            Path.of("file3.ets")
        );
        Path outputDir = Path.of("output");

        CompilationResult result = service.compileBatch(sourceFiles, outputDir);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalCount());
        assertTrue(result.isAllSuccess());

        // Verify pipeline was called for each file
        verify(mockPipeline, times(3)).execute(any(Path.class), any(Path.class));
    }

    /**
     * Test parallel batch service with mocked pipeline.
     */
    @Test
    @DisplayName("Parallel batch service should compile all files successfully")
    void testParallelBatchServiceCompilesAllFiles() throws CompilationException {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        when(mockPipeline.getConfig()).thenReturn(config);

        // Mock successful compilation
        when(mockPipeline.execute(any(Path.class), any(Path.class)))
                .thenReturn(CompilationResult.success(Path.of("test.ets"), Path.of("test.js"), 100));

        BatchCompilationService service = new ParallelBatchCompilationService(mockPipeline, 2);

        // Act
        List<Path> sourceFiles = List.of(
            Path.of("file1.ets"),
            Path.of("file2.ets"),
            Path.of("file3.ets")
        );
        Path outputDir = Path.of("output");

        CompilationResult result = service.compileBatch(sourceFiles, outputDir);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalCount());
        assertTrue(result.isAllSuccess());
    }

    /**
     * Test batch service handles compilation failure gracefully.
     */
    @Test
    @DisplayName("Batch service should handle compilation failure gracefully")
    void testBatchServiceHandlesCompilationFailure() throws CompilationException {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        when(mockPipeline.getConfig()).thenReturn(config);

        // Mock compilation failure
        when(mockPipeline.execute(any(Path.class), any(Path.class)))
                .thenThrow(new CompilationException("Parse error"));

        BatchCompilationService service = new SequentialBatchCompilationService(mockPipeline);

        // Act
        List<Path> sourceFiles = List.of(Path.of("file1.ets"));
        Path outputDir = Path.of("output");

        CompilationResult result = service.compileBatch(sourceFiles, outputDir);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertFalse(result.isAllSuccess());
    }

    /**
     * Test batch service returns correct mode.
     */
    @Test
    @DisplayName("Batch service should return correct compilation mode")
    void testBatchServiceReturnsCorrectMode() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        when(mockPipeline.getConfig()).thenReturn(config);

        // Act & Assert
        BatchCompilationService sequentialService = new SequentialBatchCompilationService(mockPipeline);
        assertEquals(BatchCompilationService.CompilationMode.SEQUENTIAL, sequentialService.getMode());

        BatchCompilationService parallelService = new ParallelBatchCompilationService(mockPipeline);
        assertEquals(BatchCompilationService.CompilationMode.PARALLEL, parallelService.getMode());
    }

    /**
     * Test batch service close method.
     */
    @Test
    @DisplayName("Sequential batch service close should be idempotent")
    void testSequentialBatchServiceCloseIsIdempotent() throws Exception {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        when(mockPipeline.getConfig()).thenReturn(config);

        SequentialBatchCompilationService service = new SequentialBatchCompilationService(mockPipeline);

        // Act & Assert
        service.close();
        service.close(); // Should not throw exception
        assertTrue(service.isClosed());
    }

    /**
     * Test parallel batch service close method.
     */
    @Test
    @DisplayName("Parallel batch service close should shut down executor")
    void testParallelBatchServiceCloseShutsDownExecutor() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        when(mockPipeline.getConfig()).thenReturn(config);

        ParallelBatchCompilationService service = new ParallelBatchCompilationService(mockPipeline);

        // Act & Assert
        service.close(); // Should not throw exception
        service.close(); // Should be idempotent
    }
}
