package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Converter for property declarations.
 * Handles: property: type = initialValue;
 */
public class PropertyDeclarationConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "PropertyDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String name = json.get("name").getAsString();
        PropertyDeclaration propDecl = new PropertyDeclaration(name);

        JsonElement typeElem = json.get("type");
        if (typeElem != null && !typeElem.isJsonNull()) {
            String type = typeElem.getAsString();
            if (type != null && !type.isEmpty()) {
                propDecl.setTypeAnnotation(type);
            }
        }

        JsonElement initElem = json.get("initializer");
        if (initElem != null && !initElem.isJsonNull()) {
            String initializer = null;
            // First check if there's a pre-processed initializerText (priority)
            if (json.has("initializerText")) {
                initializer = json.get("initializerText").getAsString();
            } else if (initElem.isJsonObject()) {
                // Complex expression - convert it to strip TypeScript syntax
                JsonObject initObj = initElem.getAsJsonObject();
                if (initObj.has("text")) {
                    initializer = initObj.get("text").getAsString();
                } else {
                    initializer = context.convertExpression(initObj);
                }
            } else if (initElem.isJsonPrimitive() && initElem.getAsJsonPrimitive().isString()) {
                // Simple string value
                initializer = initElem.getAsString();
            }

            if (initializer != null && !initializer.isEmpty()) {
                propDecl.setInitializer(initializer);
            }
        }

        // Convert decorators
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        if (decoratorsArray != null) {
            for (int i = 0; i < decoratorsArray.size(); i++) {
                JsonObject decObj = decoratorsArray.get(i).getAsJsonObject();
                String decName = decObj.get("name").getAsString();
                propDecl.addDecorator(new Decorator(decName));
            }
        }

        return propDecl;
    }
}
