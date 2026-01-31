package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for spread expressions.
 * Handles: ...array, ...object
 */
public class SpreadConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "SpreadElement".equals(kindName) ||
               "SpreadAssignment".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject expr = json.getAsJsonObject("expression");
        String exprStr = expr != null ? context.convertExpression(expr) : "";
        return "..." + exprStr;
    }
}
