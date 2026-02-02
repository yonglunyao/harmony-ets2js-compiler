package com.ets2jsc.domain.model.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a function/method call expression.
 */
public class CallExpression implements AstNode {
    private final List<AstNode> arguments;
    private String functionName;
    private AstNode callee;
    private boolean _isComponentCall;

    public CallExpression(String functionName) {
        this.functionName = functionName;
        this.arguments = new ArrayList<>();
        this._isComponentCall = false;
    }

    @Override
    public String getType() {
        return "CallExpression";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public List<AstNode> getArguments() {
        return arguments;
    }

    public void addArgument(AstNode argument) {
        this.arguments.add(argument);
    }

    public AstNode getCallee() {
        return callee;
    }

    public void setCallee(AstNode callee) {
        this.callee = callee;
    }

    public boolean isComponentCall() {
        return _isComponentCall;
    }

    public void setComponentCall(boolean componentCall) {
        _isComponentCall = componentCall;
    }

    /**
     * Returns true if this is a special control flow call.
     */
    public boolean isControlFlow() {
        return "ForEach".equals(functionName) ||
                "LazyForEach".equals(functionName) ||
                "If".equals(functionName);
    }
}
