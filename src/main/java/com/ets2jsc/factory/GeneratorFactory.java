package com.ets2jsc.factory;

import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.generator.JsWriter;
import com.ets2jsc.generator.SourceMapGenerator;
import com.ets2jsc.generator.writer.CodeWriter;
import com.ets2jsc.generator.writer.IndentationManager;

/**
 * Factory interface for creating generator instances.
 * <p>
 * Implementations of this interface are responsible for creating
 * configured generator instances. This enables dependency injection
 * and makes testing easier.
 */
public interface GeneratorFactory {

    /**
     * Creates a code generator for the given configuration.
     *
     * @param config the compiler configuration
     * @return the code generator
     */
    CodeGenerator createCodeGenerator(CompilerConfig config);

    /**
     * Creates a code generator with a custom code writer.
     *
     * @param config the compiler configuration
     * @param codeWriter the code writer
     * @return the code generator
     */
    CodeGenerator createCodeGenerator(CompilerConfig config, CodeWriter codeWriter);

    /**
     * Creates a JS writer.
     *
     * @return the JS writer
     */
    JsWriter createJsWriter();

    /**
     * Creates a source map generator.
     *
     * @return the source map generator
     */
    SourceMapGenerator createSourceMapGenerator();

    /**
     * Creates a code writer.
     *
     * @return the code writer
     */
    CodeWriter createCodeWriter();

    /**
     * Creates a code writer with the specified indentation.
     *
     * @param indentString the indentation string
     * @return the code writer
     */
    CodeWriter createCodeWriter(String indentString);

    /**
     * Creates an indentation manager.
     *
     * @return the indentation manager
     */
    IndentationManager createIndentationManager();

    /**
     * Creates an indentation manager with the specified indent string.
     *
     * @param indentString the indent string
     * @return the indentation manager
     */
    IndentationManager createIndentationManager(String indentString);
}
