package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Converter for class declarations.
 * Handles: class MyClass { ... }
 */
public class ClassDeclarationConverter implements NodeConverter {

    @Override
    public boolean canConvert(String kindName) {
        return "ClassDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String name = json.get("name").getAsString();
        ClassDeclaration classDecl = new ClassDeclaration(name);

        // Convert decorators first
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        boolean hasEntryDecorator = false;
        if (decoratorsArray != null) {
            for (int i = 0; i < decoratorsArray.size(); i++) {
                JsonObject decObj = decoratorsArray.get(i).getAsJsonObject();
                String decName = decObj.get("name").getAsString();
                classDecl.addDecorator(new Decorator(decName));
                // Check for @Entry decorator
                if ("Entry".equals(decName)) {
                    hasEntryDecorator = true;
                }
            }
        }

        // Set export flag
        boolean isExported = json.has("isExport") && json.get("isExport").getAsBoolean();
        if (isExported) {
            classDecl.setExport(true);
        } else if (hasEntryDecorator) {
            // @Entry components must be exported - automatically add export
            classDecl.setExport(true);
        }

        // Convert members
        JsonArray membersArray = json.getAsJsonArray("members");
        if (membersArray != null) {
            for (int i = 0; i < membersArray.size(); i++) {
                JsonObject memberObj = membersArray.get(i).getAsJsonObject();
                AstNode member = context.convertStatement(memberObj);
                if (member instanceof PropertyDeclaration) {
                    classDecl.addMember((PropertyDeclaration) member);
                } else if (member instanceof MethodDeclaration) {
                    classDecl.addMember((MethodDeclaration) member);
                }
            }
        }

        return classDecl;
    }
}
