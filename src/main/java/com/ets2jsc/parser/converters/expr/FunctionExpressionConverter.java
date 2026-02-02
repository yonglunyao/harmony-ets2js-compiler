package com.ets2jsc.parser.converters.expr;

import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for function expressions.
 * Handles: const fn = function() { ... } or const fn = function name() { ... }
 */
public class FunctionExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "FunctionExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("function");

        // Optional function name
        JsonNode nameNode = json.get("name");
        if (nameNode != null && !nameNode.isNull()) {
            String name = nameNode.asText();
            if (!name.isEmpty()) {
                sb.append(" ").append(name);
            }
        }

        // Parameters
        sb.append("(");
        JsonNode paramsNode = json.get("parameters");
        if (paramsNode != null && paramsNode.isArray()) {
            for (int i = 0; i < paramsNode.size(); i++) {
                if (i > 0) sb.append(", ");
                JsonNode param = paramsNode.get(i);
                String paramName = param.get("name").asText();
                sb.append(paramName);
            }
        }
        sb.append(")");

        // Body - for expressions, just indicate there's a body
        JsonNode bodyNode = json.get("body");
        if (bodyNode != null) {
            sb.append(" { ... }");
        }

        return sb.toString();
    }
}
