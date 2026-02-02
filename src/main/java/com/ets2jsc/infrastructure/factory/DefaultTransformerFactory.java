package com.ets2jsc.infrastructure.factory;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.infrastructure.transformer.AstTransformer;
import com.ets2jsc.infrastructure.transformer.BuildMethodTransformer;
import com.ets2jsc.infrastructure.transformer.ComponentTransformer;
import com.ets2jsc.infrastructure.transformer.DecoratorTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of TransformerFactory.
 * <p>
 * Creates standard transformer instances in the correct order.
 */
public class DefaultTransformerFactory implements TransformerFactory {

    @Override
    public List<AstTransformer> createTransformers(CompilerConfig config) {
        List<AstTransformer> transformers = new ArrayList<>();
        transformers.add(createDecoratorTransformer(config.isPartialUpdateMode()));
        transformers.add(createBuildMethodTransformer(config.isPartialUpdateMode()));
        transformers.add(createComponentTransformer());
        return transformers;
    }

    @Override
    public AstTransformer createDecoratorTransformer(boolean partialUpdateMode) {
        return new DecoratorTransformer(partialUpdateMode);
    }

    @Override
    public AstTransformer createBuildMethodTransformer(boolean partialUpdateMode) {
        return new BuildMethodTransformer(partialUpdateMode);
    }

    @Override
    public AstTransformer createComponentTransformer() {
        return new ComponentTransformer();
    }
}
