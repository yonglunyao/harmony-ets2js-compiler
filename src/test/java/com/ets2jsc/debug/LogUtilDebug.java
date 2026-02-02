package com.ets2jsc.debug;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.ast.ClassDeclaration;
import com.ets2jsc.infrastructure.parser.AstBuilder;
import com.ets2jsc.infrastructure.generator.CodeGenerator;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Debug test to understand LogUtil.ets compilation
 */
public class LogUtilDebug {
    public static void main(String[] args) throws Exception {
        String sourcePath = "src/test/resources/fixtures/integration/projects/harmony-utils/src/main/ets/utils/LogUtil.ets";

        // Read source file
        String sourceCode = Files.readString(Paths.get(sourcePath));

        // Parse to AST
        AstBuilder astBuilder = new AstBuilder();
        SourceFile sourceFile = astBuilder.build(sourcePath, sourceCode);

        System.out.println("=== AST Statements ===");
        System.out.println("Statement count: " + sourceFile.getStatements().size());

        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            System.out.println("Statement " + i + ": " + sourceFile.getStatements().get(i).getClass().getSimpleName());
        }

        // Find class declaration
        for (var stmt : sourceFile.getStatements()) {
            if (stmt instanceof ClassDeclaration) {
                ClassDeclaration classDecl = (ClassDeclaration) stmt;
                System.out.println("\n=== Class: " + classDecl.getName() + " ===");
                System.out.println("Export: " + classDecl.isExport());
                System.out.println("Members: " + classDecl.getMembers().size());
                System.out.println("Methods: " + classDecl.getMethods().size());
                System.out.println("Properties: " + classDecl.getProperties().size());
            }
        }

        // Generate code
        CodeGenerator codeGenerator = new CodeGenerator();
        String jsCode = codeGenerator.generate(sourceFile);

        System.out.println("\n=== Generated JavaScript ===");
        System.out.println("Length: " + jsCode.length());
        System.out.println("Content:");
        System.out.println(jsCode);
    }
}
