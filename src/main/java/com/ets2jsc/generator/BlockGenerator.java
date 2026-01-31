package com.ets2jsc.generator;

import com.ets2jsc.ast.*;
import com.ets2jsc.config.CompilerConfig;

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
            if (stmt instanceof ForeachStatement) {
                sb.append(((ForeachStatement) stmt).accept(generator));
            } else if (stmt instanceof IfStatement) {
                sb.append(((IfStatement) stmt).accept(generator));
            } else {
                String stmtCode = stmt.accept(generator);
                if (stmtCode != null && !stmtCode.isEmpty()) {
                    if (stmt instanceof Block) {
                        sb.append(stmtCode);
                    } else {
                        sb.append(indentation.getCurrent()).append(stmtCode).append("\n");
                    }
                }
            }
        }

        return sb.toString();
    }
}
