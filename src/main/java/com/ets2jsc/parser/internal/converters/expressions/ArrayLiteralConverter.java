package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Converter for array literal expressions.
 * Handles: [1, 2, 3], [a, b, c]
 */
public class ArrayLiteralConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ArrayLiteralExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode elementsNode = json.get("elements");
        if (elementsNode == null || !elementsNode.isArray() || elementsNode.size() == 0) {
            return "[]";
        }

        ArrayNode elements = (ArrayNode) elementsNode;
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(context.convertExpression(elements.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }
}
