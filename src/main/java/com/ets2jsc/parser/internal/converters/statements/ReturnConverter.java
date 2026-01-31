package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

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
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject exprObj = json.getAsJsonObject("expression");
        if (exprObj != null) {
            String expression = context.convertExpression(exprObj);
            return new ExpressionStatement("return " + expression);
        }
        return new ExpressionStatement("return");
    }
}
