package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for call expressions.
 * Handles: functionCall(), methodCall(), import('module')
 */
public class CallExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "CallExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject expression = json.getAsJsonObject("expression");
        String base = context.convertExpression(expression);

        // Check for dynamic import pattern: import('module')
        if ("import".equals(base)) {
            JsonArray argsArray = json.getAsJsonArray("arguments");
            if (argsArray != null && argsArray.size() > 0) {
                JsonElement argElement = argsArray.get(0);
                String modulePath = "";
                if (argElement.isJsonObject()) {
                    modulePath = context.convertExpression(argElement.getAsJsonObject());
                } else if (argElement.isJsonPrimitive() && argElement.getAsJsonPrimitive().isString()) {
                    modulePath = argElement.getAsString();
                }
                return "import(" + modulePath + ")";
            }
        }

        JsonArray argsArray = json.getAsJsonArray("arguments");
        StringBuilder args = new StringBuilder();
        if (argsArray != null) {
            List<String> argStrings = new ArrayList<>();
            for (JsonElement argElement : argsArray) {
                String arg = "";
                if (argElement.isJsonObject()) {
                    arg = context.convertExpression(argElement.getAsJsonObject());
                } else if (argElement.isJsonPrimitive() && argElement.getAsJsonPrimitive().isString()) {
                    arg = argElement.getAsString();
                } else if (argElement.isJsonNull()) {
                    arg = "null";
                }
                argStrings.add(arg != null ? arg.trim() : "");
            }
            args.append(String.join(", ", argStrings));
        }

        return base + "(" + args + ")";
    }
}
