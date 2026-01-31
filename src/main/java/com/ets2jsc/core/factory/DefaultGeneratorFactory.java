package com.ets2jsc.core.factory;

import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.generator.JsWriter;
import com.ets2jsc.generator.SourceMapGenerator;
import com.ets2jsc.generator.writer.CodeWriter;
import com.ets2jsc.generator.writer.IndentationManager;

/**
 * Default implementation of GeneratorFactory.
 * <p>
 * Creates standard generator instances.
 */
public class DefaultGeneratorFactory implements GeneratorFactory {

    private final String indentString;

    /**
     * Creates a factory with default 2-space indentation.
     */
    public DefaultGeneratorFactory() {
        this("  ");
    }

    /**
     * Creates a factory with the specified indentation string.
     *
     * @param indentString the indentation string to use
     */
    public DefaultGeneratorFactory(String indentString) {
        this.indentString = indentString != null ? indentString : "  ";
    }

    @Override
    public CodeGenerator createCodeGenerator(CompilerConfig config) {
        return new CodeGenerator(config);
    }

    @Override
    public CodeGenerator createCodeGenerator(CompilerConfig config, CodeWriter codeWriter) {
        // Note: Current CodeGenerator doesn't support CodeWriter injection
        // This is a placeholder for future refactoring
        return new CodeGenerator(config);
    }

    @Override
    public JsWriter createJsWriter() {
        return new JsWriter();
    }

    @Override
    public SourceMapGenerator createSourceMapGenerator() {
        return new SourceMapGenerator();
    }

    @Override
    public CodeWriter createCodeWriter() {
        return new CodeWriter(createIndentationManager());
    }

    @Override
    public CodeWriter createCodeWriter(String indentString) {
        return new CodeWriter(createIndentationManager(indentString));
    }

    @Override
    public IndentationManager createIndentationManager() {
        return new IndentationManager(indentString);
    }

    @Override
    public IndentationManager createIndentationManager(String indentString) {
        return new IndentationManager(indentString);
    }
}
