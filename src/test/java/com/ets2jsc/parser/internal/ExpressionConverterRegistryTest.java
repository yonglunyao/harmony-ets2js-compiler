package com.ets2jsc.parser.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ExpressionConverterRegistry.
 */
@DisplayName("ExpressionConverterRegistry Tests")
class ExpressionConverterRegistryTest {

    @Test
    @DisplayName("Test registry initialization registers all converters")
    void testRegistryInitializationRegistersAllConverters() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertNotNull(registry);
        assertTrue(registry.size() > 0, "Registry should have converters registered");
    }

    @Test
    @DisplayName("Test has common expression converters")
    void testHasCommonExpressionConverters() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertTrue(registry.hasConverter("Identifier"), "Should have Identifier converter");
        assertTrue(registry.hasConverter("StringLiteral"), "Should have StringLiteral converter");
        assertTrue(registry.hasConverter("NumericLiteral"), "Should have NumericLiteral converter");
        assertTrue(registry.hasConverter("BinaryExpression"), "Should have BinaryExpression converter");
        assertTrue(registry.hasConverter("CallExpression"), "Should have CallExpression converter");
    }

    @Test
    @DisplayName("Test has literal converters")
    void testHasLiteralConverters() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertTrue(registry.hasConverter("TrueLiteral"), "Should have TrueLiteral converter");
        assertTrue(registry.hasConverter("StringLiteral"), "Should have StringLiteral converter");
        assertTrue(registry.hasConverter("NumericLiteral"), "Should have NumericLiteral converter");
        assertTrue(registry.hasConverter("ArrayLiteralExpression"), "Should have ArrayLiteralExpression converter");
        assertTrue(registry.hasConverter("ObjectLiteralExpression"), "Should have ObjectLiteralExpression converter");
    }

    @Test
    @DisplayName("Test has access converters")
    void testHasAccessConverters() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertTrue(registry.hasConverter("PropertyAccessExpression"), "Should have PropertyAccessExpression converter");
        assertTrue(registry.hasConverter("ElementAccessExpression"), "Should have ElementAccessExpression converter");
    }

    @Test
    @DisplayName("Test has function converters")
    void testHasFunctionConverters() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertTrue(registry.hasConverter("ArrowFunction"), "Should have ArrowFunction converter");
        assertTrue(registry.hasConverter("FunctionExpression"), "Should have FunctionExpression converter");
        assertTrue(registry.hasConverter("NewExpression"), "Should have NewExpression converter");
    }

    @Test
    @DisplayName("Test has unary/binary operators")
    void testHasUnaryBinaryOperators() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertTrue(registry.hasConverter("BinaryExpression"), "Should have BinaryExpression converter");
        assertTrue(registry.hasConverter("PrefixUnaryExpression"), "Should have PrefixUnaryExpression converter");
        assertTrue(registry.hasConverter("ConditionalExpression"), "Should have ConditionalExpression converter");
    }

    @Test
    @DisplayName("Test has template converters")
    void testHasTemplateConverters() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertTrue(registry.hasConverter("TemplateExpression"), "Should have TemplateExpression converter");
        assertTrue(registry.hasConverter("TaggedTemplateExpression"), "Should have TaggedTemplateExpression converter");
    }

    @Test
    @DisplayName("Test has advanced expression converters")
    void testHasAdvancedExpressionConverters() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertTrue(registry.hasConverter("SpreadElement"), "Should have SpreadElement converter");
        assertTrue(registry.hasConverter("AsExpression"), "Should have AsExpression converter");
        assertTrue(registry.hasConverter("AwaitExpression"), "Should have AwaitExpression converter");
        assertTrue(registry.hasConverter("TypeOfExpression"), "Should have TypeOfExpression converter");
        assertTrue(registry.hasConverter("DeleteExpression"), "Should have DeleteExpression converter");
    }

    @Test
    @DisplayName("Test has special expression converters")
    void testHasSpecialExpressionConverters() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertTrue(registry.hasConverter("YieldExpression"), "Should have YieldExpression converter");
        assertTrue(registry.hasConverter("VoidExpression"), "Should have VoidExpression converter");
        assertTrue(registry.hasConverter("RegularExpressionLiteral"), "Should have RegularExpressionLiteral converter");
        assertTrue(registry.hasConverter("ImportExpression"), "Should have ImportExpression converter");
    }

    @Test
    @DisplayName("Test convert with pre-generated text")
    void testConvertWithPreGeneratedText() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();
        ConversionContext context = new ConversionContext();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("kindName", "Identifier");
        json.put("text", "myVariable");

        String result = registry.convert(json, context);

        assertEquals("myVariable", result, "Should return pre-generated text");
    }

    @Test
    @DisplayName("Test convert with empty text falls back to converter")
    void testConvertWithEmptyTextFallsBackToConverter() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();
        ConversionContext context = new ConversionContext();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("kindName", "Identifier");
        json.put("text", "");

        String result = registry.convert(json, context);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Test convert returns trimmed string")
    void testConvertReturnsTrimmedString() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();
        ConversionContext context = new ConversionContext();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("kindName", "Identifier");
        json.put("text", "  myVariable  ");

        String result = registry.convert(json, context);

        assertEquals("myVariable", result, "Should trim whitespace");
    }

    @Test
    @DisplayName("Test findConverter for known expression type")
    void testFindConverterForKnownExpressionType() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        NodeConverter converter = registry.findConverter("Identifier");

        assertNotNull(converter, "Should find converter for Identifier");
        assertTrue(converter.canConvert("Identifier"), "Converter should handle Identifier");
    }

    @Test
    @DisplayName("Test findConverter throws for unknown expression type")
    void testFindConverterThrowsForUnknownExpressionType() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertThrows(UnsupportedOperationException.class, () -> {
            registry.findConverter("NonExistentExpression");
        });
    }

    @Test
    @DisplayName("Test register adds custom converter")
    void testRegisterAddsCustomConverter() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();
        int initialSize = registry.size();

        NodeConverter customConverter = new NodeConverter() {
            @Override
            public Object convert(com.fasterxml.jackson.databind.JsonNode json, ConversionContext context) {
                return "custom";
            }

            @Override
            public boolean canConvert(String kindName) {
                return "CustomExpression".equals(kindName);
            }
        };

        registry.register(customConverter);

        assertEquals(initialSize + 1, registry.size());
        assertTrue(registry.hasConverter("CustomExpression"));
    }

    @Test
    @DisplayName("Test has class expression converter")
    void testHasClassExpressionConverter() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertTrue(registry.hasConverter("ClassExpression"), "Should have ClassExpression converter");
    }

    @Test
    @DisplayName("Test has parenthesized converter")
    void testHasParenthesizedConverter() {
        ExpressionConverterRegistry registry = new ExpressionConverterRegistry();

        assertTrue(registry.hasConverter("ParenthesizedExpression"), "Should have ParenthesizedExpression converter");
    }
}
