package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.parser.internal.ConversionContext;
import com.google.gson.JsonObject;

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
    protected String getLoopHeader(JsonObject json, ConversionContext context) {
        JsonObject initializer = json.getAsJsonObject("initializer");
        JsonObject condition = json.getAsJsonObject("condition");
        JsonObject incrementor = json.getAsJsonObject("incrementor");

        String initStr = initializer != null ? context.convertExpression(initializer) : "";
        String condStr = condition != null ? context.convertExpression(condition) : "";
        String incrStr = incrementor != null ? context.convertExpression(incrementor) : "";

        StringBuilder sb = new StringBuilder();
        sb.append("for (").append(initStr).append("; ");
        sb.append(condStr).append("; ");
        sb.append(incrStr).append(") {\n");

        return sb.toString();
    }

    @Override
    protected JsonObject getLoopBody(JsonObject json) {
        return json.getAsJsonObject("statement");
    }
}
