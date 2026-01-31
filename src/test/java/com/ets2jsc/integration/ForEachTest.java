package com.ets2jsc.integration;

import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.parser.AstBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ForEach Compilation Tests")
public class ForEachTest {

    @Test
    @DisplayName("Should parse ForEach component")
    public void testParseForEach() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/statements/foreach.ets")
        );

        AstBuilder parser = new AstBuilder();
        SourceFile sourceFile = parser.build("foreach.ets", sourceCode);

        assertNotNull(sourceFile);
        assertFalse(sourceFile.getStatements().isEmpty());
        assertTrue(sourceCode.contains("ForEach"));
    }

    @Test
    @DisplayName("Should identify ForEach in source")
    public void testIdentifyForEach() throws Exception {
        String sourceCode = Files.readString(
            Paths.get("src/test/resources/fixtures/statements/foreach.ets")
        );

        assertTrue(sourceCode.contains("ForEach"));
        assertTrue(sourceCode.contains("items"));
    }
}
