package com.ets2jsc.context;

import com.ets2jsc.domain.model.config.CompilerConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

/**
 * Unit tests for CompilationContext.
 */
public class CompilationContextTest {

    @Test
    public void testConstructor_WithPaths() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertEquals(Paths.get("/src/test.ets"), ctx.getSourcePath());
        assertEquals(Paths.get("/build/test.js"), ctx.getOutputPath());
    }

    @Test
    public void testConstructor_WithConfig() {
        CompilerConfig config = CompilerConfig.createDefault();
        CompilationContext ctx = new CompilationContext(
            config,
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertSame(config, ctx.getConfig());
    }

    @Test
    public void testGetConfig_Default() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertNotNull(ctx.getConfig());
    }

    @Test
    public void testGetSourceFileName_WithExtension() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertEquals("test", ctx.getSourceFileName());
    }

    @Test
    public void testGetSourceFileName_WithoutExtension() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test"),
            Paths.get("/build/test.js")
        );

        assertEquals("test", ctx.getSourceFileName());
    }

    @Test
    public void testGetSourceFileName_NullPath() {
        CompilationContext ctx = new CompilationContext(
            (CompilerConfig) null,
            null,
            Paths.get("/build/test.js")
        );

        assertEquals("default", ctx.getSourceFileName());
    }

    @Test
    public void testIsPureJavaScript_Default() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertFalse(ctx.isPureJavaScript());
    }

    @Test
    public void testIsPartialUpdateMode_Default() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertTrue(ctx.isPartialUpdateMode());
    }

    @Test
    public void testIsGenerateSourceMap_Default() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertTrue(ctx.isGenerateSourceMap());
    }

    @Test
    public void testSetAndGetAttribute() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        ctx.setAttribute("key", "value");
        assertEquals("value", ctx.getAttribute("key"));
    }

    @Test
    public void testGetAttribute_WithDefault() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertEquals("default", ctx.getAttribute("missing", "default"));
        assertEquals("default", ctx.getAttribute("key", "default"));
        ctx.setAttribute("key", "value");
        assertEquals("value", ctx.getAttribute("key", "default"));
    }

    @Test
    public void testGetAttribute_Null() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertNull(ctx.getAttribute("missing"));
    }

    @Test
    public void testHasAttribute() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertFalse(ctx.hasAttribute("key"));
        ctx.setAttribute("key", "value");
        assertTrue(ctx.hasAttribute("key"));
    }

    @Test
    public void testRemoveAttribute() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        ctx.setAttribute("key", "value");
        assertTrue(ctx.hasAttribute("key"));

        Object removed = ctx.removeAttribute("key");
        assertEquals("value", removed);
        assertFalse(ctx.hasAttribute("key"));
    }

    @Test
    public void testRemoveAttribute_NotFound() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        assertNull(ctx.removeAttribute("missing"));
    }

    @Test
    public void testAddWarning() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        ctx.addWarning("warning1");
        ctx.addWarning("warning2");

        assertTrue(ctx.hasWarnings());
        assertEquals(2, ctx.getWarnings().size());
    }

    @Test
    public void testAddError() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        ctx.addError("error1");
        ctx.addError("error2");

        assertTrue(ctx.hasErrors());
        assertEquals(2, ctx.getErrors().size());
    }

    @Test
    public void testClearDiagnostics() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        ctx.addWarning("warning");
        ctx.addError("error");
        assertTrue(ctx.hasWarnings());
        assertTrue(ctx.hasErrors());

        ctx.clearDiagnostics();
        assertFalse(ctx.hasWarnings());
        assertFalse(ctx.hasErrors());
    }

    @Test
    public void testGetElapsedTime() throws InterruptedException {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        long elapsed = ctx.getElapsedTime();
        assertTrue(elapsed >= 0);

        Thread.sleep(10);
        long newElapsed = ctx.getElapsedTime();
        assertTrue(newElapsed >= elapsed);
    }

    @Test
    public void testGetSummary() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        ctx.addWarning("test warning");
        ctx.addError("test error");

        String summary = ctx.getSummary();
        assertTrue(summary.contains("test.ets"));
        assertTrue(summary.contains("test.js"));
        assertTrue(summary.contains("Warnings: 1"));
        assertTrue(summary.contains("Errors: 1"));
    }

    @Test
    public void testToString() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        String str = ctx.toString();
        assertTrue(str.contains("CompilationContext"));
        assertTrue(str.contains("test.ets"));
    }

    @Test
    public void testGenericAttribute() {
        CompilationContext ctx = new CompilationContext(
            Paths.get("/src/test.ets"),
            Paths.get("/build/test.js")
        );

        ctx.setAttribute("number", 42);
        ctx.setAttribute("string", "hello");
        ctx.setAttribute("boolean", true);

        assertEquals(42, (int) ctx.getAttribute("number"));
        assertEquals("hello", ctx.getAttribute("string"));
        assertEquals(true, (boolean) ctx.getAttribute("boolean"));
    }
}
