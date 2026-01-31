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

        return "while (" + condition + ") {\n";
    }

    @Override
    protected JsonObject getLoopBody(JsonObject json) {
        return json.getAsJsonObject("statement");
    }
}
