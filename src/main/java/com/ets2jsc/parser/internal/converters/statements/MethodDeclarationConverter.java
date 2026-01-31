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
 * Converter for method declarations.
 * Handles: method() { ... }
 */
public class MethodDeclarationConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "MethodDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String name = json.get("name").getAsString();
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
    private void convertDecorators(MethodDeclaration methodDecl, JsonObject json) {
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        if (decoratorsArray == null) {
            return;
        }

        for (int i = 0; i < decoratorsArray.size(); i++) {
            JsonObject decObj = decoratorsArray.get(i).getAsJsonObject();
            String decName = decObj.get("name").getAsString();
            methodDecl.addDecorator(new Decorator(decName));
        }
    }

    /**
     * Converts modifiers (static, async, etc.).
     * CC: 3 (null check + loop + multiple ifs)
     */
    private void convertModifiers(MethodDeclaration methodDecl, JsonObject json) {
        JsonArray modifiersArray = json.getAsJsonArray("modifiers");
        if (modifiersArray == null) {
            return;
        }

        for (int i = 0; i < modifiersArray.size(); i++) {
            JsonObject modObj = modifiersArray.get(i).getAsJsonObject();
            String modKindName = getKindName(modObj);
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
    private void convertParameters(MethodDeclaration methodDecl, JsonObject json) {
        JsonArray paramsArray = json.getAsJsonArray("parameters");
        if (paramsArray == null) {
            return;
        }

        for (int i = 0; i < paramsArray.size(); i++) {
            JsonObject paramObj = paramsArray.get(i).getAsJsonObject();
            String paramName = paramObj.get("name").getAsString();
            String paramType = paramObj.get("type").getAsString();
            MethodDeclaration.Parameter param = new MethodDeclaration.Parameter(paramName, paramType);
            methodDecl.addParameter(param);
        }
    }

    /**
     * Converts method body.
     * CC: 2 (null checks)
     */
    private void convertBody(MethodDeclaration methodDecl, JsonObject json, ConversionContext context) {
        JsonElement bodyElem = json.get("body");
        if (bodyElem == null || bodyElem.isJsonNull()) {
            return;
        }

        AstNode body = context.convertStatement(bodyElem.getAsJsonObject());
        methodDecl.setBody(body);
    }

    /**
     * Gets kind name safely.
     * CC: 1
     */
    private String getKindName(JsonObject obj) {
        return obj.has("kindName") ? obj.get("kindName").getAsString() : "";
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
