package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Converter for array literal expressions.
 * Handles: [1, 2, 3], [a, b, c]
 */
public class ArrayLiteralConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ArrayLiteralExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonArray elements = json.getAsJsonArray("elements");
        if (elements == null || elements.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(context.convertExpression(elements.get(i).getAsJsonObject()));
        }
        sb.append("]");
        return sb.toString();
    }
}
