package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.parser.internal.ConversionContext;
import com.google.gson.JsonObject;

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
    protected String getLoopHeader(JsonObject json, ConversionContext context) {
        JsonObject initializer = json.getAsJsonObject("initializer");
        JsonObject expression = json.getAsJsonObject("expression");
        boolean awaitModifier = json.has("awaitModifier") && json.get("awaitModifier").getAsBoolean();

        String initStr = initializer != null ? context.convertExpression(initializer) : "";
        String exprStr = expression != null ? context.convertExpression(expression) : "";

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
    protected JsonObject getLoopBody(JsonObject json) {
        return json.getAsJsonObject("statement");
    }
}
