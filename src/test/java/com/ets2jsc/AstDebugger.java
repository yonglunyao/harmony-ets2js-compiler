package com.ets2jsc;

import com.ets2jsc.ast.*;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.parser.AstBuilder;

/**
 * Debug tool to inspect AST structure.
 */
public class AstDebugger {

    public static void main(String[] args) {
        String sourceFile = "src/test/resources/fixtures/myutils/package/src/main/ets/components/MainPage.ets";

        try {
            String sourceCode = java.nio.file.Files.readString(java.nio.file.Paths.get(sourceFile));

            System.out.println("=== Original ETS Code ===");
            System.out.println(sourceCode);
            System.out.println();

            // Parse
            AstBuilder parser = new AstBuilder();
            SourceFile ast = parser.build(sourceFile, sourceCode);

            System.out.println("=== AST Structure ===");
            System.out.println("SourceFile: " + ast.getFileName());
            System.out.println("Statements: " + ast.getStatements().size());
            System.out.println();

            for (AstNode node : ast.getStatements()) {
                printNode(node, 0);
            }

            System.out.println();
            System.out.println("=== Generated JavaScript ===");
            CodeGenerator generator = new CodeGenerator();
            String jsCode = generator.generate(ast);
            System.out.println(jsCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printNode(AstNode node, int indent) {
        String prefix = "  ".repeat(indent);
        System.out.println(prefix + "Node: " + node.getType());

        if (node instanceof ClassDeclaration) {
            ClassDeclaration cls = (ClassDeclaration) node;
            System.out.println(prefix + "  Name: " + cls.getName());
            System.out.println(prefix + "  IsStruct: " + cls.isStruct());
            System.out.println(prefix + "  SuperClass: " + cls.getSuperClass());
            System.out.println(prefix + "  Decorators: " + cls.getDecorators().size());
            System.out.println(prefix + "  Members: " + cls.getMembers().size());
            for (AstNode member : cls.getMembers()) {
                printNode(member, indent + 2);
            }
        } else if (node instanceof MethodDeclaration) {
            MethodDeclaration method = (MethodDeclaration) node;
            System.out.println(prefix + "  Name: " + method.getName());
            System.out.println(prefix + "  Parameters: " + method.getParameters().size());
        } else if (node instanceof PropertyDeclaration) {
            PropertyDeclaration prop = (PropertyDeclaration) node;
            System.out.println(prefix + "  Name: " + prop.getName());
            System.out.println(prefix + "  Type: " + prop.getTypeAnnotation());
            System.out.println(prefix + "  Decorators: " + prop.getDecorators().size());
        }
    }
}
