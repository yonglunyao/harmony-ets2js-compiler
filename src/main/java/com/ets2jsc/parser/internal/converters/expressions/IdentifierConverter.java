package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for identifier and import keyword expressions.
 */
public class IdentifierConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "Identifier".equals(kindName) || "ImportKeyword".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String text = json.has("text") ? json.get("text").getAsString() : "";
        return text.trim();
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
