package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.ImportStatement;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        String moduleSpecifier = extractModuleSpecifier(json);
        ImportStatement importStmt = new ImportStatement(moduleSpecifier);

        JsonNode importClauseNode = json.get("importClause");
        if (importClauseNode != null && importClauseNode.isObject()) {
            addDefaultImport(importStmt, importClauseNode);
            addNamedImports(importStmt, importClauseNode);
        }

        return importStmt;
    }

    /**
     * Extracts and cleans module specifier.
     * CC: 1
     */
    private String extractModuleSpecifier(JsonNode json) {
        String moduleSpecifier = json.get("moduleSpecifier").asText();
        return moduleSpecifier.replaceAll("^['\"]|['\"]$", "");
    }

    /**
     * Adds default import specifier if present.
     * CC: 2 (has check + null check)
     */
    private void addDefaultImport(ImportStatement importStmt, JsonNode importClauseObj) {
        if (!importClauseObj.has("name") || importClauseObj.get("name").isNull()) {
            return;
        }

        String defaultName = importClauseObj.get("name").asText();
        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                defaultName, defaultName, ImportStatement.ImportSpecifier.SpecifierKind.DEFAULT));
    }

    /**
     * Adds named and namespace import specifiers.
     * CC: 4 (null check + loop + condition checks)
     */
    private void addNamedImports(ImportStatement importStmt, JsonNode importClauseObj) {
        JsonNode namedBindingsNode = importClauseObj.get("namedBindings");
        if (namedBindingsNode == null || !namedBindingsNode.isArray()) {
            return;
        }

        ArrayNode namedBindings = (ArrayNode) namedBindingsNode;
        for (int i = 0; i < namedBindings.size(); i++) {
            JsonNode bindingObj = namedBindings.get(i);
            addBindingSpecifier(importStmt, bindingObj);
        }
    }

    /**
     * Adds a single binding specifier.
     * CC: 3 (has check + condition check)
     */
    private void addBindingSpecifier(ImportStatement importStmt, JsonNode bindingObj) {
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
    private boolean isNamespaceBinding(JsonNode bindingObj) {
        return bindingObj.has("kind") && NAMESPACE_KIND.equals(bindingObj.get("kind").asText());
    }

    /**
     * Adds namespace import specifier (* as Module).
     * CC: 1
     */
    private void addNamespaceSpecifier(ImportStatement importStmt, JsonNode bindingObj) {
        String name = bindingObj.get("name").asText();
        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                "*", name, ImportStatement.ImportSpecifier.SpecifierKind.NAMESPACE));
    }

    /**
     * Adds named import specifier ({ A, B as C }).
     * CC: 2 (has check + ternary)
     */
    private void addNamedSpecifier(ImportStatement importStmt, JsonNode bindingObj) {
        String name = bindingObj.get("name").asText();
        String propertyName = bindingObj.has("propertyName") && !bindingObj.get("propertyName").isNull()
                ? bindingObj.get("propertyName").asText() : name;

        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                propertyName, name, ImportStatement.ImportSpecifier.SpecifierKind.NAMED));
    }
}
