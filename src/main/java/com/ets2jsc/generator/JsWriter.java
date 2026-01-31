package com.ets2jsc.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(JsWriter.class);

    // Constants for character validation
    private static final char UNICODE_REPLACEMENT_CHARACTER = '\uFFFD';
    private static final int BMP_MAX_VALUE = 0xFFFF;
    private static final int PAIR_SKIP_INCREMENT = 1;

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
                    i += PAIR_SKIP_INCREMENT;
                } else if (!Character.isHighSurrogate(c) && !Character.isLowSurrogate(c) && c <= BMP_MAX_VALUE) {
                    // Valid BMP character
                    sanitized.append(c);
                } else {
                    // Invalid character, replace with placeholder
                    LOGGER.warn("Replacing invalid character at index {}: U+{}", i, Integer.toHexString(c));
                    sanitized.append(UNICODE_REPLACEMENT_CHARACTER);
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
