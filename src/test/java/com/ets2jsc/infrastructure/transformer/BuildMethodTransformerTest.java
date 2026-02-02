package com.ets2jsc.infrastructure.transformer;

import com.ets2jsc.domain.model.ast.*;
import com.ets2jsc.shared.constant.Decorators;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BuildMethodTransformer.
 */
@DisplayName("BuildMethodTransformer Tests")
class BuildMethodTransformerTest {

    @Test
    @DisplayName("Test constructor with partial update mode")
    void testConstructorWithPartialUpdateMode() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(true);

        assertNotNull(transformer);
        assertEquals("initialRender", transformer.getRenderMethodName());
    }

    @Test
    @DisplayName("Test constructor without partial update mode")
    void testConstructorWithoutPartialUpdateMode() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);

        assertNotNull(transformer);
        assertEquals("render", transformer.getRenderMethodName());
    }

    @Test
    @DisplayName("Test canTransform returns true for Component class")
    void testCanTransformReturnsTrueForComponentClass() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        ClassDeclaration classDecl = new ClassDeclaration("App");
        classDecl.addDecorator(new Decorator(Decorators.COMPONENT));

        assertTrue(transformer.canTransform(classDecl));
    }

    @Test
    @DisplayName("Test canTransform returns false for regular class")
    void testCanTransformReturnsFalseForRegularClass() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        ClassDeclaration classDecl = new ClassDeclaration("App");

        assertFalse(transformer.canTransform(classDecl));
    }

    @Test
    @DisplayName("Test canTransform returns true for build method")
    void testCanTransformReturnsTrueForBuildMethod() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        MethodDeclaration method = new MethodDeclaration("build");

        assertTrue(transformer.canTransform(method));
    }

    @Test
    @DisplayName("Test canTransform returns false for regular method")
    void testCanTransformReturnsFalseForRegularMethod() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        MethodDeclaration method = new MethodDeclaration("foo");

        assertFalse(transformer.canTransform(method));
    }

    @Test
    @DisplayName("Test transform returns same node for unsupported types")
    void testTransformReturnsSameNodeForUnsupportedTypes() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        MethodDeclaration method = new MethodDeclaration("foo");

        AstNode result = transformer.transform(method);

        assertSame(method, result);
    }

    @Test
    @DisplayName("Test transform renames build method to render")
    void testTransformRenamesBuildMethodToRender() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        MethodDeclaration method = new MethodDeclaration("build");

        AstNode result = transformer.transform(method);

        assertTrue(result instanceof MethodDeclaration);
        MethodDeclaration transformed = (MethodDeclaration) result;
        assertEquals("render", transformed.getName());
    }

    @Test
    @DisplayName("Test transform renames build method to initialRender in partial mode")
    void testTransformRenamesBuildMethodToInitialRenderInPartialMode() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(true);
        MethodDeclaration method = new MethodDeclaration("build");

        AstNode result = transformer.transform(method);

        assertTrue(result instanceof MethodDeclaration);
        MethodDeclaration transformed = (MethodDeclaration) result;
        assertEquals("initialRender", transformed.getName());
    }

    @Test
    @DisplayName("Test transform class with Component decorator")
    void testTransformClassWithComponentDecorator() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        ClassDeclaration classDecl = new ClassDeclaration("App");
        classDecl.addDecorator(new Decorator(Decorators.COMPONENT));

        MethodDeclaration buildMethod = new MethodDeclaration("build");
        classDecl.addMember(buildMethod);

        AstNode result = transformer.transform(classDecl);

        assertTrue(result instanceof ClassDeclaration);
        ClassDeclaration transformed = (ClassDeclaration) result;
        assertEquals("App", transformed.getName());
    }

    @Test
    @DisplayName("Test getRenderMethodName returns render in normal mode")
    void testGetRenderMethodNameReturnsRenderInNormalMode() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);

        assertEquals("render", transformer.getRenderMethodName());
    }

    @Test
    @DisplayName("Test getRenderMethodName returns initialRender in partial mode")
    void testGetRenderMethodNameReturnsInitialRenderInPartialMode() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(true);

        assertEquals("initialRender", transformer.getRenderMethodName());
    }

    @Test
    @DisplayName("Test transform with null body")
    void testTransformWithNullBody() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        MethodDeclaration method = new MethodDeclaration("build");
        method.setBody(null);

        AstNode result = transformer.transform(method);

        assertTrue(result instanceof MethodDeclaration);
        assertEquals("render", ((MethodDeclaration) result).getName());
    }

    @Test
    @DisplayName("Test transform non-build method is unchanged")
    void testTransformNonBuildMethodIsUnchanged() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        MethodDeclaration method = new MethodDeclaration("customMethod");

        AstNode result = transformer.transform(method);

        assertEquals("customMethod", ((MethodDeclaration) result).getName());
    }

    @Test
    @DisplayName("Test canTransform for builder method")
    void testCanTransformForBuilderMethod() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        MethodDeclaration method = new MethodDeclaration("myBuilder");
        method.addDecorator(new Decorator(Decorators.BUILDER));

        assertTrue(transformer.canTransform(method));
    }

    @Test
    @DisplayName("Test canTransform returns false for method without decorators")
    void testCanTransformReturnsFalseForMethodWithoutDecorators() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        MethodDeclaration method = new MethodDeclaration("customMethod");

        assertFalse(transformer.canTransform(method));
    }

    @Test
    @DisplayName("Test transform preserves decorators")
    void testTransformPreservesDecorators() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        MethodDeclaration method = new MethodDeclaration("build");
        Decorator decorator = new Decorator("Custom");
        method.addDecorator(decorator);

        transformer.transform(method);

        assertFalse(method.getDecorators().isEmpty());
    }

    @Test
    @DisplayName("Test transform preserves parameters")
    void testTransformPreservesParameters() {
        BuildMethodTransformer transformer = new BuildMethodTransformer(false);
        MethodDeclaration method = new MethodDeclaration("build");
        MethodDeclaration.Parameter param = new MethodDeclaration.Parameter("param1", "string");
        method.addParameter(param);

        transformer.transform(method);

        assertFalse(method.getParameters().isEmpty());
        assertEquals("param1", method.getParameters().get(0).getName());
    }
}
