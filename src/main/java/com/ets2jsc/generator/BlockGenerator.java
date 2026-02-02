package com.ets2jsc.generator;

import com.ets2jsc.ast.*;

/**
 * Generates code for block statements.
 */
public class BlockGenerator {

    private final Block block;
    private final IndentationManager indentation;
    private final AstVisitor<String> generator;

    public BlockGenerator(Block block, IndentationManager indentation, AstVisitor<String> generator) {
        this.block = block;
        this.indentation = indentation;
        this.generator = generator;
    }

    public String generate() {
        StringBuilder sb = new StringBuilder();

        for (AstNode stmt : block.getStatements()) {
            String stmtCode = generateStatementCode(stmt);
            appendStatementCode(sb, stmtCode, stmt instanceof Block);
        }

        return sb.toString();
    }

    /**
     * Generates code for a single statement.
     *
     * @param stmt the AST node representing the statement
     * @return the generated code as a string
     */
    private String generateStatementCode(AstNode stmt) {
        if (stmt instanceof ForeachStatement) {
            return ((ForeachStatement) stmt).accept(generator);
        } else if (stmt instanceof IfStatement) {
            return ((IfStatement) stmt).accept(generator);
        }
        return stmt.accept(generator);
    }

    /**
     * Appends statement code with proper indentation.
     *
     * @param sb the string builder
     * @param stmtCode the statement code
     * @param isBlock whether the statement is a block
     */
    private void appendStatementCode(StringBuilder sb, String stmtCode, boolean isBlock) {
        if (stmtCode == null || stmtCode.isEmpty()) {
            return;
        }

        if (isBlock) {
            sb.append(stmtCode);
        } else {
            sb.append(indentation.getCurrent()).append(stmtCode).append('\n');
        }
    }
}
