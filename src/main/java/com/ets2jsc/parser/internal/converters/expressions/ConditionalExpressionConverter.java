package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode conditionNode = json.get("condition");
        JsonNode whenTrueNode = json.get("whenTrue");
        JsonNode whenFalseNode = json.get("whenFalse");

        String condStr = (conditionNode != null && conditionNode.isObject()) ? context.convertExpression(conditionNode) : "";
        String trueStr = (whenTrueNode != null && whenTrueNode.isObject()) ? context.convertExpression(whenTrueNode) : "";
        String falseStr = (whenFalseNode != null && whenFalseNode.isObject()) ? context.convertExpression(whenFalseNode) : "";

        return condStr + " ? " + trueStr + " : " + falseStr;
    }
}
