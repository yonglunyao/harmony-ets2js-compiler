package com.ets2jsc.infrastructure.parser.converters.expr;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Converter for property access expressions.
 * Handles: obj.property, obj.method()
 */
public class PropertyAccessConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "PropertyAccessExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        if (isNotValidExpression(json)) {
            return convertInvalidExpression(json, context);
        }

        return convertValidPropertyAccess(json, context);
    }

    /**
     * Converts a valid property access expression.
     *
     * @param json the JSON node containing the property access
     * @param context the conversion context
     * @return the converted code string
     */
    private String convertValidPropertyAccess(JsonNode json, ConversionContext context) {
        JsonNode expressionNode = json.get("expression");
        String base = context.convertExpression(expressionNode);
        String property = extractPropertyName(json);
        String dotOperator = determineDotOperator(json);

        return base + dotOperator + property + "(" + buildArguments(json, property) + ")";
    }

    /**
     * Converts an invalid expression (null, not object, etc.).
     *
     * @param json the JSON node
     * @param context the conversion context
     * @return a placeholder string for invalid expressions
     */
    private String convertInvalidExpression(JsonNode json, ConversionContext context) {
        return "\"<invalid_expression>\"";
    }

    /**
     * Checks if JSON node represents a valid expression.
     *
     * @param json the JSON node to check
     * @return true if valid, false otherwise
     */
    private boolean isNotValidExpression(JsonNode json) {
        return json == null || !json.isObject();
    }

    /**
     * Extracts the property name from the JSON node.
     *
     * @param json the JSON node containing the property access
     * @return property name
     */
    private String extractPropertyName(JsonNode json) {
        JsonNode nameNode = json.get("name");
        return (nameNode != null && nameNode.isTextual()) ? nameNode.asText().trim() : "";
    }

    /**
     * Determines if optional chaining should be used.
     *
     * @param json the JSON node
     * @return true if optional chaining should be used
     */
    private boolean hasQuestionDotToken(JsonNode json) {
        if (json == null || !json.has("questionDotToken")) {
            return false;
        }
        return json.get("questionDotToken").asBoolean(false);
    }

    /**
     * Determines the dot operator to use based on optional chaining.
     *
     * @param json the JSON node
     * @return "." if optional chaining, "?." otherwise
     */
    private String determineDotOperator(JsonNode json) {
        return hasQuestionDotToken(json) ? "?." : ".";
    }

    /**
     * Builds argument string for property access.
     *
     * @param json the JSON node containing the property access
     * @param property the property name
     * @return the argument string
     */
    private String buildArguments(JsonNode json, String property) {
        JsonNode argsArrayNode = json.get("arguments");
        if (argsArrayNode == null || !argsArrayNode.isArray()) {
            return "";
        }

        StringBuilder args = new StringBuilder();
        for (int i = 0; i < argsArrayNode.size(); i++) {
            if (i > 0) {
                args.append(", ");
            }
            JsonNode argNode = argsArrayNode.get(i);
            String arg = (argNode != null && argNode.isTextual()) ? argNode.asText() : "";
            args.append(arg != null ? arg.trim() : "");
        }

        return args.toString();
    }
}
