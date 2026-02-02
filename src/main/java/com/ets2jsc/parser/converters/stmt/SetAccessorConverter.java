package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.ExpressionStatement;
import com.ets2jsc.domain.model.ast.MethodDeclaration;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for setter accessor declarations.
 * Handles: set propertyName(value) { this.property = value; }
 */
public class SetAccessorConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "SetAccessor".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode nameNode = json.get("name");
        String name = (nameNode != null && !nameNode.isNull()) ? nameNode.asText() : "";

        // Create a method declaration for the setter
        MethodDeclaration setter = new MethodDeclaration("set " + name);
        setter.setStatic(false);
        setter.setAsync(false);

        // Get parameters
        JsonNode paramsNode = json.get("parameters");
        if (paramsNode != null && paramsNode.isArray() && paramsNode.size() > 0) {
            JsonNode firstParam = paramsNode.get(0);
            JsonNode paramNameNode = firstParam.get("name");
            String paramName = (paramNameNode != null && !paramNameNode.isNull()) ? paramNameNode.asText() : "value";
            MethodDeclaration.Parameter param = new MethodDeclaration.Parameter(paramName);
            setter.addParameter(param);
        }

        // Check if there's a pre-generated text representation
        JsonNode textNode = json.get("text");
        if (textNode != null && !textNode.isNull()) {
            String text = textNode.asText();
            // Store the text as an expression statement
            setter.setBody(new ExpressionStatement(text));
        }

        // Convert body if present
        JsonNode bodyNode = json.get("body");
        if (bodyNode != null && bodyNode.isObject() && setter.getBody() == null) {
            AstNode body = context.convertStatement(bodyNode);
            setter.setBody(body);
        }

        return setter;
    }
}
