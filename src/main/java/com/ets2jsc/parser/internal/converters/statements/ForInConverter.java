package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.parser.internal.ConversionContext;
import com.google.gson.JsonObject;

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
    protected String getLoopHeader(JsonObject json, ConversionContext context) {
        JsonObject initializer = json.getAsJsonObject("initializer");
        JsonObject expression = json.getAsJsonObject("expression");

        String initStr = initializer != null ? context.convertExpression(initializer) : "";
        String exprStr = expression != null ? context.convertExpression(expression) : "";

        return "for (" + initStr + " in " + exprStr + ") {\n";
    }

    @Override
    protected JsonObject getLoopBody(JsonObject json) {
        return json.getAsJsonObject("statement");
    }
}
