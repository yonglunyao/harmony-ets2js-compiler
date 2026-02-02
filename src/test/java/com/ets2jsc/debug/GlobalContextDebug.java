package com.ets2jsc.debug;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.parser.AstBuilder;
import com.ets2jsc.generator.CodeGenerator;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Debug test to understand GlobalContext.ets compilation
 */
public class GlobalContextDebug {
    public static void main(String[] args) throws Exception {
        String sourcePath = "src/test/resources/fixtures/integration/projects/harmony-utils/src/main/ets/utils/GlobalContext.ets";

        // Read source file
        String sourceCode = Files.readString(Paths.get(sourcePath));
        System.out.println("=== Source Code ===");
        System.out.println(sourceCode);
        System.out.println();

        // Parse to AST
        AstBuilder astBuilder = new AstBuilder();
        SourceFile sourceFile = astBuilder.build(sourcePath, sourceCode);

        System.out.println("=== AST Statements ===");
        System.out.println("Statement count: " + sourceFile.getStatements().size());
        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            System.out.println("Statement " + i + ": " + sourceFile.getStatements().get(i).getClass().getSimpleName());
        }
        System.out.println();

        // Generate code
        CodeGenerator codeGenerator = new CodeGenerator();
        String jsCode = codeGenerator.generate(sourceFile);

        System.out.println("=== Generated JavaScript ===");
        System.out.println("Length: " + jsCode.length());
        System.out.println("Content: [" + jsCode + "]");
        System.out.println();
    }
}
