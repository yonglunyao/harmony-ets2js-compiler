package com.ets2jsc.domain.service;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.compilation.CompilationOutput;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CodeGenerationException;

import java.nio.file.Path;

/**
 * Domain service for generating JavaScript code from AST.
 * <p>
 * This service generates JavaScript code from transformed AST nodes,
 * with support for source maps and various output configurations.
 * <p>
 * The configuration is injected at construction time, not passed with each method call.
 */
public interface GeneratorService extends AutoCloseable {

    /**
     * Generates JavaScript code from a source file AST.
     *
     * @param sourceFile the source file AST to generate code from
     * @return the generated compilation output
     * @throws CodeGenerationException if code generation fails
     */
    CompilationOutput generate(SourceFile sourceFile) throws CodeGenerationException;

    /**
     * Generates JavaScript code from a single AST node.
     *
     * @param node the AST node to generate code from
     * @return the generated JavaScript code as a string
     * @throws CodeGenerationException if code generation fails
     */
    String generate(AstNode node) throws CodeGenerationException;

    /**
     * Generates JavaScript code from a source file AST and writes to a file.
     *
     * @param sourceFile the source file AST to generate code from
     * @param outputPath the path to write the generated code
     * @throws CodeGenerationException if code generation or file writing fails
     */
    void generateToFile(SourceFile sourceFile, Path outputPath) throws CodeGenerationException;

    /**
     * Generates JavaScript code with source map and writes both to files.
     *
     * @param sourceFile the source file AST to generate code from
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
