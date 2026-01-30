package com.ets2jsc.generator;

import com.ets2jsc.ast.*;
import com.ets2jsc.ast.ComponentStatement.ComponentPart;
import com.ets2jsc.ast.ComponentStatement.PartKind;
import com.ets2jsc.constant.RuntimeFunctions;
import com.ets2jsc.transformer.ComponentExpressionTransformer;

import java.util.List;

/**
 * Generates JavaScript code from transformed AST.
 * Traverses the AST and outputs formatted JavaScript code.
 */
public class CodeGenerator implements AstVisitor<String> {

    private final StringBuilder output;
    private final String indent;
    private int currentIndent;

    public CodeGenerator() {
        this.output = new StringBuilder();
        this.indent = "  ";
        this.currentIndent = 0;
    }

    /**
     * Generates JavaScript code from an AST node.
     */
    public String generate(AstNode node) {
        if (node == null) {
            return "";
        }
        return node.accept(this);
    }

    /**
     * Generates JavaScript code for a source file.
     */
    public String generate(SourceFile sourceFile) {
        output.setLength(0);
        currentIndent = 0;

        // First pass: collect and output import statements
        for (AstNode statement : sourceFile.getStatements()) {
            if (statement instanceof ImportStatement) {
                String code = statement.accept(this);
                if (!code.isEmpty()) {
                    writeLine(code);
                }
            }
        }

        // Add blank line after imports
        if (hasImportStatements(sourceFile)) {
            writeLine("");
        }

        // Second pass: output other statements
        for (AstNode statement : sourceFile.getStatements()) {
            if (!(statement instanceof ImportStatement)) {
                String code = statement.accept(this);
                if (!code.trim().isEmpty()) {
                    writeLine(code);
                }
            }
        }

        return output.toString();
    }

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
            sb.append(statement.accept(this));
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String visit(ClassDeclaration node) {
        StringBuilder sb = new StringBuilder();

        // Generate decorators (comments for now)
        for (Decorator decorator : node.getDecorators()) {
            if (!decorator.getName().equals("Component")) {
                sb.append("// @").append(decorator.getName()).append("\n");
            }
        }

        // Add export keyword if present
        if (node.isExport()) {
            sb.append("export ");
        }

        // Class declaration
        sb.append("class ").append(node.getName());

        // Extends clause
        if (node.getSuperClass() != null) {
            sb.append(" extends ").append(node.getSuperClass());
        }

        sb.append(" {\n");
        currentIndent++;

        // Generate members
        for (AstNode member : node.getMembers()) {
            String memberCode = member.accept(this);
            if (!memberCode.isEmpty()) {
                sb.append(memberCode).append("\n");
            }
        }

        currentIndent--;
        sb.append("}\n");

        return sb.toString();
    }

    @Override
    public String visit(MethodDeclaration node) {
        StringBuilder sb = new StringBuilder();

        // Method signature
        sb.append(getIndent());

        if ("constructor".equals(node.getName())) {
            sb.append("constructor() {\n");
            currentIndent++;
        } else if (node.getName().startsWith("get ")) {
            sb.append("get ")
              .append(node.getName().substring(4))
              .append("() {\n");
            currentIndent++;
        } else if (node.getName().startsWith("set ")) {
            sb.append("set ")
              .append(node.getName().substring(4))
              .append("(");
            // Add value parameter
            if (!node.getParameters().isEmpty()) {
                MethodDeclaration.Parameter param = node.getParameters().get(0);
                sb.append(param.getName());
            } else {
                sb.append("newValue");
            }
            sb.append(") {\n");
            currentIndent++;
        } else {
            // Add static keyword if present
            if (node.isStatic()) {
                sb.append("static ");
            }
            // Add async keyword if present
            if (node.isAsync()) {
                sb.append("async ");
            }

            sb.append(node.getName()).append("(");

            // Parameters
            List<MethodDeclaration.Parameter> params = node.getParameters();
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                MethodDeclaration.Parameter param = params.get(i);
                sb.append(param.getName());
                // Optional type annotation (as comment for JS)
                if (param.getType() != null) {
                    sb.append(" /* ").append(param.getType()).append(" */");
                }
            }

            sb.append(") {\n");
            currentIndent++;
        }

        // Method body
        if (node.getBody() != null) {
            String bodyCode = node.getBody().accept(this);
            // If body is ExpressionStatement, it may contain multiple lines (e.g., constructor with \n)
            if (node.getBody() instanceof com.ets2jsc.ast.ExpressionStatement) {
                // Split by newlines and indent each line
                String[] lines = bodyCode.split("\n");
                for (String line : lines) {
                    if (!line.isEmpty()) {
                        sb.append(getIndent()).append(line).append("\n");
                    }
                }
            } else {
                sb.append(bodyCode);
            }
        }

        currentIndent--;
        sb.append(getIndent()).append("}\n");

        return sb.toString();
    }

    @Override
    public String visit(PropertyDeclaration node) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent());

        // Visibility
        if (node.getVisibility() == PropertyDeclaration.Visibility.PRIVATE) {
            sb.append("private ");
        }

        // Name and type
        sb.append(node.getName());

        if (node.getTypeAnnotation() != null) {
            sb.append(": ").append(node.getTypeAnnotation());
        }

        // Initializer
        if (node.getInitializer() != null) {
            sb.append(" = ").append(node.getInitializer());
        }

        sb.append(";\n");

        return sb.toString();
    }

    @Override
    public String visit(Decorator node) {
        // Decorators are handled at class level
        return "";
    }

    @Override
    public String visit(ComponentExpression node) {
        // Component expressions are handled by the transformer
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
        // Return the expression string with semicolon
        String expr = node.getExpression();
        if (expr == null || expr.isEmpty()) {
            return "";
        }

        // Try to transform to component statement (create/pop pattern)
        AstNode transformed = ComponentExpressionTransformer.transform(expr);
        if (transformed instanceof ComponentStatement) {
            return transformed.accept(this);
        }

        // Don't add semicolon if expression already ends with one or is a block
        String trimmed = expr.trim();
        if (trimmed.endsWith(";") || trimmed.endsWith("}") || trimmed.startsWith("{")) {
            return expr;
        }

        // Add semicolon
        return expr + ";";
    }

    @Override
    public String visit(Block node) {
        StringBuilder sb = new StringBuilder();
        for (AstNode stmt : node.getStatements()) {
            // Check for special statement types
            if (stmt instanceof ForeachStatement) {
                sb.append(((ForeachStatement) stmt).accept(this));
            } else if (stmt instanceof IfStatement) {
                sb.append(((IfStatement) stmt).accept(this));
            } else {
                String stmtCode = stmt.accept(this);
                if (stmtCode != null && !stmtCode.isEmpty()) {
                    // Don't add indentation for nested blocks in component methods
                    // (they represent component closures, not code blocks)
                    if (stmt instanceof Block) {
                        sb.append(stmtCode);
                    } else {
                        sb.append(getIndent()).append(stmtCode).append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String visit(ImportStatement node) {
        // Return the string representation of the import statement
        return node.toString();
    }

    @Override
    public String visit(ExportStatement node) {
        // Return the string representation of the export statement
        // Type exports return empty string and will be filtered out
        return node.toString();
    }

    @Override
    public String visit(ComponentStatement node) {
        StringBuilder sb = new StringBuilder();
        String componentName = node.getComponentName();

        // Process each part
        for (ComponentPart part : node.getParts()) {
            switch (part.getKind()) {
                case CREATE:
                    sb.append(getIndent()).append(componentName).append(".create(").append(part.getCode()).append(");\n");
                    break;
                case METHOD:
                    sb.append(getIndent()).append(componentName).append(".").append(part.getCode()).append("\n");
                    break;
                case POP:
                    // Output children before pop() if this component has children
                    if (node.hasChildren()) {
                        // Increase indent for children
                        currentIndent++;
                        sb.append(node.getChildren().accept(this));
                        currentIndent--;
                    }
                    sb.append(getIndent()).append(componentName).append(".pop();\n");
                    break;
            }
        }

        return sb.toString();
    }

    @Override
    public String visit(ForeachStatement node) {
        StringBuilder sb = new StringBuilder();

        // 1. ForEach.create()
        sb.append(getIndent()).append("ForEach.create();\n");

        // 2. Item generator function
        sb.append(getIndent()).append("const __itemGenFunction__ = ").append(node.getItemGenerator()).append(";\n");

        // 3. Key generator function (if provided)
        String keyGen = node.getKeyGenerator();
        if (keyGen != null && !keyGen.isEmpty()) {
            sb.append(getIndent()).append("const __keyGenFunction__ = ").append(keyGen).append(";\n");
            sb.append(getIndent()).append("ForEach.keyGenerator(__keyGenFunction__);\n");
        }

        // 4. Set item generator
        sb.append(getIndent()).append("ForEach.itemGenerator(__itemGenFunction__);\n");

        // 5. ForEach.pop()
        sb.append(getIndent()).append("ForEach.pop();\n");

        return sb.toString();
    }

    @Override
    public String visit(IfStatement node) {
        StringBuilder sb = new StringBuilder();

        // 1. If.create()
        sb.append(getIndent()).append("If.create();\n");

        // 2. Build the if-else statement with branchId
        sb.append(getIndent()).append("if (").append(node.getCondition()).append(") {\n");
        currentIndent++;

        // Then branch
        sb.append(getIndent()).append("If.branchId(0);\n");
        String thenCode = node.getThenBlock().accept(this);
        sb.append(thenCode);

        currentIndent--;
        sb.append(getIndent()).append("}\n");

        // Else branch (if exists)
        if (node.hasElse()) {
            sb.append(getIndent()).append("else {\n");
            currentIndent++;

            sb.append(getIndent()).append("If.branchId(1);\n");
            String elseCode = node.getElseBlock().accept(this);
            sb.append(elseCode);

            currentIndent--;
            sb.append(getIndent()).append("}\n");
        }

        // 3. If.pop()
        sb.append(getIndent()).append("If.pop();\n");

        return sb.toString();
    }

    /**
     * Helper methods for indentation and output.
     */
    private String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentIndent; i++) {
            sb.append(indent);
        }
        return sb.toString();
    }

    private void write(String text) {
        output.append(text);
    }

    private void writeLine(String text) {
        output.append(text).append("\n");
    }

    private void writeIndent() {
        output.append(getIndent());
    }

    /**
     * Gets the generated code as a string.
     */
    public String getOutput() {
        return output.toString();
    }
}
