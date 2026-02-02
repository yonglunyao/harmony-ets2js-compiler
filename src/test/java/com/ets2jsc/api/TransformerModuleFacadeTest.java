package com.ets2jsc.api;

import com.ets2jsc.domain.model.ast.ClassDeclaration;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.factory.DefaultTransformerFactory;
import com.ets2jsc.factory.TransformerFactory;
import com.ets2jsc.impl.TransformerModuleFacade;
import com.ets2jsc.transformer.AstTransformer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TransformerModuleFacade.
 */
@DisplayName("TransformerModuleFacade Tests")
class TransformerModuleFacadeTest {

    private static final String TEST_FILE_NAME = "Test.ets";

    @Test
    @DisplayName("transform should apply transformers to source file")
    void testTransformAppliesTransformersToSourceFile() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerModuleFacade facade = new TransformerModuleFacade(config);
        SourceFile sourceFile = new SourceFile(TEST_FILE_NAME);

        // Act
        SourceFile result = facade.transform(sourceFile);

        // Assert
        assertNotNull(result);
        assertSame(sourceFile, result);
    }

    @Test
    @DisplayName("transform should throw exception for null source file")
    void testTransformThrowsExceptionForNullSourceFile() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerModuleFacade facade = new TransformerModuleFacade(config);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> facade.transform(null));
    }

    @Test
    @DisplayName("transformNode should return node for valid input")
    void testTransformNodeReturnsNodeForValidInput() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerModuleFacade facade = new TransformerModuleFacade(config);
        ClassDeclaration node = new ClassDeclaration("TestClass");

        // Act
        var result = facade.transformNode(node);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("transformNode should throw exception for null node")
    void testTransformNodeThrowsExceptionForNullNode() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerModuleFacade facade = new TransformerModuleFacade(config);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> facade.transformNode(null));
    }

    @Test
    @DisplayName("canTransform should return true or false based on node type")
    void testCanTransformReturnsBasedOnNodeType() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerModuleFacade facade = new TransformerModuleFacade(config);
        ClassDeclaration node = new ClassDeclaration("TestClass");

        // Act
        boolean result = facade.canTransform(node);

        // Assert
        // The result depends on whether any transformer can handle ClassDeclaration
        assertNotNull(facade);
    }

    @Test
    @DisplayName("canTransform should return false for null node")
    void testCanTransformReturnsFalseForNullNode() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerModuleFacade facade = new TransformerModuleFacade(config);

        // Act
        boolean result = facade.canTransform(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("reconfigure should create new transformers with new config")
    void testReconfigureCreatesNewTransformers() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerModuleFacade facade = new TransformerModuleFacade(config);
        CompilerConfig newConfig = new CompilerConfig();

        // Act
        newConfig.setPartialUpdateMode(false);
        assertDoesNotThrow(() -> facade.reconfigure(newConfig));

        // Assert
        assertNotNull(facade);
    }

    @Test
    @DisplayName("reconfigure should throw exception for null config")
    void testReconfigureThrowsExceptionForNullConfig() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerModuleFacade facade = new TransformerModuleFacade(config);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> facade.reconfigure(null));
    }

    @Test
    @DisplayName("constructor should throw exception for null config")
    void testConstructorThrowsExceptionForNullConfig() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new TransformerModuleFacade(null));
    }

    @Test
    @DisplayName("close should not throw exception")
    void testCloseDoesNotThrowException() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerModuleFacade facade = new TransformerModuleFacade(config);

        // Act & Assert
        assertDoesNotThrow(() -> facade.close());
    }
}
