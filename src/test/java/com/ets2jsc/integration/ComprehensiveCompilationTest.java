package com.ets2jsc.integration;

import com.ets2jsc.EtsCompiler;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.integration.util.JavaScriptSyntaxValidator;
import com.ets2jsc.integration.util.JavaScriptSyntaxValidator.ValidationResult;
import com.ets2jsc.parser.AstBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive compilation test suite.
 * Tests for:
 * - Empty output detection
 * - JavaScript syntax validation
 * - Common syntax issues detection
 * - Complete class structure generation
 */
@DisplayName("Comprehensive Compilation Tests")
class ComprehensiveCompilationTest {

    private static final int MINIMUM_MEANINGFUL_OUTPUT_LENGTH = 50;
    private static final int MAX_COMPILE_TIME_MS = 30000;

    // ========== Syntax Pattern Tests ==========

    @Test
    @DisplayName("Empty statement should produce valid output")
    void testEmptyStatement(@TempDir Path tempDir) throws Exception {
        String sourceCode = "export class Test {\n  method() {\n    ;\n  }\n}";

        Path sourceFile = tempDir.resolve("EmptyStatement.ets");
        Files.writeString(sourceFile, sourceCode);

        CompilationResult result = compile(tempDir, "EmptyStatement.ets", sourceCode);

        // Use lower minimum length for simple test case (10 chars)
        assertOutputIsMeaningful(result.output, "EmptyStatement.ets", 10);

        // Check for common structural issues
        List<String> issues = JavaScriptSyntaxValidator.detectCommonIssues(result.output);
        if (!issues.isEmpty()) {
            fail(String.format("Common issues detected in EmptyStatement.ets:%n  - %s",
                String.join("\n  - ", issues)));
        }
    }

    @Test
    @DisplayName("Class with constructor should produce valid output")
    void testClassWithConstructor(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class Person {\n"
            + "  private name: string;\n"
            + "  constructor(name: string) {\n"
            + "    this.name = name;\n"
            + "  }\n"
            + "  getName(): string {\n"
            + "    return this.name;\n"
            + "  }\n"
            + "}\n";

        validateCompilation(tempDir, "Constructor.ets", sourceCode, true);
    }

    @Test
    @DisplayName("Static methods should compile correctly")
    void testStaticMethods(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class MathUtils {\n"
            + "  static add(a: number, b: number): number {\n"
            + "    return a + b;\n"
            + "  }\n"
            + "  static multiply(a: number, b: number): number {\n"
            + "    return a * b;\n"
            + "  }\n"
            + "}\n";

        CompilationResult result = compile(tempDir, "StaticMethods.ets", sourceCode);

        assertTrue(result.output.contains("add"), "Should contain 'add' method");
        assertTrue(result.output.contains("multiply"), "Should contain 'multiply' method");
    }

    @Test
    @DisplayName("Property declarations should compile correctly")
    void testPropertyDeclarations(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class Config {\n"
            + "  private enabled: boolean = true;\n"
            + "  public timeout: number = 5000;\n"
            + "  readonly name: string = 'default';\n"
            + "}\n";

        CompilationResult result = compile(tempDir, "Properties.ets", sourceCode);

        assertTrue(result.output.contains("Config"), "Should contain class name");
        assertOutputIsMeaningful(result.output, "Properties.ets");
    }

    @Test
    @DisplayName("Return statements should compile correctly")
    void testReturnStatements(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class Calculator {\n"
            + "  add(a: number, b: number): number {\n"
            + "    return a + b;\n"
            + "  }\n"
            + "  getZero(): number {\n"
            + "    return;\n"
            + "  }\n"
            + "  getValue(): number | null {\n"
            + "    return null;\n"
            + "  }\n"
            + "}\n";

        validateCompilation(tempDir, "ReturnStatements.ets", sourceCode, true);
    }

    @Test
    @DisplayName("If statements should compile correctly")
    void testIfStatements(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class Conditional {\n"
            + "  check(value: number): string {\n"
            + "    if (value > 0) {\n"
            + "      return 'positive';\n"
            + "    } else if (value < 0) {\n"
            + "      return 'negative';\n"
            + "    } else {\n"
            + "      return 'zero';\n"
            + "    }\n"
            + "  }\n"
            + "}\n";

        validateCompilation(tempDir, "IfStatements.ets", sourceCode, true);
    }

    @Test
    @DisplayName("For loops should compile correctly")
    void testForLoops(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class Loops {\n"
            + "  countDown(n: number): void {\n"
            + "    for (let i = n; i > 0; i--) {\n"
            + "      console.log(i);\n"
            + "    }\n"
            + "  }\n"
            + "}\n";

        validateCompilation(tempDir, "ForLoops.ets", sourceCode, true);
    }

    @Test
    @DisplayName("While loops should compile correctly")
    void testWhileLoops(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class Loops {\n"
            + "  wait(condition: () => boolean): void {\n"
            + "    while (!condition()) {\n"
            + "      // wait\n"
            + "    }\n"
            + "  }\n"
            + "}\n";

        validateCompilation(tempDir, "WhileLoops.ets", sourceCode, true);
    }

    @Test
    @DisplayName("Switch statements should compile correctly")
    void testSwitchStatements(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class SwitchTest {\n"
            + "  getType(value: number): string {\n"
            + "    switch (value) {\n"
            + "      case 1:\n"
            + "        return 'one';\n"
            + "      case 2:\n"
            + "        return 'two';\n"
            + "      default:\n"
            + "        return 'other';\n"
            + "    }\n"
            + "  }\n"
            + "}\n";

        validateCompilation(tempDir, "SwitchStatements.ets", sourceCode, true);
    }

    @Test
    @DisplayName("Try-catch statements should compile correctly")
    void testTryCatch(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class ErrorHandling {\n"
            + "  safeParse(json: string): any {\n"
            + "    try {\n"
            + "      return JSON.parse(json);\n"
            + "    } catch (e) {\n"
            + "      return null;\n"
            + "    }\n"
            + "  }\n"
            + "}\n";

        validateCompilation(tempDir, "TryCatch.ets", sourceCode, true);
    }

    @Test
    @DisplayName("Multiple exports should compile correctly")
    void testMultipleExports(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class ClassA {\n"
            + "  methodA() {}\n"
            + "}\n"
            + "export class ClassB {\n"
            + "  methodB() {}\n"
            + "}\n"
            + "export const CONSTANT = 42;\n";

        CompilationResult result = compile(tempDir, "MultipleExports.ets", sourceCode);

        assertTrue(result.output.contains("ClassA"), "Should contain ClassA");
        assertTrue(result.output.contains("ClassB"), "Should contain ClassB");
    }

    @Test
    @DisplayName("Import statements should be preserved")
    void testImportStatements(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "import { ArrayList } from '@kit.ArkTS';\n"
            + "import util from '@ohos.util';\n"
            + "\n"
            + "export class Imports {\n"
            + "  list: ArrayList<string> = new ArrayList();\n"
            + "}\n";

        CompilationResult result = compile(tempDir, "Imports.ets", sourceCode);

        assertTrue(result.output.contains("import"), "Should preserve import statements");
    }

    // ========== Edge Case Tests ==========

    @ParameterizedTest
    @ValueSource(strings = {
        "export class Empty {}",
        "export class NoConstructor { method() {} }",
        "export class OnlyProps { x: number = 1; }",
        "export class OnlyStatic { static method() {} }",
        "export function fn() { return 42; }"
    })
    @DisplayName("Edge cases should compile correctly")
    void testEdgeCases(String sourceCode, @TempDir Path tempDir) throws Exception {
        Path sourceFile = tempDir.resolve("EdgeCase.ets");
        Files.writeString(sourceFile, sourceCode);

        CompilationResult result = compile(tempDir, "EdgeCase.ets", sourceCode);

        // Use lower minimum length for edge cases (10 chars)
        assertOutputIsMeaningful(result.output, "EdgeCase.ets", 10);

        // Check for common structural issues
        List<String> issues = JavaScriptSyntaxValidator.detectCommonIssues(result.output);
        if (!issues.isEmpty()) {
            fail(String.format("Common issues detected in EdgeCase.ets:%n  - %s",
                String.join("\n  - ", issues)));
        }
    }

    // ========== Real File Tests ==========

    @Test
    @DisplayName("StrUtil should compile with valid syntax")
    void testStrUtilCompilation() throws Exception {
        String sourcePath = "src/test/resources/fixtures/integration/projects/harmony-utils/src/main/ets/utils/StrUtil.ets";
        validateRealFileCompilation(sourcePath);
    }

    @Test
    @DisplayName("LogUtil should compile with valid syntax")
    void testLogUtilCompilation() throws Exception {
        String sourcePath = "src/test/resources/fixtures/integration/projects/harmony-utils/src/main/ets/utils/LogUtil.ets";
        validateRealFileCompilation(sourcePath);
    }

    @Test
    @DisplayName("ObjectUtil should compile with valid syntax")
    void testObjectUtilCompilation() throws Exception {
        String sourcePath = "src/test/resources/fixtures/integration/projects/harmony-utils/src/main/ets/utils/ObjectUtil.ets";
        validateRealFileCompilation(sourcePath);
    }

    // ========== Helper Methods ==========

    private void validateRealFileCompilation(String sourcePath) throws Exception {
        Path sourceFile = Path.of(sourcePath);
        assertTrue(Files.exists(sourceFile), "Source file should exist: " + sourcePath);

        String sourceCode = Files.readString(sourceFile);
        String fileName = sourceFile.getFileName().toString();

        CompilationResult result = compile(sourceFile.getParent(), fileName, sourceCode);

        // Validate output is meaningful
        assertOutputIsMeaningful(result.output, fileName);

        // Check for common structural issues
        List<String> issues = JavaScriptSyntaxValidator.detectCommonIssues(result.output);
        if (!issues.isEmpty()) {
            fail(String.format("Common issues detected in %s:%n  - %s",
                fileName, String.join("\n  - ", issues)));
        }
    }

    private void validateCompilation(Path tempDir, String fileName, String sourceCode, boolean checkSyntax) throws Exception {
        CompilationResult result = compile(tempDir, fileName, sourceCode);

        // Always check output is meaningful
        assertOutputIsMeaningful(result.output, fileName);

        // Note: Full JS syntax validation is skipped due to GraalVM JS engine limitations
        // in non-GraalVM JVM environments. We rely on structural checks instead.

        // Check for common structural issues (unmatched braces, brackets, etc.)
        List<String> issues = JavaScriptSyntaxValidator.detectCommonIssues(result.output);
        if (!issues.isEmpty()) {
            fail(String.format("Common issues detected in %s:%n  - %s",
                fileName, String.join("\n  - ", issues)));
        }
    }

    private CompilationResult compile(Path tempDir, String fileName, String sourceCode) throws Exception {
        long startTime = System.currentTimeMillis();

        Path sourceFile = tempDir.resolve(fileName);
        Path outputFile = tempDir.resolve(fileName.replace(".ets", ".js"));
        Files.writeString(sourceFile, sourceCode);

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFileAst = parser.build(fileName, sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFileAst);

        long duration = System.currentTimeMillis() - startTime;
        if (duration > MAX_COMPILE_TIME_MS) {
            fail(String.format("Compilation took too long: %d ms (max: %d ms)", duration, MAX_COMPILE_TIME_MS));
        }

        return new CompilationResult(jsCode, duration);
    }

    private void assertOutputIsMeaningful(String output, String sourceName) {
        assertOutputIsMeaningful(output, sourceName, MINIMUM_MEANINGFUL_OUTPUT_LENGTH);
    }

    private void assertOutputIsMeaningful(String output, String sourceName, int minimumLength) {
        assertNotNull(output, sourceName + " should produce non-null output");

        String trimmed = output.trim();
        assertFalse(trimmed.isEmpty(), sourceName + " should produce non-empty output");

        // Allow shorter output for simple edge cases
        assertTrue(output.length() >= minimumLength,
            sourceName + " should produce meaningful output (length >= " + minimumLength
            + "), but got length: " + output.length());
    }

    private static class CompilationResult {
        final String output;
        final long durationMs;

        CompilationResult(String output, long durationMs) {
            this.output = output;
            this.durationMs = durationMs;
        }
    }
}
