package com.ets2jsc.parser;

import com.ets2jsc.domain.model.ast.AstNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for AST conversion operations.
 * Provides shared state, conversion utilities, and converter registries.
 */
public class ConversionContext {

    private final ObjectMapper objectMapper;
    private final Map<String, Object> cache;
    private final ExpressionConverterRegistry expressionConverter;
    private final StatementConverterRegistry statementConverter;

    public ConversionContext() {
        this.objectMapper = new ObjectMapper();
        this.cache = new HashMap<>();
        this.expressionConverter = new ExpressionConverterRegistry();
        this.statementConverter = new StatementConverterRegistry();
    }

    public ConversionContext(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.cache = new HashMap<>();
        this.expressionConverter = new ExpressionConverterRegistry();
        this.statementConverter = new StatementConverterRegistry();
    }

    /**
     * Converts a JSON expression to a JavaScript string.
     */
    public String convertExpression(JsonNode expr) {
        return expressionConverter.convert(expr, this);
    }

    /**
     * Converts a JSON statement to an AST node.
     */
    public AstNode convertStatement(JsonNode stmt) {
        return statementConverter.convert(stmt, this);
    }

    /**
     * Gets the ObjectMapper instance for JSON parsing.
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Gets a value from the cache.
     */
    @SuppressWarnings("unchecked")
    public <T> T getCached(String key, Class<T> type) {
        Object value = cache.get(key);
        return value != null ? (T) value : null;
    }

    /**
     * Puts a value in the cache.
     */
    public void putCache(String key, Object value) {
        cache.put(key, value);
    }

    /**
     * Clears the cache.
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Escapes special characters in template literals.
     */
    public String escapeTemplateLiteral(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("`", "\\`")
                  .replace("${", "\\${");
    }

    /**
     * Gets the expression converter registry.
     */
    public ExpressionConverterRegistry getExpressionConverter() {
        return expressionConverter;
    }

    /**
     * Gets the statement converter registry.
     */
    public StatementConverterRegistry getStatementConverter() {
        return statementConverter;
    }
}
