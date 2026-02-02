package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.Decorator;
import com.ets2jsc.domain.model.ast.PropertyDeclaration;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        String name = json.get("name").asText();
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
    private void setTypeAnnotation(PropertyDeclaration propDecl, JsonNode json) {
        JsonNode typeNode = json.get("type");
        if (typeNode == null || typeNode.isNull()) {
            return;
        }

        String type = typeNode.asText();
        if (type != null && !type.isEmpty()) {
            propDecl.setTypeAnnotation(type);
        }
    }

    /**
     * Sets initializer if present.
     * CC: 3 (null check + instance checks)
     */
    private void setInitializer(PropertyDeclaration propDecl, JsonNode json, ConversionContext context) {
        JsonNode initNode = json.get("initializer");
        if (initNode == null || initNode.isNull()) {
            return;
        }

        String initializer = extractInitializer(initNode, json, context);
        if (initializer != null && !initializer.isEmpty()) {
            propDecl.setInitializer(initializer);
        }
    }

    /**
     * Extracts initializer value from JsonNode.
     * CC: 3 (if-else for different types)
     */
    private String extractInitializer(JsonNode initNode, JsonNode json, ConversionContext context) {
        // Priority 1: Pre-processed initializerText
        if (json.has("initializerText")) {
            String initializerText = json.get("initializerText").asText();
            return stripTypeArguments(initializerText);
        }

        // Priority 2: Complex expression object
        if (initNode.isObject()) {
            return extractComplexInitializer(initNode, context);
        }

        // Priority 3: Simple string value (Jackson doesn't have isJsonPrimitive, we check isValueNode)
        if (initNode.isValueNode() && initNode.isTextual()) {
            return initNode.asText();
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
    private String extractComplexInitializer(JsonNode initObj, ConversionContext context) {
        if (initObj.has("text")) {
            return initObj.get("text").asText();
        }

        return context.convertExpression(initObj);
    }

    /**
     * Converts decorators and adds them to property declaration.
     * CC: 2 (null check + loop)
     */
    private void convertDecorators(PropertyDeclaration propDecl, JsonNode json) {
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
            propDecl.addDecorator(new Decorator(decName));
        }
    }
}
