package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ExpressionStatement;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Converter for variable statements.
 * Handles: let x = value;, const y = value;
 */
public class VariableConverter implements NodeConverter {

    private static final String DEFAULT_DECLARATION_KIND = "const";

    @Override
    public boolean canConvert(String kindName) {
        return "VariableStatement".equals(kindName) || "FirstStatement".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject declarationList = json.getAsJsonObject("declarationList");
        if (declarationList == null) {
            return new ExpressionStatement("// variable declaration");
        }

        JsonObject declaration = getFirstDeclaration(declarationList);
        if (declaration == null) {
            return new ExpressionStatement("// variable declaration");
        }

        return buildVariableDeclaration(declaration, declarationList, context);
    }

    /**
     * Gets the first declaration from the list.
     * CC: 3 (null check + size check + instance check)
     */
    private JsonObject getFirstDeclaration(JsonObject declarationList) {
        JsonArray declarations = declarationList.getAsJsonArray("declarations");
        if (declarations == null || declarations.size() == 0) {
            return null;
        }

        JsonElement declElement = declarations.get(0);
        if (!declElement.isJsonObject()) {
            return null;
        }

        return declElement.getAsJsonObject();
    }

    /**
     * Builds variable declaration statement.
     * CC: 3 (method calls)
     */
    private ExpressionStatement buildVariableDeclaration(JsonObject declaration, JsonObject declarationList,
                                                        ConversionContext context) {
        String name = extractName(declaration);
        String init = extractInitializer(declaration, context);
        String kind = extractDeclarationKind(declarationList);

        return formatVariableDeclaration(kind, name, init);
    }

    /**
     * Extracts variable name.
     * CC: 1
     */
    private String extractName(JsonObject declaration) {
        return declaration.has("name") ? declaration.get("name").getAsString() : "";
    }

    /**
     * Extracts initializer expression.
     * CC: 2 (null check + instance check)
     */
    private String extractInitializer(JsonObject declaration, ConversionContext context) {
        JsonElement initElement = declaration.get("initializer");
        if (initElement == null || !initElement.isJsonObject()) {
            return "";
        }

        return context.convertExpression(initElement.getAsJsonObject());
    }

    /**
     * Extracts declaration kind (let, const, var).
     * CC: 2 (has check + ternary)
     */
    private String extractDeclarationKind(JsonObject declarationList) {
        if (!declarationList.has("declarationKind")) {
            return DEFAULT_DECLARATION_KIND;
        }
        return declarationList.get("declarationKind").getAsString();
    }

    /**
     * Formats variable declaration string.
     * CC: 2 (ternary)
     */
    private ExpressionStatement formatVariableDeclaration(String kind, String name, String init) {
        StringBuilder sb = new StringBuilder();
        sb.append(kind).append(" ").append(name);

        if (!init.isEmpty()) {
            sb.append(" = ").append(init);
        }

        return new ExpressionStatement(sb.toString());
    }
}
