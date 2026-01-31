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

        convertDecorators(classDecl, json);
        setExportFlag(classDecl, json);
        convertMembers(classDecl, json, context);

        return classDecl;
    }

    /**
     * Converts decorators and returns if @Entry was found.
     * CC: 3 (null check + loop + early continue)
     */
    private void convertDecorators(ClassDeclaration classDecl, JsonObject json) {
        JsonArray decoratorsArray = json.getAsJsonArray("decorators");
        if (decoratorsArray == null) {
            return;
        }

        for (int i = 0; i < decoratorsArray.size(); i++) {
            JsonObject decObj = decoratorsArray.get(i).getAsJsonObject();
            String decName = decObj.get("name").getAsString();
            classDecl.addDecorator(new Decorator(decName));
        }
    }

    /**
     * Sets export flag based on isExport or @Entry decorator.
     * CC: 3 (multiple conditions)
     */
    private void setExportFlag(ClassDeclaration classDecl, JsonObject json) {
        boolean isExported = json.has("isExport") && json.get("isExport").getAsBoolean();
        boolean hasEntryDecorator = classDecl.hasDecorator("Entry");

        if (isExported || hasEntryDecorator) {
            classDecl.setExport(true);
        }
    }

    /**
     * Converts class members.
     * CC: 3 (null check + loop + instance checks)
     */
    private void convertMembers(ClassDeclaration classDecl, JsonObject json, ConversionContext context) {
        JsonArray membersArray = json.getAsJsonArray("members");
        if (membersArray == null) {
            return;
        }

        for (int i = 0; i < membersArray.size(); i++) {
            JsonObject memberObj = membersArray.get(i).getAsJsonObject();
            AstNode member = context.convertStatement(memberObj);
            addMember(classDecl, member);
        }
    }

    /**
     * Adds member to class declaration if valid type.
     * CC: 2 (instance checks)
     */
    private void addMember(ClassDeclaration classDecl, AstNode member) {
        if (member instanceof PropertyDeclaration) {
            classDecl.addMember((PropertyDeclaration) member);
        } else if (member instanceof MethodDeclaration) {
            classDecl.addMember((MethodDeclaration) member);
        }
    }
}
