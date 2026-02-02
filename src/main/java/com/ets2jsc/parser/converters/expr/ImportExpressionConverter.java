package com.ets2jsc.parser.converters.expr;

import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for dynamic import expressions.
 * Handles: import('module')
 */
public class ImportExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ImportExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Handle dynamic import: await import('module')
        // TypeScript ImportExpression uses 'expression' field (not 'argument')
        JsonNode exprNode = json.get("expression");
        String modulePath = (exprNode != null && exprNode.isObject()) ? context.convertExpression(exprNode) : "";
        return "import(" + modulePath + ")";
    }
}
