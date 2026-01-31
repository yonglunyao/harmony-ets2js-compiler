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

    @Override
    public boolean canConvert(String kindName) {
        return "VariableStatement".equals(kindName) || "FirstStatement".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject declarationList = json.getAsJsonObject("declarationList");
        if (declarationList != null) {
            JsonArray declarations = declarationList.getAsJsonArray("declarations");
            if (declarations != null && declarations.size() > 0) {
                JsonElement declElement = declarations.get(0);
                if (declElement.isJsonObject()) {
                    JsonObject decl = declElement.getAsJsonObject();
                    String name = decl.has("name") ? decl.get("name").getAsString() : "";

                    String init = "";
                    JsonElement initElement = decl.get("initializer");
                    if (initElement != null && initElement.isJsonObject()) {
                        init = context.convertExpression(initElement.getAsJsonObject());
                    }

                    // Get the declaration keyword (let, const, var) from the source
                    String declarationKind = "const"; // default
                    if (declarationList.has("declarationKind")) {
                        declarationKind = declarationList.get("declarationKind").getAsString();
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append(declarationKind).append(" ").append(name);
                    if (!init.isEmpty()) {
                        sb.append(" = ").append(init);
                    }
                    return new ExpressionStatement(sb.toString());
                }
            }
        }
        return new ExpressionStatement("// variable declaration");
    }
}
