package com.ets2jsc.infrastructure.parser.converters.stmt;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for...in loop statements.
 * Handles: for (let key in object) { ... }
 */
public class ForInConverter extends LoopConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ForInStatement".equals(kindName);
    }

    @Override
    protected String getLoopHeader(JsonNode json, ConversionContext context) {
        JsonNode initializerNode = json.get("initializer");
        JsonNode expressionNode = json.get("expression");

        String initStr = (initializerNode != null && initializerNode.isObject()) ? context.convertExpression(initializerNode) : "";
        String exprStr = (expressionNode != null && expressionNode.isObject()) ? context.convertExpression(expressionNode) : "";

        return "for (" + initStr + " in " + exprStr + ") {\n";
    }

    @Override
    protected JsonNode getLoopBody(JsonNode json) {
        return json.get("statement");
    }
}
