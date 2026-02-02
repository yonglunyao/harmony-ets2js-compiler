package com.ets2jsc.integration;

import com.ets2jsc.compiler.CompilerFactory;
import com.ets2jsc.compiler.ICompiler;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.infrastructure.generator.CodeGenerator;
import com.ets2jsc.infrastructure.parser.AstBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for detecting empty compilation output.
 * This test ensures that compilation produces meaningful output,
 * not empty files which would indicate a compiler bug.
 */
@DisplayName("Empty Output Detection Tests")
class EmptyOutputDetectionTest {

    private static final int MINIMUM_MEANINGFUL_OUTPUT_LENGTH = 50;
    private static final String SOURCE_MAP_COMMENT = "# sourceMappingURL";

    /**
     * Test that GlobalContext.ets produces meaningful output.
     * This file was found to produce nearly empty output (only source map reference).
     * This test serves as a regression test for this bug.
     */
    @Test
    @DisplayName("GlobalContext utility class should produce meaningful output")
    void testGlobalContextOutputNotEmpty() throws Exception {
        String sourcePath = "src/test/resources/fixtures/integration/projects/harmony-utils/src/main/ets/utils/GlobalContext.ets";

        String sourceCode = Files.readString(Path.of(sourcePath));

        CompilerConfig config = CompilerConfig.createDefault();
        Path outputPath = Path.of("target/test/GlobalContext.js");
        Files.createDirectories(outputPath.getParent());

        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            compiler.compile(Path.of(sourcePath), outputPath);
        }

        String output = Files.readString(outputPath);

        assertOutputIsMeaningful(output, "GlobalContext.ets");
    }

    /**
     * Test that a plain TypeScript class (not @Component struct) produces output.
     */
    @Test
    @DisplayName("Plain TypeScript class should produce meaningful output")
    void testPlainTypeScriptClassOutput(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class StringUtils {\n"
            + "    static isEmpty(str: string): boolean {\n"
            + "        return !str || str.length === 0;\n"
            + "    }\n"
            + "}\n";

        Path sourceFile = tempDir.resolve("StringUtils.ets");
        Path outputFile = tempDir.resolve("StringUtils.js");
        Files.writeString(sourceFile, sourceCode);

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFileAst = parser.build("StringUtils.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFileAst);

        assertOutputIsMeaningful(jsCode, "StringUtils.ets");
    }

    /**
     * Test that a class with export produces meaningful output.
     */
    @Test
    @DisplayName("Exported class should produce meaningful output")
    void testExportedClassOutput(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class CacheUtil {\n"
            + "    private static cache = new Map<string, any>();\n"
            + "    static set(key: string, value: any): void {\n"
            + "        this.cache.set(key, value);\n"
            + "    }\n"
            + "    static get(key: string): any {\n"
            + "        return this.cache.get(key);\n"
            + "    }\n"
            + "}\n";

        Path sourceFile = tempDir.resolve("CacheUtil.ets");
        Files.writeString(sourceFile, sourceCode);

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFileAst = parser.build("CacheUtil.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFileAst);

        assertOutputIsMeaningful(jsCode, "CacheUtil.ets");
        assertTrue(jsCode.contains("CacheUtil"), "Output should contain class name");
    }

    /**
     * Test that a class with static methods produces meaningful output.
     */
    @Test
    @DisplayName("Class with static methods should produce meaningful output")
    void testStaticMethodsOutput(@TempDir Path tempDir) throws Exception {
        String sourceCode = ""
            + "export class MathUtil {\n"
            + "    static add(a: number, b: number): number {\n"
            + "        return a + b;\n"
            + "    }\n"
            + "    static multiply(a: number, b: number): number {\n"
            + "        return a * b;\n"
            + "    }\n"
            + "}\n";

        Path sourceFile = tempDir.resolve("MathUtil.ets");
        Files.writeString(sourceFile, sourceCode);

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFileAst = parser.build("MathUtil.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFileAst);

        assertOutputIsMeaningful(jsCode, "MathUtil.ets");
        assertTrue(jsCode.contains("add") || jsCode.contains("multiply"),
            "Output should contain method names");
    }

    /**
     * Test that compilation preserves essential code structure.
     */
    @Test
    @DisplayName("Compilation should preserve essential code structure")
    void testEssentialStructurePreserved(@TempDir Path tempDir) throws Exception {
        String className = "DataUtil";
        String methodName = "processData";
        String sourceCode = ""
            + "export class " + className + " {\n"
            + "    " + methodName + "(data: string): string {\n"
            + "        return data.toUpperCase();\n"
            + "    }\n"
            + "}\n";

        Path sourceFile = tempDir.resolve("DataUtil.ets");
        Files.writeString(sourceFile, sourceCode);

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFileAst = parser.build("DataUtil.ets", sourceCode);

        CodeGenerator generator = new CodeGenerator();
        String jsCode = generator.generate(sourceFileAst);

        assertOutputIsMeaningful(jsCode, "DataUtil.ets");
        assertTrue(jsCode.contains(className), "Output should contain class name: " + className);
    }

    /**
     * Assert that the compiled output is meaningful and not empty.
     *
     * @param output the compiled JavaScript output
     * @param sourceName the name of the source file (for error messages)
     */
    private void assertOutputIsMeaningful(String output, String sourceName) {
        assertNotNull(output, sourceName + " should produce non-null output");

        // Remove source map comment for length check
        String outputWithoutSourceMap = output;
        if (output.contains(SOURCE_MAP_COMMENT)) {
            int sourceMapIndex = output.indexOf(SOURCE_MAP_COMMENT);
            outputWithoutSourceMap = output.substring(0, sourceMapIndex).trim();
        }

        assertFalse(outputWithoutSourceMap.isEmpty(),
            sourceName + " should produce non-empty output");

        assertTrue(outputWithoutSourceMap.length() >= MINIMUM_MEANINGFUL_OUTPUT_LENGTH,
            sourceName + " should produce meaningful output (length >= " + MINIMUM_MEANINGFUL_OUTPUT_LENGTH
            + "), but got length: " + outputWithoutSourceMap.length());

        // Output should not be just whitespace
        String trimmed = outputWithoutSourceMap.trim();
        assertFalse(trimmed.isEmpty(),
            sourceName + " should produce output with actual content, not just whitespace");
    }
}
