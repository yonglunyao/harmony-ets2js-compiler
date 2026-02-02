package com.ets2jsc.infrastructure.parser.converters.expr;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for delete expressions.
 * Handles: delete object.property or delete array[index]
 */
public class DeleteExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "DeleteExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode operand = json.get("expression");
        if (operand != null && !operand.isNull()) {
            String expression = context.convertExpression(operand);
            return "delete " + expression;
        }
        return "delete undefined";
    }
}
