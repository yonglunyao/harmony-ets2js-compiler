package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.google.gson.JsonObject;

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
    protected String getLoopHeader(JsonObject json, ConversionContext context) {
        return "do {\n";
    }

    @Override
    protected JsonObject getLoopBody(JsonObject json) {
        return json.getAsJsonObject("statement");
    }

    @Override
    protected String getLoopFooter() {
        return "} while (";
    }

    @Override
    protected String formatBody(com.ets2jsc.ast.AstNode stmt) {
        StringBuilder sb = new StringBuilder();
        if (stmt instanceof com.ets2jsc.ast.Block) {
            com.ets2jsc.ast.Block block = (com.ets2jsc.ast.Block) stmt;
            for (com.ets2jsc.ast.AstNode blockStmt : block.getStatements()) {
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
    protected AstNode convertLoop(JsonObject json, ConversionContext context) {
        // Priority 1: Check for pre-generated text
        if (json.has("text")) {
            String text = json.get("text").getAsString();
            if (!text.isEmpty()) {
                return new ExpressionStatement(text);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getLoopHeader(json, context));

        JsonObject body = getLoopBody(json);
        if (body != null) {
            AstNode stmt = context.convertStatement(body);
            sb.append(formatBody(stmt));
        }

        // Add condition after the closing brace
        JsonObject expression = json.getAsJsonObject("expression");
        String condition = expression != null ? context.convertExpression(expression) : "";
        sb.append(getLoopFooter()).append(condition).append(")");

        return new ExpressionStatement(sb.toString());
    }
}
