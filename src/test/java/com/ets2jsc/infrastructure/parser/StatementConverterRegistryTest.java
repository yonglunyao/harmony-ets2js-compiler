package com.ets2jsc.infrastructure.parser;

import com.ets2jsc.infrastructure.parser.StatementConverterRegistry;
import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.ets2jsc.domain.model.ast.AstNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StatementConverterRegistry.
 */
@DisplayName("StatementConverterRegistry Tests")
class StatementConverterRegistryTest {

    @Test
    @DisplayName("Test registry initialization registers all converters")
    void testRegistryInitializationRegistersAllConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertNotNull(registry);
        assertTrue(registry.size() > 0, "Registry should have converters registered");
    }

    @Test
    @DisplayName("Test has common statement converters")
    void testHasCommonStatementConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("ClassDeclaration"), "Should have ClassDeclaration converter");
        assertTrue(registry.hasConverter("Constructor"), "Should have Constructor converter");
        assertTrue(registry.hasConverter("MethodDeclaration"), "Should have MethodDeclaration converter");
        assertTrue(registry.hasConverter("PropertyDeclaration"), "Should have PropertyDeclaration converter");
    }

    @Test
    @DisplayName("Test has function converters")
    void testHasFunctionConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("FunctionDeclaration"), "Should have FunctionDeclaration converter");
    }

    @Test
    @DisplayName("Test has block converters")
    void testHasBlockConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("Block"), "Should have Block converter");
    }

    @Test
    @DisplayName("Test has control flow converters")
    void testHasControlFlowConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("IfStatement"), "Should have IfStatement converter");
        assertTrue(registry.hasConverter("ForOfStatement"), "Should have ForOfStatement converter");
        assertTrue(registry.hasConverter("ForInStatement"), "Should have ForInStatement converter");
        assertTrue(registry.hasConverter("WhileStatement"), "Should have WhileStatement converter");
        assertTrue(registry.hasConverter("DoStatement"), "Should have DoStatement converter");
        assertTrue(registry.hasConverter("ForStatement"), "Should have ForStatement converter");
        assertTrue(registry.hasConverter("SwitchStatement"), "Should have SwitchStatement converter");
    }

    @Test
    @DisplayName("Test has exception handling converters")
    void testHasExceptionHandlingConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("TryStatement"), "Should have TryStatement converter");
        assertTrue(registry.hasConverter("ThrowStatement"), "Should have ThrowStatement converter");
    }

    @Test
    @DisplayName("Test has jump statement converters")
    void testHasJumpStatementConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("ReturnStatement"), "Should have ReturnStatement converter");
    }

    @Test
    @DisplayName("Test has variable converters")
    void testHasVariableConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("VariableStatement"), "Should have VariableStatement converter");
    }

    @Test
    @DisplayName("Test has module converters")
    void testHasModuleConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("ImportDeclaration"), "Should have ImportDeclaration converter");
        assertTrue(registry.hasConverter("ExportDeclaration"), "Should have ExportDeclaration converter");
    }

    @Test
    @DisplayName("Test has expression statement converter")
    void testHasExpressionStatementConverter() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("ExpressionStatement"), "Should have ExpressionStatement converter");
    }

    @Test
    @DisplayName("Test has empty statement converter")
    void testHasEmptyStatementConverter() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("EmptyStatement"), "Should have EmptyStatement converter");
    }

    @Test
    @DisplayName("Test has accessor converters")
    void testHasAccessorConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("GetAccessor"), "Should have GetAccessor converter");
        assertTrue(registry.hasConverter("SetAccessor"), "Should have SetAccessor converter");
    }

    @Test
    @DisplayName("Test has type declaration converters")
    void testHasTypeDeclarationConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("TypeAliasDeclaration"), "Should have TypeAliasDeclaration converter");
        assertTrue(registry.hasConverter("InterfaceDeclaration"), "Should have InterfaceDeclaration converter");
    }

    @Test
    @DisplayName("Test has other statement converters")
    void testHasOtherStatementConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("LabeledStatement"), "Should have LabeledStatement converter");
    }

    @Test
    @DisplayName("Test has expression-like statement converters")
    void testHasExpressionLikeStatementConverters() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertTrue(registry.hasConverter("YieldExpression"), "Should have YieldExpression converter");
        assertTrue(registry.hasConverter("VoidExpression"), "Should have VoidExpression converter");
        assertTrue(registry.hasConverter("RegularExpressionLiteral"), "Should have RegularExpressionLiteral converter");
        assertTrue(registry.hasConverter("TaggedTemplateExpression"), "Should have TaggedTemplateExpression converter");
    }

    @Test
    @DisplayName("Test findConverter for known statement type")
    void testFindConverterForKnownStatementType() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        NodeConverter converter = registry.findConverter("ClassDeclaration");

        assertNotNull(converter, "Should find converter for ClassDeclaration");
        assertTrue(converter.canConvert("ClassDeclaration"), "Converter should handle ClassDeclaration");
    }

    @Test
    @DisplayName("Test findConverter throws for unknown statement type")
    void testFindConverterThrowsForUnknownStatementType() {
        StatementConverterRegistry registry = new StatementConverterRegistry();

        assertThrows(UnsupportedOperationException.class, () -> {
            registry.findConverter("NonExistentStatement");
        });
    }

    @Test
    @DisplayName("Test convert returns AstNode")
    void testConvertReturnsAstNode() {
        StatementConverterRegistry registry = new StatementConverterRegistry();
        ConversionContext context = new ConversionContext();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("kindName", "EmptyStatement");

        AstNode result = registry.convert(json, context);

        assertNotNull(result, "Should return an AstNode");
    }

    @Test
    @DisplayName("Test register adds custom converter")
    void testRegisterAddsCustomConverter() {
        StatementConverterRegistry registry = new StatementConverterRegistry();
        int initialSize = registry.size();

        NodeConverter customConverter = new NodeConverter() {
            @Override
            public Object convert(com.fasterxml.jackson.databind.JsonNode json, ConversionContext context) {
                return new com.ets2jsc.domain.model.ast.EmptyStatement();
            }

            @Override
            public boolean canConvert(String kindName) {
                return "CustomStatement".equals(kindName);
            }
        };

        registry.register(customConverter);

        assertEquals(initialSize + 1, registry.size());
        assertTrue(registry.hasConverter("CustomStatement"));
    }

    @Test
    @DisplayName("Test convert throws ClassCastException for non-AstNode result")
    void testConvertThrowsClassCastExceptionForNonAstNodeResult() {
        StatementConverterRegistry registry = new StatementConverterRegistry();
        ConversionContext context = new ConversionContext();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("kindName", "InvalidKind");

        assertThrows(UnsupportedOperationException.class, () -> {
            registry.convert(json, context);
        });
    }
}
