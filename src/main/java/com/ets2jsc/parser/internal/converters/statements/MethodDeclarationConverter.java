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

        // Convert decorators
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        if (decoratorsArray != null) {
            for (int i = 0; i < decoratorsArray.size(); i++) {
                JsonObject decObj = decoratorsArray.get(i).getAsJsonObject();
                String decName = decObj.get("name").getAsString();
                methodDecl.addDecorator(new Decorator(decName));
            }
        }

        // Convert modifiers (static, async, etc.)
        JsonArray modifiersArray = json.getAsJsonArray("modifiers");
        if (modifiersArray != null) {
            for (int i = 0; i < modifiersArray.size(); i++) {
                JsonObject modObj = modifiersArray.get(i).getAsJsonObject();
                String modKindName = modObj.has("kindName") ? modObj.get("kindName").getAsString() : "";
                if ("StaticKeyword".equals(modKindName) || "static".equals(modKindName)) {
                    methodDecl.setStatic(true);
                }
                if ("AsyncKeyword".equals(modKindName) || "async".equals(modKindName)) {
                    methodDecl.setAsync(true);
                }
            }
        }

        // Convert parameters
        JsonArray paramsArray = json.getAsJsonArray("parameters");
        if (paramsArray != null) {
            for (int i = 0; i < paramsArray.size(); i++) {
                JsonObject paramObj = paramsArray.get(i).getAsJsonObject();
                String paramName = paramObj.get("name").getAsString();
                String paramType = paramObj.get("type").getAsString();
                MethodDeclaration.Parameter param = new MethodDeclaration.Parameter(paramName, paramType);
                methodDecl.addParameter(param);
            }
        }

        // Convert body
        JsonElement bodyElem = json.get("body");
        if (bodyElem != null && !bodyElem.isJsonNull()) {
            AstNode body = context.convertStatement(bodyElem.getAsJsonObject());
            methodDecl.setBody(body);
        }

        return methodDecl;
    }
}
