package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
    public Object convert(JsonObject json, ConversionContext context) {
        // Check for pre-generated text first
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").getAsString());
        }

        StringBuilder sb = new StringBuilder();

        // Build try block
        sb.append("try {\n");
        appendBlockContent(sb, json.getAsJsonObject("tryBlock"), context);
        sb.append("\n}");

        // Build catch block
        JsonObject catchClause = json.getAsJsonObject("catchClause");
        if (catchClause != null) {
            appendCatchClause(sb, catchClause, context);
        }

        // Build finally block
        JsonObject finallyBlock = json.getAsJsonObject("finallyBlock");
        if (finallyBlock != null) {
            sb.append(" finally {\n");
            appendBlockContent(sb, finallyBlock, context);
            sb.append("\n}");
        }

        return new ExpressionStatement(sb.toString());
    }

    /**
     * Appends catch clause to the output.
     * CC: 3 (if-else for variable name check)
     */
    private void appendCatchClause(StringBuilder sb, JsonObject catchClause, ConversionContext context) {
        String varName = extractVariableName(catchClause);

        if (!varName.isEmpty()) {
            sb.append(" catch (").append(varName).append(") {\n");
        } else {
            sb.append(" catch {\n");
        }

        JsonObject catchBlock = catchClause.getAsJsonObject("block");
        appendBlockContent(sb, catchBlock, context);
        sb.append("\n}");
    }

    /**
     * Extracts variable name from catch clause.
     * CC: 2 (null check + array size check)
     */
    private String extractVariableName(JsonObject catchClause) {
        JsonObject varDecl = catchClause.getAsJsonObject("variableDeclaration");
        if (varDecl == null) {
            return "";
        }

        JsonArray declarations = varDecl.getAsJsonArray("declarations");
        if (declarations == null || declarations.size() == 0) {
            return "";
        }

        JsonObject decl = declarations.get(0).getAsJsonObject();
        return decl.has("name") ? decl.get("name").getAsString() : "";
    }

    /**
     * Appends block content with proper indentation.
     * Eliminates duplicate code used for try/catch/finally blocks.
     * CC: 2 (null check + instance check)
     */
    private void appendBlockContent(StringBuilder sb, JsonObject block, ConversionContext context) {
        if (block == null) {
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
