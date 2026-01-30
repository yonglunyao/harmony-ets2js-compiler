package com.ets2jsc;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.MethodDeclaration;
import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.parser.AstBuilder;
import com.ets2jsc.transformer.ComponentTransformer;
import com.ets2jsc.transformer.DecoratorTransformer;
import com.ets2jsc.transformer.BuildMethodTransformer;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DebugTest {
    public static void main(String[] args) throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/test-if.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("test-if.ets", sourceCode);

        // Apply transformations
        DecoratorTransformer decoratorTransformer = new DecoratorTransformer(true);
        BuildMethodTransformer buildMethodTransformer = new BuildMethodTransformer(true);
        ComponentTransformer componentTransformer = new ComponentTransformer();

        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            AstNode node = sourceFile.getStatements().get(i);
            if (node instanceof ClassDeclaration) {
                node = decoratorTransformer.transform(node);
                node = buildMethodTransformer.transform(node);
                node = componentTransformer.transform(node);
                sourceFile.getStatements().set(i, node);
            }
        }

        // Generate code with ArkUI runtime mode
        CompilerConfig config = CompilerConfig.createDefault();
        config.setPureJavaScript(false);
        CodeGenerator generator = new CodeGenerator(config);
        String jsCode = generator.generate(sourceFile);

        System.out.println("Generated JavaScript (ArkUI Runtime mode):");
        System.out.println("===========================================");
        System.out.println(jsCode);
        System.out.println("===========================================");
    }
}
