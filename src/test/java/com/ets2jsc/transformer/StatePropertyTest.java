package com.ets2jsc.transformer;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.ClassDeclaration;
import com.ets2jsc.domain.model.ast.PropertyDeclaration;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.parser.AstBuilder;
import com.ets2jsc.transformer.DecoratorTransformer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for @State property compilation.
 */
@DisplayName("State Property Compilation Tests")
public class StatePropertyTest {

    @Test
    @DisplayName("Should identify state properties")
    public void testIdentifyStateProperties() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/basic/state-property.ets")
        );

        // Parse
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("state-property.ets", sourceCode);

        // Find class declaration
        ClassDeclaration classDecl = findClassDeclaration(sourceFile);
        assertNotNull(classDecl);

        // Check for state properties
        long stateCount = classDecl.getProperties().stream()
            .filter(p -> p.hasDecorator("State"))
            .count();

        assertEquals(2, stateCount, "Should have 2 state properties");
    }

    @Test
    @DisplayName("Should create private variables for state")
    public void testCreatePrivateVariables() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/basic/state-property.ets")
        );

        // Parse and transform
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("state-property.ets", sourceCode);

        DecoratorTransformer transformer = new DecoratorTransformer(true);
        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            AstNode node = sourceFile.getStatements().get(i);
            if (node instanceof ClassDeclaration) {
                AstNode transformed = transformer.transform(node);
                ClassDeclaration classDecl = (ClassDeclaration) transformed;

                // Check for private variables with __ suffix
                boolean hasCountPrivate = classDecl.getProperties().stream()
                    .anyMatch(p -> "count__".equals(p.getName()));

                assertTrue(hasCountPrivate, "Should have private count__ variable");
            }
        }
    }

    @Test
    @DisplayName("Should create getter/setter for state")
    public void testCreateGetterSetter() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/basic/state-property.ets")
        );

        // Parse and transform
        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("state-property.ets", sourceCode);

        DecoratorTransformer transformer = new DecoratorTransformer(true);
        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            AstNode node = sourceFile.getStatements().get(i);
            if (node instanceof ClassDeclaration) {
                AstNode transformed = transformer.transform(node);
                ClassDeclaration classDecl = (ClassDeclaration) transformed;

                // Check for getter and setter methods
                boolean hasCountGetter = classDecl.getMethods().stream()
                    .anyMatch(m -> "get count".equals(m.getName()));

                boolean hasCountSetter = classDecl.getMethods().stream()
                    .anyMatch(m -> "set count".equals(m.getName()));

                assertTrue(hasCountGetter, "Should have getter for count");
                assertTrue(hasCountSetter, "Should have setter for count");
            }
        }
    }

    private ClassDeclaration findClassDeclaration(SourceFile sourceFile) {
        for (AstNode node : sourceFile.getStatements()) {
            if (node instanceof ClassDeclaration) {
                return (ClassDeclaration) node;
            }
        }
        return null;
    }
}
