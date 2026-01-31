package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import com.ets2jsc.constant.Components;
import com.ets2jsc.constant.Decorators;
import com.ets2jsc.constant.RuntimeFunctions;
import com.ets2jsc.constant.Symbols;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms build() methods to render()/initialRender() methods.
 * Handles the conversion of UI component declarations to create/pop pattern.
 */
public class BuildMethodTransformer implements AstTransformer {

    private final boolean partialUpdateMode;
    private final ComponentTransformer componentTransformer;

    public BuildMethodTransformer(boolean partialUpdateMode) {
        this.partialUpdateMode = partialUpdateMode;
        this.componentTransformer = new ComponentTransformer();
    }

    @Override
    public AstNode transform(AstNode node) {
        if (node instanceof ClassDeclaration) {
            return transformClassDeclaration((ClassDeclaration) node);
        } else if (node instanceof MethodDeclaration) {
            return transformMethodDeclaration((MethodDeclaration) node);
        }
        return node;
    }

    @Override
    public boolean canTransform(AstNode node) {
        if (node instanceof ClassDeclaration) {
            return ((ClassDeclaration) node).hasDecorator(Decorators.COMPONENT);
        }
        if (node instanceof MethodDeclaration) {
            MethodDeclaration method = (MethodDeclaration) node;
            return method.isBuildMethod() || method.isBuilderMethod();
        }
        return false;
    }

    /**
     * Transforms a class declaration, converting build() to render()/initialRender()
     * and transforming @Builder methods.
     */
    private ClassDeclaration transformClassDeclaration(ClassDeclaration classDecl) {
        List<MethodDeclaration> methods = classDecl.getMethods();

        for (MethodDeclaration method : methods) {
            if (method.isBuildMethod()) {
                transformBuildMethod(classDecl, method);
            } else if (method.isBuilderMethod()) {
                transformBuilderMethod(classDecl, method);
            }
        }

        return classDecl;
    }

    /**
     * Transforms a build() method to render() or initialRender().
     */
    private MethodDeclaration transformMethodDeclaration(MethodDeclaration method) {
        if (!method.isBuildMethod()) {
            return method;
        }

        // Rename method
        String newName = partialUpdateMode ? "initialRender" : "render";
        method.setName(newName);

        // Transform method body (component expressions)
        AstNode body = method.getBody();
        if (body != null) {
            // Transform component expressions in body
            AstNode transformedBody = transformMethodBody(body);
            method.setBody(transformedBody);
        }

        return method;
    }

    /**
     * Transforms the build() method.
     */
    private void transformBuildMethod(ClassDeclaration classDecl, MethodDeclaration buildMethod) {
        // Rename method
        String newName = partialUpdateMode ? "initialRender" : "render";
        buildMethod.setName(newName);

        // Process method body to transform component expressions
        AstNode body = buildMethod.getBody();
        if (body != null) {
            AstNode transformedBody = transformMethodBody(body);
            buildMethod.setBody(transformedBody);
        }
    }

    /**
     * Transforms a method body, converting component expressions.
     */
    private AstNode transformMethodBody(AstNode body) {
        List<String> statements = new ArrayList<>();

        if (body instanceof ExpressionStatement) {
            ExpressionStatement exprStmt = (ExpressionStatement) body;
            String expression = exprStmt.getExpression();

            // Transform component expressions
            String transformed = transformComponentExpressions(expression);
            return new ExpressionStatement(transformed);
        }

        return body;
    }

    /**
     * Transforms component expressions in the method body.
     * Converts declarative UI to create/pop pattern.
     */
    private String transformComponentExpressions(String body) {
        StringBuilder result = new StringBuilder();

        // Simple transformation for basic patterns
        // In production, this would use proper AST traversal

        // Transform Text('Hello') -> Text.create('Hello'); Text.pop();
        String lines[] = body.split("\\n");
        for (String line : lines) {
            String transformed = transformLine(line);
            result.append(transformed).append("\n");
        }

        return result.toString();
    }

    /**
     * Transforms a single line of code.
     */
    private String transformLine(String line) {
        line = line.trim();

        // Skip empty lines
        if (line.isEmpty()) {
            return "";
        }

        // Check for component creation
        for (String component : Components.ALL_COMPONENTS) {
            // Pattern: ComponentName(...)
            if (line.startsWith(component + "(")) {
                return transformComponentCreation(component, line);
            }
        }

        return line;
    }

    /**
     * Transforms a component creation expression.
     */
    private String transformComponentCreation(String componentName, String line) {
        StringBuilder result = new StringBuilder();

        // Create component
        result.append(componentName).append(".create(");

        // Extract arguments
        int argStart = line.indexOf('(');
        int argEnd = line.lastIndexOf(')');
        if (argEnd > argStart) {
            result.append(line, argStart + 1, argEnd);
        }

        result.append(");");

        // Pop component
        result.append("\n").append(componentName).append(".pop();");

        return result.toString();
    }

    /**
     * Returns the render method name based on compilation mode.
     */
    public String getRenderMethodName() {
        return partialUpdateMode ? "initialRender" : "render";
    }

    /**
     * Transforms a @Builder method.
     * Adds __builder__ parameter and converts component expressions to create/pop pattern.
     */
    private void transformBuilderMethod(ClassDeclaration classDecl, MethodDeclaration builderMethod) {
        // Add __builder__ parameter as first parameter
        MethodDeclaration.Parameter builderParam = new MethodDeclaration.Parameter(
            Symbols.BUILDER_PARAM_NAME,
            RuntimeFunctions.BUILDER_PARAM
        );
        builderParam.setHasDefault(true);
        builderParam.setDefaultValue("undefined");
        builderMethod.getParameters().add(0, builderParam);

        // Transform method body (component expressions)
        AstNode body = builderMethod.getBody();
        if (body != null) {
            AstNode transformedBody = transformMethodBody(body);
            builderMethod.setBody(transformedBody);
        }
    }
}
