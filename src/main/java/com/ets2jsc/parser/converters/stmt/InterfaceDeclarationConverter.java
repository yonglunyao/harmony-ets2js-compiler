package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.EmptyStatement;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for interface declarations.
 * Interfaces are compile-time only and don't generate runtime code.
 * Handles: interface MyInterface { ... }
 */
public class InterfaceDeclarationConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "InterfaceDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Interfaces are compile-time only, no runtime code generated
        // Return EmptyStatement instead of null to avoid errors
        return new EmptyStatement();
    }
}
