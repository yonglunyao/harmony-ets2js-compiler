package com.ets2jsc.infrastructure.generator.strategy;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.infrastructure.generator.context.GenerationContext;

/**
 * Strategy interface for code generation.
 * <p>
 * Implementations of this interface handle code generation for specific
 * AST node types, following the Strategy pattern for flexible code generation.
 * </p>
 *
 * @since 1.0
 */
public interface CodeGenerationStrategy {

    /**
     * Generates code for the given AST node.
     *
     * @param node the AST node to generate code for
     * @param context the generation context
     * @return the generated code
     */
    String generate(AstNode node, GenerationContext context);

    /**
     * Returns true if this strategy can handle the given node type.
     *
     * @param node the AST node to check
     * @return true if this strategy can handle the node
     */
    boolean canHandle(AstNode node);

    /**
     * Returns the priority of this strategy. Higher priority strategies are checked first.
     *
     * @return the priority value (default is 0)
     */
    default int getPriority() {
        return 0;
    }
}
