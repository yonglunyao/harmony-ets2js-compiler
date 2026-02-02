package com.ets2jsc.api;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CodeGenerationException;

import java.nio.file.Path;

/**
 * Interface for generating JavaScript code from AST.
 * <p>
 * This is the primary facade for the GeneratorModule, providing a single
 * entry point for all code generation operations. Implementations should
 * handle code generation, source map generation, and file writing.
 */
public interface ICodeGenerator extends AutoCloseable {

    /**
     * Generates JavaScript code from a source file AST.
     *
     * @param sourceFile the source file to generate code from
     * @return the generated JavaScript code as a string
     * @throws CodeGenerationException if code generation fails
     */
    String generate(SourceFile sourceFile) throws CodeGenerationException;

    /**
     * Generates JavaScript code from a single AST node.
     *
     * @param node the node to generate code from
     * @return the generated JavaScript code as a string
     * @throws CodeGenerationException if code generation fails
     */
    String generate(AstNode node) throws CodeGenerationException;

    /**
     * Generates JavaScript code and writes it to a file.
     *
     * @param sourceFile the source file to generate code from
     * @param outputPath the path to write the generated code
     * @throws CodeGenerationException if code generation or file writing fails
     */
    void generateToFile(SourceFile sourceFile, Path outputPath) throws CodeGenerationException;

    /**
     * Generates JavaScript code with source map and writes both to files.
     *
     * @param sourceFile the source file to generate code from
     * @param outputPath the path to write the generated code
     * @param sourceMapPath the path to write the source map
     * @throws CodeGenerationException if code generation or file writing fails
     */
    void generateWithSourceMap(SourceFile sourceFile, Path outputPath, Path sourceMapPath)
            throws CodeGenerationException;

    /**
     * Reconfigures the code generator with a new configuration.
     *
     * @param config the new configuration to apply
     */
    void reconfigure(CompilerConfig config);

    /**
     * Closes the code generator and releases any resources.
     */
    @Override
    void close();
}
