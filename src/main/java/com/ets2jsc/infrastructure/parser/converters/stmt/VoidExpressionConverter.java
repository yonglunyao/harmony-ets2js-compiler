package com.ets2jsc.infrastructure.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.EmptyStatement;
import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for void expressions.
 * Handles: void expression
 */
public class VoidExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "VoidExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Void expressions don't produce values, return empty
        return new EmptyStatement();
    }
}
