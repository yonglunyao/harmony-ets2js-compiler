package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.EmptyStatement;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for tagged template expressions.
 * Handles: tag`template ${expression}`
 */
public class TaggedTemplateExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "TaggedTemplateExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Tagged templates need special handling, return empty for now
        return new EmptyStatement();
    }
}
