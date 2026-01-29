package com.ets2jsc.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a source file in the ETS/TypeScript AST.
 * Contains top-level declarations such as classes, functions, and imports.
 */
public class SourceFile implements AstNode {
    private String fileName;
    private String sourceText;
    private List<AstNode> statements;
    private List<String> imports;

    public SourceFile(String fileName) {
        this.fileName = fileName;
        this.statements = new ArrayList<>();
        this.imports = new ArrayList<>();
    }

    public SourceFile(String fileName, String sourceText) {
        this(fileName);
        this.sourceText = sourceText;
    }

    @Override
    public String getType() {
        return "SourceFile";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public List<AstNode> getStatements() {
        return statements;
    }

    public void addStatement(AstNode statement) {
        this.statements.add(statement);
    }

    public List<String> getImports() {
        return imports;
    }

    public void addImport(String importPath) {
        this.imports.add(importPath);
    }
}
