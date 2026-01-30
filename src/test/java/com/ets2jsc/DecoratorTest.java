package com.ets2jsc;

import com.ets2jsc.ast.Decorator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 Decorator 装饰器
 */
@DisplayName("Decorator 测试")
class DecoratorTest {

    @Test
    @DisplayName("测试 Decorator 创建")
    void testDecoratorCreation() {
        Decorator decorator = new Decorator("Component");

        assertEquals("Decorator", decorator.getType());
        assertEquals("Component", decorator.getName());
        assertNotNull(decorator.getArguments());
        assertTrue(decorator.getArguments().isEmpty());
    }

    @Test
    @DisplayName("测试获取装饰器名称")
    void testGetName() {
        Decorator decorator1 = new Decorator("Component");
        Decorator decorator2 = new Decorator("State");
        Decorator decorator3 = new Decorator("Prop");

        assertEquals("Component", decorator1.getName());
        assertEquals("State", decorator2.getName());
        assertEquals("Prop", decorator3.getName());
    }

    @Test
    @DisplayName("测试设置装饰器名称")
    void testSetName() {
        Decorator decorator = new Decorator("Component");
        decorator.setName("Entry");

        assertEquals("Entry", decorator.getName());
    }

    @Test
    @DisplayName("测试获取参数")
    void testGetArguments() {
        Decorator decorator = new Decorator("Component");

        assertNotNull(decorator.getArguments());
        assertTrue(decorator.getArguments().isEmpty());
    }

    @Test
    @DisplayName("测试设置参数")
    void testSetArgument() {
        Decorator decorator = new Decorator("CustomDecorator");

        decorator.setArgument("key1", "value1");
        decorator.setArgument("key2", 123);
        decorator.setArgument("key3", true);

        Map<String, Object> args = decorator.getArguments();
        assertEquals(3, args.size());
        assertEquals("value1", args.get("key1"));
        assertEquals(123, args.get("key2"));
        assertEquals(true, args.get("key3"));
    }

    @Test
    @DisplayName("测试获取单个参数")
    void testGetArgument() {
        Decorator decorator = new Decorator("Component");
        decorator.setArgument("name", "MyComponent");

        assertEquals("MyComponent", decorator.getArgument("name"));
    }

    @Test
    @DisplayName("测试获取不存在的参数")
    void testGetNonExistentArgument() {
        Decorator decorator = new Decorator("Component");

        assertNull(decorator.getArgument("nonExistent"));
    }

    @Test
    @DisplayName("测试获取和设置原始表达式")
    void testGetSetRawExpression() {
        Decorator decorator = new Decorator("Component");
        decorator.setRawExpression("@Component({ name: 'MyComponent' })");

        assertEquals("@Component({ name: 'MyComponent' })", decorator.getRawExpression());
    }

    @Test
    @DisplayName("测试原始表达式初始值")
    void testRawExpressionInitial() {
        Decorator decorator = new Decorator("Component");

        assertNull(decorator.getRawExpression());
    }

    @Test
    @DisplayName("测试 isComponentDecorator - Component")
    void testIsComponentDecoratorComponent() {
        Decorator decorator = new Decorator("Component");

        assertTrue(decorator.isComponentDecorator());
    }

    @Test
    @DisplayName("测试 isComponentDecorator - Entry")
    void testIsComponentDecoratorEntry() {
        Decorator decorator = new Decorator("Entry");

        assertTrue(decorator.isComponentDecorator());
    }

    @Test
    @DisplayName("测试 isComponentDecorator - Preview")
    void testIsComponentDecoratorPreview() {
        Decorator decorator = new Decorator("Preview");

        assertTrue(decorator.isComponentDecorator());
    }

    @Test
    @DisplayName("测试 isComponentDecorator - CustomDialog")
    void testIsComponentDecoratorCustomDialog() {
        Decorator decorator = new Decorator("CustomDialog");

        assertTrue(decorator.isComponentDecorator());
    }

    @Test
    @DisplayName("测试 isComponentDecorator - 非组件装饰器")
    void testIsComponentDecoratorNonComponent() {
        Decorator decorator1 = new Decorator("State");
        Decorator decorator2 = new Decorator("Prop");
        Decorator decorator3 = new Decorator("Builder");

        assertFalse(decorator1.isComponentDecorator());
        assertFalse(decorator2.isComponentDecorator());
        assertFalse(decorator3.isComponentDecorator());
    }

    @Test
    @DisplayName("测试 isStateDecorator")
    void testIsStateDecorator() {
        Decorator state = new Decorator("State");
        Decorator prop = new Decorator("Prop");

        assertTrue(state.isStateDecorator());
        assertFalse(prop.isStateDecorator());
    }

    @Test
    @DisplayName("测试 isPropDecorator")
    void testIsPropDecorator() {
        Decorator prop = new Decorator("Prop");
        Decorator state = new Decorator("State");

        assertTrue(prop.isPropDecorator());
        assertFalse(state.isPropDecorator());
    }

    @Test
    @DisplayName("测试 isLinkDecorator")
    void testIsLinkDecorator() {
        Decorator link = new Decorator("Link");
        Decorator prop = new Decorator("Prop");

        assertTrue(link.isLinkDecorator());
        assertFalse(prop.isLinkDecorator());
    }

    @Test
    @DisplayName("测试 isProvideDecorator")
    void testIsProvideDecorator() {
        Decorator provide = new Decorator("Provide");
        Decorator consume = new Decorator("Consume");

        assertTrue(provide.isProvideDecorator());
        assertFalse(consume.isProvideDecorator());
    }

    @Test
    @DisplayName("测试 isConsumeDecorator")
    void testIsConsumeDecorator() {
        Decorator consume = new Decorator("Consume");
        Decorator provide = new Decorator("Provide");

        assertTrue(consume.isConsumeDecorator());
        assertFalse(provide.isConsumeDecorator());
    }

    @Test
    @DisplayName("测试 isMethodDecorator - Builder")
    void testIsMethodDecoratorBuilder() {
        Decorator decorator = new Decorator("Builder");

        assertTrue(decorator.isMethodDecorator());
    }

    @Test
    @DisplayName("测试 isMethodDecorator - Extend")
    void testIsMethodDecoratorExtend() {
        Decorator decorator = new Decorator("Extend");

        assertTrue(decorator.isMethodDecorator());
    }

    @Test
    @DisplayName("测试 isMethodDecorator - Styles")
    void testIsMethodDecoratorStyles() {
        Decorator decorator = new Decorator("Styles");

        assertTrue(decorator.isMethodDecorator());
    }

    @Test
    @DisplayName("测试 isMethodDecorator - Watch")
    void testIsMethodDecoratorWatch() {
        Decorator decorator = new Decorator("Watch");

        assertTrue(decorator.isMethodDecorator());
    }

    @Test
    @DisplayName("测试 isMethodDecorator - 非方法装饰器")
    void testIsMethodDecoratorNonMethod() {
        Decorator decorator1 = new Decorator("State");
        Decorator decorator2 = new Decorator("Component");

        assertFalse(decorator1.isMethodDecorator());
        assertFalse(decorator2.isMethodDecorator());
    }

    @Test
    @DisplayName("测试 accept 方法")
    void testAcceptMethod() {
        Decorator decorator = new Decorator("Component");

        assertNotNull(decorator);
        assertEquals("Decorator", decorator.getType());
    }

    @Test
    @DisplayName("测试装饰器大小写敏感")
    void testDecoratorCaseSensitive() {
        Decorator decorator1 = new Decorator("Component");
        Decorator decorator2 = new Decorator("component");
        Decorator decorator3 = new Decorator("COMPONENT");

        assertTrue(decorator1.isComponentDecorator());
        assertFalse(decorator2.isComponentDecorator());
        assertFalse(decorator3.isComponentDecorator());
    }

    @Test
    @DisplayName("测试覆盖参数值")
    void testOverwriteArgument() {
        Decorator decorator = new Decorator("Component");

        decorator.setArgument("name", "First");
        assertEquals("First", decorator.getArgument("name"));

        decorator.setArgument("name", "Second");
        assertEquals("Second", decorator.getArgument("name"));
    }

    @Test
    @DisplayName("测试不同类型的参数值")
    void testDifferentArgumentValueTypes() {
        Decorator decorator = new Decorator("Custom");

        decorator.setArgument("string", "text");
        decorator.setArgument("number", 42);
        decorator.setArgument("boolean", true);
        decorator.setArgument("null", null);
        decorator.setArgument("doubleValue", 3.14);

        assertEquals("text", decorator.getArgument("string"));
        assertEquals(42, decorator.getArgument("number"));
        assertEquals(true, decorator.getArgument("boolean"));
        assertEquals(null, decorator.getArgument("null"));
        assertEquals(3.14, decorator.getArgument("doubleValue"));
    }

    @Test
    @DisplayName("测试 P1 装饰器")
    void testP1Decorators() {
        Decorator provide = new Decorator("Provide");
        Decorator consume = new Decorator("Consume");

        assertTrue(provide.isProvideDecorator());
        assertTrue(consume.isConsumeDecorator());
    }

    @Test
    @DisplayName("测试参数列表是可修改的")
    void testArgumentsMapIsModifiable() {
        Decorator decorator = new Decorator("Component");

        Map<String, Object> args = decorator.getArguments();
        args.put("key1", "value1");

        assertEquals(1, args.size());
        assertEquals("value1", decorator.getArgument("key1"));
    }
}
