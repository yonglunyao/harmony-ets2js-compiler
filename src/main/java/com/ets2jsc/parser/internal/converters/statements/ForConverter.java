package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.parser.internal.ConversionContext;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for for loop statements.
 * Handles: for (init; condition; increment) { ... }
 */
public class ForConverter extends LoopConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ForStatement".equals(kindName);
    }

    @Override
    protected String getLoopHeader(JsonNode json, ConversionContext context) {
        JsonNode initializerNode = json.get("initializer");
        JsonNode conditionNode = json.get("condition");
        JsonNode incrementorNode = json.get("incrementor");

        String initStr = (initializerNode != null && initializerNode.isObject()) ? context.convertExpression(initializerNode) : "";
        String condStr = (conditionNode != null && conditionNode.isObject()) ? context.convertExpression(conditionNode) : "";
        String incrStr = (incrementorNode != null && incrementorNode.isObject()) ? context.convertExpression(incrementorNode) : "";

        StringBuilder sb = new StringBuilder();
        sb.append("for (").append(initStr).append("; ");
        sb.append(condStr).append("; ");
        sb.append(incrStr).append(") {\n");

        return sb.toString();
    }

    @Override
    protected JsonNode getLoopBody(JsonNode json) {
        return json.get("statement");
    }
}
