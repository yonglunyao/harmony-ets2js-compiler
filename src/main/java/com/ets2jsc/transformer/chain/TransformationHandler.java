package com.ets2jsc.transformer.chain;

import com.ets2jsc.domain.model.ast.AstNode;

/**
 * Handler interface for the Chain of Responsibility pattern.
 * <p>
 * Each handler can process an AST node and either:
 * <ul>
 *   <li>Transform the node and pass it to the next handler</li>
 *   <li>Transform the node and stop the chain</li>
 *   <li>Pass the node unchanged to the next handler</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface TransformationHandler {

    /**
     * Processes the given AST node and returns the transformed result.
     *
     * @param node the AST node to process
     * @param chain the transformation chain for passing to the next handler
     * @return the transformed node
     * @throws Exception if transformation fails
     */
    AstNode handle(AstNode node, TransformationChain chain) throws Exception;

    /**
     * Returns true if this handler can process the given node.
     *
     * @param node the AST node to check
     * @return true if this handler can process the node, false otherwise
     */
    boolean canHandle(AstNode node);
}
