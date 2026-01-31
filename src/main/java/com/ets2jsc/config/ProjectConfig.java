package com.ets2jsc.config;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Project configuration loaded from HarmonyOS project files.
 * Reads manifest.json, module.json, and aceBuild.json.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectConfig {
    private String bundleName;
    private String moduleName;
    private int minAPIVersion;
    private String targetAPIVersion;
    private String mainPage;
    private String[] pages;

    /**
     * Creates ProjectConfig from JSON string.
     */
    public static ProjectConfig fromJson(String jsonContent) {
        Gson gson = new Gson();
        return gson.fromJson(jsonContent, ProjectConfig.class);
    }

    /**
     * Creates ProjectConfig from JSON file.
     */
    public static ProjectConfig fromFile(Path configPath) throws IOException {
        String content = Files.readString(configPath);
        return fromJson(content);
    }
}
