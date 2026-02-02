package com.ets2jsc.integration;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.infrastructure.generator.CodeGenerator;
import com.ets2jsc.infrastructure.parser.AstBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Iteration 4 features.
 * Tests: object literals, new expressions, template strings, dynamic import, interface, etc.
 */
@DisplayName("Iteration 4 Feature Tests")
public class Iteration4Test {

    @Test
    @DisplayName("Should parse object literals correctly")
    public void testObjectLiterals() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/integration/test-iteration4.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-iteration4.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());

        // Generate code and verify object literals are preserved
        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        assertNotNull(jsCode);
        // Should contain object literal syntax
        assertTrue(jsCode.contains("{") && jsCode.contains("}"));
        // Should not contain JSON object representation
        assertFalse(jsCode.contains("{\"kind\":"));
    }

    @Test
    @DisplayName("Should parse new expressions correctly")
    public void testNewExpression() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/integration/test-iteration4.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-iteration4.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        assertNotNull(jsCode);
        // Should contain "new" keyword
        assertTrue(jsCode.contains("new "));
        // Should not contain JSON representation
        assertFalse(jsCode.contains("{\"kind\":215"));
    }

    @Test
    @DisplayName("Should parse template strings correctly")
    public void testTemplateStrings() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/integration/test-iteration4.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-iteration4.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        assertNotNull(jsCode);
        // Should contain template string syntax
        assertTrue(jsCode.contains("`") || jsCode.contains("${"));
    }

    @Test
    @DisplayName("Should handle @Entry decorator")
    public void testEntryDecorator() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/integration/test-iteration4.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-iteration4.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        assertNotNull(jsCode);
        // @Entry decorated components should be exported
        assertTrue(jsCode.contains("export class TestPage"));
    }

    @Test
    @DisplayName("Should remove type annotations from variables")
    public void testTypeAnnotations() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/integration/test-iteration4.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-iteration4.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        assertNotNull(jsCode);
        // Type annotations should be removed from variable declarations
        // Should have "const files = []" not "const files: FileInfo = []"
        // This is a loose check - we verify the code doesn't contain TypeScript type syntax
        assertFalse(jsCode.contains(": FileInfo"));
    }

    @Test
    @DisplayName("Should convert octal literals to decimal")
    public void testOctalLiterals() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/integration/test-iteration4.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-iteration4.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        assertNotNull(jsCode);
        // Octal 0o1 should be converted to decimal 1
        // The output should not contain "0o" prefix for octal literals
        assertFalse(jsCode.contains("0o1") || jsCode.contains("0O1"));
    }

    @Test
    @DisplayName("Should handle dynamic import")
    public void testDynamicImport() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/integration/test-iteration4.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-iteration4.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        assertNotNull(jsCode);
        // Should contain import() function
        assertTrue(jsCode.contains("import("));
    }

    @Test
    @DisplayName("Should ignore interface declarations")
    public void testInterfaceDeclaration() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/integration/test-iteration4.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-iteration4.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        assertNotNull(jsCode);
        // Interface declarations should not generate runtime code
        assertFalse(jsCode.contains("interface FileInfo"));
    }

    @Test
    @DisplayName("Should remove type assertions")
    public void testTypeAssertion() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/integration/test-iteration4.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-iteration4.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        assertNotNull(jsCode);
        // Type assertions (as Type) should be removed
        // "error as BusinessError" should become just "error"
        assertFalse(jsCode.contains(" as "));
    }

    @Test
    @DisplayName("Full compilation test for iteration 4")
    public void testFullCompilation() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/integration/test-iteration4.ets")
        );

        // Full compilation pipeline
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-iteration4.ets", sourceCode);

        // Generate code
        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFile);

        // Verify output is valid JavaScript
        assertNotNull(jsCode);
        assertFalse(jsCode.isEmpty());

        // Should not contain TypeScript-specific syntax
        assertFalse(jsCode.contains("interface "));
        assertFalse(jsCode.contains(": FileInfo"));
        assertFalse(jsCode.contains(" as "));

        // Should contain JavaScript equivalents
        assertTrue(jsCode.contains("function ") || jsCode.contains("const ") || jsCode.contains("class "));
    }
}
