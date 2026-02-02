package com.ets2jsc.infrastructure.generator;

import com.ets2jsc.domain.model.ast.*;
import com.ets2jsc.domain.model.config.CompilerConfig;

/**
 * Handles component-related code generation.
 * Extracted from CodeGenerator to reduce class size.
 */
public class ComponentCodeGenerator {

    private final CompilerConfig config;
    private final IndentationManager indentation;
    private final AstVisitor<String> codeGenerator;

    public ComponentCodeGenerator(CompilerConfig config, IndentationManager indentation, AstVisitor<String> codeGenerator) {
        this.config = config;
        this.indentation = indentation;
        this.codeGenerator = codeGenerator;
    }

    /**
     * Generates code for ForEach statement.
     * CC: 2 (if-else)
     */
    public String visitForeach(ForeachStatement node) {
        if (config.isPureJavaScript()) {
            return generateForEachJS(node);
        }
        return generateForEachArkUI(node);
    }

    /**
     * Generates pure JavaScript forEach.
     * CC: 1
     */
    private String generateForEachJS(ForeachStatement node) {
        return indentation.getCurrent() + node.arrayExpression() + ".forEach(" + node.itemGenerator() + ");\n";
    }

    /**
     * Generates ArkUI ForEach with create/pop.
     * CC: 1 (string building)
     */
    private String generateForEachArkUI(ForeachStatement node) {
        StringBuilder sb = new StringBuilder();
        String indent = indentation.getCurrent();

        sb.append(indent).append("ForEach.create();\n");

        sb.append(indent).append("const __itemGenFunction__ = ")
          .append(node.itemGenerator()).append(";\n");

        String keyGen = node.keyGenerator();
        if (keyGen != null && !keyGen.isEmpty()) {
            sb.append(indent).append("const __keyGenFunction__ = ").append(keyGen).append(";\n");
            sb.append(indent).append("ForEach.keyGenerator(__keyGenFunction__);\n");
        }

        sb.append(indent).append("ForEach.itemGenerator(__itemGenFunction__);\n");
        sb.append(indent).append("ForEach.pop();\n");

        return sb.toString();
    }

    /**
     * Generates code for If statement.
     * CC: 2 (if-else)
     */
    public String visitIf(IfStatement node, boolean insideComponentClass) {
        if (config.isPureJavaScript() || !insideComponentClass) {
            return generateStandardIfElse(node);
        }
        return generateArkUIIf(node);
    }

    /**
     * Generates standard JavaScript if-else.
     * CC: 2 (if check + string building)
     */
    private String generateStandardIfElse(IfStatement node) {
        StringBuilder sb = new StringBuilder();
        String indent = indentation.getCurrent();

        sb.append(indent).append("if (").append(node.getCondition()).append(") {\n");
        indentation.increase();
        sb.append(generateBlockBody(node.getThenBlock()));
        indentation.decrease();
        sb.append(indent).append("}\n");

        if (node.hasElse()) {
            sb.append(indent).append("else {\n");
            indentation.increase();
            sb.append(generateBlockBody(node.getElseBlock()));
            indentation.decrease();
            sb.append(indent).append("}\n");
        }

        return sb.toString();
    }

    /**
     * Generates ArkUI if-else with If.create(), branchId(), pop().
     * CC: 1 (string building)
     */
    private String generateArkUIIf(IfStatement node) {
        StringBuilder sb = new StringBuilder();
        String indent = indentation.getCurrent();

        sb.append(indent).append("If.create();\n");
        sb.append(generateIfBranch(node, 0));
        if (node.hasElse()) {
            sb.append(generateElseBranch(node));
        }
        sb.append(indent).append("If.pop();\n");

        return sb.toString();
    }

    /**
     * Generates if branched.
     * CC: 1 (string building)
     */
    private String generateIfBranch(IfStatement node, int branchId) {
        StringBuilder sb = new StringBuilder();
        String indent = indentation.getCurrent();

        sb.append(indent).append("if (").append(node.getCondition()).append(") {\n");
        indentation.increase();
        sb.append(indentation.getCurrent()).append("If.branchId(").append(branchId).append(");\n");
        sb.append(generateBlockBody(node.getThenBlock()));
        indentation.decrease();
        sb.append(indent).append("}\n");

        return sb.toString();
    }

    /**
     * Generates else branch.
     * CC: 1 (string building)
     */
    private String generateElseBranch(IfStatement node) {
        StringBuilder sb = new StringBuilder();
        String indent = indentation.getCurrent();

        sb.append(indent).append("else {\n");
        indentation.increase();
        sb.append(indentation.getCurrent()).append("If.branchId(1);\n");
        sb.append(generateBlockBody(node.getElseBlock()));
        indentation.decrease();
        sb.append(indent).append("}\n");

        return sb.toString();
    }

    /**
     * Generates block body content.
     * CC: 2 (loop + condition check)
     */
    private String generateBlockBody(Block block) {
        StringBuilder sb = new StringBuilder();

        for (AstNode stmt : block.getStatements()) {
            String stmtCode = generateStatement(stmt);
            if (stmtCode != null && !stmtCode.isEmpty()) {
                sb.append(stmtCode);
            }
        }

        return sb.toString();
    }

    /**
     * Generates a single statement.
     * CC: 2 (instance checks)
     */
    private String generateStatement(AstNode stmt) {
        if (stmt instanceof ForeachStatement) {
            return visitForeach((ForeachStatement) stmt);
        } else if (stmt instanceof IfStatement) {
            return visitIf((IfStatement) stmt, true); // Always ArkUI mode in nested blocks
        } else if (stmt instanceof Block) {
            return generateBlockBody((Block) stmt);
        } else {
            // Use the codeGenerator to properly visit the node
            String stmtCode = stmt.accept(codeGenerator);
            if (!stmtCode.endsWith("\n")) {
                stmtCode = stmtCode + "\n";
            }
            return indentation.getCurrent() + stmtCode;
        }
    }

    /**
     * Generates code for ComponentStatement.
     * CC: 1 (method call)
     */
    public String visitComponentStatement(ComponentStatement node) {
        ComponentStatementRenderer renderer = new ComponentStatementRenderer(indentation, codeGenerator);
        return renderer.render(node);
    }

    /**
     * Helper class for rendering component statements.
     */
    private static class ComponentStatementRenderer {
        private final IndentationManager indentation;
        private final AstVisitor<String> codeGenerator;

        ComponentStatementRenderer(IndentationManager indentation, AstVisitor<String> codeGenerator) {
            this.indentation = indentation;
            this.codeGenerator = codeGenerator;
        }

        String render(ComponentStatement node) {
            StringBuilder sb = new StringBuilder();
            String componentName = node.getComponentName();

            for (ComponentStatement.ComponentPart part : node.getParts()) {
                sb.append(renderPart(componentName, node, part));
            }

            return sb.toString();
        }

        String renderPart(String componentName, ComponentStatement node, ComponentStatement.ComponentPart part) {
            return switch (part.kind()) {
                case CREATE -> renderCreatePart(componentName, part.code());
                case METHOD -> renderMethodPart(componentName, part.code());
                case POP -> renderPopPart(componentName, node);
                default -> "";
            };
        }

        String renderCreatePart(String componentName, String code) {
            return indentation.getCurrent() + componentName + ".create(" + code + ");\n";
        }

        String renderMethodPart(String componentName, String code) {
            return indentation.getCurrent() + componentName + "." + code + "\n";
        }

        String renderPopPart(String componentName, ComponentStatement node) {
            StringBuilder sb = new StringBuilder();

            if (node.hasChildren()) {
                indentation.increase();
                sb.append(node.getChildren().accept(codeGenerator));
                indentation.decrease();
            }

            sb.append(indentation.getCurrent()).append(componentName).append(".pop();\n");
            return sb.toString();
        }
    }
}
