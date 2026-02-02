package com.ets2jsc.infrastructure.parser.converters.expr;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for spread expressions.
 * Handles: ...array, ...object
 */
public class SpreadConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "SpreadElement".equals(kindName) ||
               "SpreadAssignment".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode exprNode = json.get("expression");
        String exprStr = (exprNode != null && exprNode.isObject()) ? context.convertExpression(exprNode) : "";
        return "..." + exprStr;
    }
}
