package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

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
    public Object convert(JsonObject json, ConversionContext context) {
        String kindName = json.has("kindName") ? json.get("kindName").getAsString() : "";

        // Non-null assertion ! has no runtime effect
        if ("NonNullExpression".equals(kindName)) {
            JsonObject expr = json.getAsJsonObject("expression");
            return expr != null ? context.convertExpression(expr) : "";
        }

        // Type assertion has no runtime effect
        // First check if there's a pre-generated text (without the type assertion)
        if (json.has("text")) {
            return json.get("text").getAsString();
        }

        JsonObject expr = json.getAsJsonObject("expression");
        return expr != null ? context.convertExpression(expr) : "";
    }
}
