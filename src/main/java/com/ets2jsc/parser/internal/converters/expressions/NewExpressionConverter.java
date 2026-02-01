package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for new expressions.
 * Handles: new Constructor(), new Class(arg1, arg2)
 */
public class NewExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "NewExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode newExprNode = json.get("expression");
        String exprStr = (newExprNode != null && newExprNode.isObject()) ? context.convertExpression(newExprNode) : "";
        StringBuilder sb = new StringBuilder();
        sb.append("new ").append(exprStr).append("(");
        JsonNode argumentsNode = json.get("arguments");
        if (argumentsNode != null && argumentsNode.isArray() && argumentsNode.size() > 0) {
            ArrayNode arguments = (ArrayNode) argumentsNode;
            List<String> argStrings = new ArrayList<>();
            for (int i = 0; i < arguments.size(); i++) {
                String argStr = context.convertExpression(arguments.get(i));
                argStrings.add(argStr);
            }
            sb.append(String.join(", ", argStrings));
        }
        sb.append(")");
        return sb.toString();
    }
}
