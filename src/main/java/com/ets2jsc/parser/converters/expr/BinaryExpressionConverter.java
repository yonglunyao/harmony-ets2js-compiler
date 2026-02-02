package com.ets2jsc.parser.converters.expr;

import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode leftNode = json.get("left");
        String operator = json.has("operator") ? json.get("operator").asText() : "";
        JsonNode rightNode = json.get("right");

        String leftStr = (leftNode != null && leftNode.isObject()) ? context.convertExpression(leftNode) : "";
        String rightStr = (rightNode != null && rightNode.isObject()) ? context.convertExpression(rightNode) : "";

        return leftStr + " " + operator + " " + rightStr;
    }
}
