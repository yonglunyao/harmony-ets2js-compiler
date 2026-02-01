package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for call expressions.
 * Handles: functionCall(), methodCall(), import('module'), $r(), $rawfile()
 */
public class CallExpressionConverter implements NodeConverter {

    private static final String IMPORT_KEYWORD = "import";
    private static final String RESOURCE_REF_EXPRESSION = "ResourceReferenceExpression";
    private static final String CALL_EXPRESSION = "CallExpression";

    @Override
    public boolean canConvert(String kindName) {
        return CALL_EXPRESSION.equals(kindName) || RESOURCE_REF_EXPRESSION.equals(kindName);
    }

    @Override
    public Object convert(JsonNode json, ConversionContext context) {
        String kindName = getKindName(json);

        if (RESOURCE_REF_EXPRESSION.equals(kindName)) {
            return convertResourceReference(json);
        }

        return convertCallExpression(json, context);
    }

    /**
     * Converts a standard call expression.
     * CC: 3 (null check + import check + else)
     */
    private String convertCallExpression(JsonNode json, ConversionContext context) {
        JsonNode expressionNode = json.get("expression");
        if (expressionNode == null || !expressionNode.isObject()) {
            return "";
        }

        String base = context.convertExpression(expressionNode);

        // Check for dynamic import pattern: import('module')
        if (IMPORT_KEYWORD.equals(base)) {
            return convertDynamicImport(json);
        }

        // Regular function/method call
        String args = convertArguments(json, context);
        return base + "(" + args + ")";
    }

    /**
     * Converts dynamic import expression.
     * CC: 2 (null check + else)
     */
    private String convertDynamicImport(JsonNode json) {
        JsonNode argsArrayNode = json.get("arguments");
        if (argsArrayNode == null || !argsArrayNode.isArray()) {
            return "import()";
        }

        ArrayNode argsArray = (ArrayNode) argsArrayNode;
        if (argsArray.size() == 0) {
            return "import()";
        }

        String modulePath = extractArgumentString(argsArray.get(0));
        return "import(" + modulePath + ")";
    }

    /**
     * Converts all arguments to a comma-separated string.
     * CC: 3 (null check + loop + ternary)
     */
    private String convertArguments(JsonNode json, ConversionContext context) {
        JsonNode argsArrayNode = json.get("arguments");
        if (argsArrayNode == null || !argsArrayNode.isArray()) {
            return "";
        }

        ArrayNode argsArray = (ArrayNode) argsArrayNode;
        List<String> argStrings = new ArrayList<>();
        for (JsonNode argElement : argsArray) {
            String arg = convertSingleArgument(argElement, context);
            argStrings.add(arg != null ? arg.trim() : "");
        }

        return String.join(", ", argStrings);
    }

    /**
     * Converts a single argument element to string.
     * CC: 3 (instance checks)
     */
    private String convertSingleArgument(JsonNode argElement, ConversionContext context) {
        if (argElement.isObject()) {
            return context.convertExpression(argElement);
        }

        if (argElement.isTextual()) {
            return argElement.asText();
        }

        if (argElement.isNull()) {
            return "null";
        }

        return "";
    }

    /**
     * Extracts string value from argument element.
     * CC: 2 (instance checks)
     */
    private String extractArgumentString(JsonNode argElement) {
        if (argElement.isObject()) {
            // For complex objects, try to get text content
            if (argElement.has("text")) {
                return argElement.get("text").asText();
            }
        }

        if (argElement.isTextual()) {
            return argElement.asText();
        }

        return "";
    }

    /**
     * Convert resource reference call ($r or $rawfile) to runtime function call.
     * CC: 3 (if-else for type check)
     */
    private String convertResourceReference(JsonNode json) {
        String resourceRefType = json.has("resourceRefType") ? json.get("resourceRefType").asText() : "";
        JsonNode argsArrayNode = json.get("arguments");

        if ("rawfile".equals(resourceRefType)) {
            return convertRawfileReference(argsArrayNode);
        }

        if ("r".equals(resourceRefType)) {
            return convertRResourceReference(argsArrayNode);
        }

        return "";
    }

    /**
     * Converts $rawfile() reference.
     * CC: 2 (null check)
     */
    private String convertRawfileReference(JsonNode argsArrayNode) {
        if (argsArrayNode != null && argsArrayNode.isArray() && argsArrayNode.size() > 0) {
            ArrayNode argsArray = (ArrayNode) argsArrayNode;
            String filename = extractAndCleanArgument(argsArray.get(0));
            return "__getRawFileId__(\"" + filename + "\")";
        }
        return "__getRawFileId__(\"\")";
    }

    /**
     * Converts $r() resource reference.
     * CC: 3 (null check + parse check + else)
     */
    private String convertRResourceReference(JsonNode argsArrayNode) {
        if (argsArrayNode == null || !argsArrayNode.isArray()) {
            return "__getResourceId__(10003, undefined, \"\", \"\")";
        }

        ArrayNode argsArray = (ArrayNode) argsArrayNode;
        if (argsArray.size() == 0) {
            return "__getResourceId__(10003, undefined, \"\", \"\")";
        }

        String resourcePath = extractAndCleanArgument(argsArray.get(0));
        ResourcePath path = parseResourcePath(resourcePath);

        if (path.isValid()) {
            int typeId = ResourceTypeIdMapper.getTypeId(path.type);
            return "__getResourceId__(" + typeId + ", undefined, \"" + path.module + "\", \"" + path.name + "\")";
        }

        return "__getResourceId__(10003, undefined, \"\", \"\")";
    }

    /**
     * Extracts and cleans argument string (removes quotes).
     * CC: 1
     */
    private String extractAndCleanArgument(JsonNode argElement) {
        String value = extractArgumentString(argElement);
        return value.replaceAll("^['\"]|['\"]$", "");
    }

    /**
     * Parses resource path into components.
     * CC: 2 (length check)
     */
    private ResourcePath parseResourcePath(String resourcePath) {
        String[] parts = resourcePath.split("\\.");
        if (parts.length >= 3) {
            String module = parts[0];
            String type = parts[parts.length - 2];
            String name = parts[parts.length - 1];
            return new ResourcePath(module, type, name);
        }
        return new ResourcePath("", "", "");
    }

    /**
     * Safely extracts kindName from JsonNode.
     * CC: 2 (null check + has check)
     */
    private String getKindName(JsonNode json) {
        if (json == null || !json.has("kindName")) {
            return "";
        }
        return json.get("kindName").asText();
    }

    /**
     * Helper class to represent parsed resource path.
     */
    private static class ResourcePath {
        final String module;
        final String type;
        final String name;

        ResourcePath(String module, String type, String name) {
            this.module = module;
            this.type = type;
            this.name = name;
        }

        boolean isValid() {
            return !module.isEmpty() && !type.isEmpty() && !name.isEmpty();
        }
    }

    /**
     * Mapper for resource type IDs.
     * CC: 1 (switch with default)
     */
    private static class ResourceTypeIdMapper {
        private static final int COLOR = 10001;
        private static final int FLOAT = 10002;
        private static final int STRING = 10003;
        private static final int PLURAL = 10004;
        private static final int BOOLEAN = 10005;
        private static final int INTARRAY = 10006;
        private static final int INTEGER = 10007;
        private static final int PATTERN = 10008;
        private static final int STRARRAY = 10009;
        private static final int MEDIA = 10010;
        private static final int FONT = 10011;
        private static final int PROFILE = 10012;

        static int getTypeId(String typeName) {
            switch (typeName) {
                case "color": return COLOR;
                case "float": return FLOAT;
                case "string": return STRING;
                case "plural": return PLURAL;
                case "boolean": return BOOLEAN;
                case "intarray": return INTARRAY;
                case "integer": return INTEGER;
                case "pattern": return PATTERN;
                case "strarray": return STRARRAY;
                case "media": return MEDIA;
                case "font": return FONT;
                case "profile": return PROFILE;
                default: return STRING;
            }
        }
    }
}
