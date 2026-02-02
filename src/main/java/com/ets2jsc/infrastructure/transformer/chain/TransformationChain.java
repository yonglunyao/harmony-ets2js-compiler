package com.ets2jsc.infrastructure.transformer.chain;

import com.ets2jsc.domain.model.ast.AstNode;

/**
 * Chain interface for the Chain of Responsibility pattern.
 * <p>
 * This interface represents the chain of transformation handlers,
 * allowing each handler to invoke the next handler in the chain.
 * </p>
 *
 * @since 1.0
 */
public interface TransformationChain {

    /**
     * Processes the given AST node through the next handler in the chain.
     *
     * @param node the AST node to process
     * @return the transformed node
     * @throws Exception if transformation fails
     */
    AstNode proceed(AstNode node) throws Exception;
}
