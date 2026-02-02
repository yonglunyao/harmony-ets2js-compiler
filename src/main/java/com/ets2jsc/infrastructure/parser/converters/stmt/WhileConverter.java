package com.ets2jsc.infrastructure.parser.converters.stmt;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for while loop statements.
 * Handles: while (condition) { ... }
 */
public class WhileConverter extends LoopConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "WhileStatement".equals(kindName);
    }

    @Override
    protected String getLoopHeader(JsonNode json, ConversionContext context) {
        JsonNode expressionNode = json.get("expression");
        String condition = (expressionNode != null && expressionNode.isObject()) ? context.convertExpression(expressionNode) : "";

        return "while (" + condition + ") {\n";
    }

    @Override
    protected JsonNode getLoopBody(JsonNode json) {
        return json.get("statement");
    }
}
