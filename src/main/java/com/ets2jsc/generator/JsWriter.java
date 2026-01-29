package com.ets2jsc.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes generated JavaScript code to files.
 * Handles formatting and file output.
 */
public class JsWriter {

    /**
     * Writes JavaScript code to a file.
     */
    public void write(Path outputPath, String code) throws IOException {
        // Ensure parent directory exists
        Path parentDir = outputPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // Write code to file
        Files.writeString(outputPath, code);
    }

    /**
     * Writes JavaScript code with source map reference.
     */
    public void writeWithSourceMap(Path outputPath, String code, String sourceMapPath) throws IOException {
        // Append source map reference
        String codeWithSourceMap = code + "\n//# sourceMappingURL=" + sourceMapPath;
        write(outputPath, codeWithSourceMap);
    }

    /**
     * Formats JavaScript code (basic formatting).
     */
    public String format(String code) {
        // Basic formatting - in production, use a proper formatter
        return code
                .replace(";}", ";\n}")
                .replace("{\n", "{\n  ")
                .replace("\n\n", "\n");
    }

    /**
     * Minifies JavaScript code (basic minification).
     */
    public String minify(String code) {
        // Basic minification - in production, use a proper minifier
        return code
                .replaceAll("\\s+", " ")
                .replaceAll("/\\*.*?\\*/", "")
                .replaceAll("//.*", "");
    }
}
