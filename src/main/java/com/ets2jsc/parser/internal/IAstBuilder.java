package com.ets2jsc.parser.internal;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.SourceFile;

/**
 * Internal interface for AST builder.
 * <p>
 * This interface enables testability and dependency injection within the parser module.
 */
public interface IAstBuilder {

    /**
     * Creates an AST from source code.
     *
     * @param fileName the file name
     * @param sourceCode the source code
     * @return the created SourceFile
     */
    SourceFile build(String fileName, String sourceCode);

    /**
     * Validates the AST structure.
     *
     * @param node the node to validate
     * @return true if the node is valid, false otherwise
     */
    boolean validate(AstNode node);

    /**
     * Processes the AST and applies transformations.
     *
     * @param node the node to process
     * @return the processed node
     */
    AstNode process(AstNode node);
}
