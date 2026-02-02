package com.ets2jsc.parser;

import com.ets2jsc.parser.converters.expr.*;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Registry for expression converters.
 * Handles conversion of JSON expressions to JavaScript strings.
 */
public class ExpressionConverterRegistry extends NodeConverterRegistry {

    public ExpressionConverterRegistry() {
        super();
    }

    @Override
    protected void initializeConverters() {
        // Register all expression converters
        register(new LiteralConverter());
        register(new IdentifierConverter());
        register(new StringLiteralConverter());
        register(new NumericLiteralConverter());
        register(new BinaryExpressionConverter());
        register(new CallExpressionConverter());
        register(new PropertyAccessConverter());
        register(new TemplateExpressionConverter());
        register(new ArrayLiteralConverter());
        register(new ObjectLiteralConverter());
        register(new UnaryExpressionConverter());
        register(new ConditionalExpressionConverter());
        register(new ElementAccessConverter());
        register(new ParenthesizedConverter());
        register(new TypeOfConverter());
        register(new NewExpressionConverter());
        register(new SpreadConverter());
        register(new AsExpressionConverter());
        register(new AwaitExpressionConverter());
        register(new ArrowFunctionConverter());
        register(new ImportExpressionConverter());
        register(new DeleteExpressionConverter());
        register(new FunctionExpressionConverter());
        register(new ClassExpressionConverter());
        register(new YieldExpressionConverter());
        register(new VoidExpressionConverter());
        register(new RegularExpressionLiteralConverter());
        register(new TaggedTemplateExpressionConverter());
        register(new MethodDeclarationExpressionConverter());
    }

    /**
     * Converts a JSON expression to a JavaScript string.
     */
    public String convert(JsonNode json, ConversionContext context) {
        String kindName = json.has("kindName") ? json.get("kindName").asText() : "";

        // Check for pre-generated text first (optimization)
        if (json.has("text")) {
            String text = json.get("text").asText();
            if (!text.isEmpty()) {
                return text.trim();
            }
        }

        NodeConverter converter = findConverter(kindName);
        Object result = converter.convert(json, context);

        if (result instanceof String) {
            return ((String) result).trim();
        }
        return result.toString();
    }
}
