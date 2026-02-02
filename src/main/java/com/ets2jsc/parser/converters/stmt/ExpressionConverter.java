package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.CallExpression;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.ast.ForeachStatement;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode exprNode = json.get("expression");
        String kindName = exprNode.has("kindName") ? exprNode.get("kindName").asText() : "";

        // Check for special components: ForEach
        if ("CallExpression".equals(kindName)) {
            String componentName = exprNode.has("componentName") ? exprNode.get("componentName").asText() : "";
            if ("ForEach".equals(componentName)) {
                return convertForEachExpression(exprNode, context);
            }
        }

        String expression = context.convertExpression(exprNode);
        return new ExpressionStatement(expression);
    }

    private ForeachStatement convertForEachExpression(JsonNode json, ConversionContext context) {
        // ForEach has 3 arguments: array, itemGenerator, keyGenerator
        JsonNode argsNode = json.get("arguments");

        String arrayExpr = "";
        String itemGenExpr = "";
        String keyGenExpr = "";

        if (argsNode != null && argsNode.isArray() && argsNode.size() >= 2) {
            ArrayNode argsArray = (ArrayNode) argsNode;
            arrayExpr = context.convertExpression(argsArray.get(0));
            itemGenExpr = context.convertExpression(argsArray.get(1));

            if (argsArray.size() >= 3) {
                keyGenExpr = context.convertExpression(argsArray.get(2));
            }
        }

        return new ForeachStatement(arrayExpr, itemGenExpr, keyGenExpr);
    }
}
