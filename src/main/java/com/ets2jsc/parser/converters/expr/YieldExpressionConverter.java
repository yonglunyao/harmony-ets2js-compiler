package com.ets2jsc.parser.converters.expr;

import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for yield expressions.
 * Handles: yield value or yield
 */
public class YieldExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "YieldExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Check for pre-generated text
        if (json.has("text")) {
            return json.get("text").asText();
        }
        // Basic conversion for yield
        JsonNode exprNode = json.get("expression");
        if (exprNode != null && !exprNode.isNull()) {
            return "yield " + context.convertExpression(exprNode);
        }
        return "yield";
    }
}
