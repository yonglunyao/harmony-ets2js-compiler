package com.ets2jsc.parser.internal;

import com.ets2jsc.ast.AstNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ConversionContext.
 */
@DisplayName("ConversionContext Tests")
class ConversionContextTest {

    @Test
    @DisplayName("Test default constructor initializes all fields")
    void testDefaultConstructor() {
        ConversionContext context = new ConversionContext();

        assertNotNull(context.getObjectMapper());
        assertNotNull(context.getExpressionConverter());
        assertNotNull(context.getStatementConverter());
    }

    @Test
    @DisplayName("Test constructor with custom ObjectMapper")
    void testConstructorWithObjectMapper() {
        ObjectMapper customMapper = new ObjectMapper();
        ConversionContext context = new ConversionContext(customMapper);

        assertNotNull(context);
        assertEquals(customMapper, context.getObjectMapper());
    }

    @Test
    @DisplayName("Test cache operations")
    void testCacheOperations() {
        ConversionContext context = new ConversionContext();

        // Test put and get
        context.putCache("key1", "value1");
        String value = context.getCached("key1", String.class);

        assertEquals("value1", value);

        // Test get non-existent key
        String nullValue = context.getCached("nonExistent", String.class);
        assertNull(nullValue);
    }

    @Test
    @DisplayName("Test clearCache removes all cached values")
    void testClearCache() {
        ConversionContext context = new ConversionContext();

        context.putCache("key1", "value1");
        context.putCache("key2", "value2");

        assertEquals("value1", context.getCached("key1", String.class));
        assertEquals("value2", context.getCached("key2", String.class));

        context.clearCache();

        assertNull(context.getCached("key1", String.class));
        assertNull(context.getCached("key2", String.class));
    }

    @Test
    @DisplayName("Test escapeTemplateLiteral escapes backslashes")
    void testEscapeTemplateLiteralEscapesBackslashes() {
        ConversionContext context = new ConversionContext();

        String result = context.escapeTemplateLiteral("test\\value");

        assertEquals("test\\\\value", result);
    }

    @Test
    @DisplayName("Test escapeTemplateLiteral escapes backticks")
    void testEscapeTemplateLiteralEscapesBackticks() {
        ConversionContext context = new ConversionContext();

        String result = context.escapeTemplateLiteral("test`value");

        assertEquals("test\\`value", result);
    }

    @Test
    @DisplayName("Test escapeTemplateLiteral with null input")
    void testEscapeTemplateLiteralWithNull() {
        ConversionContext context = new ConversionContext();

        String result = context.escapeTemplateLiteral(null);

        assertEquals("", result);
    }

    @Test
    @DisplayName("Test escapeTemplateLiteral with empty string")
    void testEscapeTemplateLiteralWithEmptyString() {
        ConversionContext context = new ConversionContext();

        String result = context.escapeTemplateLiteral("");

        assertEquals("", result);
    }

    @Test
    @DisplayName("Test escapeTemplateLiteral escapes dollar braces")
    void testEscapeTemplateLiteralEscapesDollarBraces() {
        ConversionContext context = new ConversionContext();

        String result = context.escapeTemplateLiteral("test${value}");

        // The method should escape ${ to \$
        assertTrue(result.contains("\\${"));
    }

    @Test
    @DisplayName("Test getExpressionConverter returns registry")
    void testGetExpressionConverter() {
        ConversionContext context = new ConversionContext();

        assertNotNull(context.getExpressionConverter());
    }

    @Test
    @DisplayName("Test getStatementConverter returns registry")
    void testGetStatementConverter() {
        ConversionContext context = new ConversionContext();

        assertNotNull(context.getStatementConverter());
    }

    @Test
    @DisplayName("Test cache can store different types")
    void testCacheCanStoreDifferentTypes() {
        ConversionContext context = new ConversionContext();

        context.putCache("stringKey", "stringValue");
        context.putCache("intKey", 42);
        context.putCache("boolKey", true);

        assertEquals("stringValue", context.getCached("stringKey", String.class));
        assertEquals(42, (int) context.getCached("intKey", Integer.class));
        assertEquals(true, (boolean) context.getCached("boolKey", Boolean.class));
    }

    @Test
    @DisplayName("Test cache overwrites existing value")
    void testCacheOverwritesExistingValue() {
        ConversionContext context = new ConversionContext();

        context.putCache("key", "value1");
        assertEquals("value1", context.getCached("key", String.class));

        context.putCache("key", "value2");
        assertEquals("value2", context.getCached("key", String.class));
    }

    @Test
    @DisplayName("Test getObjectMapper returns configured mapper")
    void testGetObjectMapperReturnsConfiguredMapper() {
        ConversionContext context = new ConversionContext();

        ObjectMapper mapper = context.getObjectMapper();

        assertNotNull(mapper);
    }
}
