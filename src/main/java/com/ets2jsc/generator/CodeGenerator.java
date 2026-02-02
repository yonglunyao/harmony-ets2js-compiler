package com.ets2jsc.generator;

import com.ets2jsc.domain.model.ast.*;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.constant.Symbols;
import com.ets2jsc.transformer.ComponentExpressionTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates JavaScript code from transformed AST.
 * Uses helper classes to manage complexity and maintain low cyclomatic complexity.
 */
public class CodeGenerator implements AstVisitor<String> {

    private final StringBuilder output;
    private final IndentationManager indentation;
    private final CompilerConfig config;
    private boolean insideComponentClass = false;
    private List<String> currentBuilderMethods = new ArrayList<>();

    private final ComponentCodeGenerator componentCodeGenerator;

    public CodeGenerator(CompilerConfig config) {
        this.config = config != null ? config : new CompilerConfig();
        this.output = new StringBuilder();
        this.indentation = new IndentationManager();
        this.insideComponentClass = false;
        this.componentCodeGenerator = new ComponentCodeGenerator(this.config, this.indentation, this);
    }

    public CodeGenerator() {
        this(null);
    }

    /**
     * Generates JavaScript code from an AST node.
     * CC: 2 (null check + method call)
     */
    public String generate(AstNode node) {
        if (node == null) {
            throw new IllegalArgumentException("AST node cannot be null");
        }
        return node.accept(this);
    }

    /**
     * Generates JavaScript code for a source file.
     * CC: 3 (method calls + loop)
     */
    public String generate(SourceFile sourceFile) {
        output.setLength(0);
        indentation.reset();

        generateImportStatements(sourceFile);
        generateOtherStatements(sourceFile);

        return output.toString();
    }

    /**
     * Generates import statements.
     * CC: 2 (loop + method call)
     */
    private void generateImportStatements(SourceFile sourceFile) {
        for (AstNode statement : sourceFile.getStatements()) {
            if (statement instanceof ImportStatement) {
                String code = statement.accept(this);
                if (!code.isEmpty()) {
                    writeLine(code);
                }
            }
        }

        if (hasImportStatements(sourceFile)) {
            writeLine("");
        }
    }

    /**
     * Generates non-import statements.
     * CC: 2 (loop + condition check)
     */
    private void generateOtherStatements(SourceFile sourceFile) {
        for (AstNode statement : sourceFile.getStatements()) {
            if (!(statement instanceof ImportStatement)) {
                String code = statement.accept(this);
                if (!code.trim().isEmpty()) {
                    writeLine(code);
                }
            }
        }
    }

    /**
     * Checks if source file has import statements.
     * CC: 2 (loop + condition check)
     */
    private boolean hasImportStatements(SourceFile sourceFile) {
        for (AstNode statement : sourceFile.getStatements()) {
            if (statement instanceof ImportStatement) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String visit(SourceFile node) {
        StringBuilder sb = new StringBuilder();
        for (AstNode statement : node.getStatements()) {
            sb.append(statement.accept(this)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Visits ClassDeclaration node.
     * CC: 3 (save/restore + loop)
     */
    @Override
    public String visit(ClassDeclaration node) {
        ClassGenerationContext context = saveAndSetClassContext(node);
        StringBuilder sb = new StringBuilder();

        sb.append(generateClassDeclaration(node));
        sb.append(generateClassMembers(node));

        restoreClassContext(context);
        return sb.toString();
    }

    /**
     * Saves current class context and sets new one.
     * CC: 1
     */
    private ClassGenerationContext saveAndSetClassContext(ClassDeclaration node) {
        ClassGenerationContext context = new ClassGenerationContext(insideComponentClass, currentBuilderMethods);
        insideComponentClass = node.hasDecorator("Component");
        currentBuilderMethods = node.getBuilderMethodNames();
        return context;
    }

    /**
     * Restores previous class context.
     * CC: 1
     */
    private void restoreClassContext(ClassGenerationContext context) {
        insideComponentClass = context.wasInsideComponentClass();
        currentBuilderMethods = context.getBuilderMethods();
    }

    /**
     * Generates class declaration line.
     * CC: 2 (if check + string building)
     */
    private String generateClassDeclaration(ClassDeclaration node) {
        StringBuilder sb = new StringBuilder();

        // Generate decorator comments
        for (Decorator decorator : node.getDecorators()) {
            if (!decorator.getName().equals("Component")) {
                sb.append("// @").append(decorator.getName()).append("\n");
            }
        }

        // Export declaration
        sb.append(generateExportDeclaration(node));

        // Class declaration
        sb.append("class ").append(node.getName());

        if (node.getSuperClass() != null) {
            sb.append(" extends ").append(node.getSuperClass());
        }

        sb.append(" {\n");
        indentation.increase();

        return sb.toString();
    }

    /**
     * Generates export declaration.
     * CC: 2 (if checks)
     */
    private String generateExportDeclaration(ClassDeclaration node) {
        StringBuilder sb = new StringBuilder();

        boolean isEntry = node.hasDecorator("Entry");
        if (isEntry && node.isExport()) {
            sb.append("export default ");
        } else if (node.isExport()) {
            sb.append("export ");
        }

        return sb.toString();
    }

    /**
     * Generates class members.
     * CC: 2 (loop + condition check)
     */
    private String generateClassMembers(ClassDeclaration node) {
        StringBuilder sb = new StringBuilder();

        for (AstNode member : node.getMembers()) {
            String memberCode = member.accept(this);
            if (!memberCode.isEmpty()) {
                sb.append(memberCode).append("\n");
            }
        }

        indentation.decrease();
        sb.append(indentation.getCurrent()).append("}\n");

        return sb.toString();
    }

    @Override
    public String visit(MethodDeclaration node) {
        MethodGenerator generator = new MethodGenerator(node, indentation, this);
        return generator.generate();
    }

    @Override
    public String visit(PropertyDeclaration node) {
        PropertyGenerator generator = new PropertyGenerator(node, indentation);
        return generator.generate();
    }

    @Override
    public String visit(Decorator node) {
        return "";
    }

    @Override
    public String visit(ComponentExpression node) {
        return "";
    }

    @Override
    public String visit(CallExpression node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getFunctionName()).append("(");

        List<AstNode> args = node.getArguments();
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(args.get(i).accept(this));
        }

        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visit(ExpressionStatement node) {
        String expr = node.getExpression();
        if (expr == null || expr.isEmpty()) {
            return "";
        }

        // Check for @Builder method call
        String transformed = BuilderMethodTransformer.transform(expr, currentBuilderMethods);
        if (!transformed.equals(expr)) {
            return transformed;
        }

        // Try to transform to component statement
        AstNode transformedNode = ComponentExpressionTransformer.transform(expr);
        if (transformedNode instanceof ComponentStatement) {
            return transformedNode.accept(this);
        }

        // Format expression statement
        return formatExpressionStatement(expr);
    }

    /**
     * Formats expression statement with semicolon if needed.
     * CC: 2 (condition checks)
     */
    private String formatExpressionStatement(String expr) {
        String trimmed = expr.trim();
        if (trimmed.endsWith(";")) {
            return expr;
        }
        // Object literals and blocks need semicolons for proper ASI (Automatic Semicolon Insertion)
        // when followed by other statements like destructuring assignments
        return expr + ";";
    }

    @Override
    public String visit(Block node) {
        BlockGenerator generator = new BlockGenerator(node, indentation, this);
        return generator.generate();
    }

    @Override
    public String visit(ImportStatement node) {
        return node.toString();
    }

    @Override
    public String visit(ExportStatement node) {
        return node.toString();
    }

    @Override
    public String visit(ComponentStatement node) {
        return componentCodeGenerator.visitComponentStatement(node);
    }

    @Override
    public String visit(ForeachStatement node) {
        return componentCodeGenerator.visitForeach(node);
    }

    @Override
    public String visit(IfStatement node) {
        return componentCodeGenerator.visitIf(node, insideComponentClass);
    }

    @Override
    public String visit(EmptyStatement node) {
        // Empty statement produces no output
        return "";
    }

    // Helper classes for context management

    private static class ClassGenerationContext {
        private final boolean wasInsideComponentClass;
        private final List<String> builderMethods;

        ClassGenerationContext(boolean insideComponentClass, List<String> builderMethods) {
            this.wasInsideComponentClass = insideComponentClass;
            this.builderMethods = new ArrayList<>(builderMethods);
        }

        boolean wasInsideComponentClass() {
            return wasInsideComponentClass;
        }

        List<String> getBuilderMethods() {
            return builderMethods;
        }
    }

    /**
     * Sets whether we are inside a component class.
     */
    public void setInsideComponentClass(boolean insideComponentClass) {
        this.insideComponentClass = insideComponentClass;
    }

    /**
     * Checks if we are currently inside a component class.
     */
    public boolean isInsideComponentClass() {
        return insideComponentClass;
    }

    /**
     * Gets the generated code as a string.
     */
    public String getOutput() {
        return output.toString();
    }

    /**
     * Gets current indentation.
     */
    private String getIndent() {
        return indentation.getCurrent();
    }

    /**
     * Writes text to output.
     */
    private void write(String text) {
        output.append(text);
    }

    /**
     * Writes text with newline.
     */
    private void writeLine(String text) {
        output.append(text).append("\n");
    }

    /**
     * Writes indent to output.
     */
    private void writeIndent() {
        output.append(getIndent());
    }
}
