package com.ets2jsc.parser;

import com.ets2jsc.ast.*;
import com.ets2jsc.constant.Decorators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enhanced simple parser for ETS files.
 * Provides basic parsing for struct/class declarations with members.
 */
public class EnhancedSimpleParser {

    private static final Pattern STRUCT_PATTERN = Pattern.compile("struct\\s+(\\w+)\\s*\\{");
    private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+(\\w+)");
    private static final Pattern STATE_PATTERN = Pattern.compile("@State\\s+(\\w+)\\s*:\\s*(\\w+)\\s*=\\s*([^;]+);");
    private static final Pattern BUILD_METHOD_PATTERN = Pattern.compile("build\\s*\\(\\s*\\)\\s*\\{");
    private static final Pattern METHOD_PATTERN = Pattern.compile("(\\w+)\\s*\\(([^)]*)\\)\\s*\\{");

    /**
     * Parse ETS source code into a SourceFile.
     */
    public SourceFile parse(String fileName, String sourceCode) {
        SourceFile sourceFile = new SourceFile(fileName, sourceCode);

        // Check for @Component decorator
        boolean hasComponent = sourceCode.contains("@" + Decorators.COMPONENT);

        // Check for struct declaration
        Matcher structMatcher = STRUCT_PATTERN.matcher(sourceCode);
        if (structMatcher.find()) {
            String structName = structMatcher.group(1);
            ClassDeclaration classDecl = parseStructContent(sourceCode, structName, hasComponent);
            sourceFile.addStatement(classDecl);
        }
        // Check for class declaration
        else {
            Matcher classMatcher = CLASS_PATTERN.matcher(sourceCode);
            if (classMatcher.find()) {
                String className = classMatcher.group(1);
                ClassDeclaration classDecl = parseClassContent(sourceCode, className);
                sourceFile.addStatement(classDecl);
            }
        }

        return sourceFile;
    }

    /**
     * Parse struct content including members.
     */
    private ClassDeclaration parseStructContent(String sourceCode, String structName, boolean hasComponent) {
        ClassDeclaration classDecl = new ClassDeclaration(structName);
        classDecl.setStruct(true);

        // Add @Component decorator if present
        if (hasComponent) {
            classDecl.addDecorator(new Decorator(Decorators.COMPONENT));
        }

        // Extract body content
        int bodyStart = sourceCode.indexOf("{");
        int bodyEnd = sourceCode.lastIndexOf("}");
        if (bodyStart == -1 || bodyEnd == -1) {
            return classDecl;
        }

        String bodyContent = sourceCode.substring(bodyStart + 1, bodyEnd);

        // Parse @State properties
        parseStateProperties(bodyContent, classDecl);

        // Parse build() method
        if (sourceCode.contains("build()")) {
            parseBuildMethod(bodyContent, classDecl);
        }

        return classDecl;
    }

    /**
     * Parse class content.
     */
    private ClassDeclaration parseClassContent(String sourceCode, String className) {
        ClassDeclaration classDecl = new ClassDeclaration(className);

        // Extract body content
        int bodyStart = sourceCode.indexOf("{");
        int bodyEnd = sourceCode.lastIndexOf("}");
        if (bodyStart == -1 || bodyEnd == -1) {
            return classDecl;
        }

        String bodyContent = sourceCode.substring(bodyStart + 1, bodyEnd);

        // Parse methods (static methods)
        String[] lines = bodyContent.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("static ")) {
                parseStaticMethod(line, classDecl);
            }
        }

        return classDecl;
    }

    /**
     * Parse @State properties.
     */
    private void parseStateProperties(String bodyContent, ClassDeclaration classDecl) {
        Matcher matcher = STATE_PATTERN.matcher(bodyContent);
        while (matcher.find()) {
            String propName = matcher.group(1);
            String propType = matcher.group(2);
            String propValue = matcher.group(3).trim();

            PropertyDeclaration propDecl = new PropertyDeclaration(propName);
            propDecl.setTypeAnnotation(propType);
            propDecl.setInitializer(propValue);
            propDecl.addDecorator(new Decorator(Decorators.STATE));
            propDecl.setVisibility(PropertyDeclaration.Visibility.PRIVATE);

            classDecl.addMember(propDecl);
        }
    }

    /**
     * Parse build() method.
     */
    private void parseBuildMethod(String bodyContent, ClassDeclaration classDecl) {
        MethodDeclaration methodDecl = new MethodDeclaration("build");
        methodDecl.setReturnType("void");

        // Simple parsing: extract build method body
        int buildIndex = bodyContent.indexOf("build()");
        if (buildIndex != -1) {
            int openBrace = bodyContent.indexOf("{", buildIndex);
            int closeBrace = bodyContent.lastIndexOf("}");
            if (openBrace != -1 && closeBrace != -1) {
                String methodBody = bodyContent.substring(openBrace + 1, closeBrace);
                // Store as expression statement for now
                methodDecl.setBody(new ExpressionStatement(methodBody));
            }
        }

        classDecl.addMember(methodDecl);
    }

    /**
     * Parse static method.
     */
    private void parseStaticMethod(String line, ClassDeclaration classDecl) {
        // Parse: static methodName(param: type): returnType {
        Pattern methodPattern = Pattern.compile("static\\s+(\\w+)\\s*\\(([^)]*)\\)");
        Matcher matcher = methodPattern.matcher(line);
        if (matcher.find()) {
            String methodName = matcher.group(1);
            MethodDeclaration methodDecl = new MethodDeclaration(methodName);
            methodDecl.setStatic(true);
            classDecl.addMember(methodDecl);
        }
    }
}
