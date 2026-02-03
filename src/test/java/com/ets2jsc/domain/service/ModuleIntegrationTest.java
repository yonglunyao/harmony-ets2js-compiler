package com.ets2jsc.domain.service;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.application.di.ModuleServiceProvider;
import com.ets2jsc.shared.exception.ParserException;
import com.ets2jsc.infrastructure.factory.DefaultModuleFactory;
import com.ets2jsc.infrastructure.parser.ParserModuleFacade;
import com.ets2jsc.infrastructure.transformer.TransformerModuleFacade;
import com.ets2jsc.shared.exception.CompilationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the service architecture.
 * Tests the interaction between different services through their interfaces.
 */
@DisplayName("Service Integration Tests")
class ModuleIntegrationTest {

    private static final String TEST_SOURCE_CODE = """
            @Component
            struct MyComponent {
                @State message: string = 'Hello';

                build() {
                    Text(this.message);
                }
            }
            """;

    @Test
    @DisplayName("ModuleFactory should create all required services")
    void testModuleFactoryCreatesAllRequiredModules() {
        // Arrange & Act
        try (ModuleFactoryService factory = new DefaultModuleFactory()) {
            ParserService parser = factory.createParser();
            TransformerService transformer = factory.createTransformer(CompilerConfig.createDefault());
            GeneratorService generator = factory.createGenerator(CompilerConfig.createDefault());
            ConfigService config = factory.createConfig();

            // Assert
            assertNotNull(parser);
            assertNotNull(transformer);
            assertNotNull(generator);
            assertNotNull(config);
        }
    }

    @Test
    @DisplayName("ParserModuleFacade should parse valid ETS code")
    void testParserModuleFacadeParsesValidEtsCode() throws ParserException {
        // Arrange
        ParserService parser = new ParserModuleFacade();

        // Act
        SourceFile result = parser.parseString("Test.ets", TEST_SOURCE_CODE);

        // Assert
        assertNotNull(result);
        assertEquals("Test.ets", result.getFileName());
        assertFalse(result.getStatements().isEmpty());
    }

    @Test
    @DisplayName("ParserModuleFacade should check file types correctly")
    void testParserModuleFacadeChecksFileTypesCorrectly(@TempDir Path tempDir) throws Exception {
        // Arrange
        ParserService parser = new ParserModuleFacade();

        // Create actual test files
        Path etsFile = tempDir.resolve("test.ets");
        Path tsFile = tempDir.resolve("test.ts");
        Path javaFile = tempDir.resolve("test.java");

        Files.writeString(etsFile, "@Component struct Test {}");
        Files.writeString(tsFile, "export class Test {}");
        Files.writeString(javaFile, "public class Test {}");

        // Act & Assert
        assertTrue(parser.canParse(etsFile), "Should parse .ets files");
        assertTrue(parser.canParse(tsFile), "Should parse .ts files");
        assertFalse(parser.canParse(javaFile), "Should not parse .java files");
    }

    @Test
    @DisplayName("TransformerModuleFacade should transform source file")
    void testTransformerModuleFacadeTransformsSourceFile() throws CompilationException {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerService transformer = new TransformerModuleFacade(config);
        SourceFile sourceFile = new SourceFile("Test.ets");

        // Act
        SourceFile result = transformer.transform(sourceFile);

        // Assert
        assertNotNull(result);
        assertSame(sourceFile, result);
    }

    @Test
    @DisplayName("TransformerModuleFacade should reconfigure with new config")
    void testTransformerModuleFacadeReconfiguresWithNewConfig() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        TransformerService transformer = new TransformerModuleFacade(config);

        // Act
        CompilerConfig newConfig = new CompilerConfig();
        newConfig.setPartialUpdateMode(false);
        assertDoesNotThrow(() -> transformer.reconfigure(newConfig));
    }

    @Test
    @DisplayName("ConfigModuleFacade should provide default configuration")
    void testConfigModuleFacadeProvidesDefaultConfiguration() {
        // Arrange & Act
        ConfigService config = new com.ets2jsc.infrastructure.factory.ConfigModuleFacade();

        // Act
        CompilerConfig result = config.createDefault();

        // Assert
        assertNotNull(result);
        assertTrue(config.isValid());
    }

    @Test
    @DisplayName("ConfigModuleFacade should load from properties")
    void testConfigModuleFacadeLoadsFromProperties() {
        // Arrange
        ConfigService config = new com.ets2jsc.infrastructure.factory.ConfigModuleFacade();
        java.util.Map<String, Object> properties = new java.util.HashMap<>();
        properties.put("projectPath", "/test/project");
        properties.put("buildPath", "build");
        properties.put("sourcePath", "src/main/ets");

        // Act
        CompilerConfig result = config.loadFromProperties(properties);

        // Assert
        assertNotNull(result);
        assertEquals("/test/project", result.getProjectPath());
        assertEquals("build", result.getBuildPath());
        assertEquals("src/main/ets", result.getSourcePath());
    }

    @Test
    @DisplayName("DefaultModuleFactory should close all resources")
    void testDefaultModuleFactoryClosesAllResources() {
        // Arrange
        ModuleFactoryService factory = new DefaultModuleFactory();
        factory.createParser();

        // Act & Assert
        assertDoesNotThrow(() -> factory.close());
        assertTrue(((DefaultModuleFactory) factory).isClosed());
    }

    @Test
    @DisplayName("ModuleServiceProvider should provide singleton instance")
    void testModuleServiceProviderProvidesSingletonInstance() {
        // Arrange & Act
        ModuleServiceProvider instance1 = ModuleServiceProvider.getInstance();
        ModuleServiceProvider instance2 = ModuleServiceProvider.getInstance();

        // Assert
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("ModuleServiceProvider should provide services")
    void testModuleServiceProviderProvidesServices() {
        // Arrange
        ModuleServiceProvider provider = ModuleServiceProvider.getInstance();

        // Act & Assert
        assertNotNull(provider.getParser());
        assertNotNull(provider.getTransformer(CompilerConfig.createDefault()));
        assertNotNull(provider.getGenerator(CompilerConfig.createDefault()));
        assertNotNull(provider.getConfig());
    }
}
