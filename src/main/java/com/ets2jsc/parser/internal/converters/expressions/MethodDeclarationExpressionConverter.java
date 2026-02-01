package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Converter for method declarations in expression context.
 * Handles method shorthand in object literals: { methodName() { ... } }
 */
public class MethodDeclarationExpressionConverter implements NodeConverter {

    private static final String METHOD_DECLARATION = "MethodDeclaration";

    @Override
    public boolean canConvert(String kindName) {
        return METHOD_DECLARATION.equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        return convertMethodDeclaration(json, context);
    }

    /**
     * Converts a method declaration to JavaScript function shorthand syntax.
     * Generates: methodName(param1, param2) { body }
     * CC: 2 (null checks)
     */
    private String convertMethodDeclaration(JsonNode json, ConversionContext context) {
        String name = json.has("name") ? json.get("name").asText() : "";

        // Check for async modifier
        boolean isAsync = hasModifier(json, "AsyncKeyword");
        String prefix = isAsync ? "async " : "";

        // Build parameter list
        String params = convertParameters(json, context);

        // Convert body
        String body = convertBody(json, context);

        return prefix + name + params + " " + body;
    }

    /**
     * Converts method parameters.
     * CC: 2 (null check + loop)
     */
    private String convertParameters(JsonNode json, ConversionContext context) {
        JsonNode paramsNode = json.get("parameters");
        if (paramsNode == null || !paramsNode.isArray()) {
            return "()";
        }

        ArrayNode paramsArray = (ArrayNode) paramsNode;
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < paramsArray.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            JsonNode param = paramsArray.get(i);
            String paramName = param.has("name") ? param.get("name").asText() : "";

            // Handle rest parameter
            if (hasDotDotDotPrefix(param)) {
                sb.append("...");
            }
            sb.append(paramName);

            // Handle default value
            if (param.has("initializer") && !param.get("initializer").isNull()) {
                JsonNode initializer = param.get("initializer");
                String defaultVal = context.convertExpression(initializer);
                sb.append(" = ").append(defaultVal);
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Converts method body.
     * CC: 2 (null checks)
     */
    private String convertBody(JsonNode json, ConversionContext context) {
        JsonNode bodyNode = json.get("body");
        if (bodyNode == null || bodyNode.isNull()) {
            return "{}";
        }

        // Convert body statement
        try {
            Object bodyResult = context.convertStatement(bodyNode);
            return bodyResult.toString();
        } catch (Exception e) {
            // Fallback to placeholder
            return "{ ... }";
        }
    }

    /**
     * Checks if the node has a specific modifier.
     * CC: 3 (null checks + loop)
     */
    private boolean hasModifier(JsonNode json, String modifierKindName) {
        JsonNode modifiersNode = json.get("modifiers");
        if (modifiersNode == null || !modifiersNode.isArray()) {
            return false;
        }

        ArrayNode modifiersArray = (ArrayNode) modifiersNode;
        for (int i = 0; i < modifiersArray.size(); i++) {
            JsonNode mod = modifiersArray.get(i);
            if (mod != null && mod.has("kindName")) {
                String kind = mod.get("kindName").asText();
                if (modifierKindName.equals(kind)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if parameter has ... prefix (rest parameter).
     * CC: 2 (null check + has check)
     */
    private boolean hasDotDotDotPrefix(JsonNode param) {
        if (param == null || !param.has("dotDotDotToken")) {
            return false;
        }
        return param.get("dotDotDotToken").asBoolean(false);
    }
}
