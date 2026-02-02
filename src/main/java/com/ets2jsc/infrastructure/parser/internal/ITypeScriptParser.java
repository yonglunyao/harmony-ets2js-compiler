package com.ets2jsc.infrastructure.parser.internal;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.shared.exception.ParserException;

/**
 * Internal interface for TypeScript/ETS parser.
 * <p>
 * This interface enables testability and dependency injection within the parser module.
 */
public interface ITypeScriptParser extends AutoCloseable {

    /**
     * Parses ETS/TypeScript source code into a SourceFile AST node.
     *
     * @param fileName the file name for error reporting
     * @param sourceCode the source code to parse
     * @return the parsed SourceFile
     * @throws ParserException if parsing fails
     */
    SourceFile parse(String fileName, String sourceCode) throws ParserException;

    /**
     * Closes the parser and releases any resources.
     */
    @Override
    void close();
}
