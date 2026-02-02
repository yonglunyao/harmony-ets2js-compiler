package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.EmptyStatement;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        return new EmptyStatement();
    }
}
