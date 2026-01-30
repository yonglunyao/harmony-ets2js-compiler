package com.ets2jsc.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * ForEach statement AST node.
 * Represents a ForEach component expression that needs to be transformed to create/pop pattern.
 *
 * Example:
 * Input: ForEach(this.items, (item, index) => { Text(item.name) }, (item) => item.id)
 * Output:
 *   ForEach.create();
 *   const __itemGenFunction__ = (item, index) => { Text.create(item.name); Text.pop(); };
 *   const __keyGenFunction__ = (item) => item.id;
 *   ForEach.itemGenerator(__itemGenFunction__);
 *   ForEach.keyGenerator(__keyGenFunction__);
 *   ForEach.pop();
 */
public class ForeachStatement implements AstNode {

    private final String arrayExpression;
    private final String itemGenerator;
    private final String keyGenerator;

    public ForeachStatement(String arrayExpression, String itemGenerator, String keyGenerator) {
        this.arrayExpression = arrayExpression;
        this.itemGenerator = itemGenerator;
        this.keyGenerator = keyGenerator;
    }

    public String getArrayExpression() {
        return arrayExpression;
    }

    public String getItemGenerator() {
        return itemGenerator;
    }

    public String getKeyGenerator() {
        return keyGenerator;
    }

    @Override
    public String getType() {
        return "ForeachStatement";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
