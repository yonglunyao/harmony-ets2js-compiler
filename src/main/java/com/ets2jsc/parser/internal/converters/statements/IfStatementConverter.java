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
        JsonObject exprObj = json.getAsJsonObject("expression");
        String condition = context.convertExpression(exprObj);

        // Convert then block
        JsonElement thenElem = json.get("thenStatement");
        Block thenBlock = new Block();
        if (thenElem != null && !thenElem.isJsonNull()) {
            AstNode thenNode = context.convertStatement(thenElem.getAsJsonObject());
            if (thenNode instanceof Block) {
                thenBlock = (Block) thenNode;
            } else if (thenNode != null) {
                thenBlock = new Block();
                thenBlock.addStatement(thenNode);
            }
        }

        // Convert else block (if exists)
        JsonElement elseElem = json.get("elseStatement");
        Block elseBlock = null;
        if (elseElem != null && !elseElem.isJsonNull()) {
            AstNode elseNode = context.convertStatement(elseElem.getAsJsonObject());
            if (elseNode instanceof Block) {
                elseBlock = (Block) elseNode;
            } else if (elseNode != null) {
                elseBlock = new Block();
                elseBlock.addStatement(elseNode);
            }
        }

        return new IfStatement(condition, thenBlock, elseBlock);
    }
}
