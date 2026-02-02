package com.ets2jsc.infrastructure.parser;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.SourceFile;

/**
 * Builds and processes AST nodes.
 * Provides utilities for AST manipulation.
 */
public class AstBuilder {

    /**
     * Creates an AST from source code.
     */
    public SourceFile build(String fileName, String sourceCode) {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        return parser.parse(fileName, sourceCode);
    }

    /**
     * Validates the AST structure.
     */
    public boolean validate(AstNode node) {
        if (node == null) {
            return false;
        }

        // Basic validation - node type should not be empty
        String type = node.getType();
        return type != null && !type.isEmpty();
    }

    /**
     * Processes the AST and applies transformations.
     */
    public AstNode process(AstNode node) {
        if (!validate(node)) {
            throw new IllegalArgumentException("Invalid AST node");
        }

        // Apply default processing
        return node;
    }
}
