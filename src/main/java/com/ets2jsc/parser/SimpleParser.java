package com.ets2jsc.parser;

import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.Decorator;
import com.ets2jsc.ast.SourceFile;

/**
 * Simple regex-based parser as fallback when TypeScript parser is unavailable.
 * Parses basic ETS structures without full TypeScript support.
 */
public class SimpleParser {

    /**
     * Parses source code using simple regex patterns.
     * CC: 2 (loop + condition checks)
     */
    public SourceFile parse(String fileName, String sourceCode) {
        SourceFile sourceFile = new SourceFile(fileName, sourceCode);
        String[] lines = sourceCode.split("\n");

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (shouldSkipLine(trimmedLine)) {
                continue;
            }

            processLine(trimmedLine, sourceCode, sourceFile);
        }

        return sourceFile;
    }

    /**
     * Checks if a line should be skipped.
     * CC: 2 (condition checks)
     */
    private boolean shouldSkipLine(String line) {
        return line.isEmpty() || line.startsWith("//");
    }

    /**
     * Processes a single line of source code.
     * CC: 2 (if checks)
     */
    private void processLine(String line, String sourceCode, SourceFile sourceFile) {
        if (line.startsWith("import ")) {
            sourceFile.addImport(line);
        } else if (line.startsWith("struct ") || line.startsWith("@Component")) {
            parseStructDeclaration(sourceCode, sourceFile);
        }
    }

    /**
     * Parses struct declaration from source code.
     * CC: 3 (index checks + condition check)
     */
    private void parseStructDeclaration(String sourceCode, SourceFile sourceFile) {
        int structIndex = sourceCode.indexOf("struct ");
        if (structIndex == -1) {
            return;
        }

        String structName = extractStructName(sourceCode, structIndex);
        if (structName == null || structName.isEmpty()) {
            return;
        }

        ClassDeclaration classDecl = createStructDeclaration(structName, sourceCode, structIndex);
        sourceFile.addStatement(classDecl);
    }

    /**
     * Extracts struct name from source code.
     * CC: 2 (index checks)
     */
    private String extractStructName(String sourceCode, int structIndex) {
        int nameStart = structIndex + 7; // "struct ".length()
        int nameEnd = sourceCode.indexOf("{", nameStart);

        if (nameEnd == -1) {
            return null;
        }

        return sourceCode.substring(nameStart, nameEnd).trim();
    }

    /**
     * Creates a struct class declaration.
     * CC: 2 (index check + condition check)
     */
    private ClassDeclaration createStructDeclaration(String structName, String sourceCode, int structIndex) {
        ClassDeclaration classDecl = new ClassDeclaration(structName);
        classDecl.setStruct(true);

        // Check for @Component decorator
        int componentIndex = sourceCode.lastIndexOf("@Component", structIndex);
        if (componentIndex != -1 && componentIndex < structIndex) {
            classDecl.addDecorator(new Decorator("Component"));
        }

        return classDecl;
    }
}
