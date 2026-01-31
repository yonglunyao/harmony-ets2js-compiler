package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.Block;
import com.ets2jsc.ast.ComponentRegistry;
import com.ets2jsc.ast.ComponentStatement;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.constant.Symbols;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.ets2jsc.transformer.ComponentExpressionTransformer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Converter for block statements.
 * Handles: { statements... }
 */
public class BlockConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "Block".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        Block block = new Block();
        JsonArray statementsArray = json.getAsJsonArray("statements");
        if (statementsArray != null) {
            int i = Symbols.INDEX_ZERO;
            while (i < statementsArray.size()) {
                JsonObject stmtObj = statementsArray.get(i).getAsJsonObject();
                String stmtKindName = stmtObj.has("kindName") ? stmtObj.get("kindName").getAsString() : "";

                // Check if this is an ExpressionStatement with a component CallExpression followed by a Block
                if ("ExpressionStatement".equals(stmtKindName) && i + 1 < statementsArray.size()) {
                    JsonObject exprObj = stmtObj.getAsJsonObject("expression");
                    String exprKindName = exprObj.has("kindName") ? exprObj.get("kindName").getAsString() : "";

                    if ("CallExpression".equals(exprKindName)) {
                        JsonObject idObj = exprObj.getAsJsonObject("expression");
                        if (idObj != null && "Identifier".equals(idObj.get("kindName").getAsString())) {
                            String componentName = idObj.get("name").getAsString();
                            // Check if this is a built-in container component
                            if (ComponentRegistry.isContainerComponent(componentName)) {
                                JsonObject nextStmtObj = statementsArray.get(i + 1).getAsJsonObject();
                                String nextKindName = nextStmtObj.has("kindName") ? nextStmtObj.get("kindName").getAsString() : "";

                                if ("Block".equals(nextKindName)) {
                                    // This is a component with children block
                                    AstNode componentStmt = context.convertStatement(stmtObj);
                                    Block childrenBlock = (Block) context.convertStatement(nextStmtObj);

                                    if (componentStmt instanceof ComponentStatement) {
                                        ((ComponentStatement) componentStmt).setChildren(childrenBlock);
                                    } else if (componentStmt instanceof ExpressionStatement) {
                                        ComponentStatement compStmt = (ComponentStatement) ComponentExpressionTransformer.transform(
                                            ((ExpressionStatement) componentStmt).getExpression());
                                        if (compStmt != null) {
                                            compStmt.setChildren(childrenBlock);
                                            block.addStatement(compStmt);
                                        } else {
                                            block.addStatement(componentStmt);
                                        }
                                    } else {
                                        block.addStatement(componentStmt);
                                    }
                                    // Skip the next statement (the children block)
                                    i += Symbols.CHILD_BLOCK_SKIP_COUNT;
                                    continue;
                                }
                            }
                        }
                    }
                }

                AstNode stmt = context.convertStatement(stmtObj);
                if (stmt != null) {
                    block.addStatement(stmt);
                }
                i++;
            }
        }
        return block;
    }
}
