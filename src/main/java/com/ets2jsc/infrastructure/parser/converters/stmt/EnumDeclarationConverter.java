package com.ets2jsc.infrastructure.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.EmptyStatement;
import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for enum declarations.
 * Handles: enum MyEnum { A, B, C }
 *
 * Enums are TypeScript specific. While JavaScript doesn't have native enum support,
 * they are typically compiled to object literals. For ETS to JS compilation purposes,
 * we treat them as compile-time only constructs (type definitions).
 */
public class EnumDeclarationConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "EnumDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Enum declarations are compile-time only for type checking
        // In ETS context, enums are used primarily for type safety
        return new EmptyStatement();
    }
}
