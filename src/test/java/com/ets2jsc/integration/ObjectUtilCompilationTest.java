package com.ets2jsc.integration;

import com.ets2jsc.domain.model.ast.ClassDeclaration;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.compiler.CompilerFactory;
import com.ets2jsc.compiler.ICompiler;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.parser.AstBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test ObjectUtil compilation to debug missing class issue.
 */
@DisplayName("ObjectUtil Compilation Debug Test")
class ObjectUtilCompilationTest {

    @Test
    @DisplayName("ObjectUtil should produce complete class definition")
    void testObjectUtilCompilation() throws Exception {
        String sourcePath = "src/test/resources/fixtures/integration/projects/harmony-utils/src/main/ets/utils/ObjectUtil.ets";
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
        assertEquals("ObjectUtil", classDecl.getName());
        System.out.println("Class: " + classDecl.getName());
        System.out.println("  Export: " + classDecl.isExport());
        System.out.println("  Members: " + classDecl.getMembers().size());
        System.out.println("  Methods: " + classDecl.getMethods().size());
        System.out.println("  Properties: " + classDecl.getProperties().size());

        Path outputPath = Path.of("target/test-out/debug/ObjectUtil.js");
        Files.createDirectories(outputPath.getParent());

        System.out.println("\n=== Compile Phase ===");
        CompilerConfig config = CompilerConfig.createDefault();
        try (ICompiler compiler = CompilerFactory.createCompiler(config)) {
            compiler.compile(Path.of(sourcePath), outputPath);
        }

        String output = Files.readString(outputPath);

        // Verify output has content
        assertNotNull(output);
        assertTrue(output.length() > 100, "Output should be more than 100 characters, got: " + output.length());

        // Verify class is exported
        assertTrue(output.contains("export class ObjectUtil"), "Should contain exported class");

        // Print for debugging
        System.out.println("\n=== Generated ObjectUtil.js ===");
        System.out.println(output.substring(0, Math.min(500, output.length())));
    }
}
