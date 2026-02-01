package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.EmptyStatement;
import com.ets2jsc.ast.ExportStatement;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for export assignment statements.
 * Handles:
 * - export default expression
 * - export = expression (TypeScript specific)
 */
public class ExportAssignmentConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ExportAssignment".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        boolean isExportEquals = isExportEquals(json);

        if (isExportEquals) {
            // export = expression (TypeScript specific)
            // This is CommonJS export syntax, not needed for ES modules
            return new EmptyStatement();
        }

        // export default expression
        String expressionString = extractExpressionString(json, context);
        return new ExportStatement(null, false, "default " + expressionString);
    }

    /**
     * Checks if this is an export equals assignment.
     * CC: 1
     */
    private boolean isExportEquals(JsonNode json) {
        return json.has("isExportEquals") && json.get("isExportEquals").asBoolean();
    }

    /**
     * Extracts the expression as a string.
     * CC: 2
     */
    private String extractExpressionString(JsonNode json, ConversionContext context) {
        if (!json.has("expression") || json.get("expression").isNull()) {
            return "";
        }

        JsonNode expression = json.get("expression");
        return context.convertExpression(expression);
    }
}
