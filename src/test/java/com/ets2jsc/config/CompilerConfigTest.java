package com.ets2jsc.config;

import com.ets2jsc.domain.model.config.CompilerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CompilerConfig
 */
@DisplayName("Compiler Configuration Tests")
class CompilerConfigTest {

    @Test
    @DisplayName("Test default configuration")
    void testDefaultConfig() {
        CompilerConfig config = CompilerConfig.createDefault();

        assertEquals(CompilerConfig.CompileMode.MODULE_JSON, config.getCompileMode());
        assertTrue(config.isPartialUpdateMode());
        assertFalse(config.isPreview());
        assertTrue(config.isGenerateSourceMap());
        assertFalse(config.isGenerateDeclarations());
        assertFalse(config.isMinifyOutput());
        assertTrue(config.isProcessTs());
        assertFalse(config.isEnableLazyImport());
        assertTrue(config.isValidateApi());
        assertFalse(config.isPureJavaScript());
    }

    @Test
    @DisplayName("Test compile mode setting")
    void testCompileMode() {
        CompilerConfig config = new CompilerConfig();

        config.setCompileMode(CompilerConfig.CompileMode.JSBUNDLE);
        assertEquals(CompilerConfig.CompileMode.JSBUNDLE, config.getCompileMode());

        config.setCompileMode(CompilerConfig.CompileMode.MODULE_JSON);
        assertEquals(CompilerConfig.CompileMode.MODULE_JSON, config.getCompileMode());

        config.setCompileMode(CompilerConfig.CompileMode.ES_MODULE);
        assertEquals(CompilerConfig.CompileMode.ES_MODULE, config.getCompileMode());
    }

    @Test
    @DisplayName("Test partial update mode")
    void testPartialUpdateMode() {
        CompilerConfig config = new CompilerConfig();

        config.setPartialUpdateMode(true);
        assertTrue(config.isPartialUpdateMode());
        assertEquals("initialRender", config.getRenderMethodName());

        config.setPartialUpdateMode(false);
        assertFalse(config.isPartialUpdateMode());
        assertEquals("render", config.getRenderMethodName());
    }

    @Test
    @DisplayName("Test pure JavaScript mode")
    void testPureJavaScriptMode() {
        CompilerConfig config = new CompilerConfig();

        config.setPureJavaScript(true);
        assertTrue(config.isPureJavaScript());

        config.setPureJavaScript(false);
        assertFalse(config.isPureJavaScript());
    }

    @Test
    @DisplayName("Test source map generation option")
    void testGenerateSourceMap() {
        CompilerConfig config = new CompilerConfig();

        config.setGenerateSourceMap(true);
        assertTrue(config.isGenerateSourceMap());

        config.setGenerateSourceMap(false);
        assertFalse(config.isGenerateSourceMap());
    }

    @Test
    @DisplayName("Test path configuration")
    void testPathConfiguration() {
        CompilerConfig config = new CompilerConfig();

        config.setProjectPath("/path/to/project");
        assertEquals("/path/to/project", config.getProjectPath());

        config.setBuildPath("/path/to/build");
        assertEquals("/path/to/build", config.getBuildPath());

        config.setSourcePath("/path/to/source");
        assertEquals("/path/to/source", config.getSourcePath());
    }

    @Test
    @DisplayName("Test entry point configuration")
    void testEntryConfiguration() {
        CompilerConfig config = new CompilerConfig();

        config.addEntry("index", "src/main/ets/Index.ets");
        assertEquals("src/main/ets/Index.ets", config.getEntryObj().get("index"));

        config.addEntry("pages", "src/main/ets/pages");
        assertEquals("src/main/ets/pages", config.getEntryObj().get("pages"));
        assertEquals(2, config.getEntryObj().size());
    }

    @Test
    @DisplayName("Test output options")
    void testOutputOptions() {
        CompilerConfig config = new CompilerConfig();

        config.setGenerateDeclarations(true);
        assertTrue(config.isGenerateDeclarations());

        config.setMinifyOutput(true);
        assertTrue(config.isMinifyOutput());

        config.setPreview(true);
        assertTrue(config.isPreview());
    }

    @Test
    @DisplayName("Test process options")
    void testProcessOptions() {
        CompilerConfig config = new CompilerConfig();

        config.setProcessTs(false);
        assertFalse(config.isProcessTs());

        config.setEnableLazyImport(true);
        assertTrue(config.isEnableLazyImport());

        config.setValidateApi(false);
        assertFalse(config.isValidateApi());
    }

    @Test
    @DisplayName("Test render method name")
    void testRenderMethodName() {
        CompilerConfig config = new CompilerConfig();

        // Partial update mode
        config.setPartialUpdateMode(true);
        assertEquals("initialRender", config.getRenderMethodName());

        // Standard mode
        config.setPartialUpdateMode(false);
        assertEquals("render", config.getRenderMethodName());
    }

    @Test
    @DisplayName("Test multiple configuration combinations")
    void testMultipleConfigurationCombinations() {
        CompilerConfig config = new CompilerConfig();

        config.setCompileMode(CompilerConfig.CompileMode.ES_MODULE);
        config.setPartialUpdateMode(false);
        config.setPureJavaScript(true);
        config.setGenerateSourceMap(false);
        config.setMinifyOutput(true);
        config.setProcessTs(false);

        assertEquals(CompilerConfig.CompileMode.ES_MODULE, config.getCompileMode());
        assertFalse(config.isPartialUpdateMode());
        assertTrue(config.isPureJavaScript());
        assertFalse(config.isGenerateSourceMap());
        assertTrue(config.isMinifyOutput());
        assertFalse(config.isProcessTs());
        assertEquals("render", config.getRenderMethodName());
    }
}
