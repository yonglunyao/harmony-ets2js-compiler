package com.ets2jsc.generator;

import com.ets2jsc.ast.*;
import com.ets2jsc.ast.ComponentStatement.ComponentPart;
import com.ets2jsc.ast.ComponentStatement.PartKind;
import com.ets2jsc.config.CompilerConfig;
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
    private final CompilerConfig config;
    private boolean insideComponentClass = false;

    public CodeGenerator(CompilerConfig config) {
        this.output = new StringBuilder();
        this.indent = "  ";
        this.currentIndent = 0;
        this.config = config != null ? config : new CompilerConfig();
        this.insideComponentClass = false;
    }

    public CodeGenerator() {
        this(null);
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
        // Set insideComponentClass flag for this class
        boolean previousInsideComponentClass = insideComponentClass;
        insideComponentClass = node.hasDecorator("Component");

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

        // Restore previous insideComponentClass flag
        insideComponentClass = previousInsideComponentClass;

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

        // Note: JavaScript class properties don't support visibility keywords like 'private'
        // Use comments to indicate private properties instead
        // if (node.getVisibility() == PropertyDeclaration.Visibility.PRIVATE) {
        //     sb.append("// private: ");
        // }

        // Name and type
        sb.append(node.getName());

        // Type annotations are not supported in JavaScript class properties
        // if (node.getTypeAnnotation() != null) {
        //     sb.append(": ").append(node.getTypeAnnotation());
        // }

        // Initializer - ensure string literals are quoted
        if (node.getInitializer() != null) {
            String initializer = node.getInitializer();
            // Check if this looks like an unquoted string value
            // (identifier without quotes that should be a string literal)
            if (needsQuoting(initializer)) {
                sb.append(" = \"").append(escapeJsString(initializer)).append("\"");
            } else {
                sb.append(" = ").append(initializer);
            }
        }

        sb.append(";\n");

        return sb.toString();
    }

    /**
     * Checks if a value looks like it needs quotes (unquoted string literal).
     */
    private boolean needsQuoting(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        // If already quoted, don't quote again
        if (value.startsWith("\"") || value.startsWith("'") || value.startsWith("`")) {
            return false;
        }
        // If it's a number, boolean, null, undefined, or this.xxx, don't quote
        if (value.matches("\\d+") || value.equals("true") || value.equals("false") ||
            value.equals("null") || value.equals("undefined") || value.startsWith("this.") ||
            value.startsWith("new ") || value.contains("(") || value.contains("{") ||
            value.contains("[") || value.startsWith("$")) {
            return false;
        }
        // Otherwise, it might be an unquoted string literal
        return true;
    }

    /**
     * Escapes special characters in JavaScript strings.
     */
    private String escapeJsString(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
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

        if (config.isPureJavaScript()) {
            // Pure JavaScript mode: use standard Array.forEach instead of ForEach runtime
            String itemGen = node.getItemGenerator();
            sb.append(getIndent()).append("array.forEach(").append(itemGen).append(");\n");
        } else {
            // ArkUI runtime mode: include ForEach.create(), pop()
            sb.append(getIndent()).append("ForEach.create();\n");

            sb.append(getIndent()).append("const __itemGenFunction__ = ").append(node.getItemGenerator()).append(";\n");

            String keyGen = node.getKeyGenerator();
            if (keyGen != null && !keyGen.isEmpty()) {
                sb.append(getIndent()).append("const __keyGenFunction__ = ").append(keyGen).append(";\n");
                sb.append(getIndent()).append("ForEach.keyGenerator(__keyGenFunction__);\n");
            }

            sb.append(getIndent()).append("ForEach.itemGenerator(__itemGenFunction__);\n");

            sb.append(getIndent()).append("ForEach.pop();\n");
        }

        return sb.toString();
    }

    @Override
    public String visit(IfStatement node) {
        StringBuilder sb = new StringBuilder();

        // Generate standard if-else if pure JavaScript mode OR not inside a component class
        boolean useStandardIfElse = config.isPureJavaScript() || !insideComponentClass;

        if (useStandardIfElse) {
            // Standard if-else without ArkUI runtime
            sb.append(getIndent()).append("if (").append(node.getCondition()).append(") {\n");
            currentIndent++;
            String thenCode = node.getThenBlock().accept(this);
            sb.append(thenCode);
            currentIndent--;
            sb.append(getIndent()).append("}\n");

            if (node.hasElse()) {
                sb.append(getIndent()).append("else {\n");
                currentIndent++;
                String elseCode = node.getElseBlock().accept(this);
                sb.append(elseCode);
                currentIndent--;
                sb.append(getIndent()).append("}\n");
            }
        } else {
            // ArkUI runtime mode: include If.create(), branchId(), pop()
            sb.append(getIndent()).append("If.create();\n");

            sb.append(getIndent()).append("if (").append(node.getCondition()).append(") {\n");
            currentIndent++;

            sb.append(getIndent()).append("If.branchId(0);\n");
            String thenCode = node.getThenBlock().accept(this);
            sb.append(thenCode);

            currentIndent--;
            sb.append(getIndent()).append("}\n");

            if (node.hasElse()) {
                sb.append(getIndent()).append("else {\n");
                currentIndent++;

                sb.append(getIndent()).append("If.branchId(1);\n");
                String elseCode = node.getElseBlock().accept(this);
                sb.append(elseCode);

                currentIndent--;
                sb.append(getIndent()).append("}\n");
            }

            sb.append(getIndent()).append("If.pop();\n");
        }

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
