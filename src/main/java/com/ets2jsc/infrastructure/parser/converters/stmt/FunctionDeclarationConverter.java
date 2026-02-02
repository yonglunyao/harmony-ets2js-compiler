package com.ets2jsc.infrastructure.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.ExpressionStatement;
import com.ets2jsc.infrastructure.generator.CodeGenerator;
import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode nameNode = json.get("name");
        String name = (nameNode != null && !nameNode.isNull()) ? nameNode.asText() : "anonymous";
        boolean isAsync = hasAsyncModifier(json);
        String params = convertParameters(json, context);
        String body = convertBody(json, context);

        return buildFunctionDeclaration(name, isAsync, params, body);
    }

    /**
     * Checks if function has async modifier.
     * CC: 2 (null check + loop)
     */
    private boolean hasAsyncModifier(JsonNode json) {
        JsonNode modifiersNode = json.get("modifiers");
        if (modifiersNode == null || !modifiersNode.isArray()) {
            return false;
        }

        ArrayNode modifiersArray = (ArrayNode) modifiersNode;
        for (int i = 0; i < modifiersArray.size(); i++) {
            JsonNode modObj = modifiersArray.get(i);
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
    private String convertParameters(JsonNode json, ConversionContext context) {
        JsonNode paramsNode = json.get("parameters");
        if (paramsNode == null || !paramsNode.isArray()) {
            return "";
        }

        ArrayNode paramsArray = (ArrayNode) paramsNode;
        if (paramsArray.size() == 0) {
            return "";
        }

        StringBuilder params = new StringBuilder();
        for (int i = 0; i < paramsArray.size(); i++) {
            if (i > 0) {
                params.append(", ");
            }
            JsonNode paramObj = paramsArray.get(i);
            JsonNode paramNameNode = paramObj.get("name");
            String paramName = (paramNameNode != null && !paramNameNode.isNull()) ? paramNameNode.asText() : "param" + i;

            // Handle rest parameter (...)
            if (hasDotDotDotToken(paramObj)) {
                params.append("...");
            }
            params.append(paramName);

            // Handle default value
            if (paramObj.has("initializer") && !paramObj.get("initializer").isNull()) {
                JsonNode initializer = paramObj.get("initializer");
                String defaultVal = context.convertExpression(initializer);
                params.append(" = ").append(defaultVal);
            }
        }
        return params.toString();
    }

    /**
     * Converts function body to string.
     * CC: 3 (null checks)
     */
    private String convertBody(JsonNode json, ConversionContext context) {
        JsonNode bodyNode = json.get("body");
        if (bodyNode == null || bodyNode.isNull() || !bodyNode.isObject()) {
            return "";
        }

        JsonNode statementsNode = bodyNode.get("statements");
        if (statementsNode == null || !statementsNode.isArray()) {
            return "";
        }

        ArrayNode statementsArray = (ArrayNode) statementsNode;
        return convertStatements(statementsArray, context);
    }

    /**
     * Converts statements array to string.
     * CC: 2 (loop + null check)
     */
    private String convertStatements(ArrayNode statementsArray, ConversionContext context) {
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < statementsArray.size(); i++) {
            JsonNode stmtObj = statementsArray.get(i);
            AstNode stmt = context.convertStatement(stmtObj);
            if (stmt != null) {
                String stmtCode = stmt.accept(new CodeGenerator());
                body.append("  ").append(stmtCode).append("\n");
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
    private String getKindName(JsonNode obj) {
        return obj.has("kindName") ? obj.get("kindName").asText() : "";
    }

    /**
     * Checks if kind name represents async keyword.
     * CC: 2 (equals checks)
     */
    private boolean isAsyncKeyword(String kindName) {
        return "AsyncKeyword".equals(kindName) || "async".equals(kindName);
    }

    /**
     * Checks if parameter has ... prefix (rest parameter).
     * CC: 2 (null check + has check)
     */
    private boolean hasDotDotDotToken(JsonNode param) {
        if (param == null) {
            return false;
        }
        // Check both field names for compatibility
        if (param.has("hasDotDotDot") && param.get("hasDotDotDot").asBoolean(false)) {
            return true;
        }
        if (param.has("dotDotDotToken") && param.get("dotDotDotToken").asBoolean(false)) {
            return true;
        }
        return false;
    }
}
