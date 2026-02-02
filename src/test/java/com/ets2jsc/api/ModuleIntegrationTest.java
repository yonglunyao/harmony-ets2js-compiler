package com.ets2jsc.api;

import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.di.ModuleServiceProvider;
import com.ets2jsc.shared.exception.ParserException;
import com.ets2jsc.impl.DefaultModuleFactory;
import com.ets2jsc.impl.ParserModuleFacade;
import com.ets2jsc.impl.TransformerModuleFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the module facade architecture.
 * Tests the interaction between different modules through their facade interfaces.
 */
@DisplayName("Module Integration Tests")
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
    @DisplayName("ModuleFactory should create all required modules")
    void testModuleFactoryCreatesAllRequiredModules() {
        // Arrange & Act
        try (IModuleFactory factory = new DefaultModuleFactory()) {
            IParser parser = factory.createParser();
            ITransformer transformer = factory.createTransformer(CompilerConfig.createDefault());
            ICodeGenerator generator = factory.createCodeGenerator(CompilerConfig.createDefault());
            IConfig config = factory.createConfig();

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
        IParser parser = new ParserModuleFacade();

        // Act
        SourceFile result = parser.parseString("Test.ets", TEST_SOURCE_CODE);

        // Assert
        assertNotNull(result);
        assertEquals("Test.ets", result.getFileName());
        assertFalse(result.getStatements().isEmpty());
    }

    @Test
    @DisplayName("ParserModuleFacade should identify supported file types")
    void testParserModuleFacadeIdentifiesSupportedFileTypes(@TempDir Path tempDir) throws IOException {
        // Arrange
        IParser parser = new ParserModuleFacade();
        Path etsFile = tempDir.resolve("test.ets");
        Path tsFile = tempDir.resolve("test.ts");
        Path javaFile = tempDir.resolve("test.java");

        Files.createFile(etsFile);
        Files.createFile(tsFile);
        Files.createFile(javaFile);

        // Act & Assert
        assertTrue(parser.canParse(etsFile), "Should parse .ets files");
        assertTrue(parser.canParse(tsFile), "Should parse .ts files");
        assertFalse(parser.canParse(javaFile), "Should not parse .java files");
    }

    @Test
    @DisplayName("TransformerModuleFacade should transform source file")
    void testTransformerModuleFacadeTransformsSourceFile() {
        // Arrange
        CompilerConfig config = CompilerConfig.createDefault();
        ITransformer transformer = new TransformerModuleFacade(config);
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
        ITransformer transformer = new TransformerModuleFacade(config);

        // Act
        CompilerConfig newConfig = new CompilerConfig();
        newConfig.setPartialUpdateMode(false);
        assertDoesNotThrow(() -> transformer.reconfigure(newConfig));
    }

    @Test
    @DisplayName("ConfigModuleFacade should provide default configuration")
    void testConfigModuleFacadeProvidesDefaultConfiguration() {
        // Arrange & Act
        IConfig config = new com.ets2jsc.impl.ConfigModuleFacade();

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
        IConfig config = new com.ets2jsc.impl.ConfigModuleFacade();
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
        IModuleFactory factory = new DefaultModuleFactory();
        factory.createParser();

        // Act & Assert
        assertDoesNotThrow(() -> factory.close());
        assertTrue(((com.ets2jsc.impl.DefaultModuleFactory) factory).isClosed());
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
    @DisplayName("ModuleServiceProvider should provide modules")
    void testModuleServiceProviderProvidesModules() {
        // Arrange
        ModuleServiceProvider provider = ModuleServiceProvider.getInstance();

        // Act
        IParser parser = provider.getParser();
        IConfig config = provider.getConfig();

        // Assert
        assertNotNull(parser);
        assertNotNull(config);
    }

    @Test
    @DisplayName("Full compilation pipeline should work through facades")
    void testFullCompilationPipelineThroughFacades(@TempDir Path tempDir) throws Exception {
        // Arrange
        IModuleFactory factory = new DefaultModuleFactory();
        IParser parser = factory.createParser();
        ITransformer transformer = factory.createTransformer(CompilerConfig.createDefault());
        ICodeGenerator generator = factory.createCodeGenerator(CompilerConfig.createDefault());

        // Create a test file
        Path testFile = tempDir.resolve("Test.ets");
        Files.writeString(testFile, TEST_SOURCE_CODE);

        // Act
        SourceFile sourceFile = parser.parseFile(testFile);
        SourceFile transformedFile = transformer.transform(sourceFile);
        String jsCode = generator.generate(transformedFile);

        // Assert
        assertNotNull(sourceFile);
        assertNotNull(transformedFile);
        assertNotNull(jsCode);
        assertFalse(jsCode.isEmpty());

        // Cleanup
        factory.close();
    }

    @Test
    @DisplayName("Modules should be compatible with AutoCloseable")
    void testModulesAreCompatibleWithAutoCloseable() {
        // Arrange & Act
        IModuleFactory factory = new DefaultModuleFactory();

        // This should compile without issues
        try (factory) {
            IParser parser = factory.createParser();
            ITransformer transformer = factory.createTransformer(CompilerConfig.createDefault());
            ICodeGenerator generator = factory.createCodeGenerator(CompilerConfig.createDefault());

            // Assert
            assertNotNull(parser);
            assertNotNull(transformer);
            assertNotNull(generator);
        }
        // Factory should be closed automatically
    }
}
