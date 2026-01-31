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
        boolean isAsync = hasAsyncModifier(json);
        String params = convertParameters(json);
        String body = convertBody(json, context);

        return buildFunctionDeclaration(name, isAsync, params, body);
    }

    /**
     * Checks if function has async modifier.
     * CC: 2 (null check + loop)
     */
    private boolean hasAsyncModifier(JsonObject json) {
        JsonArray modifiersArray = json.getAsJsonArray("modifiers");
        if (modifiersArray == null) {
            return false;
        }

        for (int i = 0; i < modifiersArray.size(); i++) {
            JsonObject modObj = modifiersArray.get(i).getAsJsonObject();
            String modKindName = getKindName(modObj);
            if (isAsyncKeyword(modKindName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts function parameters to string.
     * CC: 2 (null check + loop)
     */
    private String convertParameters(JsonObject json) {
        JsonArray paramsArray = json.getAsJsonArray("parameters");
        if (paramsArray == null || paramsArray.size() == 0) {
            return "";
        }

        StringBuilder params = new StringBuilder();
        for (int i = 0; i < paramsArray.size(); i++) {
            if (i > 0) {
                params.append(", ");
            }
            JsonObject paramObj = paramsArray.get(i).getAsJsonObject();
            String paramName = paramObj.get("name").getAsString();
            params.append(paramName);
        }
        return params.toString();
    }

    /**
     * Converts function body to string.
     * CC: 3 (null checks)
     */
    private String convertBody(JsonObject json, ConversionContext context) {
        JsonElement bodyElem = json.get("body");
        if (bodyElem == null || bodyElem.isJsonNull()) {
            return "";
        }

        JsonObject bodyObj = bodyElem.getAsJsonObject();
        JsonArray statementsArray = bodyObj.getAsJsonArray("statements");
        if (statementsArray == null) {
            return "";
        }

        return convertStatements(statementsArray, context);
    }

    /**
     * Converts statements array to string.
     * CC: 2 (loop + null check)
     */
    private String convertStatements(JsonArray statementsArray, ConversionContext context) {
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < statementsArray.size(); i++) {
            JsonObject stmtObj = statementsArray.get(i).getAsJsonObject();
            AstNode stmt = context.convertStatement(stmtObj);
            if (stmt != null) {
                String stmtCode = stmt.accept(new CodeGenerator());
                body.append("  ").append(stmtCode);
            }
        }
        return body.toString();
    }

    /**
     * Builds function declaration string.
     * CC: 2 (ternary for async)
     */
    private ExpressionStatement buildFunctionDeclaration(String name, boolean isAsync, String params, String body) {
        StringBuilder sb = new StringBuilder();

        if (isAsync) {
            sb.append("async ");
        }

        sb.append("function ").append(name).append("(").append(params).append(") {\n");
        sb.append(body);
        sb.append("}\n");

        return new ExpressionStatement(sb.toString());
    }

    /**
     * Gets kind name safely.
     * CC: 1
     */
    private String getKindName(JsonObject obj) {
        return obj.has("kindName") ? obj.get("kindName").getAsString() : "";
    }

    /**
     * Checks if kind name represents async keyword.
     * CC: 2 (equals checks)
     */
    private boolean isAsyncKeyword(String kindName) {
        return "AsyncKeyword".equals(kindName) || "async".equals(kindName);
    }
}
