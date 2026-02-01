package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Converter for try-catch-finally statements.
 * Handles: try { ... } catch (e) { ... } finally { ... }
 */
public class TryConverter implements NodeConverter {

    private static final String INDENT = "  ";

    @Override
    public boolean canConvert(String kindName) {
        return "TryStatement".equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        // Check for pre-generated text first
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").asText());
        }

        StringBuilder sb = new StringBuilder();

        // Build try block
        sb.append("try {\n");
        JsonNode tryBlockNode = json.get("tryBlock");
        appendBlockContent(sb, tryBlockNode, context);
        sb.append("\n}");

        // Build catch block
        JsonNode catchClauseNode = json.get("catchClause");
        if (catchClauseNode != null && catchClauseNode.isObject()) {
            appendCatchClause(sb, catchClauseNode, context);
        }

        // Build finally block
        JsonNode finallyBlockNode = json.get("finallyBlock");
        if (finallyBlockNode != null && finallyBlockNode.isObject()) {
            sb.append(" finally {\n");
            appendBlockContent(sb, finallyBlockNode, context);
            sb.append("\n}");
        }

        return new ExpressionStatement(sb.toString());
    }

    /**
     * Appends catch clause to the output.
     * CC: 3 (if-else for variable name check)
     */
    private void appendCatchClause(StringBuilder sb, JsonNode catchClause, ConversionContext context) {
        String varName = extractVariableName(catchClause);

        if (!varName.isEmpty()) {
            sb.append(" catch (").append(varName).append(") {\n");
        } else {
            sb.append(" catch {\n");
        }

        JsonNode catchBlockNode = catchClause.get("block");
        appendBlockContent(sb, catchBlockNode, context);
        sb.append("\n}");
    }

    /**
     * Extracts variable name from catch clause.
     * CC: 2 (null check + array size check)
     */
    private String extractVariableName(JsonNode catchClause) {
        JsonNode varDeclNode = catchClause.get("variableDeclaration");
        if (varDeclNode == null || !varDeclNode.isObject()) {
            return "";
        }

        JsonNode declarationsNode = varDeclNode.get("declarations");
        if (declarationsNode == null || !declarationsNode.isArray()) {
            return "";
        }

        ArrayNode declarations = (ArrayNode) declarationsNode;
        if (declarations.size() == 0) {
            return "";
        }

        JsonNode decl = declarations.get(0);
        return decl.has("name") ? decl.get("name").asText() : "";
    }

    /**
     * Appends block content with proper indentation.
     * Eliminates duplicate code used for try/catch/finally blocks.
     * CC: 2 (null check + instance check)
     */
    private void appendBlockContent(StringBuilder sb, JsonNode block, ConversionContext context) {
        if (block == null || !block.isObject()) {
            return;
        }

        AstNode node = context.convertStatement(block);
        if (!(node instanceof Block)) {
            return;
        }

        Block codeBlock = (Block) node;
        CodeGenerator generator = new CodeGenerator();

        for (AstNode stmt : codeBlock.getStatements()) {
            String stmtCode = stmt.accept(generator);
            sb.append(INDENT).append(stmtCode);
        }
    }
}
