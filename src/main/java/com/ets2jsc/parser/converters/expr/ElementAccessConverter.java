package com.ets2jsc.parser.converters.expr;

import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode elementNode = json.get("expression");
        String elementStr = (elementNode != null && elementNode.isObject()) ? context.convertExpression(elementNode) : "";
        JsonNode argumentExprNode = json.get("argumentExpression");
        String argStr = (argumentExprNode != null && argumentExprNode.isObject()) ? context.convertExpression(argumentExprNode) : "";
        return elementStr + "[" + argStr + "]";
    }
}
