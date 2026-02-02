package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.shared.constant.Symbols;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Converter for variable statements.
 * Handles: let x = value;, const y = value;
 */
public class VariableConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "VariableStatement".equals(kindName) || "FirstStatement".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        JsonNode declarationListNode = json.get("declarationList");
        if (declarationListNode == null || !declarationListNode.isObject()) {
            return new ExpressionStatement("// variable declaration");
        }

        ArrayNode declarations = getDeclarations(declarationListNode);
        if (declarations == null || declarations.size() == 0) {
            return new ExpressionStatement("// variable declaration");
        }

        return buildVariableDeclarations(declarations, declarationListNode, context);
    }

    /**
     * Gets all declarations from the list.
     * CC: 3 (null check + size check + instance check)
     */
    private ArrayNode getDeclarations(JsonNode declarationList) {
        JsonNode declarationsNode = declarationList.get("declarations");
        if (declarationsNode == null || !declarationsNode.isArray()) {
            return null;
        }

        return (ArrayNode) declarationsNode;
    }

    /**
     * Builds variable declaration statement for all declarations.
     * CC: 3 (null check + loop + method calls)
     */
    private ExpressionStatement buildVariableDeclarations(ArrayNode declarations, JsonNode declarationList,
                                                          ConversionContext context) {
        String kind = extractDeclarationKind(declarationList);

        StringBuilder sb = new StringBuilder();
        sb.append(kind).append(" ");

        // Build comma-separated list of declarations
        for (int i = 0; i < declarations.size(); i++) {
            JsonNode declaration = declarations.get(i);
            if (!declaration.isObject()) {
                continue;
            }

            if (i > 0) {
                sb.append(", ");
            }

            String name = extractName(declaration);
            String init = extractInitializer(declaration, context);

            sb.append(name);
            if (!init.isEmpty()) {
                sb.append(" = ").append(init);
            }
        }

        return new ExpressionStatement(sb.toString());
    }

    /**
     * Extracts variable name.
     * CC: 1
     */
    private String extractName(JsonNode declaration) {
        return declaration.has("name") ? declaration.get("name").asText() : "";
    }

    /**
     * Extracts initializer expression.
     * CC: 2 (null check + instance check)
     */
    private String extractInitializer(JsonNode declaration, ConversionContext context) {
        JsonNode initNode = declaration.get("initializer");
        if (initNode == null || !initNode.isObject()) {
            return "";
        }

        return context.convertExpression(initNode);
    }

    /**
     * Extracts declaration kind (let, const, var).
     * Uses Symbols.DEFAULT_DECLARATION_KIND as fallback.
     * CC: 2 (has check + ternary)
     */
    private String extractDeclarationKind(JsonNode declarationList) {
        if (!declarationList.has("declarationKind")) {
            return Symbols.DEFAULT_DECLARATION_KIND;
        }
        return declarationList.get("declarationKind").asText();
    }

    /**
     * Formats variable declaration string.
     * CC: 2 (ternary)
     */
    private ExpressionStatement formatVariableDeclaration(String kind, String name, String init) {
        StringBuilder sb = new StringBuilder();
        sb.append(kind).append(" ").append(name);

        if (!init.isEmpty()) {
            sb.append(" = ").append(init);
        }

        return new ExpressionStatement(sb.toString());
    }
}
