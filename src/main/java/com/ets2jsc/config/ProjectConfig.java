package com.ets2jsc.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Project configuration loaded from HarmonyOS project files.
 * Reads manifest.json, module.json, and aceBuild.json.
 */
public class ProjectConfig {
    private String bundleName;
    private String moduleName;
    private int minAPIVersion;
    private String targetAPIVersion;
    private String mainPage;
    private String[] pages;

    public static ProjectConfig fromJson(String jsonContent) {
        Gson gson = new Gson();
        return gson.fromJson(jsonContent, ProjectConfig.class);
    }

    public static ProjectConfig fromFile(Path configPath) throws IOException {
        String content = Files.readString(configPath);
        return fromJson(content);
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getMinAPIVersion() {
        return minAPIVersion;
    }

    public void setMinAPIVersion(int minAPIVersion) {
        this.minAPIVersion = minAPIVersion;
    }

    public String getTargetAPIVersion() {
        return targetAPIVersion;
    }

    public void setTargetAPIVersion(String targetAPIVersion) {
        this.targetAPIVersion = targetAPIVersion;
    }

    public String getMainPage() {
        return mainPage;
    }

    public void setMainPage(String mainPage) {
        this.mainPage = mainPage;
    }

    public String[] getPages() {
        return pages;
    }

    public void setPages(String[] pages) {
        this.pages = pages;
    }
}
