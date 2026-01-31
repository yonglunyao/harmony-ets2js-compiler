package com.ets2jsc.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Compiler configuration for the ETS to JS compiler.
 * Contains all settings needed for the compilation process.
 */
public class CompilerConfig {
    // Path configuration
    private String projectPath;
    private String buildPath;
    private String sourcePath;

    // Compilation mode
    private CompileMode compileMode;
    private boolean partialUpdateMode;
    private boolean isPreview;

    // Output configuration
    private boolean generateSourceMap;
    private boolean generateDeclarations;
    private boolean minifyOutput;

    // Feature flags
    private boolean processTs;
    private boolean enableLazyImport;
    private boolean validateApi;
    private boolean pureJavaScript;  // Generate pure JS without ArkUI runtime dependencies

    // Entry points
    private Map<String, String> entryObj;

    public enum CompileMode {
        JSBUNDLE,    // Traditional bundle mode
        MODULE_JSON, // Stage model - module.json based
        ES_MODULE    // ES Module mode
    }

    public CompilerConfig() {
        this.compileMode = CompileMode.MODULE_JSON;
        this.partialUpdateMode = true;
        this.isPreview = false;
        this.generateSourceMap = true;
        this.generateDeclarations = false;
        this.minifyOutput = false;
        this.processTs = true;
        this.enableLazyImport = false;
        this.validateApi = true;
        this.entryObj = new HashMap<>();
    }

    // Getters and Setters

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getBuildPath() {
        return buildPath;
    }

    public void setBuildPath(String buildPath) {
        this.buildPath = buildPath;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public CompileMode getCompileMode() {
        return compileMode;
    }

    public void setCompileMode(CompileMode compileMode) {
        this.compileMode = compileMode;
    }

    public boolean isPartialUpdateMode() {
        return partialUpdateMode;
    }

    public void setPartialUpdateMode(boolean partialUpdateMode) {
        this.partialUpdateMode = partialUpdateMode;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    public boolean isGenerateSourceMap() {
        return generateSourceMap;
    }

    public void setGenerateSourceMap(boolean generateSourceMap) {
        this.generateSourceMap = generateSourceMap;
    }

    public boolean isGenerateDeclarations() {
        return generateDeclarations;
    }

    public void setGenerateDeclarations(boolean generateDeclarations) {
        this.generateDeclarations = generateDeclarations;
    }

    public boolean isMinifyOutput() {
        return minifyOutput;
    }

    public void setMinifyOutput(boolean minifyOutput) {
        this.minifyOutput = minifyOutput;
    }

    public boolean isProcessTs() {
        return processTs;
    }

    public void setProcessTs(boolean processTs) {
        this.processTs = processTs;
    }

    public boolean isEnableLazyImport() {
        return enableLazyImport;
    }

    public void setEnableLazyImport(boolean enableLazyImport) {
        this.enableLazyImport = enableLazyImport;
    }

    public boolean isValidateApi() {
        return validateApi;
    }

    public void setValidateApi(boolean validateApi) {
        this.validateApi = validateApi;
    }

    public Map<String, String> getEntryObj() {
        return entryObj;
    }

    public void setEntryObj(Map<String, String> entryObj) {
        this.entryObj = entryObj;
    }

    public void addEntry(String key, String value) {
        this.entryObj.put(key, value);
    }

    public boolean isPureJavaScript() {
        return pureJavaScript;
    }

    public void setPureJavaScript(boolean pureJavaScript) {
        this.pureJavaScript = pureJavaScript;
    }

    /**
     * Returns the render method name based on compilation mode.
     * In partial update mode, it's 'initialRender', otherwise 'render'.
     */
    public String getRenderMethodName() {
        return partialUpdateMode ? "initialRender" : "render";
    }

    /**
     * Gets the source root directory as a Path.
     * Returns null if sourcePath is not configured.
     */
    public java.nio.file.Path getSourceRootDir() {
        if (sourcePath == null || sourcePath.isEmpty()) {
            return null;
        }
        return java.nio.file.Path.of(sourcePath);
    }

    /**
     * Creates a default configuration for development.
     */
    public static CompilerConfig createDefault() {
        CompilerConfig config = new CompilerConfig();
        config.setProjectPath(System.getProperty("user.dir"));
        config.setBuildPath("build");
        config.setSourcePath("src/main/ets");
        return config;
    }
}
