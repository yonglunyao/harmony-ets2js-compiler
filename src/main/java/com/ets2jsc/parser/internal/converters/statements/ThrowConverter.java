package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for throw statements.
 * Handles: throw expression;
 */
public class ThrowConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ThrowStatement".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode expr = json.get("expression");
        if (expr != null && !expr.isNull()) {
            String expression = context.convertExpression(expr);
            return new ExpressionStatement("throw " + expression);
        }
        return new ExpressionStatement("throw");
    }
}
