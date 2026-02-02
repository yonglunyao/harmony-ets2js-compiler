package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Converter for constructor declarations.
 * Handles: constructor() { ... }
 * Converts constructors to MethodDeclaration with name "constructor".
 */
public class ConstructorConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "Constructor".equals(kindName) || "ConstructorDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        MethodDeclaration constructorDecl = new MethodDeclaration("constructor");

        convertDecorators(constructorDecl, json);
        convertParameters(constructorDecl, json);
        convertBody(constructorDecl, json, context);

        return constructorDecl;
    }

    /**
     * Converts decorators.
     * CC: 2 (null check + loop)
     */
    private void convertDecorators(MethodDeclaration constructorDecl, JsonNode json) {
        JsonNode decoratorsNode = json.get("decorators");
        if (decoratorsNode == null || !decoratorsNode.isArray()) {
            return;
        }

        ArrayNode decoratorsArray = (ArrayNode) decoratorsNode;
        for (int i = 0; i < decoratorsArray.size(); i++) {
            JsonNode decObj = decoratorsArray.get(i);
            String decName = decObj.get("name").asText();
            constructorDecl.addDecorator(new Decorator(decName));
        }
    }

    /**
     * Converts parameters.
     * CC: 2 (null check + loop)
     */
    private void convertParameters(MethodDeclaration constructorDecl, JsonNode json) {
        JsonNode paramsNode = json.get("parameters");
        if (paramsNode == null || !paramsNode.isArray()) {
            return;
        }

        ArrayNode paramsArray = (ArrayNode) paramsNode;
        for (int i = 0; i < paramsArray.size(); i++) {
            JsonNode paramObj = paramsArray.get(i);
            String paramName = paramObj.get("name").asText();
            String paramType = paramObj.has("type") && !paramObj.get("type").isNull()
                ? paramObj.get("type").asText()
                : "any";
            MethodDeclaration.Parameter param = new MethodDeclaration.Parameter(paramName, paramType);
            constructorDecl.addParameter(param);
        }
    }

    /**
     * Converts constructor body.
     * CC: 2 (null checks)
     */
    private void convertBody(MethodDeclaration constructorDecl, JsonNode json, ConversionContext context) {
        JsonNode bodyNode = json.get("body");
        if (bodyNode == null || bodyNode.isNull() || !bodyNode.isObject()) {
            return;
        }

        AstNode body = context.convertStatement(bodyNode);
        constructorDecl.setBody(body);
    }
}
