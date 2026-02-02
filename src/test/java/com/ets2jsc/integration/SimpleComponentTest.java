package com.ets2jsc.integration;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.parser.AstBuilder;
import com.ets2jsc.transformer.BuildMethodTransformer;
import com.ets2jsc.transformer.DecoratorTransformer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for simple component compilation.
 */
@DisplayName("Simple Component Compilation Tests")
public class SimpleComponentTest {

    @Test
    @DisplayName("Should parse simple component")
    public void testParseSimpleComponent() throws Exception {
        // Load test fixture
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/basic/simple-component.ets")
        );

        // Parse source
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("simple-component.ets", sourceCode);

        // Verify structure
        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());

        // Find class declaration
        ClassDeclaration classDecl = findClassDeclaration(sourceFile);
        assertNotNull(classDecl);
        assertEquals("Hello", classDecl.getName());
        assertTrue(classDecl.hasDecorator("Component"));
    }

    @Test
    @DisplayName("Should transform struct to class")
    public void testTransformStructToClass() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/basic/simple-component.ets")
        );

        // Parse and transform
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("simple-component.ets", sourceCode);

        DecoratorTransformer transformer = new DecoratorTransformer(true);
        for (AstNode node : sourceFile.getStatements()) {
            if (node instanceof ClassDeclaration) {
                AstNode transformed = transformer.transform(node);
                ClassDeclaration classDecl = (ClassDeclaration) transformed;

                // Verify transformation
                assertEquals("View", classDecl.getSuperClass());
                assertFalse(classDecl.isStruct());
            }
        }
    }

    @Test
    @DisplayName("Should transform build method to render")
    public void testTransformBuildMethod() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/basic/simple-component.ets")
        );

        // Parse and transform
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("simple-component.ets", sourceCode);

        BuildMethodTransformer transformer = new BuildMethodTransformer(true);
        for (AstNode node : sourceFile.getStatements()) {
            if (node instanceof ClassDeclaration) {
                AstNode transformed = transformer.transform(node);
                ClassDeclaration classDecl = (ClassDeclaration) transformed;

                // Check if build method was renamed to initialRender
                boolean hasInitialRender = classDecl.getMethods().stream()
                    .anyMatch(m -> "initialRender".equals(m.getName()));

                assertTrue(hasInitialRender, "Should have initialRender method");
            }
        }
    }

    @Test
    @DisplayName("Should generate JavaScript code")
    public void testGenerateJavaScript() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/basic/simple-component.ets")
        );

        // Full compilation pipeline
        // Parse
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("simple-component.ets", sourceCode);

        // Apply transformations
        DecoratorTransformer decoratorTransformer = new DecoratorTransformer(true);
        BuildMethodTransformer buildMethodTransformer = new BuildMethodTransformer(true);

        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            AstNode node = sourceFile.getStatements().get(i);
            if (node instanceof ClassDeclaration) {
                node = decoratorTransformer.transform(node);
                node = buildMethodTransformer.transform(node);
                sourceFile.getStatements().set(i, node);
            }
        }

        // Generate code
        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        // Verify output
        assertNotNull(jsCode);
        assertTrue(jsCode.contains("class Hello"));
        assertTrue(jsCode.contains("extends View"));
    }

    private ClassDeclaration findClassDeclaration(SourceFile sourceFile) {
        for (AstNode node : sourceFile.getStatements()) {
            if (node instanceof ClassDeclaration) {
                return (ClassDeclaration) node;
            }
        }
        return null;
    }
}
