package com.ets2jsc.domain.model.ast;

/**
 * Base interface for all AST nodes in the ETS compiler.
 * Represents a node in the Abstract Syntax Tree.
 */
public interface AstNode {
    /**
     * Returns the type of this AST node.
     * @return the node type as a string
     */
    String getType();

    /**
     * Accepts a visitor for processing this node.
     * @param visitor the visitor to process this node
     * @param <T> the return type of the visitor
     * @return the result of visiting this node
     */
    <T> T accept(AstVisitor<T> visitor);
}
