package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Converter for template expressions.
 * Handles: `hello ${name}` and `hello world`
 */
public class TemplateExpressionConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "TemplateExpression".equals(kindName) ||
               "NoSubstitutionTemplateLiteral".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String kindName = json.has("kindName") ? json.get("kindName").getAsString() : "";

        if ("NoSubstitutionTemplateLiteral".equals(kindName)) {
            // Handle template strings without interpolation: `hello world`
            String templateText = json.has("text") ? json.get("text").getAsString() : "";
            return "`" + context.escapeTemplateLiteral(templateText) + "`";
        }

        // Handle template strings with interpolation: `hello ${name}`
        JsonObject head = json.getAsJsonObject("head");
        String headText = head != null && head.has("text") ? head.get("text").getAsString() : "";

        JsonArray spans = json.getAsJsonArray("templateSpans");

        StringBuilder sb = new StringBuilder();
        sb.append("`");

        // Add head text
        sb.append(context.escapeTemplateLiteral(headText));

        if (spans != null) {
            for (int i = 0; i < spans.size(); i++) {
                JsonObject span = spans.get(i).getAsJsonObject();

                // Handle interpolation expression
                JsonObject expr = span.getAsJsonObject("expression");
                String exprStr = expr != null ? context.convertExpression(expr) : "";
                sb.append("${").append(exprStr).append("}");

                // Handle text after interpolation
                JsonObject literal = span.getAsJsonObject("literal");
                String litText = literal != null && literal.has("text") ? literal.get("text").getAsString() : "";
                sb.append(context.escapeTemplateLiteral(litText));
            }
        }

        sb.append("`");
        return sb.toString();
    }
}
