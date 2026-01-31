package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converter for property declarations.
 * Handles: property: type = initialValue;
 */
public class PropertyDeclarationConverter implements NodeConverter {

    private static final String KEYWORD_NEW = "new ";
    private static final Pattern TYPE_ARGUMENTS_PATTERN = Pattern.compile("(new\\s+\\w+)<[^>]*>");

    @Override
    public boolean canConvert(String kindName) {
        return "PropertyDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String name = json.get("name").getAsString();
        PropertyDeclaration propDecl = new PropertyDeclaration(name);

        setTypeAnnotation(propDecl, json);
        setInitializer(propDecl, json, context);
        convertDecorators(propDecl, json);

        return propDecl;
    }

    /**
     * Sets type annotation if present.
     * CC: 2 (null check + empty check)
     */
    private void setTypeAnnotation(PropertyDeclaration propDecl, JsonObject json) {
        JsonElement typeElem = json.get("type");
        if (typeElem == null || typeElem.isJsonNull()) {
            return;
        }

        String type = typeElem.getAsString();
        if (type != null && !type.isEmpty()) {
            propDecl.setTypeAnnotation(type);
        }
    }

    /**
     * Sets initializer if present.
     * CC: 3 (null check + instance checks)
     */
    private void setInitializer(PropertyDeclaration propDecl, JsonObject json, ConversionContext context) {
        JsonElement initElem = json.get("initializer");
        if (initElem == null || initElem.isJsonNull()) {
            return;
        }

        String initializer = extractInitializer(initElem, json, context);
        if (initializer != null && !initializer.isEmpty()) {
            propDecl.setInitializer(initializer);
        }
    }

    /**
     * Extracts initializer value from JsonElement.
     * CC: 3 (if-else for different types)
     */
    private String extractInitializer(JsonElement initElem, JsonObject json, ConversionContext context) {
        // Priority 1: Pre-processed initializerText
        if (json.has("initializerText")) {
            String initializerText = json.get("initializerText").getAsString();
            return stripTypeArguments(initializerText);
        }

        // Priority 2: Complex expression object
        if (initElem.isJsonObject()) {
            return extractComplexInitializer(initElem.getAsJsonObject(), context);
        }

        // Priority 3: Simple string value
        if (initElem.isJsonPrimitive() && initElem.getAsJsonPrimitive().isString()) {
            return initElem.getAsString();
        }

        return null;
    }

    /**
     * Strips TypeScript type arguments from new expressions.
     * Converts: new Map<string, Object>() to new Map()
     * CC: 2 (null check + matcher find)
     */
    private String stripTypeArguments(String text) {
        if (text == null || !text.contains(KEYWORD_NEW)) {
            return text;
        }

        Matcher matcher = TYPE_ARGUMENTS_PATTERN.matcher(text);
        return matcher.replaceAll("$1");
    }

    /**
     * Extracts initializer from complex expression object.
     * CC: 3 (has check + else)
     */
    private String extractComplexInitializer(JsonObject initObj, ConversionContext context) {
        if (initObj.has("text")) {
            return initObj.get("text").getAsString();
        }

        return context.convertExpression(initObj);
    }

    /**
     * Converts decorators and adds them to property declaration.
     * CC: 2 (null check + loop)
     */
    private void convertDecorators(PropertyDeclaration propDecl, JsonObject json) {
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        if (decoratorsArray == null) {
            return;
        }

        for (int i = 0; i < decoratorsArray.size(); i++) {
            JsonElement decElement = decoratorsArray.get(i);
            if (decElement.isJsonNull() || !decElement.isJsonObject()) {
                continue;
            }
            JsonObject decObj = decElement.getAsJsonObject();
            String decName = decObj.has("name") ? decObj.get("name").getAsString() : "";
            propDecl.addDecorator(new Decorator(decName));
        }
    }
}
