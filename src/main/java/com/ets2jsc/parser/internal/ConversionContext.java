package com.ets2jsc.parser.internal;

import com.ets2jsc.ast.AstNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for AST conversion operations.
 * Provides shared state, conversion utilities, and converter registries.
 */
public class ConversionContext {

    private final Gson gson;
    private final Map<String, Object> cache;
    private final ExpressionConverterRegistry expressionConverter;
    private final StatementConverterRegistry statementConverter;

    public ConversionContext() {
        this.gson = new Gson();
        this.cache = new HashMap<>();
        this.expressionConverter = new ExpressionConverterRegistry();
        this.statementConverter = new StatementConverterRegistry();
    }

    public ConversionContext(Gson gson) {
        this.gson = gson;
        this.cache = new HashMap<>();
        this.expressionConverter = new ExpressionConverterRegistry();
        this.statementConverter = new StatementConverterRegistry();
    }

    /**
     * Converts a JSON expression to a JavaScript string.
     */
    public String convertExpression(JsonObject expr) {
        return expressionConverter.convert(expr, this);
    }

    /**
     * Converts a JSON statement to an AST node.
     */
    public AstNode convertStatement(JsonObject stmt) {
        return statementConverter.convert(stmt, this);
    }

    /**
     * Gets the GSON instance for JSON parsing.
     */
    public Gson getGson() {
        return gson;
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
