package com.ets2jsc.infrastructure.generator;

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
        ensureParentDirectoryExists(outputPath);
        byte[] utf8Bytes = sanitizeCodeToUtf8(code);
        Files.write(outputPath, utf8Bytes);
    }

    /**
     * Ensures the parent directory exists, creating it if necessary.
     *
     * @param outputPath the output path whose parent directory should exist
     * @throws IOException if directory creation fails
     */
    private void ensureParentDirectoryExists(Path outputPath) throws IOException {
        Path parentDir = outputPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Sanitizes the code to UTF-8 bytes, replacing invalid characters.
     *
     * @param code the code to sanitize
     * @return UTF-8 encoded bytes of the sanitized code
     */
    private byte[] sanitizeCodeToUtf8(String code) {
        try {
            return code.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return sanitizeInvalidCharacters(code);
        }
    }

    /**
     * Filters out problematic characters from the code.
     *
     * @param code the code containing potentially invalid characters
     * @return UTF-8 encoded bytes of the sanitized code
     */
    private byte[] sanitizeInvalidCharacters(String code) {
        StringBuilder sanitized = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (isValidSurrogatePair(code, i, c)) {
                i += PAIR_SKIP_INCREMENT;
            } else if (isValidBmpCharacter(c)) {
                sanitized.append(c);
            } else {
                logInvalidCharacter(i, c);
                sanitized.append(UNICODE_REPLACEMENT_CHARACTER);
            }
        }
        return sanitized.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Checks if the character at the given index forms a valid surrogate pair.
     *
     * @param code the full code string
     * @param i the current index
     * @param c the current character
     * @return true if this is the high surrogate of a valid pair
     */
    private boolean isValidSurrogatePair(String code, int i, char c) {
        return Character.isHighSurrogate(c)
                && i + 1 < code.length()
                && Character.isLowSurrogate(code.charAt(i + 1));
    }

    /**
     * Checks if the character is a valid BMP character.
     *
     * @param c the character to validate
     * @return true if valid BMP character
     */
    private boolean isValidBmpCharacter(char c) {
        return !Character.isHighSurrogate(c) && !Character.isLowSurrogate(c) && c <= BMP_MAX_VALUE;
    }

    /**
     * Logs a warning about an invalid character.
     *
     * @param index the index of the invalid character
     * @param c the invalid character
     */
    private void logInvalidCharacter(int index, char c) {
        LOGGER.warn("Replacing invalid character at index {}: U+{}", index, Integer.toHexString(c));
    }

    /**
     * Writes JavaScript code with source map reference.
     */
    public void writeWithSourceMap(Path outputPath, String code, String sourceMapPath) throws IOException {
        // Don't append source map reference for now - it can cause issues
        // TODO: Fix source map generation
        write(outputPath, code);
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
