package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.ExpressionStatement;
import com.ets2jsc.domain.model.ast.MethodDeclaration;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
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
        JsonNode nameNode = json.get("name");
        String name = (nameNode != null && !nameNode.isNull()) ? nameNode.asText() : "";

        // Create a method declaration for the getter
        MethodDeclaration getter = new MethodDeclaration("get " + name);
        getter.setStatic(false);
        getter.setAsync(false);

        // Check if there's a pre-generated text representation
        JsonNode textNode = json.get("text");
        if (textNode != null && !textNode.isNull()) {
            String text = textNode.asText();
            // Store the text as an expression statement
            getter.setBody(new ExpressionStatement(text));
        }

        // Convert body if present
        JsonNode bodyNode = json.get("body");
        if (bodyNode != null && bodyNode.isObject() && getter.getBody() == null) {
            AstNode body = context.convertStatement(bodyNode);
            getter.setBody(body);
        }

        return getter;
    }
}
