package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for binary expressions.
 * Handles: +, -, *, /, %, &&, ||, ===, !==, <, >, <=, >=, etc.
 */
public class BinaryExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "BinaryExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject left = json.getAsJsonObject("left");
        String operator = json.has("operator") ? json.get("operator").getAsString() : "";
        JsonObject right = json.getAsJsonObject("right");

        String leftStr = context.convertExpression(left);
        String rightStr = context.convertExpression(right);

        return leftStr + " " + operator + " " + rightStr;
    }
}
