package com.ets2jsc.infrastructure.parser;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for converting JSON nodes to AST nodes or strings.
 * Implementations handle specific node types from TypeScript Compiler API output.
 */
public interface NodeConverter {

    /**
     * Converts a JSON node to the target representation.
     *
     * @param json the JSON node representing the AST node
     * @param context the conversion context providing shared state and utilities
     * @return the converted result (may be String, AstNode, or other types)
     * @throws UnsupportedOperationException if the converter cannot handle the given node
     */
    Object convert(JsonNode json, ConversionContext context);

    /**
     * Checks if this converter can handle the given node type.
     *
     * @param kindName the TypeScript kind name (e.g., "CallExpression", "BinaryExpression")
     * @return true if this converter can handle the node type
     */
    boolean canConvert(String kindName);

    /**
     * Returns the priority of this converter. Higher priority converters are checked first.
     * Used when multiple converters can handle the same node type.
     *
     * @return the priority value (default is 0)
     */
    default int getPriority() {
        return 0;
    }
}
