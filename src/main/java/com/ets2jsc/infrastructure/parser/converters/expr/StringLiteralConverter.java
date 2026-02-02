package com.ets2jsc.infrastructure.parser.converters.expr;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for string literal expressions.
 * Handles: "string", 'string'
 */
public class StringLiteralConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "StringLiteral".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // String literals may already have quotes from parse-ets.js
        String strText = json.has("text") ? json.get("text").asText() : "";
        if (strText.startsWith("\"") || strText.startsWith("'")) {
            return strText;
        }
        return "\"" + strText + "\"";
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
