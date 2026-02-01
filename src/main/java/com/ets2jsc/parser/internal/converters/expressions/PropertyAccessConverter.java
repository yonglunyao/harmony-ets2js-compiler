package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
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
        JsonNode expressionNode = json.get("expression");
        String base = context.convertExpression(expressionNode);
        String property = json.has("name") ? json.get("name").asText().trim() : "";

        // Check for optional chaining (?.)
        boolean isOptionalChain = hasQuestionDotToken(json);
        String dotOperator = isOptionalChain ? "?." : ".";

        // Check if this is a chained call (expression is CallExpression with arguments)
        JsonNode argsArrayNode = json.get("arguments");
        if (argsArrayNode != null && argsArrayNode.isArray()) {
            ArrayNode argsArray = (ArrayNode) argsArrayNode;
            StringBuilder args = new StringBuilder();
            for (int i = 0; i < argsArray.size(); i++) {
                if (i > 0) args.append(", ");
                JsonNode argNode = argsArray.get(i);
                String arg = (argNode != null && argNode.isTextual()) ? argNode.asText() : "";
                args.append(arg != null ? arg.trim() : "");
            }
            return base + dotOperator + property + "(" + args + ")";
        }

        return base + dotOperator + property;
    }

    /**
     * Checks if expression has question dot token (optional chaining).
     * CC: 2 (null check + has check)
     */
    private boolean hasQuestionDotToken(JsonNode json) {
        if (json == null || !json.has("questionDotToken")) {
            return false;
        }
        return json.get("questionDotToken").asBoolean(false);
    }
}
