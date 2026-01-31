package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for arrow function expressions.
 * Handles: (args) => expression, (args) => { statements }
 */
public class ArrowFunctionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ArrowFunction".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        // Arrow functions are already handled in parse-ets.js
        String arrowText = json.has("text") ? json.get("text").getAsString() : "";
        return arrowText.trim();
    }
}
