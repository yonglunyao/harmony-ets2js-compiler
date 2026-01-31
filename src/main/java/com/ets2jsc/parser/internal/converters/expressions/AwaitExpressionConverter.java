package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

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
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject awaitExpr = json.getAsJsonObject("expression");
        if (awaitExpr != null) {
            String awaitResult = context.convertExpression(awaitExpr);
            return "await " + awaitResult.trim();
        }
        return "await";
    }
}
