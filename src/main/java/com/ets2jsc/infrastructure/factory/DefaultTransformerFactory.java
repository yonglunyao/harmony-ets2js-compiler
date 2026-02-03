package com.ets2jsc.infrastructure.factory;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.infrastructure.transformer.AstTransformer;
import com.ets2jsc.infrastructure.transformer.BuildMethodTransformer;
import com.ets2jsc.infrastructure.transformer.ComponentTransformer;
import com.ets2jsc.infrastructure.transformer.DecoratorTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Default implementation of TransformerFactory.
 * <p>
 * Creates transformer instances in the correct order.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DefaultTransformerFactory implements TransformerFactory {

    private final CompilerConfig config;

    /**
     * Creates a factory instance.
     */
    public DefaultTransformerFactory() {
        this.config = CompilerConfig.createDefault();
    }

    /**
     * Creates a factory with specific configuration.
     *
     * @param config compiler configuration
     */
    public DefaultTransformerFactory(CompilerConfig config) {
        this.config = config;
    }

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
