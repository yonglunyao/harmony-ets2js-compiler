package com.ets2jsc.infrastructure.parser.converters.expr;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for tagged template expressions.
 * Handles: tag`template ${expression}`
 */
public class TaggedTemplateExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "TaggedTemplateExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Check for pre-generated text
        if (json.has("text")) {
            return json.get("text").asText();
        }
        // Basic conversion - tag followed by template
        JsonNode tagNode = json.get("tag");
        String tag = tagNode != null ? context.convertExpression(tagNode) : "";
        return tag + "``";
    }
}
