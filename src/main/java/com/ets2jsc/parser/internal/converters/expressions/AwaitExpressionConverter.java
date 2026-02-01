package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for await expressions.
 * Handles: await expression
 */
public class AwaitExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "AwaitExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode awaitExprNode = json.get("expression");
        if (awaitExprNode != null && awaitExprNode.isObject()) {
            String awaitResult = context.convertExpression(awaitExprNode);
            return "await " + awaitResult.trim();
        }
        return "await";
    }
}
