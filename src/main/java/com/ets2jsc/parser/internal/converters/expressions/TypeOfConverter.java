package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for typeof expressions.
 * Handles: typeof expression
 */
public class TypeOfConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "TypeOfExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject typeOfExpr = json.getAsJsonObject("expression");
        String exprStr = typeOfExpr != null ? context.convertExpression(typeOfExpr) : "";
        return "typeof " + exprStr;
    }
}
