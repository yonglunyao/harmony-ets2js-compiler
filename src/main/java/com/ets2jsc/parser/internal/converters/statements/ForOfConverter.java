package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.parser.internal.ConversionContext;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for for...of loop statements.
 * Handles: for (let item of array) { ... }
 * Handles: for await (let item of asyncArray) { ... }
 */
public class ForOfConverter extends LoopConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ForOfStatement".equals(kindName);
    }

    @Override
    protected String getLoopHeader(JsonNode json, ConversionContext context) {
        JsonNode initializerNode = json.get("initializer");
        JsonNode expressionNode = json.get("expression");
        boolean awaitModifier = json.has("awaitModifier") && json.get("awaitModifier").asBoolean();

        String initStr = (initializerNode != null && initializerNode.isObject()) ? context.convertExpression(initializerNode) : "";
        String exprStr = (expressionNode != null && expressionNode.isObject()) ? context.convertExpression(expressionNode) : "";

        StringBuilder sb = new StringBuilder();
        if (awaitModifier) {
            sb.append("for await (");
        } else {
            sb.append("for (");
        }
        sb.append(initStr).append(" of ").append(exprStr).append(") {\n");

        return sb.toString();
    }

    @Override
    protected JsonNode getLoopBody(JsonNode json) {
        return json.get("statement");
    }
}
