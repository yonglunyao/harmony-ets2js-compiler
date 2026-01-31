package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Converter for object literal expressions.
 * Handles: {a: 1, b: 2}, {a, b}
 */
public class ObjectLiteralConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ObjectLiteralExpression".equals(kindName) ||
               "PropertyAssignment".equals(kindName) ||
               "ShorthandPropertyAssignment".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String kindName = json.has("kindName") ? json.get("kindName").getAsString() : "";

        if ("PropertyAssignment".equals(kindName)) {
            String propName = json.has("name") ? json.get("name").getAsString() : "";
            JsonObject propValue = json.getAsJsonObject("value");
            String valueStr = propValue != null ? context.convertExpression(propValue) : "";
            return propName + ": " + valueStr;
        }

        if ("ShorthandPropertyAssignment".equals(kindName)) {
            String shortName = json.has("name") ? json.get("name").getAsString() : "";
            return shortName;
        }

        // ObjectLiteralExpression
        JsonArray properties = json.getAsJsonArray("properties");
        if (properties == null || properties.size() == 0) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < properties.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(context.convertExpression(properties.get(i).getAsJsonObject()));
        }
        sb.append("}");
        return sb.toString();
    }
}
