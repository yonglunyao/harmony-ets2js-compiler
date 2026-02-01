package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Abstract base class for loop statement converters.
 * Uses template method pattern to eliminate code duplication among loop types.
 */
public abstract class LoopConverter implements NodeConverter {

    @Override
    public final Object convert(JsonNode json, ConversionContext context) {
        // Priority 1: Check for pre-generated text (TypeScript parser already generated)
        if (json.has("text")) {
            String text = json.get("text").asText();
            if (!text.isEmpty()) {
                return new ExpressionStatement(text);
            }
        }

        // Priority 2: Use template method for conversion
        return convertLoop(json, context);
    }

    /**
     * Template method: Converts the loop statement.
     * Subclasses only need to implement the specific parts.
     * Can be overridden for special cases like do...while loops.
     */
    protected AstNode convertLoop(JsonNode json, ConversionContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append(getLoopHeader(json, context));

        JsonNode body = getLoopBody(json);
        if (body != null && body.isObject()) {
            AstNode stmt = context.convertStatement(body);
            sb.append(formatBody(stmt));
        }

        sb.append(getLoopFooter());
        return new ExpressionStatement(sb.toString());
    }

    /**
     * Hook method: Gets the loop header (e.g., "for (let x of array) {\n").
     */
    protected abstract String getLoopHeader(JsonNode json, ConversionContext context);

    /**
     * Hook method: Gets the loop body JSON node.
     */
    protected abstract JsonNode getLoopBody(JsonNode json);

    /**
     * Hook method: Gets the loop footer (e.g., "}").
     */
    protected String getLoopFooter() {
        return "}";
    }

    /**
     * Formats the body of the loop.
     */
    protected String formatBody(AstNode stmt) {
        StringBuilder sb = new StringBuilder();
        if (stmt instanceof Block block) {
            for (AstNode blockStmt : block.getStatements()) {
                String stmtCode = blockStmt.accept(new CodeGenerator());
                sb.append("  ").append(stmtCode);
            }
        } else {
            String stmtCode = stmt.accept(new CodeGenerator());
            sb.append("  ").append(stmtCode);
        }
        return sb.toString();
    }
}
