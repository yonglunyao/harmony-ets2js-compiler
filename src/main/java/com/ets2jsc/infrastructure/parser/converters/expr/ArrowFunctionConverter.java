package com.ets2jsc.infrastructure.parser.converters.expr;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        // Arrow functions are already handled in parse-ets.js
        String arrowText = json.has("text") ? json.get("text").asText() : "";
        return arrowText.trim();
    }
}
