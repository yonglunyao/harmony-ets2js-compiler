package com.ets2jsc.integration;

import com.ets2jsc.domain.model.ast.ClassDeclaration;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.application.compile.CompilationPipeline;
import com.ets2jsc.application.compile.CompilationPipelineFactory;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.infrastructure.parser.AstBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test LogUtil compilation to debug missing class issue.
 */
@DisplayName("LogUtil Compilation Debug Test")
class LogUtilCompilationTest {

    @Test
    @DisplayName("LogUtil should produce complete class definition")
    void testLogUtilCompilation() throws Exception {
        String sourcePath = "src/test/resources/fixtures/integration/projects/harmony-utils/src/main/ets/utils/LogUtil.ets";
        String sourceCode = Files.readString(Path.of(sourcePath));

        System.out.println("=== Parse Phase ===");
        AstBuilder astBuilder = new AstBuilder();
        SourceFile sourceFile = astBuilder.build(sourcePath, sourceCode);

        System.out.println("Statement count: " + sourceFile.getStatements().size());
        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            System.out.println("  " + i + ": " + sourceFile.getStatements().get(i).getClass().getSimpleName());
        }

        // Find class declaration
        ClassDeclaration classDecl = null;
        for (var stmt : sourceFile.getStatements()) {
            if (stmt instanceof ClassDeclaration) {
                classDecl = (ClassDeclaration) stmt;
                break;
            }
        }

        assertNotNull(classDecl, "ClassDeclaration should be parsed");
        assertEquals("LogUtil", classDecl.getName());
        System.out.println("Class: " + classDecl.getName());
        System.out.println("  Export: " + classDecl.isExport());
        System.out.println("  Members: " + classDecl.getMembers().size());
        System.out.println("  Methods: " + classDecl.getMethods().size());
        System.out.println("  Properties: " + classDecl.getProperties().size());

        Path outputPath = Path.of("target/test-out/debug/LogUtil.js");
        Files.createDirectories(outputPath.getParent());

        System.out.println("\n=== Compile Phase ===");
        CompilerConfig config = CompilerConfig.createDefault();
        try (CompilationPipeline pipeline = CompilationPipelineFactory.createPipeline(config)) {
            pipeline.execute(Path.of(sourcePath), outputPath);
        }

        String output = Files.readString(outputPath);

        // Verify output has content
        assertNotNull(output);
        assertTrue(output.length() > 100, "Output should be more than 100 characters, got: " + output.length());

        // Verify class is exported
        assertTrue(output.contains("export class LogUtil"), "Should contain exported class");

        // Verify methods are present
        assertTrue(output.contains("static init"), "Should contain init method");
        assertTrue(output.contains("static debug"), "Should contain debug method");
        assertTrue(output.contains("static info"), "Should contain info method");

        // Print for debugging
        System.out.println("\n=== Generated LogUtil.js ===");
        System.out.println(output.substring(0, Math.min(500, output.length())));
    }
}
