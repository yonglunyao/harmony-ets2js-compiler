package com.ets2jsc.infrastructure.transformer;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.ComponentStatement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ComponentExpressionTransformer.
 */
@DisplayName("ComponentExpressionTransformer Tests")
class ComponentExpressionTransformerTest {

    @Test
    @DisplayName("Test transform returns null for null input")
    void testTransformReturnsNullForNullInput() {
        AstNode result = ComponentExpressionTransformer.transform(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Test transform returns null for empty string")
    void testTransformReturnsNullForEmptyString() {
        AstNode result = ComponentExpressionTransformer.transform("");

        assertNull(result);
    }

    @Test
    @DisplayName("Test transform returns null for whitespace only")
    void testTransformReturnsNullForWhitespaceOnly() {
        AstNode result = ComponentExpressionTransformer.transform("   ");

        assertNull(result);
    }

    @Test
    @DisplayName("Test transform returns null for lowercase component name")
    void testTransformReturnsNullForLowercaseComponentName() {
        AstNode result = ComponentExpressionTransformer.transform("text('Hello')");

        assertNull(result);
    }

    @Test
    @DisplayName("Test transform returns null for non-component call")
    void testTransformReturnsNullForNonComponentCall() {
        AstNode result = ComponentExpressionTransformer.transform("foo.bar()");

        assertNull(result);
    }

    @Test
    @DisplayName("Test transform returns null for custom component")
    void testTransformReturnsNullForCustomComponent() {
        AstNode result = ComponentExpressionTransformer.transform("MyCustomComponent('Hello')");

        assertNull(result);
    }

    @Test
    @DisplayName("Test transform returns null for this property access")
    void testTransformReturnsNullForThisPropertyAccess() {
        AstNode result = ComponentExpressionTransformer.transform("Text(this.message)");

        assertNull(result);
    }

    @Test
    @DisplayName("Test transform returns null for $r resource reference")
    void testTransformReturnsNullForResourceReference() {
        AstNode result = ComponentExpressionTransformer.transform("Text($r('app.string.name'))");

        assertNull(result);
    }

    @Test
    @DisplayName("Test transform returns null for $rawfile reference")
    void testTransformReturnsNullForRawfileReference() {
        AstNode result = ComponentExpressionTransformer.transform("Text($rawfile('test.png'))");

        assertNull(result);
    }

    @Test
    @DisplayName("Test transform returns null for arrow function callback")
    void testTransformReturnsNullForArrowFunctionCallback() {
        AstNode result = ComponentExpressionTransformer.transform("Button('Click').onClick(() => {})");

        assertNull(result);
    }

    @Test
    @DisplayName("Test transform for simple Text component")
    void testTransformForSimpleTextComponent() {
        AstNode result = ComponentExpressionTransformer.transform("Text('Hello')");

        assertNotNull(result);
        assertTrue(result instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result;
        assertEquals("Text", stmt.getComponentName());
        assertEquals(2, stmt.getParts().size()); // CREATE, POP
    }

    @Test
    @DisplayName("Test transform for Text component with no arguments")
    void testTransformForTextComponentWithNoArguments() {
        AstNode result = ComponentExpressionTransformer.transform("Text()");

        assertNotNull(result);
        assertTrue(result instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result;
        assertEquals("Text", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for Column component")
    void testTransformForColumnComponent() {
        AstNode result = ComponentExpressionTransformer.transform("Column()");

        assertNotNull(result);
        assertTrue(result instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result;
        assertEquals("Column", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for Row component")
    void testTransformForRowComponent() {
        AstNode result = ComponentExpressionTransformer.transform("Row()");

        assertNotNull(result);
        assertTrue(result instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result;
        assertEquals("Row", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for Stack component")
    void testTransformForStackComponent() {
        AstNode result = ComponentExpressionTransformer.transform("Stack()");

        assertNotNull(result);
        assertTrue(result instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result;
        assertEquals("Stack", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for Button component")
    void testTransformForButtonComponent() {
        AstNode result = ComponentExpressionTransformer.transform("Button('Click me')");

        assertNotNull(result);
        assertTrue(result instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result;
        assertEquals("Button", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform preserves component name")
    void testTransformPreservesComponentName() {
        AstNode result = ComponentExpressionTransformer.transform("Image('test.png')");

        assertNotNull(result);
        ComponentStatement stmt = (ComponentStatement) result;
        assertEquals("Image", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform handles whitespace in expression")
    void testTransformHandlesWhitespaceInExpression() {
        AstNode result = ComponentExpressionTransformer.transform("  Text  (  'Hello'  )  ");

        assertNotNull(result);
        assertTrue(result instanceof ComponentStatement);
    }

    @Test
    @DisplayName("Test transform for component with literal argument")
    void testTransformForComponentWithLiteralArgument() {
        AstNode result = ComponentExpressionTransformer.transform("Text(123)");

        assertNotNull(result);
        assertTrue(result instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result;
        assertEquals("Text", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for Image component")
    void testTransformForImageComponent() {
        AstNode result = ComponentExpressionTransformer.transform("Image('logo.png')");

        assertNotNull(result);
        assertTrue(result instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result;
        assertEquals("Image", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for TextInput component")
    void testTransformForTextInputComponent() {
        AstNode result = ComponentExpressionTransformer.transform("TextInput({ placeholder: 'Enter text' })");

        assertNotNull(result);
        assertTrue(result instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result;
        assertEquals("TextInput", stmt.getComponentName());
    }
}
