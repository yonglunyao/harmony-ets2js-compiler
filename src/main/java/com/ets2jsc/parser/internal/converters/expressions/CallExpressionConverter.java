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

    // Resource type IDs
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

    @Override
    public boolean canConvert(String kindName) {
        return "CallExpression".equals(kindName) || "ResourceReferenceExpression".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        // Check if this is a resource reference call
        String kindName = json.has("kindName") ? json.get("kindName").getAsString() : "";
        if ("ResourceReferenceExpression".equals(kindName)) {
            return convertResourceReference(json);
        }

        JsonObject expression = json.getAsJsonObject("expression");
        String base = context.convertExpression(expression);

        // Check for dynamic import pattern: import('module')
        if ("import".equals(base)) {
            JsonArray argsArray = json.getAsJsonArray("arguments");
            if (argsArray != null && argsArray.size() > 0) {
                JsonElement argElement = argsArray.get(0);
                String modulePath = "";
                if (argElement.isJsonObject()) {
                    modulePath = context.convertExpression(argElement.getAsJsonObject());
                } else if (argElement.isJsonPrimitive() && argElement.getAsJsonPrimitive().isString()) {
                    modulePath = argElement.getAsString();
                }
                return "import(" + modulePath + ")";
            }
        }

        JsonArray argsArray = json.getAsJsonArray("arguments");
        StringBuilder args = new StringBuilder();
        if (argsArray != null) {
            List<String> argStrings = new ArrayList<>();
            for (JsonElement argElement : argsArray) {
                String arg = "";
                if (argElement.isJsonObject()) {
                    arg = context.convertExpression(argElement.getAsJsonObject());
                } else if (argElement.isJsonPrimitive() && argElement.getAsJsonPrimitive().isString()) {
                    arg = argElement.getAsString();
                } else if (argElement.isJsonNull()) {
                    arg = "null";
                }
                argStrings.add(arg != null ? arg.trim() : "");
            }
            args.append(String.join(", ", argStrings));
        }

        return base + "(" + args + ")";
    }

    /**
     * Convert resource reference call ($r or $rawfile) to runtime function call.
     */
    private String convertResourceReference(JsonObject json) {
        String resourceRefType = json.has("resourceRefType") ? json.get("resourceRefType").getAsString() : "";
        JsonArray argsArray = json.getAsJsonArray("arguments");

        if ("rawfile".equals(resourceRefType)) {
            // Convert $rawfile('icon.png') to __getRawFileId__('icon.png')
            if (argsArray != null && argsArray.size() > 0) {
                String filename = argsArray.get(0).getAsString();
                // Remove surrounding quotes if present
                filename = filename.replaceAll("^['\"]|['\"]$", "");
                return "__getRawFileId__(\"" + filename + "\")";
            }
            return "__getRawFileId__(\"\")";
        } else if ("r".equals(resourceRefType)) {
            // Convert $r('app.string.name') to __getResourceId__(type, bundle, module, name)
            if (argsArray != null && argsArray.size() > 0) {
                String resourcePath = argsArray.get(0).getAsString();
                // Remove surrounding quotes if present
                resourcePath = resourcePath.replaceAll("^['\"]|['\"]$", "");
                // Parse format: 'app.type.name' or 'module.type.name'
                String[] parts = resourcePath.split("\\.");
                if (parts.length >= 3) {
                    String module = parts[0];
                    String type = parts[parts.length - 2];
                    String name = parts[parts.length - 1];
                    int typeId = getResourceTypeId(type);
                    return "__getResourceId__(" + typeId + ", undefined, \"" + module + "\", \"" + name + "\")";
                }
            }
            return "__getResourceId__(10003, undefined, \"\", \"\")";
        }

        return "";
    }

    /**
     * Get resource type ID from type name.
     */
    private int getResourceTypeId(String typeName) {
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
