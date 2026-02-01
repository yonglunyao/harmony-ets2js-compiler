package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for getter accessor declarations.
 * Handles: get propertyName() { return value; }
 */
public class GetAccessorConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "GetAccessor".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        String name = json.get("name").asText();

        // Create a method declaration for the getter
        MethodDeclaration getter = new MethodDeclaration("get " + name);
        getter.setStatic(false);
        getter.setAsync(false);

        // Get the body if present - we'll store it as an expression statement
        JsonNode bodyNode = json.get("body");
        if (bodyNode != null && bodyNode.isObject()) {
            // For now, just indicate there's a body
            // The actual body statements would be processed in code generation
        }

        return getter;
    }
}
