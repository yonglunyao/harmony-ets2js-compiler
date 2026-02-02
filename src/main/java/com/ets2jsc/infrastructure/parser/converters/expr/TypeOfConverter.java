package com.ets2jsc.infrastructure.parser.converters.expr;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode typeOfExprNode = json.get("expression");
        String exprStr = (typeOfExprNode != null && typeOfExprNode.isObject()) ? context.convertExpression(typeOfExprNode) : "";
        return "typeof " + exprStr;
    }
}
