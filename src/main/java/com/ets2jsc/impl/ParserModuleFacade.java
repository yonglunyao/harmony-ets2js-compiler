package com.ets2jsc.impl;

import com.ets2jsc.api.IParser;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.shared.exception.ParserException;
import com.ets2jsc.infrastructure.parser.AstBuilder;
import com.ets2jsc.infrastructure.parser.TypeScriptScriptParser;
import com.ets2jsc.infrastructure.parser.internal.IAstBuilder;
import com.ets2jsc.infrastructure.parser.internal.ITypeScriptParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Facade for the ParserModule.
 * <p>
 * This class provides a single entry point for all parsing operations,
 * internally coordinating between TypeScriptScriptParser and AstBuilder.
 */
public class ParserModuleFacade implements IParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserModuleFacade.class);

    // Supported file extensions for ETS/TypeScript
    private static final String EXT_ETS = ".ets";
    private static final String EXT_TS = ".ts";
    private static final String EXT_TSX = ".tsx";
    private static final String EXT_JSX = ".jsx";

    private final ITypeScriptParser scriptParser;
    private final IAstBuilder astBuilder;

    /**
     * Creates a new parser module facade with default implementations.
     */
    public ParserModuleFacade() {
        this.scriptParser = createTypeScriptParser();
        this.astBuilder = createAstBuilder();
    }

    /**
     * Creates a new parser module facade with specific implementations.
     * This constructor enables dependency injection for testing.
     *
     * @param scriptParser the TypeScript parser to use
     * @param astBuilder the AST builder to use
     */
    public ParserModuleFacade(ITypeScriptParser scriptParser, IAstBuilder astBuilder) {
        this.scriptParser = scriptParser;
        this.astBuilder = astBuilder;
    }

    @Override
    public SourceFile parseFile(Path sourcePath) throws ParserException {
        validateSourcePath(sourcePath);

        try {
            String sourceCode = Files.readString(sourcePath);
            return parseString(sourcePath.toString(), sourceCode);
        } catch (Exception e) {
            throw new ParserException("Failed to parse file: " + sourcePath, e);
        }
    }

    @Override
    public SourceFile parseString(String fileName, String sourceCode) throws ParserException {
        validateArguments(fileName, sourceCode);

        try {
            return scriptParser.parse(fileName, sourceCode);
        } catch (ParserException e) {
            throw e;
        } catch (Exception e) {
            throw new ParserException("Failed to parse source code from: " + fileName, e);
        }
    }

    @Override
    public boolean canParse(Path sourcePath) {
        if (sourcePath == null || !Files.isRegularFile(sourcePath)) {
            return false;
        }

        String fileName = sourcePath.toString().toLowerCase();
        return fileName.endsWith(EXT_ETS)
                || fileName.endsWith(EXT_TS)
                || fileName.endsWith(EXT_TSX)
                || fileName.endsWith(EXT_JSX);
    }

    @Override
    public void close() {
        // Clean up resources if needed
        try {
            if (scriptParser != null) {
                scriptParser.close();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to close script parser", e);
        }
    }

    /**
     * Validates that the source path is not null and is a regular file.
     *
     * @param sourcePath the path to validate
     * @throws IllegalArgumentException if the path is invalid
     */
    private void validateSourcePath(Path sourcePath) {
        if (sourcePath == null) {
            throw new IllegalArgumentException("Source path cannot be null");
        }
        if (!Files.isRegularFile(sourcePath)) {
            throw new IllegalArgumentException("Source path is not a regular file: " + sourcePath);
        }
    }

    /**
     * Validates that the parsing arguments are not null.
     *
     * @param fileName the file name to validate
     * @param sourceCode the source code to validate
     * @throws IllegalArgumentException if any argument is invalid
     */
    private void validateArguments(String fileName, String sourceCode) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        if (sourceCode == null) {
            throw new IllegalArgumentException("Source code cannot be null");
        }
    }

    /**
     * Creates the default TypeScript parser implementation.
     *
     * @return a new TypeScriptScriptParser instance
     */
    private ITypeScriptParser createTypeScriptParser() {
        TypeScriptScriptParser parser = new TypeScriptScriptParser();
        return new ITypeScriptParser() {
            @Override
            public SourceFile parse(String fileName, String sourceCode) throws ParserException {
                return parser.parse(fileName, sourceCode);
            }

            @Override
            public void close() {
                // TypeScriptScriptParser doesn't need explicit cleanup
            }
        };
    }

    /**
     * Creates the default AST builder implementation.
     *
     * @return a new AstBuilder instance
     */
    private IAstBuilder createAstBuilder() {
        AstBuilder builder = new AstBuilder();
        return new IAstBuilder() {
            @Override
            public SourceFile build(String fileName, String sourceCode) {
                return builder.build(fileName, sourceCode);
            }

            @Override
            public boolean validate(com.ets2jsc.domain.model.ast.AstNode node) {
                return builder.validate(node);
            }

            @Override
            public com.ets2jsc.domain.model.ast.AstNode process(com.ets2jsc.domain.model.ast.AstNode node) {
                return builder.process(node);
            }
        };
    }
}
