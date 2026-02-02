package com.ets2jsc.parser.converters.expr;

import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode parenExprNode = json.get("expression");
        String exprStr = (parenExprNode != null && parenExprNode.isObject()) ? context.convertExpression(parenExprNode) : "";
        return "(" + exprStr + ")";
    }
}
