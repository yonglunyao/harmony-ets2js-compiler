package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for new expressions.
 * Handles: new Constructor(), new Class(arg1, arg2)
 */
public class NewExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "NewExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        JsonObject newExpr = json.getAsJsonObject("expression");
        String exprStr = newExpr != null ? context.convertExpression(newExpr) : "";
        StringBuilder sb = new StringBuilder();
        sb.append("new ").append(exprStr).append("(");
        JsonArray arguments = json.getAsJsonArray("arguments");
        if (arguments != null && arguments.size() > 0) {
            List<String> argStrings = new ArrayList<>();
            for (int i = 0; i < arguments.size(); i++) {
                String argStr = context.convertExpression(arguments.get(i).getAsJsonObject());
                argStrings.add(argStr);
            }
            sb.append(String.join(", ", argStrings));
        }
        sb.append(")");
        return sb.toString();
    }
}
