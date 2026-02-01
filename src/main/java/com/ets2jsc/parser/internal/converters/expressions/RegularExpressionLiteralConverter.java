package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for regular expression literals.
 * Handles: /pattern/flags
 */
public class RegularExpressionLiteralConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "RegularExpressionLiteral".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Check for pre-generated text
        if (json.has("text")) {
            return json.get("text").asText();
        }
        // Regex literals need to preserve their format
        // For now, return a basic pattern
        return "/.*/";
    }
}
