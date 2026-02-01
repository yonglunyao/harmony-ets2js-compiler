package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for void expressions.
 * Handles: void expression
 */
public class VoidExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "VoidExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Check for pre-generated text
        if (json.has("text")) {
            return json.get("text").asText();
        }
        // Void expressions evaluate to undefined
        JsonNode exprNode = json.get("expression");
        if (exprNode != null && !exprNode.isNull()) {
            String expr = context.convertExpression(exprNode);
            return "void " + expr;
        }
        return "void 0";
    }
}
