package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.EmptyStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for module declarations.
 * Handles: declare module 'module-name' { ... }
 *
 * Module declarations are TypeScript specific and used for ambient typing.
 * They are compile-time only and don't generate runtime code.
 */
public class ModuleDeclarationConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ModuleDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Module declarations are compile-time only, no runtime code generated
        // They are used for ambient type declarations in TypeScript
        return new EmptyStatement();
    }
}
