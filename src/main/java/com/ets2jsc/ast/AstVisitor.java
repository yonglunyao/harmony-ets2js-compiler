package com.ets2jsc.ast;

/**
 * Visitor interface for traversing and processing AST nodes.
 * Implements the Visitor design pattern for AST manipulation.
 */
public interface AstVisitor<T> {
    /**
     * Visit a source file node.
     */
    T visit(SourceFile node);

    /**
     * Visit a class declaration node.
     */
    T visit(ClassDeclaration node);

    /**
     * Visit a method declaration node.
     */
    T visit(MethodDeclaration node);

    /**
     * Visit a property declaration node.
     */
    T visit(PropertyDeclaration node);

    /**
     * Visit a decorator node.
     */
    T visit(Decorator node);

    /**
     * Visit a component expression node.
     */
    T visit(ComponentExpression node);

    /**
     * Visit a call expression node.
     */
    T visit(CallExpression node);

    /**
     * Visit an expression statement node.
     */
    T visit(ExpressionStatement node);

    /**
     * Visit a block node.
     */
    T visit(Block node);

    /**
     * Visit an import statement node.
     */
    T visit(ImportStatement node);

    /**
     * Visit an export statement node.
     */
    T visit(ExportStatement node);

    /**
     * Visit a component statement node.
     */
    T visit(ComponentStatement node);
}
