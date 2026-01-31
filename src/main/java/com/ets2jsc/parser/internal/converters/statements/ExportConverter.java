package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ExportStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
    public Object convert(JsonObject json, ConversionContext context) {
        boolean isTypeOnly = isTypeOnlyExport(json);
        String moduleSpecifier = extractModuleSpecifier(json);
        String exportClause = buildExportClause(json);

        return new ExportStatement(null, isTypeOnly, exportClause);
    }

    /**
     * Checks if this is a type-only export.
     * CC: 2 (has check + boolean)
     */
    private boolean isTypeOnlyExport(JsonObject json) {
        return json.has("isTypeOnly") && json.get("isTypeOnly").getAsBoolean();
    }

    /**
     * Extracts and cleans module specifier.
     * CC: 2 (has check + null check)
     */
    private String extractModuleSpecifier(JsonObject json) {
        if (!json.has("moduleSpecifier") || json.get("moduleSpecifier").isJsonNull()) {
            return null;
        }

        String moduleSpecifier = json.get("moduleSpecifier").getAsString();
        return moduleSpecifier.replaceAll("^['\"]|['\"]$", "");
    }

    /**
     * Builds the export clause string.
     * CC: 3 (null checks)
     */
    private String buildExportClause(JsonObject json) {
        JsonObject exportClauseObj = json.getAsJsonObject("exportClause");
        if (exportClauseObj == null) {
            return null;
        }

        JsonArray elementsArray = exportClauseObj.getAsJsonArray("elements");
        if (elementsArray == null || elementsArray.size() == 0) {
            return null;
        }

        return buildNamedExports(elementsArray);
    }

    /**
     * Builds named exports string like "{ A, B as C }".
     * CC: 2 (loop + ternary)
     */
    private String buildNamedExports(JsonArray elementsArray) {
        StringBuilder exportStr = new StringBuilder("{ ");

        for (int i = 0; i < elementsArray.size(); i++) {
            if (i > 0) {
                exportStr.append(", ");
            }

            JsonObject element = elementsArray.get(i).getAsJsonObject();
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
    private String getElementName(JsonObject element) {
        return element.has("name") ? element.get("name").getAsString() : "";
    }

    /**
     * Extracts element property name.
     * CC: 2 (has check + null check)
     */
    private String getElementPropertyName(JsonObject element) {
        if (!element.has("propertyName") || element.get("propertyName").isJsonNull()) {
            return null;
        }
        return element.get("propertyName").getAsString();
    }
}
