package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.constant.Symbols;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Converter for switch statements.
 * Handles: switch (expression) { case ...: ... default: ... }
 */
public class SwitchConverter implements NodeConverter {

    private static final String KIND_NAME = "kindName";
    private static final String KIND_CASE_CLAUSE = "CaseClause";
    private static final String KIND_DEFAULT_CLAUSE = "DefaultClause";

    @Override
    public boolean canConvert(String kindName) {
        return "SwitchStatement".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").getAsString());
        }

        String exprStr = convertSwitchExpression(json, context);
        StringBuilder sb = new StringBuilder();
        sb.append("switch (").append(exprStr).append(") {\n");

        processCaseBlock(json, sb, context);

        sb.append("}");
        return new ExpressionStatement(sb.toString());
    }

    /**
     * Converts the switch expression.
     */
    private String convertSwitchExpression(JsonObject json, ConversionContext context) {
        JsonObject expression = json.getAsJsonObject("expression");
        return expression != null ? context.convertExpression(expression) : Symbols.EMPTY_STRING;
    }

    /**
     * Processes the case block and all its clauses.
     */
    private void processCaseBlock(JsonObject json, StringBuilder sb, ConversionContext context) {
        JsonObject caseBlock = json.getAsJsonObject("caseBlock");
        if (caseBlock == null) {
            return;
        }

        JsonArray clauses = caseBlock.getAsJsonArray("clauses");
        if (clauses == null) {
            return;
        }

        for (int i = Symbols.INDEX_ZERO; i < clauses.size(); i++) {
            JsonObject clause = clauses.get(i).getAsJsonObject();
            String kindName = getKindName(clause);
            processClause(clause, kindName, sb, context);
        }
    }

    /**
     * Processes a single case or default clause.
     */
    private void processClause(JsonObject clause, String kindName, StringBuilder sb, ConversionContext context) {
        if (KIND_CASE_CLAUSE.equals(kindName)) {
            processCaseClause(clause, sb, context);
        } else if (KIND_DEFAULT_CLAUSE.equals(kindName)) {
            processDefaultClause(clause, sb, context);
        }
    }

    /**
     * Processes a case clause.
     */
    private void processCaseClause(JsonObject clause, StringBuilder sb, ConversionContext context) {
        String caseExpr = convertClauseExpression(clause, context);
        sb.append("  case ").append(caseExpr).append(":\n");

        processStatements(clause, sb, context);
        sb.append("    break;\n");
    }

    /**
     * Processes a default clause.
     */
    private void processDefaultClause(JsonObject clause, StringBuilder sb, ConversionContext context) {
        sb.append("  default:\n");
        processStatements(clause, sb, context);
    }

    /**
     * Converts the clause expression for a case statement.
     */
    private String convertClauseExpression(JsonObject clause, ConversionContext context) {
        JsonObject clauseExpr = clause.getAsJsonObject("expression");
        return clauseExpr != null ? context.convertExpression(clauseExpr) : Symbols.EMPTY_STRING;
    }

    /**
     * Processes all statements within a clause.
     */
    private void processStatements(JsonObject clause, StringBuilder sb, ConversionContext context) {
        JsonArray stmts = clause.getAsJsonArray("statements");
        if (stmts == null) {
            return;
        }

        for (int j = Symbols.INDEX_ZERO; j < stmts.size(); j++) {
            JsonObject stmt = stmts.get(j).getAsJsonObject();
            AstNode stmtNode = context.convertStatement(stmt);
            if (stmtNode != null) {
                String stmtCode = stmtNode.accept(new CodeGenerator());
                sb.append("    ").append(stmtCode);
            }
        }
    }

    /**
     * Gets the kind name from a JSON object.
     */
    private String getKindName(JsonObject json) {
        return json.has(KIND_NAME) ? json.get(KIND_NAME).getAsString() : Symbols.EMPTY_STRING;
    }
}
