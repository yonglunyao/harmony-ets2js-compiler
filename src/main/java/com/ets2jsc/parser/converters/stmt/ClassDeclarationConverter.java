package com.ets2jsc.parser.converters.stmt;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.ast.PropertyDeclaration;
import com.ets2jsc.parser.ConversionContext;
import com.ets2jsc.parser.NodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
    public Object convert(JsonNode json, ConversionContext context) {
        String name = json.get("name").asText();
        ClassDeclaration classDecl = new ClassDeclaration(name);

        try {
            convertDecorators(classDecl, json);
            setExportFlag(classDecl, json);
            setExtends(classDecl, json);
            convertMembers(classDecl, json, context);
        } catch (Exception e) {
            LOGGER.error("Failed to convert class {}: {}", name, e.getMessage(), e);
            throw e;
        }

        return classDecl;
    }

    /**
     * Converts decorators and adds them to class declaration.
     * Uses Guard Clause for null checks.
     * CC: 3 (null check + loop + early continue)
     */
    private void convertDecorators(ClassDeclaration classDecl, JsonNode json) {
        JsonNode decoratorsNode = json.get("decorators");
        if (decoratorsNode == null || !decoratorsNode.isArray()) {
            return;
        }

        ArrayNode decoratorsArray = (ArrayNode) decoratorsNode;
        for (int i = 0; i < decoratorsArray.size(); i++) {
            // Guard Clause: skip null decorators
            JsonNode decNode = decoratorsArray.get(i);
            if (decNode == null || decNode.isNull()) {
                continue;
            }
            String decName = decNode.get("name").asText();
            classDecl.addDecorator(new Decorator(decName));
        }
    }

    /**
     * Sets export flag based on isExport or @Entry decorator.
     * CC: 3 (multiple conditions)
     */
    private void setExportFlag(ClassDeclaration classDecl, JsonNode json) {
        boolean isExported = json.has("isExport") && json.get("isExport").asBoolean();
        boolean hasEntryDecorator = classDecl.hasDecorator("Entry");

        if (isExported || hasEntryDecorator) {
            classDecl.setExport(true);
        }
    }

    /**
     * Sets extends clause if present.
     * CC: 3 (null checks + loop)
     */
    private void setExtends(ClassDeclaration classDecl, JsonNode json) {
        JsonNode heritageClausesNode = json.get("heritageClauses");
        if (heritageClausesNode == null || !heritageClausesNode.isArray()) {
            return;
        }

        ArrayNode heritageArray = (ArrayNode) heritageClausesNode;
        for (int i = 0; i < heritageArray.size(); i++) {
            JsonNode clause = heritageArray.get(i);
            if (clause == null || !clause.isObject()) {
                continue;
            }

            // Check token type (extends or implements)
            String token = clause.has("token") ? clause.get("token").asText() : "";
            if ("extends".equals(token) || "ExtendsKeyword".equals(token)) {
                // Get the first type from types array
                JsonNode typesNode = clause.get("types");
                if (typesNode != null && typesNode.isArray() && typesNode.size() > 0) {
                    String extendsClass = typesNode.get(0).asText();
                    classDecl.setSuperClass(extendsClass);
                }
            }
        }
    }

    /**
     * Converts class members.
     * CC: 3 (null check + loop + instance checks)
     */
    private void convertMembers(ClassDeclaration classDecl, JsonNode json, ConversionContext context) {
        JsonNode membersNode = json.get("members");
        if (membersNode == null || !membersNode.isArray()) {
            return;
        }

        ArrayNode membersArray = (ArrayNode) membersNode;
        for (int i = 0; i < membersArray.size(); i++) {
            JsonNode memberNode = membersArray.get(i);
            // Check if member is not null before converting
            if (memberNode == null || memberNode.isNull()) {
                LOGGER.debug("Skipping null member at index {} in class {}", i, classDecl.getName());
                continue;
            }
            if (!memberNode.isObject()) {
                LOGGER.warn("Member at index {} in class {} is not an Object: {}", i, classDecl.getName(), memberNode);
                continue;
            }
            JsonNode memberObj = membersArray.get(i);
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
