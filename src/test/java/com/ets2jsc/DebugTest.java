package com.ets2jsc;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.parser.AstBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DebugTest {
    public static void main(String[] args) throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/simple-component.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("simple-component.ets", sourceCode);

        for (AstNode node : sourceFile.getStatements()) {
            if (node instanceof ClassDeclaration) {
                ClassDeclaration classDecl = (ClassDeclaration) node;
                System.out.println("Class: " + classDecl.getName());
                System.out.println("Methods:");
                for (MethodDeclaration method : classDecl.getMethods()) {
                    System.out.println("  - " + method.getName());
                }
            }
        }
    }
}
