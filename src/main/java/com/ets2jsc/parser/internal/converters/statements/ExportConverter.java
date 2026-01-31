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
        // Check if this is a type-only export
        boolean isTypeOnly = json.has("isTypeOnly") && json.get("isTypeOnly").getAsBoolean();

        // Get module specifier if present (for re-exports)
        String moduleSpecifier = null;
        if (json.has("moduleSpecifier") && !json.get("moduleSpecifier").isJsonNull()) {
            moduleSpecifier = json.get("moduleSpecifier").getAsString();
            moduleSpecifier = moduleSpecifier.replaceAll("^['\"]|['\"]$", "");
        }

        // Build the export statement string
        StringBuilder exportStr = new StringBuilder();

        // Handle named exports/re-exports
        JsonObject exportClauseObj = json.getAsJsonObject("exportClause");
        if (exportClauseObj != null) {
            JsonArray elementsArray = exportClauseObj.getAsJsonArray("elements");
            if (elementsArray != null && elementsArray.size() > 0) {
                exportStr.append("{ ");
                for (int i = 0; i < elementsArray.size(); i++) {
                    if (i > 0) {
                        exportStr.append(", ");
                    }
                    JsonObject element = elementsArray.get(i).getAsJsonObject();
                    String name = element.has("name") ? element.get("name").getAsString() : "";
                    String propertyName = element.has("propertyName") && !element.get("propertyName").isJsonNull()
                        ? element.get("propertyName").getAsString() : null;

                    if (propertyName != null && !propertyName.equals(name)) {
                        // { propertyName as name }
                        exportStr.append(propertyName).append(" as ").append(name);
                    } else {
                        // { name }
                        exportStr.append(name);
                    }
                }
                exportStr.append(" }");

                // Add module specifier if present (re-export)
                if (moduleSpecifier != null && !moduleSpecifier.isEmpty()) {
                    exportStr.append(" from '").append(moduleSpecifier).append("'");
                }
            }
        }

        // Create ExportStatement with the generated string
        String declaration = exportStr.length() > 0 ? exportStr.toString() : null;
        return new ExportStatement(null, isTypeOnly, declaration);
    }
}
