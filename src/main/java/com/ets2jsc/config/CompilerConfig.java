package com.ets2jsc.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Compiler configuration for the ETS to JS compiler.
 * Contains all settings needed for the compilation process.
 */
@Getter
@Setter
@NoArgsConstructor
public class CompilerConfig {
    // Path configuration
    private String projectPath;
    private String buildPath;
    private String sourcePath;

    // Compilation mode
    private CompileMode compileMode = CompileMode.MODULE_JSON;
    private boolean partialUpdateMode = true;
    private boolean isPreview = false;

    // Output configuration
    private boolean generateSourceMap = true;
    private boolean generateDeclarations = false;
    private boolean minifyOutput = false;

    // Feature flags
    private boolean processTs = true;
    private boolean enableLazyImport = false;
    private boolean validateApi = true;
    private boolean pureJavaScript;  // Generate pure JS without ArkUI runtime dependencies

    // Entry points
    private Map<String, String> entryObj = new HashMap<>();

    public enum CompileMode {
        JSBUNDLE,    // Traditional bundle mode
        MODULE_JSON, // Stage model - module.json based
        ES_MODULE    // ES Module mode
    }

    /**
     * Adds an entry point to the configuration.
     */
    public void addEntry(String key, String value) {
        if (this.entryObj == null) {
            this.entryObj = new HashMap<>();
        }
        this.entryObj.put(key, value);
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
        String userDir = System.getProperty("user.dir");
        if (userDir == null || userDir.isEmpty()) {
            userDir = "."; // Fallback to current directory
        }
        config.setProjectPath(userDir);
        config.setBuildPath("build");
        config.setSourcePath("src/main/ets");
        return config;
    }
}
