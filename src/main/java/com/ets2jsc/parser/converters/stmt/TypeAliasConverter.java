package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.EmptyStatement;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for type alias declarations.
 * Type aliases are compile-time only and don't generate runtime code.
 * Handles: type MyType = string | number;
 */
public class TypeAliasConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "TypeAliasDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Type aliases are compile-time only, no runtime code generated
        // Return EmptyStatement instead of null to avoid errors
        return new EmptyStatement();
    }
}
