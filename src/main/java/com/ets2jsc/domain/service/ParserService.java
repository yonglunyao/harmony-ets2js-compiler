package com.ets2jsc.domain.service;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.shared.exception.ParserException;

import java.nio.file.Path;

/**
 * Domain service for parsing TypeScript/ETS source code into AST.
 * <p>
 * This service is responsible for converting source code into an
 * Abstract Syntax Tree (AST) that can be processed by the compiler.
 */
public interface ParserService extends AutoCloseable {

    /**
     * Parses a source file and returns the AST.
     *
     * @param sourcePath the path to the source file
     * @return the parsed AST as a SourceFile
     * @throws ParserException if parsing fails
     */
    SourceFile parseFile(Path sourcePath) throws ParserException;

    /**
     * Parses source code from a string and returns the AST.
     *
     * @param fileName the file name to use for error reporting
     * @param sourceCode the source code to parse
     * @return the parsed AST as a SourceFile
     * @throws ParserException if parsing fails
     */
    SourceFile parseString(String fileName, String sourceCode) throws ParserException;

    /**
     * Checks if this parser can handle the given source file.
     *
     * @param sourcePath the path to check
     * @return true if this parser can parse the file, false otherwise
     */
    boolean canParse(Path sourcePath);

    /**
     * Closes the parser and releases any resources.
     */
    @Override
    void close();
}
