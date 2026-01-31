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

    @Override
    public boolean canConvert(String kindName) {
        return "ImportDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String moduleSpecifier = json.get("moduleSpecifier").getAsString();
        // Remove quotes from module specifier
        moduleSpecifier = moduleSpecifier.replaceAll("^['\"]|['\"]$", "");

        ImportStatement importStmt = new ImportStatement(moduleSpecifier);

        JsonObject importClauseObj = json.getAsJsonObject("importClause");
        if (importClauseObj != null) {
            // Check for default import
            if (importClauseObj.has("name") && !importClauseObj.get("name").isJsonNull()) {
                String defaultName = importClauseObj.get("name").getAsString();
                importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                    defaultName, defaultName, ImportStatement.ImportSpecifier.SpecifierKind.DEFAULT));
            }

            // Check for named imports or namespace import
            JsonArray namedBindings = importClauseObj.getAsJsonArray("namedBindings");
            if (namedBindings != null) {
                for (int i = 0; i < namedBindings.size(); i++) {
                    JsonObject bindingObj = namedBindings.get(i).getAsJsonObject();

                    if (bindingObj.has("kind") && "namespace".equals(bindingObj.get("kind").getAsString())) {
                        // Namespace import: * as Module
                        String name = bindingObj.get("name").getAsString();
                        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                            "*", name, ImportStatement.ImportSpecifier.SpecifierKind.NAMESPACE));
                    } else {
                        // Named import: { A, B as C }
                        String name = bindingObj.get("name").getAsString();
                        String propertyName = bindingObj.has("propertyName") && !bindingObj.get("propertyName").isJsonNull()
                            ? bindingObj.get("propertyName").getAsString() : name;
                        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
                            propertyName, name, ImportStatement.ImportSpecifier.SpecifierKind.NAMED));
                    }
                }
            }
        }

        return importStmt;
    }
}
