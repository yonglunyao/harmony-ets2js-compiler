package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.IfStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Converter for if statements.
 * Handles: if (condition) { ... } else { ... }
 */
public class IfStatementConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "IfStatement".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String condition = convertCondition(json, context);
        Block thenBlock = convertThenBlock(json, context);
        Block elseBlock = convertElseBlock(json, context);

        return new IfStatement(condition, thenBlock, elseBlock);
    }

    /**
     * Converts the condition expression.
     * CC: 2 (null checks)
     */
    private String convertCondition(JsonObject json, ConversionContext context) {
        JsonObject exprObj = json.getAsJsonObject("expression");
        if (exprObj == null) {
            return "true";
        }
        return context.convertExpression(exprObj);
    }

    /**
     * Converts the then block.
     * CC: 3 (null checks + instance check)
     */
    private Block convertThenBlock(JsonObject json, ConversionContext context) {
        JsonElement thenElem = json.get("thenStatement");
        if (thenElem == null || thenElem.isJsonNull()) {
            return new Block();
        }

        return convertToBlock(thenElem.getAsJsonObject(), context);
    }

    /**
     * Converts the else block (if exists).
     * CC: 3 (null checks + instance check)
     */
    private Block convertElseBlock(JsonObject json, ConversionContext context) {
        JsonElement elseElem = json.get("elseStatement");
        if (elseElem == null || elseElem.isJsonNull()) {
            return null;
        }

        return convertToBlock(elseElem.getAsJsonObject(), context);
    }

    /**
     * Converts a JsonElement to a Block.
     * CC: 2 (instance checks)
     */
    private Block convertToBlock(JsonObject elem, ConversionContext context) {
        AstNode node = context.convertStatement(elem);

        if (node instanceof Block) {
            return (Block) node;
        }

        if (node != null) {
            Block block = new Block();
            block.addStatement(node);
            return block;
        }

        return new Block();
    }
}
