package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for literal expressions using enum pattern to minimize class count.
 * Handles: TrueLiteral, FalseLiteral, NullLiteral, UndefinedLiteral, ThisKeyword
 */
public class LiteralConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "TrueLiteral".equals(kindName) ||
               "FalseLiteral".equals(kindName) ||
               "NullLiteral".equals(kindName) ||
               "UndefinedLiteral".equals(kindName) ||
               "ThisKeyword".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String kindName = json.has("kindName") ? json.get("kindName").getAsString() : "";

        switch (kindName) {
            case "TrueLiteral":
                return "true";
            case "FalseLiteral":
                return "false";
            case "NullLiteral":
                return "null";
            case "UndefinedLiteral":
                return "undefined";
            case "ThisKeyword":
                return "this";
            default:
                throw new UnsupportedOperationException("Unsupported literal: " + kindName);
        }
    }

    @Override
    public int getPriority() {
        return 100; // High priority for simple literals
    }
}
