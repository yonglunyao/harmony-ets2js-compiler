package com.ets2jsc.config;

import com.ets2jsc.config.CompilerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 CompilerConfig 配置类
 */
@DisplayName("编译器配置测试")
class CompilerConfigTest {

    @Test
    @DisplayName("测试默认配置")
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
    @DisplayName("测试编译模式设置")
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
    @DisplayName("测试部分更新模式")
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
    @DisplayName("测试纯 JavaScript 模式")
    void testPureJavaScriptMode() {
        CompilerConfig config = new CompilerConfig();

        config.setPureJavaScript(true);
        assertTrue(config.isPureJavaScript());

        config.setPureJavaScript(false);
        assertFalse(config.isPureJavaScript());
    }

    @Test
    @DisplayName("测试 SourceMap 生成选项")
    void testGenerateSourceMap() {
        CompilerConfig config = new CompilerConfig();

        config.setGenerateSourceMap(true);
        assertTrue(config.isGenerateSourceMap());

        config.setGenerateSourceMap(false);
        assertFalse(config.isGenerateSourceMap());
    }

    @Test
    @DisplayName("测试路径配置")
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
    @DisplayName("测试入口点配置")
    void testEntryConfiguration() {
        CompilerConfig config = new CompilerConfig();

        config.addEntry("index", "src/main/ets/Index.ets");
        assertEquals("src/main/ets/Index.ets", config.getEntryObj().get("index"));

        config.addEntry("pages", "src/main/ets/pages");
        assertEquals("src/main/ets/pages", config.getEntryObj().get("pages"));
        assertEquals(2, config.getEntryObj().size());
    }

    @Test
    @DisplayName("测试输出选项")
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
    @DisplayName("测试处理选项")
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
    @DisplayName("测试 render 方法名称")
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
    @DisplayName("测试多种配置组合")
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
