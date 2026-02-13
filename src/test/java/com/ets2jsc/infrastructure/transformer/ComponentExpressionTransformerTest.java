package com.ets2jsc.infrastructure.transformer;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.ComponentStatement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ComponentExpressionTransformer.
 */
@DisplayName("ComponentExpressionTransformer Tests")
class ComponentExpressionTransformerTest {

    @Test
    @DisplayName("Test transform returns empty for null input")
    void testTransformReturnsEmptyForNullInput() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform(null);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test transform returns empty for empty string")
    void testTransformReturnsEmptyForEmptyString() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test transform returns empty for whitespace only")
    void testTransformReturnsEmptyForWhitespaceOnly() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("   ");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test transform returns empty for lowercase component name")
    void testTransformReturnsEmptyForLowercaseComponentName() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("text('Hello')");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test transform returns empty for non-component call")
    void testTransformReturnsEmptyForNonComponentCall() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("foo.bar()");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test transform returns empty for custom component")
    void testTransformReturnsEmptyForCustomComponent() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("MyCustomComponent('Hello')");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test transform returns empty for this property access")
    void testTransformReturnsEmptyForThisPropertyAccess() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Text(this.message)");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test transform returns empty for $r resource reference")
    void testTransformReturnsEmptyForResourceReference() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Text($r('app.string.name'))");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test transform returns empty for $rawfile reference")
    void testTransformReturnsEmptyForRawfileReference() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Text($rawfile('test.png'))");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test transform returns empty for arrow function callback")
    void testTransformReturnsEmptyForArrowFunctionCallback() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Button('Click').onClick(() => {})");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test transform for simple Text component")
    void testTransformForSimpleTextComponent() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Text('Hello')");

        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result.get();
        assertEquals("Text", stmt.getComponentName());
        assertEquals(2, stmt.getParts().size()); // CREATE, POP
    }

    @Test
    @DisplayName("Test transform for Text component with no arguments")
    void testTransformForTextComponentWithNoArguments() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Text()");

        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result.get();
        assertEquals("Text", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for Column component")
    void testTransformForColumnComponent() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Column()");

        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result.get();
        assertEquals("Column", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for Row component")
    void testTransformForRowComponent() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Row()");

        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result.get();
        assertEquals("Row", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for Stack component")
    void testTransformForStackComponent() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Stack()");

        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result.get();
        assertEquals("Stack", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for Button component")
    void testTransformForButtonComponent() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Button('Click me')");

        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result.get();
        assertEquals("Button", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform preserves component name")
    void testTransformPreservesComponentName() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Image('test.png')");

        assertTrue(result.isPresent());
        ComponentStatement stmt = (ComponentStatement) result.get();
        assertEquals("Image", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform handles whitespace in expression")
    void testTransformHandlesWhitespaceInExpression() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("  Text  (  'Hello'  )  ");

        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ComponentStatement);
    }

    @Test
    @DisplayName("Test transform for component with literal argument")
    void testTransformForComponentWithLiteralArgument() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Text(123)");

        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result.get();
        assertEquals("Text", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for Image component")
    void testTransformForImageComponent() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("Image('logo.png')");

        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result.get();
        assertEquals("Image", stmt.getComponentName());
    }

    @Test
    @DisplayName("Test transform for TextInput component")
    void testTransformForTextInputComponent() {
        Optional<AstNode> result = ComponentExpressionTransformer.transform("TextInput({ placeholder: 'Enter text' })");

        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ComponentStatement);

        ComponentStatement stmt = (ComponentStatement) result.get();
        assertEquals("TextInput", stmt.getComponentName());
    }
}
