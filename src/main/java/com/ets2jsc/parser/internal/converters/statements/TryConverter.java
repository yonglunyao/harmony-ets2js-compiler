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

        JsonObject tryBlock = json.getAsJsonObject("tryBlock");
        JsonObject catchClause = json.getAsJsonObject("catchClause");
        JsonObject finallyBlock = json.getAsJsonObject("finallyBlock");

        StringBuilder sb = new StringBuilder();
        sb.append("try {\n");

        if (tryBlock != null) {
            AstNode tryNode = context.convertStatement(tryBlock);
            if (tryNode instanceof Block) {
                Block block = (Block) tryNode;
                for (AstNode blockStmt : block.getStatements()) {
                    String stmtCode = blockStmt.accept(new CodeGenerator());
                    sb.append("  ").append(stmtCode);
                }
            }
        }

        sb.append("\n}");

        if (catchClause != null) {
            JsonObject varDecl = catchClause.getAsJsonObject("variableDeclaration");
            String varName = "";
            if (varDecl != null) {
                JsonArray declarations = varDecl.getAsJsonArray("declarations");
                if (declarations != null && declarations.size() > 0) {
                    JsonObject decl = declarations.get(0).getAsJsonObject();
                    varName = decl.has("name") ? decl.get("name").getAsString() : "";
                }
            }

            if (!varName.isEmpty()) {
                sb.append(" catch (").append(varName).append(") {\n");
            } else {
                sb.append(" catch {\n");
            }

            JsonObject catchBlock = catchClause.getAsJsonObject("block");
            if (catchBlock != null) {
                AstNode catchNode = context.convertStatement(catchBlock);
                if (catchNode instanceof Block) {
                    Block block = (Block) catchNode;
                    for (AstNode blockStmt : block.getStatements()) {
                        String stmtCode = blockStmt.accept(new CodeGenerator());
                        sb.append("  ").append(stmtCode);
                    }
                }
            }

            sb.append("\n}");
        }

        if (finallyBlock != null) {
            sb.append(" finally {\n");

            AstNode finallyNode = context.convertStatement(finallyBlock);
            if (finallyNode instanceof Block) {
                Block block = (Block) finallyNode;
                for (AstNode blockStmt : block.getStatements()) {
                    String stmtCode = blockStmt.accept(new CodeGenerator());
                    sb.append("  ").append(stmtCode);
                }
            }

            sb.append("\n}");
        }

        return new ExpressionStatement(sb.toString());
    }
}
