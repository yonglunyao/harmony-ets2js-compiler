package com.ets2jsc.generator.internal;

import com.ets2jsc.ast.SourceFile;

/**
 * Internal interface for source map generator.
 * <p>
 * This interface enables testability and dependency injection within the generator module.
 */
public interface ISourceMapGenerator extends AutoCloseable {

    /**
     * Generates a source map for the given source file.
     *
     * @param sourceFile the source file to generate a source map for
     * @return the source map as a JSON string
     */
    String generate(SourceFile sourceFile);

    /**
     * Closes the generator and releases any resources.
     */
    @Override
    void close();
}
