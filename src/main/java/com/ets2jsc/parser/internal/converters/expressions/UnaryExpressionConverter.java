package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        String kindName = json.has("kindName") ? json.get("kindName").asText() : "";
        String operator = json.has("operator") ? json.get("operator").asText() : "";
        JsonNode operandNode = json.get("operand");
        String operandStr = (operandNode != null && operandNode.isObject()) ? context.convertExpression(operandNode) : "";

        if ("PostfixUnaryExpression".equals(kindName)) {
            return operandStr + operator;
        }
        return operator + operandStr;
    }
}
