package com.ets2jsc.parser.internal;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.EmptyStatement;
import com.ets2jsc.parser.internal.converters.statements.*;
import com.google.gson.JsonObject;

/**
 * Registry for statement converters.
 * Handles conversion of JSON statements to AST nodes.
 */
public class StatementConverterRegistry extends NodeConverterRegistry {

    public StatementConverterRegistry() {
        super();
    }

    @Override
    protected void initializeConverters() {
        // Register all statement converters
        register(new ClassDeclarationConverter());
        register(new ConstructorConverter());
        register(new MethodDeclarationConverter());
        register(new PropertyDeclarationConverter());
        register(new FunctionDeclarationConverter());
        register(new BlockConverter());
        register(new IfStatementConverter());
        register(new ForOfConverter());
        register(new ForInConverter());
        register(new WhileConverter());
        register(new DoConverter());
        register(new ForConverter());
        register(new SwitchConverter());
        register(new TryConverter());
        register(new ReturnConverter());
        register(new VariableConverter());
        register(new ImportConverter());
        register(new ExportConverter());
        register(new ExpressionConverter());
        register(new EmptyStatementConverter());
    }

    /**
     * Converts a JSON statement to an AST node.
     */
    public AstNode convert(JsonObject json, ConversionContext context) {
        String kindName = json.has("kindName") ? json.get("kindName").getAsString() : "";

        NodeConverter converter = findConverter(kindName);
        Object result = converter.convert(json, context);

        if (result instanceof AstNode) {
            return (AstNode) result;
        }
        throw new ClassCastException("Converter for " + kindName + " did not return an AstNode");
    }
}
