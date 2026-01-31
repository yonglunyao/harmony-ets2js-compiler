package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for parenthesized expressions.
 * Handles: (expression)
 */
public class ParenthesizedConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ParenthesizedExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject parenExpr = json.getAsJsonObject("expression");
        String exprStr = parenExpr != null ? context.convertExpression(parenExpr) : "";
        return "(" + exprStr + ")";
    }
}
