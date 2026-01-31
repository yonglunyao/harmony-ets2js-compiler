package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.parser.internal.ConversionContext;
import com.google.gson.JsonObject;

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
    protected String getLoopHeader(JsonObject json, ConversionContext context) {
        JsonObject expression = json.getAsJsonObject("expression");
        String condition = expression != null ? context.convertExpression(expression) : "";

        StringBuilder sb = new StringBuilder();
        sb.append("while (").append(condition).append(") {\n");

        return sb.toString();
    }

    @Override
    protected JsonObject getLoopBody(JsonObject json) {
        return json.getAsJsonObject("statement");
    }
}
