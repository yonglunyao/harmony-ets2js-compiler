package com.ets2jsc.ast;

/**
 * ForEach statement AST node.
 * Represents a ForEach component expression that needs to be transformed to create/pop pattern.
 * <p>
 * Example:
 * Input: ForEach(this.items, (item, index) => { Text(item.name) }, (item) => item.id)
 * Output:
 * ForEach.create();
 * const __itemGenFunction__ = (item, index) => { Text.create(item.name); Text.pop(); };
 * const __keyGenFunction__ = (item) => item.id;
 * ForEach.itemGenerator(__itemGenFunction__);
 * ForEach.keyGenerator(__keyGenFunction__);
 * ForEach.pop();
 */
public record ForeachStatement(String arrayExpression, String itemGenerator, String keyGenerator) implements AstNode {

    @Override
    public String getType() {
        return "ForeachStatement";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
