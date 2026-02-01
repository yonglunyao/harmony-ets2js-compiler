package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.EmptyStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for yield expressions.
 * Handles: yield value
 */
public class YieldExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "YieldExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Yield expressions are for generators, return empty for now
        return new EmptyStatement();
    }
}
