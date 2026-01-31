package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for conditional (ternary) expressions.
 * Handles: condition ? trueValue : falseValue
 */
public class ConditionalExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ConditionalExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject condition = json.getAsJsonObject("condition");
        JsonObject whenTrue = json.getAsJsonObject("whenTrue");
        JsonObject whenFalse = json.getAsJsonObject("whenFalse");

        String condStr = context.convertExpression(condition);
        String trueStr = context.convertExpression(whenTrue);
        String falseStr = context.convertExpression(whenFalse);

        return condStr + " ? " + trueStr + " : " + falseStr;
    }
}
