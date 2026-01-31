package com.ets2jsc.parser.internal.converters.statements;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.parser.internal.ConversionContext;
import com.ets2jsc.parser.internal.NodeConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converter for class declarations.
 * Handles: class MyClass { ... }
 */
public class ClassDeclarationConverter implements NodeConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassDeclarationConverter.class);

    @Override
    public boolean canConvert(String kindName) {
        return "ClassDeclaration".equals(kindName);
    }

    @Override
    public Object convert(JsonObject json, ConversionContext context) {
        String name = json.get("name").getAsString();
        ClassDeclaration classDecl = new ClassDeclaration(name);

        try {
            convertDecorators(classDecl, json);
            setExportFlag(classDecl, json);
            convertMembers(classDecl, json, context);
        } catch (Exception e) {
            LOGGER.error("Failed to convert class {}: {}", name, e.getMessage(), e);
            throw e;
        }

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
            // Check if decorator is not null before converting
            if (decoratorsArray.get(i).isJsonNull()) {
                continue;
            }
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
            JsonElement memberElement = membersArray.get(i);
            // Check if member is not null before converting
            if (memberElement.isJsonNull()) {
                LOGGER.debug("Skipping null member at index {} in class {}", i, classDecl.getName());
                continue;
            }
            if (!memberElement.isJsonObject()) {
                LOGGER.warn("Member at index {} in class {} is not a JsonObject: {}", i, classDecl.getName(), memberElement);
                continue;
            }
            JsonObject memberObj = memberElement.getAsJsonObject();
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
