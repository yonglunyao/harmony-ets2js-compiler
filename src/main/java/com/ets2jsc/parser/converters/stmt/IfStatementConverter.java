package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.IfStatement;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
        String condition = convertCondition(json, context);
        Block thenBlock = convertThenBlock(json, context);
        Block elseBlock = convertElseBlock(json, context);

        return new IfStatement(condition, thenBlock, elseBlock);
    }

    /**
     * Converts the condition expression.
     * CC: 2 (null checks)
     */
    private String convertCondition(JsonNode json, ConversionContext context) {
        JsonNode exprNode = json.get("expression");
        if (exprNode == null || !exprNode.isObject()) {
            return "true";
        }
        return context.convertExpression(exprNode);
    }

    /**
     * Converts the then block.
     * CC: 3 (null checks + instance check)
     */
    private Block convertThenBlock(JsonNode json, ConversionContext context) {
        JsonNode thenNode = json.get("thenStatement");
        if (thenNode == null || thenNode.isNull() || !thenNode.isObject()) {
            return new Block();
        }

        return convertToBlock(thenNode, context);
    }

    /**
     * Converts the else block (if exists).
     * CC: 3 (null checks + instance check)
     */
    private Block convertElseBlock(JsonNode json, ConversionContext context) {
        JsonNode elseNode = json.get("elseStatement");
        if (elseNode == null || elseNode.isNull() || !elseNode.isObject()) {
            return null;
        }

        return convertToBlock(elseNode, context);
    }

    /**
     * Converts a JsonNode to a Block.
     * CC: 2 (instance checks)
     */
    private Block convertToBlock(JsonNode elem, ConversionContext context) {
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
