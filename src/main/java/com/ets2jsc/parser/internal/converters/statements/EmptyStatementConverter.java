package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.EmptyStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonObject;

/**
 * Converter for empty statements.
 * Handles: standalone semicolon (;)
 */
public class EmptyStatementConverter implements NodeConverter {

    private static final String KIND_NAME = "EmptyStatement";

    @Override
    public boolean canConvert(String kindName) {
        return KIND_NAME.equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        return new EmptyStatement();
    }
}
