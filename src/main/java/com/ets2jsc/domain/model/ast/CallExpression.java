package com.ets2jsc.domain.model.ast;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a function/method call expression.
 */
@Getter
public class CallExpression implements AstNode {
    private final List<AstNode> arguments;
    @Setter
    private String functionName;
    @Setter
    private AstNode callee;
    @Setter
    private boolean isComponentCall;

    public CallExpression(String functionName) {
        this.functionName = functionName;
        this.arguments = new ArrayList<>();
        this.isComponentCall = false;
    }

    @Override
    public String getType() {
        return "CallExpression";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void addArgument(AstNode argument) {
        this.arguments.add(argument);
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
