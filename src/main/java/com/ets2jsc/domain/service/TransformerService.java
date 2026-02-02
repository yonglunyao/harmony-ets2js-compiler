package com.ets2jsc.domain.service;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CompilationException;

/**
 * Domain service for transforming AST nodes.
 * <p>
 * This service applies transformations to AST nodes, such as decorator
 * processing, component transformation, and build method handling.
 */
public interface TransformerService extends AutoCloseable {

    /**
     * Transforms a complete source file AST.
     *
     * @param sourceFile the source file to transform
     * @param config the compiler configuration
     * @return the transformed source file
     * @throws CompilationException if transformation fails
     */
    SourceFile transform(SourceFile sourceFile, CompilerConfig config) throws CompilationException;

    /**
     * Transforms a single AST node.
     *
     * @param node the node to transform
     * @param config the compiler configuration
     * @return the transformed node
     * @throws CompilationException if transformation fails
     */
    AstNode transformNode(AstNode node, CompilerConfig config) throws CompilationException;

    /**
     * Checks if this transformer can handle the given node.
     *
     * @param node the node to check
     * @param config the compiler configuration
     * @return true if this transformer can transform the node, false otherwise
     */
    boolean canTransform(AstNode node, CompilerConfig config);

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
