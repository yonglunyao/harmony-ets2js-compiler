package com.ets2jsc.infrastructure.parser.converters.expr;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for identifier and import keyword expressions.
 */
public class IdentifierConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "Identifier".equals(kindName)
            || "ImportKeyword".equals(kindName)
            || "SuperKeyword".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        String kindName = json.has("kindName") ? json.get("kindName").asText() : "";

        // Handle special keywords
        if ("SuperKeyword".equals(kindName)) {
            return "super";
        }
        if ("ImportKeyword".equals(kindName)) {
            return "import";
        }

        // Handle regular identifiers
        String text = json.has("text") ? json.get("text").asText() : "";
        return text.trim();
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
