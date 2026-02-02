package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.Block;
import com.ets2jsc.domain.model.ast.ExpressionStatement;
import com.ets2jsc.parser.ConversionContext;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter for do...while loop statements.
 * Handles: do { ... } while (condition)
 */
public class DoConverter extends LoopConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "DoStatement".equals(kindName);
    }

    @Override
    protected String getLoopHeader(JsonNode json, ConversionContext context) {
        return "do {\n";
    }

    @Override
    protected JsonNode getLoopBody(JsonNode json) {
        return json.get("statement");
    }

    @Override
    protected String getLoopFooter() {
        return "} while (";
    }

    @Override
    protected String formatBody(AstNode stmt) {
        StringBuilder sb = new StringBuilder();
        if (stmt instanceof Block block) {
            for (AstNode blockStmt : block.getStatements()) {
                String stmtCode = blockStmt.accept(new com.ets2jsc.generator.CodeGenerator());
                sb.append("  ").append(stmtCode);
            }
        } else {
            String stmtCode = stmt.accept(new com.ets2jsc.generator.CodeGenerator());
            sb.append("  ").append(stmtCode);
        }
        return sb.toString();
    }

    @Override
    protected AstNode convertLoop(JsonNode json, ConversionContext context) {
        // Priority 1: Check for pre-generated text
        if (json.has("text")) {
            String text = json.get("text").asText();
            if (!text.isEmpty()) {
                return new ExpressionStatement(text);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getLoopHeader(json, context));

        JsonNode body = getLoopBody(json);
        if (body != null && body.isObject()) {
            AstNode stmt = context.convertStatement(body);
            sb.append(formatBody(stmt));
        }

        // Add condition after the closing brace
        JsonNode expressionNode = json.get("expression");
        String condition = (expressionNode != null && expressionNode.isObject()) ? context.convertExpression(expressionNode) : "";
        sb.append(getLoopFooter()).append(condition).append(")");

        return new ExpressionStatement(sb.toString());
    }
}
