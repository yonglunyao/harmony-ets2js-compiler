package com.ets2jsc.generator;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Helper class for transforming @Builder method calls.
 * Extracts Builder method transformation logic from CodeGenerator.
 */
public final class BuilderMethodTransformer {

    private static final String BUILDER_VAR_NAME = "__builder__";
    private static final String BUILDER_PARAM_TYPE = "BuilderParam";

    private BuilderMethodTransformer() {
        // Prevent instantiation
    }

    /**
     * Transforms @Builder method call to BuilderParam pattern.
     * Converts: this.customText("Hello", "World")
     * To: const __builder__ = new BuilderParam();
     *     this.customText(__builder__, "Hello", "World");
     *     __builder__.build();
     * CC: 3 (loop + early return + method call)
     */
    public static String transform(String expr, List<String> builderMethods) {
        String trimmed = expr.trim();

        for (String builderMethod : builderMethods) {
            if (isBuilderMethodCall(trimmed, builderMethod)) {
                return transformBuilderCall(trimmed, builderMethod);
            }
        }

        return expr;
    }

    /**
     * Checks if expression is a builder method call.
     * CC: 1 (regex match)
     */
    private static boolean isBuilderMethodCall(String expr, String methodName) {
        String pattern = "this\\." + Pattern.quote(methodName) + "\\s*\\(";
        return expr.matches(pattern + ".*");
    }

    /**
     * Transforms a single builder method call.
     * CC: 1 (method calls)
     */
    private static String transformBuilderCall(String expr, String methodName) {
        String args = extractArguments(expr);
        String innerArgs = extractInnerArguments(args);

        return buildTransformedCode(methodName, innerArgs);
    }

    /**
     * Extracts arguments string from expression.
     * CC: 1
     */
    private static String extractArguments(String expr) {
        int argsStart = expr.indexOf('(');
        return expr.substring(argsStart); // Includes "(" and ")"
    }

    /**
     * Extracts inner arguments (without parentheses).
     * CC: 3 (length check + substring calls)
     */
    private static String extractInnerArguments(String args) {
        if (args.length() <= 1) {
            return "";
        }

        String innerArgs = args.substring(1); // Remove "("
        if (innerArgs.endsWith(")")) {
            innerArgs = innerArgs.substring(0, innerArgs.length() - 1); // Remove ")"
        }

        return innerArgs.trim();
    }

    /**
     * Builds transformed code with BuilderParam pattern.
     * CC: 2 (ternary + string building)
     */
    private static String buildTransformedCode(String methodName, String innerArgs) {
        StringBuilder sb = new StringBuilder();

        sb.append("const ").append(BUILDER_VAR_NAME).append(" = new ")
          .append(BUILDER_PARAM_TYPE).append("();\n");

        sb.append("this.").append(methodName).append("(").append(BUILDER_VAR_NAME);

        if (!innerArgs.isEmpty()) {
            sb.append(", ").append(innerArgs);
        }

        sb.append(");\n");
        sb.append(BUILDER_VAR_NAME).append(".build();");

        return sb.toString();
    }
}
