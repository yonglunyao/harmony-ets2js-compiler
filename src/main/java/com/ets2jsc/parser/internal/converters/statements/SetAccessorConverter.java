package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
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
        String name = json.get("name").asText();

        // Create a method declaration for the setter
        MethodDeclaration setter = new MethodDeclaration("set " + name);
        setter.setStatic(false);
        setter.setAsync(false);

        // Get parameters
        JsonNode paramsNode = json.get("parameters");
        if (paramsNode != null && paramsNode.isArray() && paramsNode.size() > 0) {
            JsonNode firstParam = paramsNode.get(0);
            String paramName = firstParam.get("name").asText();
            MethodDeclaration.Parameter param = new MethodDeclaration.Parameter(paramName);
            setter.addParameter(param);
        }

        // Get the body if present
        JsonNode bodyNode = json.get("body");
        if (bodyNode != null && bodyNode.isObject()) {
            // Body would be processed in code generation
        }

        return setter;
    }
}
