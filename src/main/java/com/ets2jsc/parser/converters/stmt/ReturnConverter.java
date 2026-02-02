package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.ExpressionStatement;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for return statements.
 * Handles: return expression;
 */
public class ReturnConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ReturnStatement".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode exprElement = json.get("expression");
        if (exprElement != null && !exprElement.isNull()) {
            if (!exprElement.isObject()) {
                return new ExpressionStatement("return");
            }
            String expression = context.convertExpression(exprElement);
            return new ExpressionStatement("return " + expression);
        }
        return new ExpressionStatement("return");
    }
}
