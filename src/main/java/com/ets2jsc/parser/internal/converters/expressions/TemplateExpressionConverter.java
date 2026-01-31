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

    private static final String TEMPLATE_EXPRESSION = "TemplateExpression";
    private static final String NO_SUBSTITUTION_TEMPLATE_LITERAL = "NoSubstitutionTemplateLiteral";

    @Override
    public boolean canConvert(String kindName) {
        return TEMPLATE_EXPRESSION.equals(kindName) ||
               NO_SUBSTITUTION_TEMPLATE_LITERAL.equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String kindName = getKindName(json);

        if (NO_SUBSTITUTION_TEMPLATE_LITERAL.equals(kindName)) {
            return convertNoSubstitutionTemplate(json, context);
        }

        return convertTemplateExpression(json, context);
    }

    /**
     * Converts template string without interpolation: `hello world`
     * CC: 2 (ternary)
     */
    private String convertNoSubstitutionTemplate(JsonObject json, ConversionContext context) {
        String templateText = json.has("text") ? json.get("text").getAsString() : "";
        return "`" + context.escapeTemplateLiteral(templateText) + "`";
    }

    /**
     * Converts template string with interpolation: `hello ${name}`
     * CC: 3 (null checks)
     */
    private String convertTemplateExpression(JsonObject json, ConversionContext context) {
        StringBuilder sb = new StringBuilder("`");

        sb.append(getHeadText(json, context));
        appendTemplateSpans(sb, json, context);

        sb.append("`");
        return sb.toString();
    }

    /**
     * Gets the head text of template expression.
     * CC: 2 (null checks)
     */
    private String getHeadText(JsonObject json, ConversionContext context) {
        JsonObject head = json.getAsJsonObject("head");
        if (head == null) {
            return "";
        }

        String headText = head.has("text") ? head.get("text").getAsString() : "";
        return context.escapeTemplateLiteral(headText);
    }

    /**
     * Appends template spans (interpolations + trailing text).
     * CC: 3 (null check + loop)
     */
    private void appendTemplateSpans(StringBuilder sb, JsonObject json, ConversionContext context) {
        JsonArray spans = json.getAsJsonArray("templateSpans");
        if (spans == null) {
            return;
        }

        for (int i = 0; i < spans.size(); i++) {
            JsonObject span = spans.get(i).getAsJsonObject();
            appendSingleSpan(sb, span, context);
        }
    }

    /**
     * Appends a single template span.
     * CC: 2 (null checks)
     */
    private void appendSingleSpan(StringBuilder sb, JsonObject span, ConversionContext context) {
        // Append interpolation expression
        JsonObject expr = span.getAsJsonObject("expression");
        String exprStr = expr != null ? context.convertExpression(expr) : "";
        sb.append("${").append(exprStr).append("}");

        // Append text after interpolation
        JsonObject literal = span.getAsJsonObject("literal");
        String litText = getLiteralText(literal, context);
        sb.append(litText);
    }

    /**
     * Gets literal text from literal object.
     * CC: 2 (null checks)
     */
    private String getLiteralText(JsonObject literal, ConversionContext context) {
        if (literal == null) {
            return "";
        }

        String litText = literal.has("text") ? literal.get("text").getAsString() : "";
        return context.escapeTemplateLiteral(litText);
    }

    /**
     * Gets kind name safely.
     * CC: 2 (null check + has check)
     */
    private String getKindName(JsonObject json) {
        if (json == null || !json.has("kindName")) {
            return "";
        }
        return json.get("kindName").getAsString();
    }
}
