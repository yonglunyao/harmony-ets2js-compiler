package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Converter for object literal expressions.
 * Handles: {a: 1, b: 2}, {a, b}
 */
public class ObjectLiteralConverter implements NodeConverter {

    private static final String OBJECT_LITERAL_EXPRESSION = "ObjectLiteralExpression";
    private static final String PROPERTY_ASSIGNMENT = "PropertyAssignment";
    private static final String SHORTHAND_PROPERTY_ASSIGNMENT = "ShorthandPropertyAssignment";

    @Override
    public boolean canConvert(String kindName) {
        return OBJECT_LITERAL_EXPRESSION.equals(kindName) ||
               PROPERTY_ASSIGNMENT.equals(kindName) ||
               SHORTHAND_PROPERTY_ASSIGNMENT.equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        String kindName = getKindName(json);

        if (PROPERTY_ASSIGNMENT.equals(kindName)) {
            return convertPropertyAssignment(json, context);
        }

        if (SHORTHAND_PROPERTY_ASSIGNMENT.equals(kindName)) {
            return convertShorthandProperty(json);
        }

        return convertObjectLiteral(json, context);
    }

    /**
     * Converts a property assignment (name: value).
     * CC: 2 (null checks)
     */
    private String convertPropertyAssignment(JsonNode json, ConversionContext context) {
        String propName = json.has("name") ? json.get("name").asText() : "";
        JsonNode propValueNode = json.get("value");
        String valueStr = (propValueNode != null && propValueNode.isObject()) ? context.convertExpression(propValueNode) : "";
        return propName + ": " + valueStr;
    }

    /**
     * Converts a shorthand property assignment ({name}).
     * CC: 1
     */
    private String convertShorthandProperty(JsonNode json) {
        return json.has("name") ? json.get("name").asText() : "";
    }

    /**
     * Converts an object literal expression.
     * CC: 2 (null check + loop)
     */
    private String convertObjectLiteral(JsonNode json, ConversionContext context) {
        JsonNode propertiesNode = json.get("properties");
        if (propertiesNode == null || !propertiesNode.isArray()) {
            return "{}";
        }

        ArrayNode properties = (ArrayNode) propertiesNode;
        if (properties.size() == 0) {
            return "{}";
        }

        return buildObjectLiteral(properties, context);
    }

    /**
     * Builds object literal string from properties array.
     * CC: 2 (loop + ternary)
     */
    private String buildObjectLiteral(ArrayNode properties, ConversionContext context) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < properties.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(context.convertExpression(properties.get(i)));
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Gets kind name safely.
     * CC: 2 (null check + has check)
     */
    private String getKindName(JsonNode json) {
        if (json == null || !json.has("kindName")) {
            return "";
        }
        return json.get("kindName").asText();
    }
}
