package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.transformer.DecoratorTransformer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 DecoratorTransformer 装饰器转换器
 */
@DisplayName("装饰器转换器测试")
class DecoratorTransformerTest {

    @Test
    @DisplayName("测试 @Component struct 转换为 class View")
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
    @DisplayName("测试 @State 属性转换")
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

        // 检查是否添加了构造函数
        boolean hasConstructor = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof MethodDeclaration && "constructor".equals(((MethodDeclaration) m).getName()));
        assertTrue(hasConstructor, "应该添加构造函数");

        // 检查是否移除了原始属性
        boolean hasOriginalProp = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof PropertyDeclaration && "count".equals(((PropertyDeclaration) m).getName()));
        assertFalse(hasOriginalProp, "原始属性应该被移除");

        // 检查是否添加了私有属性
        boolean hasPrivateProp = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof PropertyDeclaration && "count__".equals(((PropertyDeclaration) m).getName()));
        assertTrue(hasPrivateProp, "应该添加私有属性 count__");
    }

    @Test
    @DisplayName("测试 @Prop 属性转换")
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

        // Prop 属性不应该有构造函数初始化
        boolean hasConstructor = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof MethodDeclaration && "constructor".equals(((MethodDeclaration) m).getName()));
        assertFalse(hasConstructor, "Prop 属性不需要构造函数");
    }

    @Test
    @DisplayName("测试 @Link 属性转换")
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

        // 检查 getter/setter
        boolean hasGetter = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof MethodDeclaration && "get data".equals(((MethodDeclaration) m).getName()));
        assertTrue(hasGetter, "应该添加 getter");

        boolean hasSetter = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof MethodDeclaration && "set data".equals(((MethodDeclaration) m).getName()));
        assertTrue(hasSetter, "应该添加 setter");
    }

    @Test
    @DisplayName("测试 @Provide/@Consume 属性转换")
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

        // 检查 Provide 属性有初始化器
        boolean hasProvidePrivateProp = transformed.getMembers().stream()
            .filter(m -> m instanceof PropertyDeclaration)
            .map(m -> (PropertyDeclaration) m)
            .anyMatch(p -> "theme__".equals(p.getName()) && p.getInitializer() != null);
        assertTrue(hasProvidePrivateProp, "Provide 属性的私有变量应该有初始化器");
    }

    @Test
    @DisplayName("测试部分更新模式")
    void testPartialUpdateMode() {
        DecoratorTransformer partialModeTransformer = new DecoratorTransformer(true);
        DecoratorTransformer standardModeTransformer = new DecoratorTransformer(false);

        // 没有装饰器的类不能被转换
        ClassDeclaration plainClass = new ClassDeclaration("Test");
        assertEquals(false, partialModeTransformer.canTransform(plainClass));
        assertEquals(false, standardModeTransformer.canTransform(plainClass));

        // 带 Component 装饰器的类可以被转换
        ClassDeclaration componentClass = new ClassDeclaration("Test");
        componentClass.addDecorator(new Decorator("Component"));
        assertEquals(true, partialModeTransformer.canTransform(componentClass));
        assertEquals(true, standardModeTransformer.canTransform(componentClass));
    }

    @Test
    @DisplayName("测试转换能力检查")
    void testCanTransform() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        // 带 Component 装饰器的类可以转换
        ClassDeclaration componentClass = new ClassDeclaration("Test");
        componentClass.addDecorator(new Decorator("Component"));
        assertTrue(transformer.canTransform(componentClass));

        // 不带装饰器的类不能转换
        ClassDeclaration plainClass = new ClassDeclaration("Plain");
        assertFalse(transformer.canTransform(plainClass));

        // 带 State 装饰器的属性可以转换
        PropertyDeclaration stateProp = new PropertyDeclaration("count");
        stateProp.addDecorator(new Decorator("State"));
        assertTrue(transformer.canTransform(stateProp));

        // 带 Prop 装饰器的属性可以转换
        PropertyDeclaration propProp = new PropertyDeclaration("title");
        propProp.addDecorator(new Decorator("Prop"));
        assertTrue(transformer.canTransform(propProp));

        // 带 Link 装饰器的属性可以转换
        PropertyDeclaration linkProp = new PropertyDeclaration("data");
        linkProp.addDecorator(new Decorator("Link"));
        assertTrue(transformer.canTransform(linkProp));
    }

    @Test
    @DisplayName("测试多个状态属性")
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

        // 应该有一个构造函数
        long constructorCount = transformed.getMembers().stream()
            .filter(m -> m instanceof MethodDeclaration && "constructor".equals(((MethodDeclaration) m).getName()))
            .count();
        assertEquals(1, constructorCount, "应该有一个构造函数");

        // 应该有 3 个私有属性
        long privatePropsCount = transformed.getMembers().stream()
            .filter(m -> m instanceof PropertyDeclaration)
            .map(m -> (PropertyDeclaration) m)
            .filter(p -> p.getName().endsWith("__"))
            .count();
        assertEquals(3, privatePropsCount, "应该有 3 个私有属性");

        // 应该有 3 对 getter/setter
        long getterCount = transformed.getMembers().stream()
            .filter(m -> m instanceof MethodDeclaration)
            .map(m -> (MethodDeclaration) m)
            .filter(m -> m.getName().startsWith("get "))
            .count();
        assertEquals(3, getterCount, "应该有 3 个 getter");

        long setterCount = transformed.getMembers().stream()
            .filter(m -> m instanceof MethodDeclaration)
            .map(m -> (MethodDeclaration) m)
            .filter(m -> m.getName().startsWith("set "))
            .count();
        assertEquals(3, setterCount, "应该有 3 个 setter");
    }

    @Test
    @DisplayName("测试混合装饰器属性")
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

        // 检查是否有构造函数（State 需要）
        boolean hasConstructor = transformed.getMembers().stream()
            .anyMatch(m -> m instanceof MethodDeclaration && "constructor".equals(((MethodDeclaration) m).getName()));
        assertTrue(hasConstructor, "State 属性需要构造函数");
    }

    @Test
    @DisplayName("测试方法装饰器转换")
    void testMethodDecoratorTransformation() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        MethodDeclaration builderMethod = new MethodDeclaration("build");
        builderMethod.addDecorator(new Decorator("Builder"));

        AstNode result = transformer.transform(builderMethod);
        assertInstanceOf(MethodDeclaration.class, result);

        MethodDeclaration transformed = (MethodDeclaration) result;

        // Builder 方法应该添加 __builder__ 参数
        boolean hasBuilderParam = transformed.getParameters().stream()
            .anyMatch(p -> "__builder__".equals(p.getName()));
        assertTrue(hasBuilderParam, "Builder 方法应该有 __builder__ 参数");
    }

    @Test
    @DisplayName("测试导出类")
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
    @DisplayName("测试 @Entry 装饰器")
    void testEntryDecorator() {
        DecoratorTransformer transformer = new DecoratorTransformer(true);

        // @Entry 是一个组件装饰器，类可以被转换
        ClassDeclaration classDecl = new ClassDeclaration("EntryPage");
        classDecl.addDecorator(new Decorator("Entry"));
        classDecl.addDecorator(new Decorator("Component"));

        assertTrue(transformer.canTransform(classDecl));

        // 转换后的类仍然保持原有的 export 状态
        AstNode result = transformer.transform(classDecl);
        assertInstanceOf(ClassDeclaration.class, result);

        ClassDeclaration transformed = (ClassDeclaration) result;
        // 如果原本不是 export，转换后也不会变成 export
        assertEquals(classDecl.isExport(), transformed.isExport());
    }
}
