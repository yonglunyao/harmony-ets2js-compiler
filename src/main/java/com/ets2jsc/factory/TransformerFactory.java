package com.ets2jsc.factory;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.transformer.AstTransformer;

import java.util.List;

/**
 * Factory interface for creating transformer instances.
 * <p>
 * Implementations of this interface are responsible for creating
 * configured transformer instances. This enables dependency injection
 * and makes testing easier.
 */
public interface TransformerFactory {

    /**
     * Creates a list of transformers for the given configuration.
     *
     * @param config the compiler configuration
     * @return the list of transformers
     */
    List<AstTransformer> createTransformers(CompilerConfig config);

    /**
     * Creates a decorator transformer.
     *
     * @param partialUpdateMode whether partial update mode is enabled
     * @return the decorator transformer
     */
    AstTransformer createDecoratorTransformer(boolean partialUpdateMode);

    /**
     * Creates a build method transformer.
     *
     * @param partialUpdateMode whether partial update mode is enabled
     * @return the build method transformer
     */
    AstTransformer createBuildMethodTransformer(boolean partialUpdateMode);

    /**
     * Creates a component transformer.
     *
     * @return the component transformer
     */
    AstTransformer createComponentTransformer();
}
