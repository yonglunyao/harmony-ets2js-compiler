package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonElement;
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
        JsonElement exprElement = json.get("expression");
        if (exprElement != null && !exprElement.isJsonNull()) {
            if (!exprElement.isJsonObject()) {
                return new ExpressionStatement("return");
            }
            JsonObject exprObj = exprElement.getAsJsonObject();
            String expression = context.convertExpression(exprObj);
            return new ExpressionStatement("return " + expression);
        }
        return new ExpressionStatement("return");
    }
}
