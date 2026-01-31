package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.CallExpression;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.ast.ForeachStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Converter for expression statements.
 * Handles: expression;
 */
public class ExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ExpressionStatement".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject exprObj = json.getAsJsonObject("expression");
        String kindName = exprObj.has("kindName") ? exprObj.get("kindName").getAsString() : "";

        // Check for special components: ForEach
        if ("CallExpression".equals(kindName)) {
            String componentName = exprObj.has("componentName") ? exprObj.get("componentName").getAsString() : "";
            if ("ForEach".equals(componentName)) {
                return convertForEachExpression(exprObj, context);
            }
        }

        String expression = context.convertExpression(exprObj);
        return new ExpressionStatement(expression);
    }

    private ForeachStatement convertForEachExpression(JsonObject json, ConversionContext context) {
        // ForEach has 3 arguments: array, itemGenerator, keyGenerator
        JsonArray argsArray = json.getAsJsonArray("arguments");

        String arrayExpr = "";
        String itemGenExpr = "";
        String keyGenExpr = "";

        if (argsArray != null && argsArray.size() >= 2) {
            arrayExpr = context.convertExpression(argsArray.get(0).getAsJsonObject());
            itemGenExpr = context.convertExpression(argsArray.get(1).getAsJsonObject());

            if (argsArray.size() >= 3) {
                keyGenExpr = context.convertExpression(argsArray.get(2).getAsJsonObject());
            }
        }

        return new ForeachStatement(arrayExpr, itemGenExpr, keyGenExpr);
    }
}
