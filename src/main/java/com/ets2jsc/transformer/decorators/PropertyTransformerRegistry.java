package com.ets2jsc.transformer.decorators;

import com.ets2jsc.domain.model.ast.PropertyDeclaration;
import com.ets2jsc.transformer.decorators.impl.ConsumePropertyTransformer;
import com.ets2jsc.transformer.decorators.impl.LinkPropertyTransformer;
import com.ets2jsc.transformer.decorators.impl.PropPropertyTransformer;
import com.ets2jsc.transformer.decorators.impl.ProvidePropertyTransformer;
import com.ets2jsc.transformer.decorators.impl.StatePropertyTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for property transformers.
 * <p>
 * This class manages all available property transformers and provides
 * lookup functionality to find the appropriate transformer for a given
 * decorated property.
 * <p>
 * Transformers are checked in registration order, so more specific
 * transformers should be registered first.
 */
public class PropertyTransformerRegistry {

    private final List<PropertyTransformer> transformers;

    /**
     * Creates a new registry with default transformers registered.
     */
    public PropertyTransformerRegistry() {
        this.transformers = new ArrayList<>();
        registerDefaultTransformers();
    }

    /**
     * Registers the default property transformers.
     */
    private void registerDefaultTransformers() {
        // Register in order of specificity (most specific first)
        register(new StatePropertyTransformer());
        register(new PropPropertyTransformer());
        register(new LinkPropertyTransformer());
        register(new ProvidePropertyTransformer());
        register(new ConsumePropertyTransformer());
    }

    /**
     * Registers a property transformer.
     *
     * @param transformer the transformer to register
     */
    public void register(PropertyTransformer transformer) {
        if (transformer != null) {
            transformers.add(transformer);
        }
    }

    /**
     * Finds the appropriate transformer for a property.
     *
     * @param prop the property declaration
     * @return the transformer, or null if none found
     */
    public PropertyTransformer findTransformer(PropertyDeclaration prop) {
        for (PropertyTransformer transformer : transformers) {
            if (transformer.canHandle(prop)) {
                return transformer;
            }
        }
        return null;
    }

    /**
     * Checks if a property can be transformed.
     *
     * @param prop the property declaration
     * @return true if a transformer is available
     */
    public boolean canTransform(PropertyDeclaration prop) {
        return findTransformer(prop) != null;
    }

    /**
     * Gets all registered transformers.
     *
     * @return a list of all transformers
     */
    public List<PropertyTransformer> getAllTransformers() {
        return new ArrayList<>(transformers);
    }

    /**
     * Clears all registered transformers.
     */
    public void clear() {
        transformers.clear();
    }

    /**
     * Gets the number of registered transformers.
     *
     * @return the transformer count
     */
    public int size() {
        return transformers.size();
    }
}
