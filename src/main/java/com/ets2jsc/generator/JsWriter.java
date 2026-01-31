package com.ets2jsc.generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.MalformedInputException;
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

        // Validate and sanitize the string before writing
        byte[] utf8Bytes;
        try {
            utf8Bytes = code.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            // If getBytes fails, filter out problematic characters
            StringBuilder sanitized = new StringBuilder();
            for (int i = 0; i < code.length(); i++) {
                char c = code.charAt(i);
                if (Character.isHighSurrogate(c) && i + 1 < code.length() && Character.isLowSurrogate(code.charAt(i + 1))) {
                    // Valid surrogate pair
                    sanitized.append(c);
                    sanitized.append(code.charAt(i + 1));
                    i++; // Skip the low surrogate
                } else if (!Character.isHighSurrogate(c) && !Character.isLowSurrogate(c) && c <= 0xFFFF) {
                    // Valid BMP character
                    sanitized.append(c);
                } else {
                    // Invalid character, replace with placeholder
                    System.err.println("Warning: Replacing invalid character at index " + i + ": U+" + Integer.toHexString(c));
                    sanitized.append('\uFFFD'); // Replacement character
                }
            }
            utf8Bytes = sanitized.toString().getBytes(StandardCharsets.UTF_8);
        }

        Files.write(outputPath, utf8Bytes);
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
