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

        if (statementsArray == null) {
            return block;
        }

        processStatements(statementsArray, block, context);
        return block;
    }

    /**
     * Processes all statements in the block.
     * Handles container component + children block pattern.
     * CC: 3 (loop + early continue + nested if)
     */
    private void processStatements(JsonArray statementsArray, Block block, ConversionContext context) {
        int i = Symbols.INDEX_ZERO;
        while (i < statementsArray.size()) {
            JsonObject stmtObj = statementsArray.get(i).getAsJsonObject();

            // Check for container component pattern
            int skipCount = tryProcessContainerComponent(statementsArray, i, stmtObj, block, context);
            if (skipCount > 0) {
                i += skipCount;
                continue;
            }

            // Regular statement processing
            AstNode stmt = context.convertStatement(stmtObj);
            if (stmt != null) {
                block.addStatement(stmt);
            }
            i++;
        }
    }

    /**
     * Tries to process a container component followed by a children block.
     * Returns the number of statements to skip (0 if not a pattern).
     * CC: 4 (early returns + condition check)
     */
    private int tryProcessContainerComponent(JsonArray statementsArray, int index,
                                              JsonObject stmtObj, Block block, ConversionContext context) {
        // Must have next statement
        if (index + 1 >= statementsArray.size()) {
            return 0;
        }

        String stmtKindName = getKindName(stmtObj);
        if (!"ExpressionStatement".equals(stmtKindName)) {
            return 0;
        }

        // Extract component name from expression
        String componentName = extractComponentName(stmtObj);
        if (componentName == null) {
            return 0;
        }

        // Check if it's a container component
        if (!ComponentRegistry.isContainerComponent(componentName)) {
            return 0;
        }

        // Check if next is a Block
        JsonObject nextStmtObj = statementsArray.get(index + 1).getAsJsonObject();
        if (!"Block".equals(getKindName(nextStmtObj))) {
            return 0;
        }

        // Process container component with children
        processContainerWithChildren(stmtObj, nextStmtObj, block, context);
        return Symbols.CHILD_BLOCK_SKIP_COUNT;
    }

    /**
     * Extracts component name from ExpressionStatement.
     * CC: 2 (null checks)
     */
    private String extractComponentName(JsonObject stmtObj) {
        JsonObject exprObj = stmtObj.getAsJsonObject("expression");
        if (exprObj == null) {
            return null;
        }

        String exprKindName = getKindName(exprObj);
        if (!"CallExpression".equals(exprKindName)) {
            return null;
        }

        JsonObject idObj = exprObj.getAsJsonObject("expression");
        if (idObj == null) {
            return null;
        }

        if (!"Identifier".equals(getKindName(idObj))) {
            return null;
        }

        return idObj.get("name").getAsString();
    }

    /**
     * Processes a container component with its children block.
     * CC: 3 (if-else chain)
     */
    private void processContainerWithChildren(JsonObject componentStmt, JsonObject childrenBlockJson,
                                              Block block, ConversionContext context) {
        AstNode componentStmtNode = context.convertStatement(componentStmt);
        Block childrenBlock = (Block) context.convertStatement(childrenBlockJson);

        if (componentStmtNode instanceof ComponentStatement) {
            ((ComponentStatement) componentStmtNode).setChildren(childrenBlock);
            block.addStatement(componentStmtNode);
            return;
        }

        if (componentStmtNode instanceof ExpressionStatement) {
            ComponentStatement compStmt = transformToComponentStatement(
                    (ExpressionStatement) componentStmtNode);
            if (compStmt != null) {
                compStmt.setChildren(childrenBlock);
                block.addStatement(compStmt);
                return;
            }
        }

        block.addStatement(componentStmtNode);
    }

    /**
     * Transforms an ExpressionStatement to ComponentStatement.
     * CC: 1
     */
    private ComponentStatement transformToComponentStatement(ExpressionStatement exprStmt) {
        return (ComponentStatement) ComponentExpressionTransformer.transform(exprStmt.getExpression());
    }

    /**
     * Safely extracts kindName from JsonObject.
     * CC: 1
     */
    private String getKindName(JsonObject obj) {
        if (obj == null || !obj.has("kindName")) {
            return "";
        }
        return obj.get("kindName").getAsString();
    }
}
