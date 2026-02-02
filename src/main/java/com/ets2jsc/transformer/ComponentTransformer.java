package com.ets2jsc.transformer;

import com.ets2jsc.domain.model.ast.*;
import com.ets2jsc.shared.constant.Components;
import com.ets2jsc.shared.constant.RuntimeFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms UI component expressions to create/pop pattern.
 * Handles both built-in and custom components.
 */
public class ComponentTransformer implements AstTransformer {

    @Override
    public AstNode transform(AstNode node) {
        if (node instanceof ComponentExpression) {
            return transformComponentExpression((ComponentExpression) node);
        } else if (node instanceof CallExpression) {
            return transformCallExpression((CallExpression) node);
        }
        return node;
    }

    @Override
    public boolean canTransform(AstNode node) {
        if (node instanceof ComponentExpression) {
            ComponentExpression expr = (ComponentExpression) node;
            return Components.isBuiltinComponent(expr.getComponentName());
        }
        return false;
    }

    /**
     * Transforms a component expression to create/pop pattern.
     * e.g., Text('Hello') -> Text.create('Hello'); Text.pop();
     */
    private ComponentExpression transformComponentExpression(ComponentExpression expr) {
        String componentName = expr.getComponentName();

        // Check if it's a built-in component
        if (Components.isBuiltinComponent(componentName)) {
            // Transform to create/pop pattern
            String createStatement = generateCreateStatement(expr);
            String popStatement = generatePopStatement(componentName);

            // In real implementation, return list of statements
            return expr;
        }

        return expr;
    }

    /**
     * Transforms a call expression.
     */
    private CallExpression transformCallExpression(CallExpression expr) {
        if (expr.isComponentCall()) {
            // Transform component call
            String functionName = expr.getFunctionName();
            if (Components.isBuiltinComponent(functionName)) {
                // Transform to create/pop pattern
                return expr;
            }
        }
        return expr;
    }

    /**
     * Generates the create statement for a component.
     * e.g., Text.create('Hello')
     */
    public String generateCreateStatement(ComponentExpression expr) {
        StringBuilder sb = new StringBuilder();
        String componentName = expr.getComponentName();

        sb.append(componentName).append(".").append(RuntimeFunctions.COMPONENT_CREATE).append("(");

        // Add arguments
        List<AstNode> args = expr.getArguments();
        if (!args.isEmpty()) {
            for (int i = 0; i < args.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(args.get(i));
            }
        }

        sb.append(");");

        return sb.toString();
    }

    /**
     * Generates the pop statement for a component.
     * e.g., Text.pop();
     */
    public String generatePopStatement(String componentName) {
        return componentName + "." + RuntimeFunctions.COMPONENT_POP + "();";
    }

    /**
     * Generates attribute statements for a component.
     * e.g., Text.fontSize(16);
     */
    public List<String> generateAttributeStatements(ComponentExpression expr) {
        List<String> statements = new ArrayList<>();
        String componentName = expr.getComponentName();

        for (ComponentExpression.MethodCall call : expr.getChainedCalls()) {
            StringBuilder sb = new StringBuilder();
            sb.append(componentName).append(".").append(call.getMethodName()).append("(");

            List<AstNode> args = call.getArguments();
            for (int i = 0; i < args.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(args.get(i));
            }

            sb.append(");");
            statements.add(sb.toString());
        }

        return statements;
    }

    /**
     * Generates child component statements.
     */
    public List<String> generateChildStatements(ComponentExpression expr) {
        List<String> statements = new ArrayList<>();

        for (AstNode child : expr.getChildren()) {
            if (child instanceof ComponentExpression) {
                ComponentExpression childExpr = (ComponentExpression) child;
                statements.add(generateCreateStatement(childExpr));
                statements.addAll(generateAttributeStatements(childExpr));
                statements.add(generatePopStatement(childExpr.getComponentName()));
            }
        }

        return statements;
    }
}
