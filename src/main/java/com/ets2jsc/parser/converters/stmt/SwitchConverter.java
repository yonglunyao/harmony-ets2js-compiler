package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.ExpressionStatement;
import com.ets2jsc.shared.constant.Symbols;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        if (json.has("text")) {
            return new ExpressionStatement(json.get("text").asText());
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
    private String convertSwitchExpression(JsonNode json, ConversionContext context) {
        JsonNode expressionNode = json.get("expression");
        return (expressionNode != null && expressionNode.isObject()) ? context.convertExpression(expressionNode) : Symbols.EMPTY_STRING;
    }

    /**
     * Processes the case block and all its clauses.
     */
    private void processCaseBlock(JsonNode json, StringBuilder sb, ConversionContext context) {
        JsonNode caseBlockNode = json.get("caseBlock");
        if (caseBlockNode == null || !caseBlockNode.isObject()) {
            return;
        }

        JsonNode clausesNode = caseBlockNode.get("clauses");
        if (clausesNode == null || !clausesNode.isArray()) {
            return;
        }

        ArrayNode clauses = (ArrayNode) clausesNode;
        for (int i = Symbols.INDEX_ZERO; i < clauses.size(); i++) {
            JsonNode clause = clauses.get(i);
            String kindName = getKindName(clause);
            processClause(clause, kindName, sb, context);
        }
    }

    /**
     * Processes a single case or default clause.
     */
    private void processClause(JsonNode clause, String kindName, StringBuilder sb, ConversionContext context) {
        if (KIND_CASE_CLAUSE.equals(kindName)) {
            processCaseClause(clause, sb, context);
        } else if (KIND_DEFAULT_CLAUSE.equals(kindName)) {
            processDefaultClause(clause, sb, context);
        }
    }

    /**
     * Processes a case clause.
     */
    private void processCaseClause(JsonNode clause, StringBuilder sb, ConversionContext context) {
        String caseExpr = convertClauseExpression(clause, context);
        sb.append("  case ").append(caseExpr).append(":\n");

        processStatements(clause, sb, context);
        sb.append("    break;\n");
    }

    /**
     * Processes a default clause.
     */
    private void processDefaultClause(JsonNode clause, StringBuilder sb, ConversionContext context) {
        sb.append("  default:\n");
        processStatements(clause, sb, context);
    }

    /**
     * Converts the clause expression for a case statement.
     */
    private String convertClauseExpression(JsonNode clause, ConversionContext context) {
        JsonNode clauseExprNode = clause.get("expression");
        return (clauseExprNode != null && clauseExprNode.isObject()) ? context.convertExpression(clauseExprNode) : Symbols.EMPTY_STRING;
    }

    /**
     * Processes all statements within a clause.
     */
    private void processStatements(JsonNode clause, StringBuilder sb, ConversionContext context) {
        JsonNode stmtsNode = clause.get("statements");
        if (stmtsNode == null || !stmtsNode.isArray()) {
            return;
        }

        ArrayNode stmts = (ArrayNode) stmtsNode;
        for (int j = Symbols.INDEX_ZERO; j < stmts.size(); j++) {
            JsonNode stmt = stmts.get(j);
            AstNode stmtNode = context.convertStatement(stmt);
            if (stmtNode != null) {
                String stmtCode = stmtNode.accept(new CodeGenerator());
                sb.append("    ").append(stmtCode);
            }
        }
    }

    /**
     * Gets the kind name from a JSON node.
     */
    private String getKindName(JsonNode json) {
        return json.has(KIND_NAME) ? json.get(KIND_NAME).asText() : Symbols.EMPTY_STRING;
    }
}
