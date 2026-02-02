package com.ets2jsc.context;

import com.ets2jsc.domain.model.ast.ClassDeclaration;
import com.ets2jsc.domain.model.ast.MethodDeclaration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

/**
 * Unit tests for TransformationContext.
 */
public class TransformationContextTest {

    @Test
    public void testConstructor_WithCompilationContext() {
        CompilationContext compCtx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );
        TransformationContext ctx = new TransformationContext(compCtx);

        assertSame(compCtx, ctx.getCompilationContext());
    }

    @Test
    public void testGetConfig() {
        CompilationContext compCtx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );
        TransformationContext ctx = new TransformationContext(compCtx);

        assertNotNull(ctx.getConfig());
    }

    @Test
    public void testIsPureJavaScript() {
        CompilationContext compCtx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );
        TransformationContext ctx = new TransformationContext(compCtx);

        assertFalse(ctx.isPureJavaScript());
    }

    @Test
    public void testIsPartialUpdateMode() {
        CompilationContext compCtx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );
        TransformationContext ctx = new TransformationContext(compCtx);

        assertTrue(ctx.isPartialUpdateMode());
    }

    @Test
    public void testGetCurrentClass() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        assertNull(ctx.getCurrentClass());
        assertFalse(ctx.isInsideClass());

        ClassDeclaration classDecl = new ClassDeclaration("Test");
        ctx.setCurrentClass(classDecl);

        assertSame(classDecl, ctx.getCurrentClass());
        assertTrue(ctx.isInsideClass());
    }

    @Test
    public void testGetCurrentMethod() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        assertNull(ctx.getCurrentMethod());
        assertFalse(ctx.isInsideMethod());

        MethodDeclaration method = new MethodDeclaration("test");
        ctx.setCurrentMethod(method);

        assertSame(method, ctx.getCurrentMethod());
        assertTrue(ctx.isInsideMethod());
    }

    @Test
    public void testIsInsideComponentClass_Initial() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        assertFalse(ctx.isInsideComponentClass());
        assertEquals(0, ctx.getCounter("componentDepth"));
    }

    @Test
    public void testEnterExitComponentClass() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        assertFalse(ctx.isInsideComponentClass());

        ctx.enterComponentClass();
        assertTrue(ctx.isInsideComponentClass());

        ctx.exitComponentClass();
        assertFalse(ctx.isInsideComponentClass());
    }

    @Test
    public void testEnterExitComponentClass_Nested() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        ctx.enterComponentClass();
        ctx.enterComponentClass();
        assertTrue(ctx.isInsideComponentClass());

        ctx.exitComponentClass();
        assertTrue(ctx.isInsideComponentClass());

        ctx.exitComponentClass();
        assertFalse(ctx.isInsideComponentClass());
    }

    @Test
    public void testSetAndGetAttribute() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        ctx.setAttribute("key", "value");
        assertEquals("value", ctx.getAttribute("key"));
    }

    @Test
    public void testGetAttribute_WithDefault() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        assertEquals("default", ctx.getAttribute("missing", "default"));
        ctx.setAttribute("key", "value");
        assertEquals("value", ctx.getAttribute("key", "default"));
    }

    @Test
    public void testGetCounter() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        assertEquals(0, ctx.getCounter("test"));
    }

    @Test
    public void testIncrementCounter() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        assertEquals(1, ctx.incrementCounter("test"));
        assertEquals(2, ctx.incrementCounter("test"));
        assertEquals(2, ctx.getCounter("test"));
    }

    @Test
    public void testResetCounter() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        ctx.incrementCounter("test");
        ctx.incrementCounter("test");
        assertEquals(2, ctx.getCounter("test"));

        ctx.resetCounter("test");
        assertEquals(0, ctx.getCounter("test"));
    }

    @Test
    public void testClearCounters() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        ctx.incrementCounter("test1");
        ctx.incrementCounter("test2");
        ctx.incrementCounter("test1");

        ctx.clearCounters();
        assertEquals(0, ctx.getCounter("test1"));
        assertEquals(0, ctx.getCounter("test2"));
    }

    @Test
    public void testGenerateUniqueId() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        String id1 = ctx.generateUniqueId("temp");
        String id2 = ctx.generateUniqueId("temp");
        String id3 = ctx.generateUniqueId("other");

        assertTrue(id1.startsWith("__temp_"));
        assertTrue(id2.startsWith("__temp_"));
        assertTrue(id3.startsWith("__other_"));

        assertNotEquals(id1, id2);
    }

    @Test
    public void testPushNode_ClassDeclaration() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        ClassDeclaration classDecl = new ClassDeclaration("Test");
        ctx.pushNode(classDecl);

        assertSame(classDecl, ctx.getCurrentClass());
    }

    @Test
    public void testPushNode_MethodDeclaration() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        MethodDeclaration method = new MethodDeclaration("test");
        ctx.pushNode(method);

        assertSame(method, ctx.getCurrentMethod());
    }

    @Test
    public void testPopNode_ClassDeclaration() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        ClassDeclaration classDecl = new ClassDeclaration("Test");
        ctx.pushNode(classDecl);
        assertSame(classDecl, ctx.getCurrentClass());

        ctx.popNode(classDecl);
        assertNull(ctx.getCurrentClass());
    }

    @Test
    public void testPopNode_MethodDeclaration() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        MethodDeclaration method = new MethodDeclaration("test");
        ctx.pushNode(method);
        assertSame(method, ctx.getCurrentMethod());

        ctx.popNode(method);
        assertNull(ctx.getCurrentMethod());
    }

    @Test
    public void testPopNode_DifferentNode() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        ClassDeclaration classDecl = new ClassDeclaration("Test");
        ctx.pushNode(classDecl);

        // Popping a different node should not clear the current class
        ClassDeclaration otherDecl = new ClassDeclaration("Other");
        ctx.popNode(otherDecl);
        assertSame(classDecl, ctx.getCurrentClass());
    }

    @Test
    public void testClearAttributes() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        ctx.setAttribute("key1", "value1");
        ctx.setAttribute("key2", "value2");
        assertTrue(ctx.hasAttribute("key1"));
        assertTrue(ctx.hasAttribute("key2"));

        ctx.clearAttributes();
        assertFalse(ctx.hasAttribute("key1"));
        assertFalse(ctx.hasAttribute("key2"));
    }

    @Test
    public void testToString() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        ClassDeclaration classDecl = new ClassDeclaration("Test");
        ctx.setCurrentClass(classDecl);

        String str = ctx.toString();
        assertTrue(str.contains("TransformationContext"));
        assertTrue(str.contains("Test"));
    }

    @Test
    public void testMultipleCounters() {
        TransformationContext ctx = new TransformationContext(
            new CompilationContext(Paths.get("/src/test.ets"), Paths.get("/build/test.js"))
        );

        assertEquals(1, ctx.incrementCounter("counter1"));
        assertEquals(1, ctx.incrementCounter("counter2"));
        assertEquals(2, ctx.incrementCounter("counter1"));
        assertEquals(2, ctx.incrementCounter("counter2"));

        assertEquals(2, ctx.getCounter("counter1"));
        assertEquals(2, ctx.getCounter("counter2"));
    }
}
