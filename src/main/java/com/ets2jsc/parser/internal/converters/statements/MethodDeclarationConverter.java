package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Converter for method declarations.
 * Handles: method() { ... }
 */
public class MethodDeclarationConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "MethodDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        String name = json.get("name").asText();
        MethodDeclaration methodDecl = new MethodDeclaration(name);

        convertDecorators(methodDecl, json);
        convertModifiers(methodDecl, json);
        convertParameters(methodDecl, json);
        convertBody(methodDecl, json, context);

        return methodDecl;
    }

    /**
     * Converts decorators.
     * CC: 2 (null check + loop)
     */
    private void convertDecorators(MethodDeclaration methodDecl, JsonNode json) {
        JsonNode decoratorsNode = json.get("decorators");
        if (decoratorsNode == null || !decoratorsNode.isArray()) {
            return;
        }

        ArrayNode decoratorsArray = (ArrayNode) decoratorsNode;
        for (int i = 0; i < decoratorsArray.size(); i++) {
            JsonNode decNode = decoratorsArray.get(i);
            if (decNode == null || decNode.isNull() || !decNode.isObject()) {
                continue;
            }
            String decName = decNode.has("name") ? decNode.get("name").asText() : "";
            methodDecl.addDecorator(new Decorator(decName));
        }
    }

    /**
     * Converts modifiers (static, async, etc.).
     * CC: 3 (null check + loop + multiple ifs)
     */
    private void convertModifiers(MethodDeclaration methodDecl, JsonNode json) {
        JsonNode modifiersNode = json.get("modifiers");
        if (modifiersNode == null || !modifiersNode.isArray()) {
            return;
        }

        ArrayNode modifiersArray = (ArrayNode) modifiersNode;
        for (int i = 0; i < modifiersArray.size(); i++) {
            JsonNode modNode = modifiersArray.get(i);
            if (modNode == null || modNode.isNull() || !modNode.isObject()) {
                continue;
            }
            String modKindName = getKindName(modNode);
            applyModifier(methodDecl, modKindName);
        }
    }

    /**
     * Applies a single modifier to method declaration.
     * CC: 2 (if checks)
     */
    private void applyModifier(MethodDeclaration methodDecl, String modKindName) {
        if (isStaticKeyword(modKindName)) {
            methodDecl.setStatic(true);
        }
        if (isAsyncKeyword(modKindName)) {
            methodDecl.setAsync(true);
        }
    }

    /**
     * Converts parameters.
     * CC: 2 (null check + loop)
     */
    private void convertParameters(MethodDeclaration methodDecl, JsonNode json) {
        JsonNode paramsNode = json.get("parameters");
        if (paramsNode == null || !paramsNode.isArray()) {
            return;
        }

        ArrayNode paramsArray = (ArrayNode) paramsNode;
        for (int i = 0; i < paramsArray.size(); i++) {
            JsonNode paramNode = paramsArray.get(i);
            if (paramNode == null || paramNode.isNull() || !paramNode.isObject()) {
                continue;
            }
            String paramName = paramNode.has("name") ? paramNode.get("name").asText() : "";
            String paramType = paramNode.has("type") && !paramNode.get("type").isNull()
                ? paramNode.get("type").asText()
                : "any";
            MethodDeclaration.Parameter param = new MethodDeclaration.Parameter(paramName, paramType);
            methodDecl.addParameter(param);
        }
    }

    /**
     * Converts method body.
     * CC: 2 (null checks)
     */
    private void convertBody(MethodDeclaration methodDecl, JsonNode json, ConversionContext context) {
        JsonNode bodyNode = json.get("body");
        if (bodyNode == null || bodyNode.isNull() || !bodyNode.isObject()) {
            return;
        }

        AstNode body = context.convertStatement(bodyNode);
        methodDecl.setBody(body);
    }

    /**
     * Gets kind name safely.
     * CC: 1
     */
    private String getKindName(JsonNode obj) {
        return obj.has("kindName") ? obj.get("kindName").asText() : "";
    }

    /**
     * Checks if kind name represents static keyword.
     * CC: 2 (equals checks)
     */
    private boolean isStaticKeyword(String kindName) {
        return "StaticKeyword".equals(kindName) || "static".equals(kindName);
    }

    /**
     * Checks if kind name represents async keyword.
     * CC: 2 (equals checks)
     */
    private boolean isAsyncKeyword(String kindName) {
        return "AsyncKeyword".equals(kindName) || "async".equals(kindName);
    }
}
