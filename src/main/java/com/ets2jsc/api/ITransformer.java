package com.ets2jsc.api;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;

/**
 * Interface for transforming AST nodes.
 * <p>
 * This is the primary facade for the TransformerModule, providing a single
 * entry point for all AST transformation operations. Implementations should
 * handle the complete transformation chain including decorators, components,
 * build methods, and properties.
 */
public interface ITransformer extends AutoCloseable {

    /**
     * Transforms a complete source file AST.
     *
     * @param sourceFile the source file to transform
     * @return the transformed source file
     */
    SourceFile transform(SourceFile sourceFile);

    /**
     * Transforms a single AST node.
     *
     * @param node the node to transform
     * @return the transformed node
     */
    AstNode transformNode(AstNode node);

    /**
     * Checks if this transformer can handle the given node.
     *
     * @param node the node to check
     * @return true if this transformer can transform the node, false otherwise
     */
    boolean canTransform(AstNode node);

    /**
     * Reconfigures the transformer with a new configuration.
     *
     * @param config the new configuration to apply
     */
    void reconfigure(CompilerConfig config);

    /**
     * Closes the transformer and releases any resources.
     */
    @Override
    void close();
}
