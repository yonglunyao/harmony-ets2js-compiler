package com.ets2jsc.parser.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Base registry for node converters.
 * Manages a collection of converters and dispatches to the appropriate one.
 */
public abstract class NodeConverterRegistry {

    protected final List<NodeConverter> converters;

    protected NodeConverterRegistry() {
        this.converters = new ArrayList<>();
        initializeConverters();
        // Sort by priority (highest first)
        converters.sort(Comparator.comparingInt(NodeConverter::getPriority).reversed());
    }

    /**
     * Initializes the registry with default converters.
     * Subclasses should override this method to register specific converters.
     */
    protected abstract void initializeConverters();

    /**
     * Registers a converter.
     */
    public void register(NodeConverter converter) {
        converters.add(converter);
        // Re-sort after adding
        converters.sort(Comparator.comparingInt(NodeConverter::getPriority).reversed());
    }

    /**
     * Finds a converter for the given kind name.
     */
    protected NodeConverter findConverter(String kindName) {
        for (NodeConverter converter : converters) {
            if (converter.canConvert(kindName)) {
                return converter;
            }
        }
        throw new UnsupportedOperationException("No converter found for: " + kindName);
    }

    /**
     * Checks if a converter is available for the given kind name.
     */
    public boolean hasConverter(String kindName) {
        for (NodeConverter converter : converters) {
            if (converter.canConvert(kindName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the number of registered converters.
     */
    public int size() {
        return converters.size();
    }

    /**
     * Clears all converters.
     */
    public void clear() {
        converters.clear();
    }
}
