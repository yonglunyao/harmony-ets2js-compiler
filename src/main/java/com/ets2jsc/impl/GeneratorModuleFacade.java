package com.ets2jsc.impl;

import com.ets2jsc.api.ICodeGenerator;
import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.shared.exception.CodeGenerationException;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.generator.JsWriter;
import com.ets2jsc.generator.SourceMapGenerator;
import com.ets2jsc.generator.internal.IJsWriter;
import com.ets2jsc.generator.internal.ISourceMapGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Facade for the GeneratorModule.
 * <p>
 * This class provides a single entry point for all code generation operations,
 * internally coordinating between CodeGenerator, JsWriter, and SourceMapGenerator.
 */
public class GeneratorModuleFacade implements ICodeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorModuleFacade.class);

    private final CodeGenerator codeGenerator;
    private final IJsWriter jsWriter;
    private final ISourceMapGenerator sourceMapGenerator;
    private CompilerConfig config;

    /**
     * Creates a new generator module facade with the given configuration.
     *
     * @param config the compiler configuration
     */
    public GeneratorModuleFacade(CompilerConfig config) {
        this(config, createDefaultJsWriter(), createDefaultSourceMapGenerator());
    }

    /**
     * Creates a new generator module facade with specific implementations.
     * This constructor enables dependency injection for testing.
     *
     * @param config the compiler configuration
     * @param jsWriter the JS writer to use
     * @param sourceMapGenerator the source map generator to use
     */
    public GeneratorModuleFacade(CompilerConfig config, IJsWriter jsWriter, ISourceMapGenerator sourceMapGenerator) {
        if (config == null) {
            this.config = new CompilerConfig();
        } else {
            this.config = config;
        }

        if (jsWriter == null) {
            this.jsWriter = createDefaultJsWriter();
        } else {
            this.jsWriter = jsWriter;
        }

        if (sourceMapGenerator == null) {
            this.sourceMapGenerator = createDefaultSourceMapGenerator();
        } else {
            this.sourceMapGenerator = sourceMapGenerator;
        }

        this.codeGenerator = new CodeGenerator(this.config);
    }

    @Override
    public String generate(SourceFile sourceFile) throws CodeGenerationException {
        validateSourceFile(sourceFile);

        try {
            return codeGenerator.generate(sourceFile);
        } catch (Exception e) {
            throw new CodeGenerationException("Failed to generate code for source file: "
                    + sourceFile.getFileName(), e);
        }
    }

    @Override
    public String generate(AstNode node) throws CodeGenerationException {
        validateNode(node);

        try {
            return codeGenerator.generate(node);
        } catch (Exception e) {
            throw new CodeGenerationException("Failed to generate code for node: " + node.getType(), e);
        }
    }

    @Override
    public void generateToFile(SourceFile sourceFile, Path outputPath) throws CodeGenerationException {
        validateSourceFile(sourceFile);
        validateOutputPath(outputPath);

        String jsCode = generate(sourceFile);

        try {
            jsWriter.write(outputPath, jsCode);
        } catch (Exception e) {
            throw new CodeGenerationException("Failed to write code to file: " + outputPath, e);
        }
    }

    @Override
    public void generateWithSourceMap(SourceFile sourceFile, Path outputPath, Path sourceMapPath)
            throws CodeGenerationException {
        validateSourceFile(sourceFile);
        validateOutputPath(outputPath);
        validateOutputPath(sourceMapPath);

        String jsCode = generate(sourceFile);
        String sourceMap = generateSourceMap(sourceFile);

        try {
            String sourceMapFileName = sourceMapPath.getFileName().toString();
            jsWriter.writeWithSourceMap(outputPath, jsCode, sourceMapFileName);
            jsWriter.write(sourceMapPath, sourceMap);
        } catch (Exception e) {
            throw new CodeGenerationException("Failed to write code and source map to files", e);
        }
    }

    @Override
    public void reconfigure(CompilerConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        this.config = config;
        // Note: CodeGenerator doesn't support reconfiguration, so we'd need to recreate it
        // if reconfiguration is required. For now, we just update the config reference.
    }

    @Override
    public void close() {
        // Clean up resources if needed
        try {
            if (jsWriter != null) {
                jsWriter.close();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to close JS writer", e);
        }
    }

    /**
     * Generates a source map for the given source file.
     *
     * @param sourceFile the source file to generate a source map for
     * @return the source map as a JSON string
     */
    private String generateSourceMap(SourceFile sourceFile) {
        return sourceMapGenerator.generate(sourceFile);
    }

    /**
     * Validates that the source file is not null.
     *
     * @param sourceFile the source file to validate
     * @throws IllegalArgumentException if the source file is null
     */
    private void validateSourceFile(SourceFile sourceFile) {
        if (sourceFile == null) {
            throw new IllegalArgumentException("SourceFile cannot be null");
        }
    }

    /**
     * Validates that the AST node is not null.
     *
     * @param node the node to validate
     * @throws IllegalArgumentException if the node is null
     */
    private void validateNode(AstNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }
    }

    /**
     * Validates that the output path is not null.
     *
     * @param outputPath the output path to validate
     * @throws IllegalArgumentException if the output path is null
     */
    private void validateOutputPath(Path outputPath) {
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path cannot be null");
        }
    }

    /**
     * Creates the default JS writer implementation.
     *
     * @return a new JsWriter instance wrapped in an adapter
     */
    private static IJsWriter createDefaultJsWriter() {
        JsWriter writer = new JsWriter();
        return new IJsWriter() {
            @Override
            public void write(Path path, String content) throws Exception {
                writer.write(path, content);
            }

            @Override
            public void writeWithSourceMap(Path path, String content, String sourceMapFileName) throws Exception {
                writer.writeWithSourceMap(path, content, sourceMapFileName);
            }

            @Override
            public void close() {
                // JsWriter doesn't need explicit cleanup
            }
        };
    }

    /**
     * Creates the default source map generator implementation.
     *
     * @return a new SourceMapGenerator instance wrapped in an adapter
     */
    private static ISourceMapGenerator createDefaultSourceMapGenerator() {
        SourceMapGenerator generator = new SourceMapGenerator();
        return new ISourceMapGenerator() {
            @Override
            public String generate(SourceFile sourceFile) {
                return generator.generate();
            }

            @Override
            public void close() {
                // SourceMapGenerator doesn't need explicit cleanup
            }
        };
    }
}
