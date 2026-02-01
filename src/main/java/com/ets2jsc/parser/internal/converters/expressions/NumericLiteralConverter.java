package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.constant.Symbols;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for numeric literal expressions.
 * Handles: 123, 0o777 (octal), 0xABC (hex)
 */
public class NumericLiteralConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "NumericLiteral".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        String numText = json.has("text") ? json.get("text").asText() : "";
        // Check for computed value (for octal conversion)
        if (json.has("value")) {
            Number value = json.get("value").numberValue();
            return String.valueOf(value.intValue());
        }
        // Manual octal conversion if needed
        if (numText.startsWith("0o") || numText.startsWith("0O")) {
            String octalStr = numText.substring(2);
            try {
                int decimalValue = Integer.parseInt(octalStr, 8);
                return String.valueOf(decimalValue);
            } catch (NumberFormatException e) {
                return numText;
            }
        }
        return numText.trim();
    }

    @Override
    public int getPriority() {
        return Symbols.NUMERIC_LITERAL_CONVERTER_PRIORITY;
    }
}
