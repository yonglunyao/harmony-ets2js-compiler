package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.ExportStatement;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Converter for export declarations.
 * Handles: export { X, Y };
 */
public class ExportConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ExportDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        boolean isTypeOnly = isTypeOnlyExport(json);
        String moduleSpecifier = extractModuleSpecifier(json);
        String exportClause = buildExportClause(json);

        return new ExportStatement(null, isTypeOnly, exportClause);
    }

    /**
     * Checks if this is a type-only export.
     * CC: 2 (has check + boolean)
     */
    private boolean isTypeOnlyExport(JsonNode json) {
        return json.has("isTypeOnly") && json.get("isTypeOnly").asBoolean();
    }

    /**
     * Extracts and cleans module specifier.
     * CC: 2 (has check + null check)
     */
    private String extractModuleSpecifier(JsonNode json) {
        if (!json.has("moduleSpecifier") || json.get("moduleSpecifier").isNull()) {
            return null;
        }

        String moduleSpecifier = json.get("moduleSpecifier").asText();
        return moduleSpecifier.replaceAll("^['\"]|['\"]$", "");
    }

    /**
     * Builds the export clause string.
     * CC: 3 (null checks)
     */
    private String buildExportClause(JsonNode json) {
        JsonNode exportClauseNode = json.get("exportClause");
        if (exportClauseNode == null || !exportClauseNode.isObject()) {
            return null;
        }

        JsonNode elementsNode = exportClauseNode.get("elements");
        if (elementsNode == null || !elementsNode.isArray()) {
            return null;
        }

        ArrayNode elementsArray = (ArrayNode) elementsNode;
        if (elementsArray.size() == 0) {
            return null;
        }

        return buildNamedExports(elementsArray);
    }

    /**
     * Builds named exports string like "{ A, B as C }".
     * CC: 2 (loop + ternary)
     */
    private String buildNamedExports(ArrayNode elementsArray) {
        StringBuilder exportStr = new StringBuilder("{ ");

        for (int i = 0; i < elementsArray.size(); i++) {
            if (i > 0) {
                exportStr.append(", ");
            }

            JsonNode element = elementsArray.get(i);
            String name = getElementName(element);
            String propertyName = getElementPropertyName(element);

            exportStr.append(formatExportElement(name, propertyName));
        }

        exportStr.append(" }");
        return exportStr.toString();
    }

    /**
     * Formats a single export element.
     * CC: 2 (null check + ternary)
     */
    private String formatExportElement(String name, String propertyName) {
        if (propertyName != null && !propertyName.equals(name)) {
            // { propertyName as name }
            return propertyName + " as " + name;
        }
        // { name }
        return name;
    }

    /**
     * Extracts element name.
     * CC: 1
     */
    private String getElementName(JsonNode element) {
        return element.has("name") ? element.get("name").asText() : "";
    }

    /**
     * Extracts element property name.
     * CC: 2 (has check + null check)
     */
    private String getElementPropertyName(JsonNode element) {
        if (!element.has("propertyName") || element.get("propertyName").isNull()) {
            return null;
        }
        return element.get("propertyName").asText();
    }
}
