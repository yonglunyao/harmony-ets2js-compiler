package com.ets2jsc.generator.internal;

import java.nio.file.Path;

/**
 * Internal interface for JavaScript file writer.
 * <p>
 * This interface enables testability and dependency injection within the generator module.
 */
public interface IJsWriter extends AutoCloseable {

    /**
     * Writes content to a file.
     *
     * @param path the file path to write to
     * @param content the content to write
     * @throws Exception if writing fails
     */
    void write(Path path, String content) throws Exception;

    /**
     * Writes content with source map reference to a file.
     *
     * @param path the file path to write to
     * @param content the content to write
     * @param sourceMapFileName the source map file name
     * @throws Exception if writing fails
     */
    void writeWithSourceMap(Path path, String content, String sourceMapFileName) throws Exception;

    /**
     * Closes the writer and releases any resources.
     */
    @Override
    void close();
}
