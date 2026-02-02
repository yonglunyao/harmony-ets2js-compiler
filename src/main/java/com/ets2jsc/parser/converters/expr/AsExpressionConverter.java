package com.ets2jsc.parser.converters.expr;

import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for type assertions and non-null expressions.
 * Handles: as Type, <Type>expr, expr!
 */
public class AsExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "AsExpression".equals(kindName) ||
               "TypeAssertion".equals(kindName) ||
               "NonNullExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        String kindName = json.has("kindName") ? json.get("kindName").asText() : "";

        // Non-null assertion ! has no runtime effect
        if ("NonNullExpression".equals(kindName)) {
            JsonNode exprNode = json.get("expression");
            return (exprNode != null && exprNode.isObject()) ? context.convertExpression(exprNode) : "";
        }

        // Type assertion has no runtime effect
        // First check if there's a pre-generated text (without the type assertion)
        if (json.has("text")) {
            return json.get("text").asText();
        }

        JsonNode exprNode = json.get("expression");
        return (exprNode != null && exprNode.isObject()) ? context.convertExpression(exprNode) : "";
    }
}
