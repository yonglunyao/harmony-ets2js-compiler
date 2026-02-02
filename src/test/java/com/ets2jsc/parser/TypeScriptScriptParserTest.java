package com.ets2jsc.parser;

import com.ets2jsc.domain.model.ast.ClassDeclaration;
import com.ets2jsc.domain.model.ast.Decorator;
import com.ets2jsc.domain.model.ast.SourceFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TypeScriptScriptParser.
 */
@DisplayName("TypeScriptScriptParser Tests")
class TypeScriptScriptParserTest {

    @Test
    @DisplayName("Test parser initialization")
    void testParserInitialization() {
        assertDoesNotThrow(() -> {
            TypeScriptScriptParser parser = new TypeScriptScriptParser();
            assertNotNull(parser);
        });
    }

    @Test
    @DisplayName("Test parse simple class declaration")
    void testParseSimpleClassDeclaration() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "class App { }";

        SourceFile sourceFile = parser.parse("App.ets", sourceCode);

        assertNotNull(sourceFile);
        assertEquals("App.ets", sourceFile.getFileName());
        assertEquals(1, sourceFile.getStatements().size());
    }

    @Test
    @DisplayName("Test parse class with decorator")
    void testParseClassWithDecorator() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "@Component\nstruct App { }";

        SourceFile sourceFile = parser.parse("App.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());

        // Check for decorator
        boolean hasDecorator = sourceFile.getStatements().stream()
                .filter(ClassDeclaration.class::isInstance)
                .map(ClassDeclaration.class::cast)
                .anyMatch(cls -> !cls.getDecorators().isEmpty());

        assertTrue(hasDecorator, "Class should have @Component decorator");
    }

    @Test
    @DisplayName("Test parse class with multiple decorators")
    void testParseClassWithMultipleDecorators() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "@Entry\n@Component\nstruct App { }";

        SourceFile sourceFile = parser.parse("App.ets", sourceCode);

        assertNotNull(sourceFile);
    }

    @Test
    @DisplayName("Test parse struct to class conversion")
    void testParseStructToClassConversion() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "@Component\nstruct App {\n  @State count: number = 0;\n}";

        SourceFile sourceFile = parser.parse("App.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());
    }

    @Test
    @DisplayName("Test parse empty source")
    void testParseEmptySource() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "";

        SourceFile sourceFile = parser.parse("empty.ets", sourceCode);

        assertNotNull(sourceFile);
        assertEquals("empty.ets", sourceFile.getFileName());
    }

    @Test
    @DisplayName("Test parse with import statements")
    void testParseWithImportStatements() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "import { foo } from './bar';\nclass App { }";

        SourceFile sourceFile = parser.parse("App.ets", sourceCode);

        assertNotNull(sourceFile);
    }

    @Test
    @DisplayName("Test parse function declaration")
    void testParseFunctionDeclaration() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "function foo() { return 42; }";

        SourceFile sourceFile = parser.parse("test.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());
    }

    @Test
    @DisplayName("Test parse variable declaration")
    void testParseVariableDeclaration() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "const x = 42;";

        SourceFile sourceFile = parser.parse("test.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());
    }

    @Test
    @DisplayName("Test parse with syntax error handles gracefully")
    void testParseWithSyntaxError() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "class App { broken syntax here }";

        // Parser should still return a SourceFile, even with errors
        assertDoesNotThrow(() -> {
            SourceFile sourceFile = parser.parse("broken.ets", sourceCode);
            assertNotNull(sourceFile);
        });
    }

    @Test
    @DisplayName("Test parse class with properties")
    void testParseClassWithProperties() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "class App {\n  private name: string;\n  public age: number;\n}";

        SourceFile sourceFile = parser.parse("App.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());
    }

    @Test
    @DisplayName("Test parse class with methods")
    void testParseClassWithMethods() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "class App {\n  foo() { return 1; }\n  bar() { return 2; }\n}";

        SourceFile sourceFile = parser.parse("App.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());
    }

    @Test
    @DisplayName("Test parse arrow function")
    void testParseArrowFunction() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "const foo = () => { return 42; };";

        SourceFile sourceFile = parser.parse("test.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());
    }

    @Test
    @DisplayName("Test parse template literal")
    void testParseTemplateLiteral() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "const message = `Hello ${name}`;";

        SourceFile sourceFile = parser.parse("test.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());
    }

    @Test
    @DisplayName("Test parse export statement")
    void testParseExportStatement() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        String sourceCode = "export class App { }";

        SourceFile sourceFile = parser.parse("App.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());
    }
}
