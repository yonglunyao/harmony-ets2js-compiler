package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.EmptyStatement;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
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
        // Regex literals should preserve their text representation
        String text = json.has("text") ? json.get("text").asText() : "/.*/";
        return new EmptyStatement(); // Return empty for now, regex needs special handling
    }
}
