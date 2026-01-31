package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.transformer.DecoratorTransformer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test DecoratorTransformer Decorator转换器
 */
@DisplayName("Decorator转换器Test")
class DecoratorTransformerTest {

    @Test
    @DisplayName("Test @Component struct 转换is class View")
    void testComponentTransformation() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        ClassDeclaration classDecl = new ClassDeclaration("MyComponent");
        classDecl.addDecorator(new Decorator("Component"));
        classDecl.setStruct(true);

        AstNode result = transformer.transform(classDecl);

        assertInstanceOf(ClassDeclaration.class, result);
        ClassDeclaration transformed = (ClassDeclaration) result;

        assertFalse(transformed.isStruct());
        assertEquals("View", transformed.getSuperClass());
        assertTrue(transformed.hasDecorator("Component"));
    }

    @Test
    @DisplayName("Test @State 属性转换")
    void testStatePropertyTransformation() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        ClassDeclaration classDecl = new ClassDeclaration("StateTest");
        classDecl.addDecorator(new Decorator("Component"));
        classDecl.setStruct(true);

        PropertyDeclaration stateProp = new PropertyDeclaration("count");
        stateProp.setTypeAnnotation("number");
        stateProp.setInitializer("0");
        stateProp.addDecorator(new Decorator("State"));

        classDecl.addMember(stateProp);

        AstNode result = transformer.transform(classDecl);
        assertInstanceOf(ClassDeclaration.class, result);

        ClassDeclaration transformed = (ClassDeclaration) result;

        // Check是否Add了构造Function
        boolean hasConstructor = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof MethodDeclaration && "constructor".equals(((MethodDeclaration) m).getName()));
        assertTrue(hasConstructor, "shouldAdd构造Function");

        // Check是否移除了原始属性
        boolean hasOriginalProp = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof PropertyDeclaration && "count".equals(((PropertyDeclaration) m).getName()));
        assertFalse(hasOriginalProp, "原始属性should被移除");

        // Check是否Add了私有属性
        boolean hasPrivateProp = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof PropertyDeclaration && "count__".equals(((PropertyDeclaration) m).getName()));
        assertTrue(hasPrivateProp, "shouldAdd私有属性 count__");
    }

    @Test
    @DisplayName("Test @Prop 属性转换")
    void testPropPropertyTransformation() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        ClassDeclaration classDecl = new ClassDeclaration("PropTest");
        classDecl.addDecorator(new Decorator("Component"));
        classDecl.setStruct(true);

        PropertyDeclaration propProp = new PropertyDeclaration("title");
        propProp.setTypeAnnotation("string");
        propProp.addDecorator(new Decorator("Prop"));

        classDecl.addMember(propProp);

        AstNode result = transformer.transform(classDecl);
        assertInstanceOf(ClassDeclaration.class, result);

        ClassDeclaration transformed = (ClassDeclaration) result;

        // Prop 属性不should有构造Function初始化
        boolean hasConstructor = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof MethodDeclaration && "constructor".equals(((MethodDeclaration) m).getName()));
        assertFalse(hasConstructor, "Prop 属性不需要构造Function");
    }

    @Test
    @DisplayName("Test @Link 属性转换")
    void testLinkPropertyTransformation() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        ClassDeclaration classDecl = new ClassDeclaration("LinkTest");
        classDecl.addDecorator(new Decorator("Component"));
        classDecl.setStruct(true);

        PropertyDeclaration linkProp = new PropertyDeclaration("data");
        linkProp.setTypeAnnotation("Object");
        linkProp.addDecorator(new Decorator("Link"));

        classDecl.addMember(linkProp);

        AstNode result = transformer.transform(classDecl);
        assertInstanceOf(ClassDeclaration.class, result);

        ClassDeclaration transformed = (ClassDeclaration) result;

        // Check getter/setter
        boolean hasGetter = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof MethodDeclaration && "get data".equals(((MethodDeclaration) m).getName()));
        assertTrue(hasGetter, "shouldAdd getter");

        boolean hasSetter = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof MethodDeclaration && "set data".equals(((MethodDeclaration) m).getName()));
        assertTrue(hasSetter, "shouldAdd setter");
    }

    @Test
    @DisplayName("Test @Provide/@Consume 属性转换")
    void testProvideConsumeTransformation() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        ClassDeclaration classDecl = new ClassDeclaration("ProvideTest");
        classDecl.addDecorator(new Decorator("Component"));
        classDecl.setStruct(true);

        PropertyDeclaration provideProp = new PropertyDeclaration("theme");
        provideProp.setTypeAnnotation("string");
        provideProp.setInitializer("\"dark\"");
        provideProp.addDecorator(new Decorator("Provide"));

        PropertyDeclaration consumeProp = new PropertyDeclaration("inheritedTheme");
        consumeProp.setTypeAnnotation("string");
        consumeProp.addDecorator(new Decorator("Consume"));

        classDecl.addMember(provideProp);
        classDecl.addMember(consumeProp);

        AstNode result = transformer.transform(classDecl);
        assertInstanceOf(ClassDeclaration.class, result);

        ClassDeclaration transformed = (ClassDeclaration) result;

        // Check Provide 属性有Initializer
        boolean hasProvidePrivateProp = transformed.getMembers().stream()
            .filter(m -> m instanceof PropertyDeclaration)
            .map(m -> (PropertyDeclaration) m)
            .anyMatch(p -> "theme__".equals(p.getName()) && p.getInitializer() != null);
        assertTrue(hasProvidePrivateProp, "Provide 属性 ofPrivate Variableshould有Initializer");
    }

    @Test
    @DisplayName("Test部分UpdateMode")
    void testPartialUpdateMode() {
        DecoratorTransformer partialModeTransformer = new DecoratorTransformer(true);
        DecoratorTransformer standardModeTransformer = new DecoratorTransformer(false);

        // 没有Decorator ofClass不能被转换
        ClassDeclaration plainClass = new ClassDeclaration("Test");
        assertEquals(false, partialModeTransformer.canTransform(plainClass));
        assertEquals(false, standardModeTransformer.canTransform(plainClass));

        // 带 Component Decorator ofClass可以被转换
        ClassDeclaration componentClass = new ClassDeclaration("Test");
        componentClass.addDecorator(new Decorator("Component"));
        assertEquals(true, partialModeTransformer.canTransform(componentClass));
        assertEquals(true, standardModeTransformer.canTransform(componentClass));
    }

    @Test
    @DisplayName("Test转换能力Check")
    void testCanTransform() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        // 带 Component Decorator ofClass可以转换
        ClassDeclaration componentClass = new ClassDeclaration("Test");
        componentClass.addDecorator(new Decorator("Component"));
        assertTrue(transformer.canTransform(componentClass));

        // 不带Decorator ofClass不能转换
        ClassDeclaration plainClass = new ClassDeclaration("Plain");
        assertFalse(transformer.canTransform(plainClass));

        // 带 State Decorator of属性可以转换
        PropertyDeclaration stateProp = new PropertyDeclaration("count");
        stateProp.addDecorator(new Decorator("State"));
        assertTrue(transformer.canTransform(stateProp));

        // 带 Prop Decorator of属性可以转换
        PropertyDeclaration propProp = new PropertyDeclaration("title");
        propProp.addDecorator(new Decorator("Prop"));
        assertTrue(transformer.canTransform(propProp));

        // 带 Link Decorator of属性可以转换
        PropertyDeclaration linkProp = new PropertyDeclaration("data");
        linkProp.addDecorator(new Decorator("Link"));
        assertTrue(transformer.canTransform(linkProp));
    }

    @Test
    @DisplayName("TestMultipleStatus属性")
    void testMultipleStateProperties() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        ClassDeclaration classDecl = new ClassDeclaration("MultiState");
        classDecl.addDecorator(new Decorator("Component"));
        classDecl.setStruct(true);

        PropertyDeclaration count = new PropertyDeclaration("count");
        count.setTypeAnnotation("number");
        count.setInitializer("0");
        count.addDecorator(new Decorator("State"));

        PropertyDeclaration message = new PropertyDeclaration("message");
        message.setTypeAnnotation("string");
        message.setInitializer("\"Hello\"");
        message.addDecorator(new Decorator("State"));

        PropertyDeclaration active = new PropertyDeclaration("active");
        active.setTypeAnnotation("boolean");
        active.setInitializer("true");
        active.addDecorator(new Decorator("State"));

        classDecl.addMember(count);
        classDecl.addMember(message);
        classDecl.addMember(active);

        AstNode result = transformer.transform(classDecl);
        assertInstanceOf(ClassDeclaration.class, result);

        ClassDeclaration transformed = (ClassDeclaration) result;

        // should有一个构造Function
        long constructorCount = transformed.getMembers().stream()
            .filter(m -> m instanceof MethodDeclaration && "constructor".equals(((MethodDeclaration) m).getName()))
            .count();
        assertEquals(1, constructorCount, "should有一个构造Function");

        // should有 3 个私有属性
        long privatePropsCount = transformed.getMembers().stream()
            .filter(m -> m instanceof PropertyDeclaration)
            .map(m -> (PropertyDeclaration) m)
            .filter(p -> p.getName().endsWith("__"))
            .count();
        assertEquals(3, privatePropsCount, "should有 3 个私有属性");

        // should有 3 对 getter/setter
        long getterCount = transformed.getMembers().stream()
            .filter(m -> m instanceof MethodDeclaration)
            .map(m -> (MethodDeclaration) m)
            .filter(m -> m.getName().startsWith("get "))
            .count();
        assertEquals(3, getterCount, "should有 3 个 getter");

        long setterCount = transformed.getMembers().stream()
            .filter(m -> m instanceof MethodDeclaration)
            .map(m -> (MethodDeclaration) m)
            .filter(m -> m.getName().startsWith("set "))
            .count();
        assertEquals(3, setterCount, "should有 3 个 setter");
    }

    @Test
    @DisplayName("Test混合Decorator属性")
    void testMixedDecoratorProperties() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        ClassDeclaration classDecl = new ClassDeclaration("MixedDecorator");
        classDecl.addDecorator(new Decorator("Component"));
        classDecl.setStruct(true);

        PropertyDeclaration stateProp = new PropertyDeclaration("count");
        stateProp.addDecorator(new Decorator("State"));

        PropertyDeclaration propProp = new PropertyDeclaration("title");
        propProp.addDecorator(new Decorator("Prop"));

        PropertyDeclaration linkProp = new PropertyDeclaration("data");
        linkProp.addDecorator(new Decorator("Link"));

        PropertyDeclaration normalProp = new PropertyDeclaration("id");

        classDecl.addMember(stateProp);
        classDecl.addMember(propProp);
        classDecl.addMember(linkProp);
        classDecl.addMember(normalProp);

        AstNode result = transformer.transform(classDecl);
        assertInstanceOf(ClassDeclaration.class, result);

        ClassDeclaration transformed = (ClassDeclaration) result;

        // Check是否有构造Function（State 需要）
        boolean hasConstructor = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof MethodDeclaration && "constructor".equals(((MethodDeclaration) m).getName()));
        assertTrue(hasConstructor, "State 属性需要构造Function");
    }

    @Test
    @DisplayName("TestMethodDecorator转换")
    void testMethodDecoratorTransformation() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        MethodDeclaration builderMethod = new MethodDeclaration("build");
        builderMethod.addDecorator(new Decorator("Builder"));

        AstNode result = transformer.transform(builderMethod);
        assertInstanceOf(MethodDeclaration.class, result);

        MethodDeclaration transformed = (MethodDeclaration) result;

        // Builder MethodshouldAdd __builder__ Parameter
        boolean hasBuilderParam = transformed.getParameters().stream()
            .anyMatch(p -> "__builder__".equals(p.getName()));
        assertTrue(hasBuilderParam, "Builder Methodshould有 __builder__ Parameter");
    }

    @Test
    @DisplayName("TestExportClass")
    void testExportClass() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        ClassDeclaration classDecl = new ClassDeclaration("ExportedComponent");
        classDecl.addDecorator(new Decorator("Component"));
        classDecl.setExport(true);
        classDecl.setStruct(true);

        AstNode result = transformer.transform(classDecl);
        assertInstanceOf(ClassDeclaration.class, result);

        ClassDeclaration transformed = (ClassDeclaration) result;
        assertTrue(transformed.isExport());
    }

    @Test
    @DisplayName("Test @Entry Decorator")
    void testEntryDecorator() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        // @Entry 是一个ComponentDecorator，Class可以被转换
        ClassDeclaration classDecl = new ClassDeclaration("EntryPage");
        classDecl.addDecorator(new Decorator("Entry"));
        classDecl.addDecorator(new Decorator("Component"));

        assertTrue(transformer.canTransform(classDecl));

        // 转换后 ofClass仍然保持原有 of export Status
        AstNode result = transformer.transform(classDecl);
        assertInstanceOf(ClassDeclaration.class, result);

        ClassDeclaration transformed = (ClassDeclaration) result;
        // 如果原本不是 export，转换后也不会变成 export
        assertEquals(classDecl.isExport(), transformed.isExport());
    }
}
