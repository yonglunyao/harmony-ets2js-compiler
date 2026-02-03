package com.ets2jsc.domain.model.ast;

/**
 * If statement AST node for conditional rendering.
 * Represents an If component expression that needs to be transformed to create/pop pattern.
 *
 * Example:
 * Input: if (condition) { Text('Yes') } else { Text('No') }
 * Output:
 *   If.create();
 *   if (condition) {
 *     If.branchId(0);
 *     Text.create('Yes');
 *     Text.pop();
 *   } else {
 *     If.branchId(1);
 *     Text.create('No');
 *     Text.pop();
 *   }
 *   If.pop();
 */
import lombok.Getter;

/**
 * If statement AST node for conditional rendering.
 * Represents an If component expression that needs to be transformed to create/pop pattern.
 *
 * Example:
 * Input: if (condition) { Text('Yes') } else { Text('No') }
 * Output:
 *   If.create();
 *   if (condition) {
 *     If.branchId(0);
 *     Text.create('Yes');
 *     Text.pop();
 *   } else {
 *     If.branchId(1);
 *     Text.create('No');
 *     Text.pop();
 *   }
 *   If.pop();
 */
@Getter
public class IfStatement implements AstNode {

    private final String condition;
    private final Block thenBlock;
    private final Block elseBlock;

    public IfStatement(String condition, Block thenBlock, Block elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    /**
     * Checks if statement has else block.
     */
    public boolean hasElse() {
        return elseBlock != null && elseBlock.getStatements().size() > 0;
    }

    @Override
    public String getType() {
        return "IfStatement";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
