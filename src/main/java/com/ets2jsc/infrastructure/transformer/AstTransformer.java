package com.ets2jsc.infrastructure.transformer;

import com.ets2jsc.domain.model.ast.AstNode;

/**
 * Base interface for all AST transformers.
 */
public interface AstTransformer {
    /**
     * Transforms the given AST node.
     * @param node the node to transform
     * @return the transformed node
     */
    AstNode transform(AstNode node);

    /**
     * Returns true if this transformer can handle the given node type.
     */
    boolean canTransform(AstNode node);
}
