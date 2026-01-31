package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Converter for property access expressions.
 * Handles: obj.property, obj.method()
 */
public class PropertyAccessConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "PropertyAccessExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject expression = json.getAsJsonObject("expression");
        String base = context.convertExpression(expression);
        String property = json.has("name") ? json.get("name").getAsString().trim() : "";

        // Check if this is a chained call (expression is CallExpression with arguments)
        JsonArray argsArray = json.getAsJsonArray("arguments");
        if (argsArray != null && argsArray.size() > 0) {
            StringBuilder args = new StringBuilder();
            for (int i = 0; i < argsArray.size(); i++) {
                if (i > 0) args.append(", ");
                String arg = argsArray.get(i).getAsString();
                args.append(arg != null ? arg.trim() : "");
            }
            return base + "." + property + "(" + args + ")";
        }

        return base + "." + property;
    }
}
