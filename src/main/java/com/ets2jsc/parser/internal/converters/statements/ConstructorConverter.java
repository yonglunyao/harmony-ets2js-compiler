package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Converter for constructor declarations.
 * Handles: constructor() { ... }
 * Converts constructors to MethodDeclaration with name "constructor".
 */
public class ConstructorConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "Constructor".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
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
    private void convertDecorators(MethodDeclaration constructorDecl, JsonObject json) {
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        if (decoratorsArray == null) {
            return;
        }

        for (int i = 0; i < decoratorsArray.size(); i++) {
            JsonObject decObj = decoratorsArray.get(i).getAsJsonObject();
            String decName = decObj.get("name").getAsString();
            constructorDecl.addDecorator(new Decorator(decName));
        }
    }

    /**
     * Converts parameters.
     * CC: 2 (null check + loop)
     */
    private void convertParameters(MethodDeclaration constructorDecl, JsonObject json) {
        JsonArray paramsArray = json.getAsJsonArray("parameters");
        if (paramsArray == null) {
            return;
        }

        for (int i = 0; i < paramsArray.size(); i++) {
            JsonObject paramObj = paramsArray.get(i).getAsJsonObject();
            String paramName = paramObj.get("name").getAsString();
            String paramType = paramObj.has("type") && !paramObj.get("type").isJsonNull()
                ? paramObj.get("type").getAsString()
                : "any";
            MethodDeclaration.Parameter param = new MethodDeclaration.Parameter(paramName, paramType);
            constructorDecl.addParameter(param);
        }
    }

    /**
     * Converts constructor body.
     * CC: 2 (null checks)
     */
    private void convertBody(MethodDeclaration constructorDecl, JsonObject json, ConversionContext context) {
        JsonElement bodyElem = json.get("body");
        if (bodyElem == null || bodyElem.isJsonNull()) {
            return;
        }

        AstNode body = context.convertStatement(bodyElem.getAsJsonObject());
        constructorDecl.setBody(body);
    }
}
