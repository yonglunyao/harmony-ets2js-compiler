package com.ets2jsc.domain.model.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

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
@EqualsAndHashCode
@ToString
public class ProjectConfig {
    private String bundleName;
    private String moduleName;
    private int minAPIVersion;
    private String targetAPIVersion;
    private String mainPage;
    private String[] pages;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Creates ProjectConfig from JSON string.
     */
    public static ProjectConfig fromJson(String jsonContent) throws IOException {
        return OBJECT_MAPPER.readValue(jsonContent, ProjectConfig.class);
    }

    /**
     * Creates ProjectConfig from JSON file.
     */
    public static ProjectConfig fromFile(Path configPath) throws IOException {
        String content = Files.readString(configPath);
        return fromJson(content);
    }
}
