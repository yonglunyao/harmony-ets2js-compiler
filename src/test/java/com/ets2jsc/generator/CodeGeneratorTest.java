package com.ets2jsc.generator;

import com.ets2jsc.ast.*;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.generator.CodeGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test CodeGenerator 代码Generate器
 */
@DisplayName("代码Generate器Test")
class CodeGeneratorTest {

    @Test
    @DisplayName("TestClassDeclarationGenerate")
    void testClassDeclarationGeneration() {
        CodeGenerator generator = new CodeGenerator();

        ClassDeclaration classDecl = new ClassDeclaration("MyClass");
        classDecl.setExport(true);

        String code = generator.generate(classDecl);

        assertNotNull(code);
        assertTrue(code.contains("export class MyClass"));
    }

    @Test
    @DisplayName("Test带父Class ofClassDeclaration")
    void testClassDeclarationWithSuperClass() {
        CodeGenerator generator = new CodeGenerator();

        ClassDeclaration classDecl = new ClassDeclaration("MyView");
        classDecl.setSuperClass("View");

        String code = generator.generate(classDecl);

        assertTrue(code.contains("class MyView extends View"));
    }

    @Test
    @DisplayName("TestProperty DeclarationGenerate")
    void testPropertyDeclarationGeneration() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("myProp");
        prop.setInitializer("\"test\"");

        String code = generator.generate(prop);

        assertTrue(code.contains("myProp"));
        assertTrue(code.contains("\"test\""));
        assertTrue(code.contains(";"));
    }

    @Test
    @DisplayName("Test未加引号 of字符串自动加引号")
    void testUnquotedStringAutoQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("message");
        prop.setInitializer("Hello World");

        String code = generator.generate(prop);

        assertTrue(code.contains("\"Hello World\""),
            "未加引号 of字符串should自动Add引号");
    }

    @Test
    @DisplayName("Test数字不加引号")
    void testNumericNotQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("count");
        prop.setInitializer("42");

        String code = generator.generate(prop);

        assertTrue(code.contains("= 42"));
        assertFalse(code.contains("= \"42\""));
    }

    @Test
    @DisplayName("Test布尔值不加引号")
    void testBooleanNotQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop1 = new PropertyDeclaration("isActive");
        prop1.setInitializer("true");

        PropertyDeclaration prop2 = new PropertyDeclaration("isDisabled");
        prop2.setInitializer("false");

        String code1 = generator.generate(prop1);
        String code2 = generator.generate(prop2);

        assertTrue(code1.contains("= true"));
        assertTrue(code2.contains("= false"));
    }

    @Test
    @DisplayName("Test this 引用不加引号")
    void testThisReferenceNotQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("ref");
        prop.setInitializer("this.value");

        String code = generator.generate(prop);

        assertTrue(code.contains("= this.value"));
    }

    @Test
    @DisplayName("TestMethodDeclarationGenerate")
    void testMethodDeclarationGeneration() {
        CodeGenerator generator = new CodeGenerator();

        MethodDeclaration method = new MethodDeclaration("myMethod");
        Block body = new Block();
        body.addStatement(new ExpressionStatement("return true;"));
        method.setBody(body);

        String code = generator.generate(method);

        assertTrue(code.contains("myMethod()"));
        assertTrue(code.contains("{"));
        assertTrue(code.contains("}"));
    }

    @Test
    @DisplayName("Test构造FunctionGenerate")
    void testConstructorGeneration() {
        CodeGenerator generator = new CodeGenerator();

        MethodDeclaration constructor = new MethodDeclaration("constructor");
        Block body = new Block();
        body.addStatement(new ExpressionStatement("super();"));
        constructor.setBody(body);

        String code = generator.generate(constructor);

        assertTrue(code.contains("constructor()"));
    }

    @Test
    @DisplayName("Test getter MethodGenerate")
    void testGetterGeneration() {
        CodeGenerator generator = new CodeGenerator();

        MethodDeclaration getter = new MethodDeclaration("get value");
        Block body = new Block();
        body.addStatement(new ExpressionStatement("return this._value;"));
        getter.setBody(body);

        String code = generator.generate(getter);

        assertTrue(code.contains("get value()"));
    }

    @Test
    @DisplayName("Test setter MethodGenerate")
    void testSetterGeneration() {
        CodeGenerator generator = new CodeGenerator();

        MethodDeclaration setter = new MethodDeclaration("set value");
        Block body = new Block();
        body.addStatement(new ExpressionStatement("this._value = newValue;"));
        setter.setBody(body);

        String code = generator.generate(setter);

        assertTrue(code.contains("set value(newValue)"));
    }

    @Test
    @DisplayName("Test静态MethodGenerate")
    void testStaticMethodGeneration() {
        CodeGenerator generator = new CodeGenerator();

        MethodDeclaration method = new MethodDeclaration("staticMethod");
        method.setStatic(true);
        Block body = new Block();
        method.setBody(body);

        String code = generator.generate(method);

        assertTrue(code.contains("static staticMethod()"));
    }

    @Test
    @DisplayName("Test SourceFile Generate")
    void testSourceFileGeneration() {
        CodeGenerator generator = new CodeGenerator();

        SourceFile sourceFile = new SourceFile("test.ets", "");
        ClassDeclaration classDecl = new ClassDeclaration("TestClass");
        sourceFile.addStatement(classDecl);

        String code = generator.generate(sourceFile);

        assertNotNull(code);
        assertTrue(code.contains("class TestClass"));
    }

    @Test
    @DisplayName("Test Block Generate")
    void testBlockGeneration() {
        CodeGenerator generator = new CodeGenerator();

        Block block = new Block();
        block.addStatement(new ExpressionStatement("const x = 1;"));
        block.addStatement(new ExpressionStatement("const y = 2;"));

        String code = generator.generate(block);

        assertTrue(code.contains("const x = 1;"));
        assertTrue(code.contains("const y = 2;"));
    }

    @Test
    @DisplayName("Test ImportStatement Generate")
    void testImportStatementGeneration() {
        CodeGenerator generator = new CodeGenerator();

        ImportStatement importStmt = new ImportStatement("module");
        importStmt.addSpecifier(new ImportStatement.ImportSpecifier("defaultName", "defaultName",
            ImportStatement.ImportSpecifier.SpecifierKind.DEFAULT));

        String code = generator.generate(importStmt);

        assertTrue(code.contains("import"));
    }

    @Test
    @DisplayName("Test ExportStatement Generate")
    void testExportStatementGeneration() {
        CodeGenerator generator = new CodeGenerator();

        // Create a simple class declaration to export
        ClassDeclaration classDecl = new ClassDeclaration("TestClass");
        ExportStatement exportStmt = new ExportStatement(classDecl, false, "class TestClass {}");

        String code = generator.generate(exportStmt);

        assertTrue(code.contains("export"));
    }

    @Test
    @DisplayName("Test ForEachStatement Generate（Pure JS Mode）")
    void testForEachPureJSMode() {
        CompilerConfig config = new CompilerConfig();
        config.setPureJavaScript(true);
        CodeGenerator generator = new CodeGenerator(config);

        ForeachStatement forEach = new ForeachStatement("arr", "(item) => {}", null);
        String code = generator.generate(forEach);

        assertTrue(code.contains("forEach"));
        assertFalse(code.contains("ForEach.create()"));
    }

    @Test
    @DisplayName("Test ForEachStatement Generate（ArkUI Mode）")
    void testForEachArkUIMode() {
        CompilerConfig config = new CompilerConfig();
        config.setPureJavaScript(false);
        CodeGenerator generator = new CodeGenerator(config);

        ForeachStatement forEach = new ForeachStatement("arr", "(item) => {}", null);
        String code = generator.generate(forEach);

        assertTrue(code.contains("ForEach.create()"));
        assertTrue(code.contains("ForEach.pop()"));
    }

    @Test
    @DisplayName("Test IfStatement Generate（Pure JS Mode）")
    void testIfStatementPureJSMode() {
        CompilerConfig config = new CompilerConfig();
        config.setPureJavaScript(true);
        CodeGenerator generator = new CodeGenerator(config);

        IfStatement ifStmt = new IfStatement("condition", new Block(), null);
        String code = generator.generate(ifStmt);

        assertTrue(code.contains("if (condition)"));
        assertFalse(code.contains("If.create()"));
    }

    @Test
    @DisplayName("Test IfStatement Generate（ArkUI Mode，在ComponentClass中）")
    void testIfStatementArkUIModeInComponent() {
        CompilerConfig config = new CompilerConfig();
        config.setPureJavaScript(false);
        config.setPartialUpdateMode(true);

        CodeGenerator generator = new CodeGenerator(config);
        generator.setInsideComponentClass(true);

        IfStatement ifStmt = new IfStatement("condition", new Block(), new Block());

        String code = generator.generate(ifStmt);

        assertTrue(code.contains("If.create()"));
        assertTrue(code.contains("If.pop()"));
    }

    @Test
    @DisplayName("Test字符串转义")
    void testStringEscaping() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("text");
        prop.setInitializer("Hello \"World\"");

        String code = generator.generate(prop);

        assertTrue(code.contains("\\\""),
            "引号should被转义");
    }

    @Test
    @DisplayName("TestContains换行符 of字符串")
    void testStringWithNewline() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("text");
        prop.setInitializer("Line1\nLine2");

        String code = generator.generate(prop);

        assertTrue(code.contains("\\n"),
            "换行符should被转义");
    }

    @Test
    @DisplayName("Test已加引号 of字符串不重复加引号")
    void testAlreadyQuotedString() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("text");
        prop.setInitializer("'already quoted'");

        String code = generator.generate(prop);

        // 不should有双重引号
        assertTrue(code.contains("= 'already quoted'"));
        assertFalse(code.contains("''already quoted''"));
    }

    @Test
    @DisplayName("Test模板字符串不加引号")
    void testTemplateStringNotQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("template");
        prop.setInitializer("`Hello ${name}`");

        String code = generator.generate(prop);

        assertTrue(code.contains("= `Hello ${name}`"));
    }

    @Test
    @DisplayName("TestObject字面量不加引号")
    void testObjectLiteralNotQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("obj");
        prop.setInitializer("{ key: 'value' }");

        String code = generator.generate(prop);

        assertTrue(code.contains("= { key: 'value' }"));
    }

    @Test
    @DisplayName("TestArray字面量不加引号")
    void testArrayLiteralNotQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("arr");
        prop.setInitializer("[1, 2, 3]");

        String code = generator.generate(prop);

        assertTrue(code.contains("= [1, 2, 3]"));
    }

    @Test
    @DisplayName("Test new Expression不加引号")
    void testNewExpressionNotQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("arr");
        prop.setInitializer("new Array(10)");

        String code = generator.generate(prop);

        assertTrue(code.contains("= new Array(10)"));
    }

    @Test
    @DisplayName("Test资源引用不加引号")
    void testResourceReferenceNotQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("res");
        prop.setInitializer("$r('app.string.name')");

        String code = generator.generate(prop);

        assertTrue(code.contains("= $r('app.string.name')"));
    }

    @Test
    @DisplayName("Test null 和不加引号")
    void testNullNotQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("nothing");
        prop.setInitializer("null");

        String code = generator.generate(prop);

        assertTrue(code.contains("= null"));
    }

    @DisplayName("Test undefined 不加引号")
    void testUndefinedNotQuoted() {
        CodeGenerator generator = new CodeGenerator();

        PropertyDeclaration prop = new PropertyDeclaration("nothing");
        prop.setInitializer("undefined");

        String code = generator.generate(prop);

        assertTrue(code.contains("= undefined"));
    }
}
