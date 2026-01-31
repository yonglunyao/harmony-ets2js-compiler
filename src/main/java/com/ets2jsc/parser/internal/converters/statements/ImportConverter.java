package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ImportStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Converter for import declarations.
 * Handles: import { X, Y } from 'module';
 */
public class ImportConverter implements NodeConverter {

    private static final String NAMESPACE_KIND = "namespace";

    @Override
    public boolean canConvert(String kindName) {
        return "ImportDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String moduleSpecifier = extractModuleSpecifier(json);
        ImportStatement importStmt = new ImportStatement(moduleSpecifier);

        JsonObject importClauseObj = json.getAsJsonObject("importClause");
        if (importClauseObj != null) {
            addDefaultImport(importStmt, importClauseObj);
            addNamedImports(importStmt, importClauseObj);
        }

        return importStmt;
    }

    /**
     * Extracts and cleans module specifier.
     * CC: 1
     */
    private String extractModuleSpecifier(JsonObject json) {
        String moduleSpecifier = json.get("moduleSpecifier").getAsString();
        return moduleSpecifier.replaceAll("^['\"]|['\"]$", "");
    }

    /**
     * Adds default import specifier if present.
     * CC: 2 (has check + null check)
     */
    private void addDefaultImport(ImportStatement importStmt, JsonObject importClauseObj) {
        if (!importClauseObj.has("name") || importClauseObj.get("name").isJsonNull()) {
            return;
        }

        String defaultName = importClauseObj.get("name").getAsString();
        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                defaultName, defaultName, ImportStatement.ImportSpecifier.SpecifierKind.DEFAULT));
    }

    /**
     * Adds named and namespace import specifiers.
     * CC: 4 (null check + loop + condition checks)
     */
    private void addNamedImports(ImportStatement importStmt, JsonObject importClauseObj) {
        JsonArray namedBindings = importClauseObj.getAsJsonArray("namedBindings");
        if (namedBindings == null) {
            return;
        }

        for (int i = 0; i < namedBindings.size(); i++) {
            JsonObject bindingObj = namedBindings.get(i).getAsJsonObject();
            addBindingSpecifier(importStmt, bindingObj);
        }
    }

    /**
     * Adds a single binding specifier.
     * CC: 3 (has check + condition check)
     */
    private void addBindingSpecifier(ImportStatement importStmt, JsonObject bindingObj) {
        if (isNamespaceBinding(bindingObj)) {
            addNamespaceSpecifier(importStmt, bindingObj);
        } else {
            addNamedSpecifier(importStmt, bindingObj);
        }
    }

    /**
     * Checks if binding is a namespace import.
     * CC: 2 (has check + equals check)
     */
    private boolean isNamespaceBinding(JsonObject bindingObj) {
        return bindingObj.has("kind") && NAMESPACE_KIND.equals(bindingObj.get("kind").getAsString());
    }

    /**
     * Adds namespace import specifier (* as Module).
     * CC: 1
     */
    private void addNamespaceSpecifier(ImportStatement importStmt, JsonObject bindingObj) {
        String name = bindingObj.get("name").getAsString();
        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                "*", name, ImportStatement.ImportSpecifier.SpecifierKind.NAMESPACE));
    }

    /**
     * Adds named import specifier ({ A, B as C }).
     * CC: 2 (has check + ternary)
     */
    private void addNamedSpecifier(ImportStatement importStmt, JsonObject bindingObj) {
        String name = bindingObj.get("name").getAsString();
        String propertyName = bindingObj.has("propertyName") && !bindingObj.get("propertyName").isJsonNull()
                ? bindingObj.get("propertyName").getAsString() : name;

        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                propertyName, name, ImportStatement.ImportSpecifier.SpecifierKind.NAMED));
    }
}
