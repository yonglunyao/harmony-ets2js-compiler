package com.ets2jsc.integration;

import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.parser.AstBuilder;
import com.ets2jsc.transformer.ComponentTransformer;
import com.ets2jsc.transformer.DecoratorTransformer;
import com.ets2jsc.transformer.BuildMethodTransformer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for pure JavaScript compilation mode.
 * Pure JavaScript mode generates code without ArkUI runtime dependencies.
 */
@DisplayName("Pure JavaScript Mode Tests")
public class PureJavaScriptTest {

    @Test
    @DisplayName("Should generate standard if-else without ArkUI runtime")
    public void testPureJavaScriptIfStatement() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/statements/test-if.ets")
        );

        // Parse source
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-if.ets", sourceCode);

        // Apply transformations to convert if statements to IfStatement nodes
        DecoratorTransformer decoratorTransformer = new DecoratorTransformer(true);
        BuildMethodTransformer buildMethodTransformer = new BuildMethodTransformer(true);
        ComponentTransformer componentTransformer = new ComponentTransformer();

        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            AstNode node = sourceFile.getStatements().get(i);
            if (node instanceof ClassDeclaration) {
                node = decoratorTransformer.transform(node);
                node = buildMethodTransformer.transform(node);
                node = componentTransformer.transform(node);
                sourceFile.getStatements().set(i, node);
            }
        }

        // Generate code with pure JavaScript mode
        CompilerConfig config = CompilerConfig.createDefault();
        config.setPureJavaScript(true);
        CodeGenerator generator = new CodeGenerator(config);
        String jsCode = generator.generate(sourceFile);

        // Verify output doesn't contain ArkUI runtime calls
        assertFalse(jsCode.contains("If.create()"), "Should not contain If.create()");
        assertFalse(jsCode.contains("If.branchId("), "Should not contain If.branchId()");
        assertFalse(jsCode.contains("If.pop()"), "Should not contain If.pop()");

        // Verify it contains standard if-else
        assertTrue(jsCode.contains("if ("), "Should contain standard if statement");
    }

    @Test
    @DisplayName("Should generate ArkUI runtime when pureJavaScript is false")
    public void testArkUIRuntimeIfStatement() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/statements/test-if.ets")
        );

        // Parse source
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-if.ets", sourceCode);

        // Apply transformations to convert if statements to IfStatement nodes
        DecoratorTransformer decoratorTransformer = new DecoratorTransformer(true);
        BuildMethodTransformer buildMethodTransformer = new BuildMethodTransformer(true);
        ComponentTransformer componentTransformer = new ComponentTransformer();

        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            AstNode node = sourceFile.getStatements().get(i);
            if (node instanceof ClassDeclaration) {
                node = decoratorTransformer.transform(node);
                node = buildMethodTransformer.transform(node);
                node = componentTransformer.transform(node);
                sourceFile.getStatements().set(i, node);
            }
        }

        // Generate code without pure JavaScript mode (default)
        CompilerConfig config = CompilerConfig.createDefault();
        config.setPureJavaScript(false);
        CodeGenerator generator = new CodeGenerator(config);
        String jsCode = generator.generate(sourceFile);

        // Print for debugging
        System.out.println("=== ArkUI Runtime Mode Output ===");
        System.out.println(jsCode);
        System.out.println("=== End Output ===");

        // Verify output contains ArkUI runtime calls
        assertTrue(jsCode.contains("If.create()"), "Should contain If.create()\nActual output:\n" + jsCode);
        assertTrue(jsCode.contains("If.branchId("), "Should contain If.branchId()\nActual output:\n" + jsCode);
        assertTrue(jsCode.contains("If.pop()"), "Should contain If.pop()\nActual output:\n" + jsCode);
    }

    @Test
    @DisplayName("Should use Array.forEach in pure JavaScript mode")
    public void testPureJavaScriptForEach() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/statements/foreach.ets")
        );

        // Parse source
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("foreach.ets", sourceCode);

        // Apply transformations
        DecoratorTransformer decoratorTransformer = new DecoratorTransformer(true);
        BuildMethodTransformer buildMethodTransformer = new BuildMethodTransformer(true);
        ComponentTransformer componentTransformer = new ComponentTransformer();

        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            AstNode node = sourceFile.getStatements().get(i);
            if (node instanceof ClassDeclaration) {
                node = decoratorTransformer.transform(node);
                node = buildMethodTransformer.transform(node);
                node = componentTransformer.transform(node);
                sourceFile.getStatements().set(i, node);
            }
        }

        // Generate code with pure JavaScript mode
        CompilerConfig config = CompilerConfig.createDefault();
        config.setPureJavaScript(true);
        CodeGenerator generator = new CodeGenerator(config);
        String jsCode = generator.generate(sourceFile);

        // Verify output doesn't contain ForEach runtime calls
        assertFalse(jsCode.contains("ForEach.create()"), "Should not contain ForEach.create()");
        assertFalse(jsCode.contains("ForEach.pop()"), "Should not contain ForEach.pop()");
    }
}
