package com.ets2jsc.infrastructure.parser.converters.expr;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for class expressions.
 * Handles: const MyClass = class { ... } or const MyClass = class BaseClass { ... }
 */
public class ClassExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ClassExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("class");

        // Optional heritage (extends clause)
        JsonNode heritageNode = json.get("heritageClause");
        if (heritageNode != null && !heritageNode.isNull()) {
            String baseClass = heritageNode.asText();
            sb.append(" extends ").append(baseClass);
        }

        // Body - for expressions, just indicate there's a body
        sb.append(" { ... }");

        return sb.toString();
    }
}
