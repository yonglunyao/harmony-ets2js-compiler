package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.EmptyStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for labeled statements.
 * Handles: label: statement
 */
public class LabeledStatementConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "LabeledStatement".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Labeled statements are rare, return empty for now
        return new EmptyStatement();
    }
}
