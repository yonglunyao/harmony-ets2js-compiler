package com.ets2jsc.ast;

/**
 * Export statement AST node.
 * Represents an export declaration like:
 * - export class MyClass {}
 * - export function myFunc() {}
 * - export const myConst = 1;
 * - export interface MyInterface {}
 * - export { name } from 'module'
 * - export type { TypeName } from 'module'
 */
public class ExportStatement implements AstNode {
    private final AstNode declarationNode;
    private final String declarationString;
    private final boolean _isTypeExport;

    public ExportStatement(AstNode declaration, boolean isTypeExport) {
        this.declarationNode = declaration;
        this.declarationString = "";
        this._isTypeExport = isTypeExport;
    }

    public ExportStatement(AstNode declarationNode, boolean isTypeExport, String declarationString) {
        this.declarationNode = declarationNode;
        this.declarationString = declarationString;
        this._isTypeExport = isTypeExport;
    }

    public AstNode getDeclarationNode() {
        return declarationNode;
    }

    public String getDeclarationString() {
        return declarationString;
    }

    public boolean isTypeExport() {
        return _isTypeExport;
    }

    @Override
    public String getType() {
        return "ExportStatement";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Generate the export statement as a string.
     * Type exports return empty string (they will be removed).
     */
    @Override
    public String toString() {
        // Type exports are removed in JS
        if (_isTypeExport) {
            return "";
        }

        // If we have a pre-built declaration string, use it
        if (declarationString != null && !declarationString.isEmpty()) {
            return "export " + declarationString;
        }

        // Otherwise, use the declaration node
        final StringBuilder sb = new StringBuilder();
        sb.append("export ");

        if (declarationNode != null) {
            final String declStr = declarationNode.toString();
            sb.append(declStr);
        }

        return sb.toString();
    }
}
