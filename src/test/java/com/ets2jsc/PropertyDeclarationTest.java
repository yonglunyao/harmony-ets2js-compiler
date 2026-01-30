package com.ets2jsc;

import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.PropertyDeclaration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 PropertyDeclaration 属性声明类
 */
@DisplayName("属性声明测试")
class PropertyDeclarationTest {

    @Test
    @DisplayName("测试属性创建和基本属性")
    void testPropertyCreation() {
        PropertyDeclaration prop = new PropertyDeclaration("myProperty");

        assertEquals("myProperty", prop.getName());
        assertEquals("PropertyDeclaration", prop.getType());
        assertFalse(prop.isReadOnly());
        assertEquals(PropertyDeclaration.Visibility.INTERNAL, prop.getVisibility());
    }

    @Test
    @DisplayName("测试类型注解")
    void testTypeAnnotation() {
        PropertyDeclaration prop = new PropertyDeclaration("count");

        prop.setTypeAnnotation("number");
        assertEquals("number", prop.getTypeAnnotation());

        prop.setTypeAnnotation("string");
        assertEquals("string", prop.getTypeAnnotation());

        prop.setTypeAnnotation("boolean");
        assertEquals("boolean", prop.getTypeAnnotation());
    }

    @Test
    @DisplayName("测试初始化器")
    void testInitializer() {
        PropertyDeclaration prop = new PropertyDeclaration("message");

        prop.setInitializer("\"Hello\"");
        assertEquals("\"Hello\"", prop.getInitializer());

        prop.setInitializer("42");
        assertEquals("42", prop.getInitializer());

        prop.setInitializer("true");
        assertEquals("true", prop.getInitializer());
    }

    @Test
    @DisplayName("测试装饰器添加和检查")
    void testDecorators() {
        PropertyDeclaration prop = new PropertyDeclaration("count");

        assertFalse(prop.hasDecorator("State"));
        assertFalse(prop.hasDecorator("Prop"));

        Decorator stateDecorator = new Decorator("State");
        prop.addDecorator(stateDecorator);

        assertTrue(prop.hasDecorator("State"));
        assertFalse(prop.hasDecorator("Prop"));

        Decorator propDecorator = new Decorator("Prop");
        prop.addDecorator(propDecorator);

        assertTrue(prop.hasDecorator("State"));
        assertTrue(prop.hasDecorator("Prop"));
    }

    @Test
    @DisplayName("测试获取装饰器")
    void testGetDecorator() {
        PropertyDeclaration prop = new PropertyDeclaration("data");

        Decorator stateDecorator = new Decorator("State");
        prop.addDecorator(stateDecorator);

        Decorator retrieved = prop.getDecorator("State");
        assertNotNull(retrieved);
        assertEquals("State", retrieved.getName());

        Decorator notFound = prop.getDecorator("Prop");
        assertNull(notFound);
    }

    @Test
    @DisplayName("测试可见性设置")
    void testVisibility() {
        PropertyDeclaration prop = new PropertyDeclaration("secret");

        prop.setVisibility(PropertyDeclaration.Visibility.PRIVATE);
        assertEquals(PropertyDeclaration.Visibility.PRIVATE, prop.getVisibility());

        prop.setVisibility(PropertyDeclaration.Visibility.PUBLIC);
        assertEquals(PropertyDeclaration.Visibility.PUBLIC, prop.getVisibility());

        prop.setVisibility(PropertyDeclaration.Visibility.PROTECTED);
        assertEquals(PropertyDeclaration.Visibility.PROTECTED, prop.getVisibility());
    }

    @Test
    @DisplayName("测试只读属性")
    void testReadOnly() {
        PropertyDeclaration prop = new PropertyDeclaration("constant");

        assertFalse(prop.isReadOnly());

        prop.setReadOnly(true);
        assertTrue(prop.isReadOnly());

        prop.setReadOnly(false);
        assertFalse(prop.isReadOnly());
    }

    @Test
    @DisplayName("测试获取私有变量名")
    void testGetPrivateVarName() {
        PropertyDeclaration prop = new PropertyDeclaration("count");

        assertEquals("count__", prop.getPrivateVarName());

        PropertyDeclaration prop2 = new PropertyDeclaration("message");
        assertEquals("message__", prop2.getPrivateVarName());
    }

    @Test
    @DisplayName("测试获取 ObservedProperty 类型")
    void testGetObservedPropertyType() {
        PropertyDeclaration stateProp = new PropertyDeclaration("count");
        stateProp.addDecorator(new Decorator("State"));
        assertEquals("ObservedPropertySimple", stateProp.getObservedPropertyType());

        PropertyDeclaration propProp = new PropertyDeclaration("title");
        propProp.addDecorator(new Decorator("Prop"));
        assertEquals("SynchedPropertySimpleOneWay", propProp.getObservedPropertyType());

        PropertyDeclaration linkProp = new PropertyDeclaration("data");
        linkProp.addDecorator(new Decorator("Link"));
        assertEquals("SynchedPropertySimpleTwoWay", linkProp.getObservedPropertyType());
    }

    @Test
    @DisplayName("测试多个装饰器")
    void testMultipleDecorators() {
        PropertyDeclaration prop = new PropertyDeclaration("value");

        prop.addDecorator(new Decorator("State"));
        prop.addDecorator(new Decorator("CustomDecorator"));
        prop.addDecorator(new Decorator("AnotherDecorator"));

        assertTrue(prop.hasDecorator("State"));
        assertTrue(prop.hasDecorator("CustomDecorator"));
        assertTrue(prop.hasDecorator("AnotherDecorator"));
        assertFalse(prop.hasDecorator("Prop"));
    }

    @Test
    @DisplayName("测试带有各种装饰器的 ObservedProperty 类型")
    void testObservedPropertyTypeWithVariousDecorators() {
        // Provide decorator should use ObservedPropertySimple
        PropertyDeclaration provideProp = new PropertyDeclaration("theme");
        provideProp.addDecorator(new Decorator("Provide"));
        assertEquals("ObservedPropertySimple", provideProp.getObservedPropertyType());

        // Consume decorator should use ObservedPropertySimple
        PropertyDeclaration consumeProp = new PropertyDeclaration("inheritedTheme");
        consumeProp.addDecorator(new Decorator("Consume"));
        assertEquals("ObservedPropertySimple", consumeProp.getObservedPropertyType());
    }
}
