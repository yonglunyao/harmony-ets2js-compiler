package com.ets2jsc.transformer.chain;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.transformer.AstTransformer;

/**
 * Adapter class to integrate existing {@link AstTransformer} implementations
 * with the Chain of Responsibility pattern.
 * <p>
 * This adapter wraps an {@link AstTransformer} and implements the
 * {@link TransformationHandler} interface, allowing existing transformers
 * to be used in a transformation chain without modification.
 * </p>
 *
 * @since 1.0
 */
public class TransformerAdapter implements TransformationHandler {

    private final AstTransformer transformer;

    /**
     * Creates a new adapter for the given transformer.
     *
     * @param transformer the transformer to adapt
     * @throws IllegalArgumentException if transformer is null
     */
    public TransformerAdapter(AstTransformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer cannot be null");
        }
        this.transformer = transformer;
    }

    @Override
    public AstNode handle(AstNode node, TransformationChain chain) throws Exception {
        AstNode transformed = transformer.transform(node);
        return chain.proceed(transformed);
    }

    @Override
    public boolean canHandle(AstNode node) {
        return transformer.canTransform(node);
    }

    /**
     * Returns the wrapped transformer.
     *
     * @return the wrapped transformer
     */
    public AstTransformer getTransformer() {
        return transformer;
    }
}
