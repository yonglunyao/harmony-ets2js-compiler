package com.ets2jsc.infrastructure.generator;

import com.ets2jsc.shared.constant.Symbols;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates source maps for debugging.
 * Maps generated JavaScript back to original ETS source.
 */
public class SourceMapGenerator {

    private final Map<String, SourceMapping> mappings;
    private int lastGeneratedLine = 0;
    private int lastGeneratedColumn = 0;
    private int lastOriginalLine = 0;
    private int lastOriginalColumn = 0;

    @Getter
    public static class SourceMapping {
        private final int generatedLine;
        private final int generatedColumn;
        private final int originalLine;
        private final int originalColumn;
        private final String name;

        public SourceMapping(int generatedLine, int generatedColumn,
                           int originalLine, int originalColumn, String name) {
            this.generatedLine = generatedLine;
            this.generatedColumn = generatedColumn;
            this.originalLine = originalLine;
            this.originalColumn = originalColumn;
            this.name = name;
        }

    }

    public SourceMapGenerator() {
        this.mappings = new HashMap<>();
    }

    /**
     * Adds a mapping from generated position to original position.
     */
    public void addMapping(int generatedLine, int generatedColumn,
                          int originalLine, int originalColumn, String name) {
        String key = generatedLine + ":" + generatedColumn;
        SourceMapping mapping = new SourceMapping(
            generatedLine, generatedColumn,
            originalLine, originalColumn, name
        );
        mappings.put(key, mapping);
    }

    /**
     * Generates a source map in VLQ encoded format.
     */
    public String generate() {
        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        sb.append("  \"version\": ").append(Symbols.SOURCEMAP_VERSION).append(",\n");
        sb.append("  \"mappings\": \"");

        // Generate VLQ encoded mappings
        String encoded = encodeMappings();
        sb.append(encoded);

        sb.append("\",\n");
        sb.append("  \"sources\": [],\n");
        sb.append("  \"names\": [],\n");
        sb.append("  \"file\": \"\"\n");
        sb.append("}");

        return sb.toString();
    }

    /**
     * Encodes mappings to VLQ format.
     * Simplified implementation - production version should use proper VLQ encoding.
     */
    private String encodeMappings() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, SourceMapping> entry : mappings.entrySet()) {
            SourceMapping mapping = entry.getValue();

            // Calculate deltas
            int generatedLineDelta = mapping.getGeneratedLine() - lastGeneratedLine;
            int generatedColumnDelta = mapping.getGeneratedColumn() - lastGeneratedColumn;
            int originalLineDelta = mapping.getOriginalLine() - lastOriginalLine;
            int originalColumnDelta = mapping.getOriginalColumn() - lastOriginalColumn;

            // Encode as simplified VLQ (production would use proper VLQ encoding)
            if (generatedLineDelta > 0) {
                for (int i = 0; i < generatedLineDelta; i++) {
                    sb.append(";");
                }
            }
            sb.append(",").append(generatedColumnDelta)
              .append(",").append(originalLineDelta)
              .append(",").append(originalColumnDelta);

            // Update last positions
            lastGeneratedLine = mapping.getGeneratedLine();
            lastGeneratedColumn = mapping.getGeneratedColumn();
            lastOriginalLine = mapping.getOriginalLine();
            lastOriginalColumn = mapping.getOriginalColumn();
        }

        return sb.toString();
    }

    /**
     * Resets the generator state.
     */
    public void reset() {
        mappings.clear();
        lastGeneratedLine = 0;
        lastGeneratedColumn = 0;
        lastOriginalLine = 0;
        lastOriginalColumn = 0;
    }
}
