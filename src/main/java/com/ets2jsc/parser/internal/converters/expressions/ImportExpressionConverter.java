package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for dynamic import expressions.
 * Handles: import('module')
 */
public class ImportExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ImportExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        // Handle dynamic import: await import('module')
        // TypeScript ImportExpression uses 'expression' field (not 'argument')
        JsonObject expr = json.getAsJsonObject("expression");
        String modulePath = expr != null ? context.convertExpression(expr) : "";
        return "import(" + modulePath + ")";
    }
}
