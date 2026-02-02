package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import com.ets2jsc.ast.ComponentStatement.ComponentPart;
import com.ets2jsc.ast.ComponentStatement.PartKind;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Transforms component expressions to create/pop pattern.
 * Parses expression strings and converts them to ComponentStatement nodes.
 */
public class ComponentExpressionTransformer {

    private static final Pattern COMPONENT_CALL_PATTERN = Pattern.compile("([A-Z][a-zA-Z0-9]*)\\s*\\(([^)]*)\\)");
    private static final Pattern SIMPLE_CHAINED_CALL_PATTERN = Pattern.compile("\\.\\s*([a-zA-Z][a-zA-Z0-9]*)\\s*\\(([^)(]*)\\)");

    /**
     * Transforms an expression string to a ComponentStatement if it's a component call.
     * Only transforms simple cases with literal arguments.
     *
     * @param expression the expression string (e.g., "Text('Hello').fontSize(16)")
     * @return ComponentStatement if the expression is a component call, null otherwise
     */
    public static AstNode transform(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return null;
        }

        String trimmed = expression.trim();

        // Check if this is a component call (starts with capital letter component name)
        if (!isComponentCall(trimmed)) {
            return null;
        }

        // Only transform simple cases - check for complex expressions
        if (hasComplexExpressions(trimmed)) {
            return null; // Skip complex expressions like this.message
        }

        // Parse the component call
        return parseComponentCall(trimmed);
    }

    /**
     * Checks if an expression contains complex expressions that should not be transformed.
     */
    private static boolean hasComplexExpressions(String expression) {
        // Check for complex patterns that we shouldn't transform
        // - this.property access
        // - function calls in arguments
        // - nested object expressions
        return expression.contains("this.") ||
               expression.contains("$r(") ||
               expression.contains("$rawfile(") ||
               expression.contains("=>"); // arrow function (onClick callback)
    }

    /**
     * Checks if an expression is a component call.
     * Component calls start with a capital letter (e.g., Text(...), Column(...))
     */
    private static boolean isComponentCall(String expression) {
        // Remove any leading whitespace
        expression = expression.trim();

        // Check if it starts with a capital letter followed by '('
        // Pattern: ComponentName(...) or ComponentName   (...)
        return expression.matches("^[A-Z][a-zA-Z0-9]*\\s*\\(.*");
    }

    /**
     * Parses a component call expression.
     * Examples:
     * - Text('Hello') -> simple component
     * - Text('Hello').fontSize(16) -> component with one chained call
     * - Text('Hello').fontSize(16).fontColor('red') -> component with multiple chained calls
     */
    private static AstNode parseComponentCall(String expression) {
        // Find the component name and its arguments
        Matcher componentMatcher = COMPONENT_CALL_PATTERN.matcher(expression);
        if (!componentMatcher.find()) {
            return null;
        }

        String componentName = componentMatcher.group(1);
        String componentArgs = componentMatcher.group(2);
        int componentEnd = componentMatcher.end();

        // Check if this is a built-in component
        if (!ComponentRegistry.isBuiltinComponent(componentName)) {
            return null;
        }

        ComponentStatement statement = new ComponentStatement(componentName);

        // Add create part
        statement.addPart(new ComponentPart(PartKind.CREATE, componentArgs));

        // Parse chained calls
        String remaining = expression.substring(componentEnd);
        Matcher chainedMatcher = SIMPLE_CHAINED_CALL_PATTERN.matcher(remaining);

        while (chainedMatcher.find()) {
            String methodName = chainedMatcher.group(1);
            String methodArgs = chainedMatcher.group(2);

            // Add method part
            statement.addPart(new ComponentPart(PartKind.METHOD, methodName + "(" + methodArgs + ")"));

            // Move to next chained call
            remaining = remaining.substring(chainedMatcher.end());
            chainedMatcher.reset(remaining);
        }

        // Add pop part
        statement.addPart(new ComponentPart(PartKind.POP, ""));

        return statement;
    }
}
