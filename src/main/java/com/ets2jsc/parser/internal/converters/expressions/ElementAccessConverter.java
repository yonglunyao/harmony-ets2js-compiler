package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for element access expressions.
 * Handles: array[index], object[property]
 */
public class ElementAccessConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ElementAccessExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject elementObj = json.getAsJsonObject("expression");
        String elementStr = elementObj != null ? context.convertExpression(elementObj) : "";
        JsonObject argumentExpr = json.getAsJsonObject("argumentExpression");
        String argStr = argumentExpr != null ? context.convertExpression(argumentExpr) : "";
        return elementStr + "[" + argStr + "]";
    }
}
