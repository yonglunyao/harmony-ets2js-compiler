package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Converter for standalone function declarations.
 * Handles: function name(args) { ... }
 */
public class FunctionDeclarationConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "FunctionDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String name = json.get("name").getAsString();

        // Check for async modifier
        boolean isAsync = false;
        JsonArray modifiersArray = json.getAsJsonArray("modifiers");
        if (modifiersArray != null) {
            for (int i = 0; i < modifiersArray.size(); i++) {
                JsonObject modObj = modifiersArray.get(i).getAsJsonObject();
                String modKindName = modObj.has("kindName") ? modObj.get("kindName").getAsString() : "";
                if ("AsyncKeyword".equals(modKindName) || "async".equals(modKindName)) {
                    isAsync = true;
                    break;
                }
            }
        }

        // Convert parameters
        JsonArray paramsArray = json.getAsJsonArray("parameters");
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.size() > 0) {
            for (int i = 0; i < paramsArray.size(); i++) {
                if (i > 0) params.append(", ");
                JsonObject paramObj = paramsArray.get(i).getAsJsonObject();
                String paramName = paramObj.get("name").getAsString();
                params.append(paramName);
            }
        }

        // Convert body
        JsonElement bodyElem = json.get("body");
        StringBuilder body = new StringBuilder();
        if (bodyElem != null && !bodyElem.isJsonNull()) {
            JsonObject bodyObj = bodyElem.getAsJsonObject();
            JsonArray statementsArray = bodyObj.getAsJsonArray("statements");
            if (statementsArray != null) {
                for (int i = 0; i < statementsArray.size(); i++) {
                    JsonObject stmtObj = statementsArray.get(i).getAsJsonObject();
                    AstNode stmt = context.convertStatement(stmtObj);
                    if (stmt != null) {
                        String stmtCode = stmt.accept(new CodeGenerator());
                        body.append("  ").append(stmtCode);
                    }
                }
            }
        }

        // Build function declaration string
        StringBuilder sb = new StringBuilder();
        if (isAsync) {
            sb.append("async ");
        }
        sb.append("function ").append(name).append("(").append(params).append(") {\n");
        sb.append(body);
        sb.append("}\n");

        return new ExpressionStatement(sb.toString());
    }
}
