package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for unary expressions.
 * Handles: PrefixUnaryExpression (-1, !true), PostfixUnaryExpression (i++, i--)
 */
public class UnaryExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "PrefixUnaryExpression".equals(kindName) ||
               "PostfixUnaryExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String kindName = json.has("kindName") ? json.get("kindName").getAsString() : "";
        String operator = json.has("operator") ? json.get("operator").getAsString() : "";
        JsonObject operand = json.getAsJsonObject("operand");
        String operandStr = operand != null ? context.convertExpression(operand) : "";

        if ("PostfixUnaryExpression".equals(kindName)) {
            return operandStr + operator;
        }
        return operator + operandStr;
    }
}
