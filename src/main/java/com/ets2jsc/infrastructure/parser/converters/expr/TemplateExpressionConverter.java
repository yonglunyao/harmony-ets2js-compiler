package com.ets2jsc.infrastructure.parser.converters.expr;

import com.ets2jsc.infrastructure.parser.ConversionContext;
import com.ets2jsc.infrastructure.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
    public Object convert(JsonNode json, ConversionContext context) {
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
    private String convertNoSubstitutionTemplate(JsonNode json, ConversionContext context) {
        String templateText = json.has("text") ? json.get("text").asText() : "";
        return "`" + context.escapeTemplateLiteral(templateText) + "`";
    }

    /**
     * Converts template string with interpolation: `hello ${name}`
     * CC: 3 (null checks)
     */
    private String convertTemplateExpression(JsonNode json, ConversionContext context) {
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
    private String getHeadText(JsonNode json, ConversionContext context) {
        JsonNode headNode = json.get("head");
        if (headNode == null || !headNode.isObject()) {
            return "";
        }

        String headText = headNode.has("text") ? headNode.get("text").asText() : "";
        return context.escapeTemplateLiteral(headText);
    }

    /**
     * Appends template spans (interpolations + trailing text).
     * CC: 3 (null check + loop)
     */
    private void appendTemplateSpans(StringBuilder sb, JsonNode json, ConversionContext context) {
        JsonNode spansNode = json.get("templateSpans");
        if (spansNode == null || !spansNode.isArray()) {
            return;
        }

        ArrayNode spans = (ArrayNode) spansNode;
        for (int i = 0; i < spans.size(); i++) {
            JsonNode span = spans.get(i);
            appendSingleSpan(sb, span, context);
        }
    }

    /**
     * Appends a single template span.
     * CC: 2 (null checks)
     */
    private void appendSingleSpan(StringBuilder sb, JsonNode span, ConversionContext context) {
        // Append interpolation expression
        JsonNode exprNode = span.get("expression");
        String exprStr = (exprNode != null && exprNode.isObject()) ? context.convertExpression(exprNode) : "";
        sb.append("${").append(exprStr).append("}");

        // Append text after interpolation
        JsonNode literalNode = span.get("literal");
        String litText = getLiteralText(literalNode, context);
        sb.append(litText);
    }

    /**
     * Gets literal text from literal object.
     * CC: 2 (null checks)
     */
    private String getLiteralText(JsonNode literal, ConversionContext context) {
        if (literal == null || !literal.isObject()) {
            return "";
        }

        String litText = literal.has("text") ? literal.get("text").asText() : "";
        return context.escapeTemplateLiteral(litText);
    }

    /**
     * Gets kind name safely.
     * CC: 2 (null check + has check)
     */
    private String getKindName(JsonNode json) {
        if (json == null || !json.has("kindName")) {
            return "";
        }
        return json.get("kindName").asText();
    }
}
