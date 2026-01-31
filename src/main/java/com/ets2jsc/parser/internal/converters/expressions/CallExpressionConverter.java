package com.ets2jsc.parser.internal.converters.expressions;

import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    public Object convert(JsonObject json, ConversionContext context) {
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
    private String convertCallExpression(JsonObject json, ConversionContext context) {
        JsonObject expression = json.getAsJsonObject("expression");
        if (expression == null) {
            return "";
        }

        String base = context.convertExpression(expression);

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
    private String convertDynamicImport(JsonObject json) {
        JsonArray argsArray = json.getAsJsonArray("arguments");
        if (argsArray == null || argsArray.size() == 0) {
            return "import()";
        }

        String modulePath = extractArgumentString(argsArray.get(0));
        return "import(" + modulePath + ")";
    }

    /**
     * Converts all arguments to a comma-separated string.
     * CC: 3 (null check + loop + ternary)
     */
    private String convertArguments(JsonObject json, ConversionContext context) {
        JsonArray argsArray = json.getAsJsonArray("arguments");
        if (argsArray == null) {
            return "";
        }

        List<String> argStrings = new ArrayList<>();
        for (JsonElement argElement : argsArray) {
            String arg = convertSingleArgument(argElement, context);
            argStrings.add(arg != null ? arg.trim() : "");
        }

        return String.join(", ", argStrings);
    }

    /**
     * Converts a single argument element to string.
     * CC: 3 (instance checks)
     */
    private String convertSingleArgument(JsonElement argElement, ConversionContext context) {
        if (argElement.isJsonObject()) {
            return context.convertExpression(argElement.getAsJsonObject());
        }

        if (argElement.isJsonPrimitive() && argElement.getAsJsonPrimitive().isString()) {
            return argElement.getAsString();
        }

        if (argElement.isJsonNull()) {
            return "null";
        }

        return "";
    }

    /**
     * Extracts string value from argument element.
     * CC: 2 (instance checks)
     */
    private String extractArgumentString(JsonElement argElement) {
        if (argElement.isJsonObject()) {
            // For complex objects, try to get text content
            JsonObject obj = argElement.getAsJsonObject();
            if (obj.has("text")) {
                return obj.get("text").getAsString();
            }
        }

        if (argElement.isJsonPrimitive() && argElement.getAsJsonPrimitive().isString()) {
            return argElement.getAsString();
        }

        return "";
    }

    /**
     * Convert resource reference call ($r or $rawfile) to runtime function call.
     * CC: 3 (if-else for type check)
     */
    private String convertResourceReference(JsonObject json) {
        String resourceRefType = json.has("resourceRefType") ? json.get("resourceRefType").getAsString() : "";
        JsonArray argsArray = json.getAsJsonArray("arguments");

        if ("rawfile".equals(resourceRefType)) {
            return convertRawfileReference(argsArray);
        }

        if ("r".equals(resourceRefType)) {
            return convertResourceReference(argsArray);
        }

        return "";
    }

    /**
     * Converts $rawfile() reference.
     * CC: 2 (null check)
     */
    private String convertRawfileReference(JsonArray argsArray) {
        if (argsArray != null && argsArray.size() > 0) {
            String filename = extractAndCleanArgument(argsArray.get(0));
            return "__getRawFileId__(\"" + filename + "\")";
        }
        return "__getRawFileId__(\"\")";
    }

    /**
     * Converts $r() resource reference.
     * CC: 3 (null check + parse check + else)
     */
    private String convertResourceReference(JsonArray argsArray) {
        if (argsArray == null || argsArray.size() == 0) {
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
    private String extractAndCleanArgument(JsonElement argElement) {
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
     * Safely extracts kindName from JsonObject.
     * CC: 2 (null check + has check)
     */
    private String getKindName(JsonObject json) {
        if (json == null || !json.has("kindName")) {
            return "";
        }
        return json.get("kindName").getAsString();
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
