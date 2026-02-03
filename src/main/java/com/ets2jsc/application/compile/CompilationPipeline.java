package com.ets2jsc.application.compile;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.compilation.CompilationResult;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.domain.service.GeneratorService;
import com.ets2jsc.domain.service.ParserService;
import com.ets2jsc.domain.service.TransformerService;
import com.ets2jsc.shared.exception.CompilationException;

import java.nio.file.Path;

/**
 * Compilation pipeline that orchestrates the compilation process.
 * <p>
 * This pipeline manages the flow: Parse → Transform → Generate
 * and ensures proper resource management and error handling.
 */
public class CompilationPipeline implements AutoCloseable {

    private final ParserService parser;
    private final TransformerService transformer;
    private final GeneratorService generator;
    private final CompilerConfig config;
    private volatile boolean closed;

    /**
     * Creates a new compilation pipeline.
     *
     * @param parser the parser service
     * @param transformer the transformer service
     * @param generator the generator service
     * @param config the compiler configuration
     * @throws IllegalArgumentException if any parameter is null
     */
    public CompilationPipeline(ParserService parser, TransformerService transformer,
                             GeneratorService generator, CompilerConfig config) {
        if (parser == null) {
            throw new IllegalArgumentException("Parser cannot be null");
        }
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer cannot be null");
        }
        if (generator == null) {
            throw new IllegalArgumentException("Generator cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }

        this.parser = parser;
        this.transformer = transformer;
        this.generator = generator;
        this.config = config;
        this.closed = false;
    }

    /**
     * Executes the compilation pipeline for a source file.
     *
     * @param sourcePath the path to the source file
     * @param outputPath the path to the output file
     * @return the compilation result
     * @throws CompilationException if compilation fails
     */
    public CompilationResult execute(Path sourcePath, Path outputPath) throws CompilationException {
        checkNotClosed();
        long startTime = System.currentTimeMillis();

        try {
            // Stage 1: Parse
            SourceFile sourceFile = parser.parseFile(sourcePath);

            // Stage 2: Transform
            SourceFile transformedFile = transformer.transform(sourceFile);

            // Stage 3: Generate
            if (config.isGenerateSourceMap()) {
                Path sourceMapPath = Path.of(outputPath + ".map");
                generator.generateWithSourceMap(transformedFile, outputPath, sourceMapPath);
            } else {
                generator.generateToFile(transformedFile, outputPath);
            }

            long duration = System.currentTimeMillis() - startTime;
            return CompilationResult.success(sourcePath, outputPath, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            if (e instanceof CompilationException) {
                throw (CompilationException) e;
            }
            throw new CompilationException("Pipeline execution failed: " + sourcePath, e);
        }
    }

    /**
     * Gets the parser service.
     *
     * @return the parser service
     */
    public ParserService getParser() {
        return parser;
    }

    /**
     * Gets the transformer service.
     *
     * @return the transformer service
     */
    public TransformerService getTransformer() {
        return transformer;
    }

    /**
     * Gets the generator service.
     *
     * @return the generator service
     */
    public GeneratorService getGenerator() {
        return generator;
    }

    /**
     * Gets the compiler configuration.
     *
     * @return the compiler configuration
     */
    public CompilerConfig getConfig() {
        return config;
    }

    /**
     * Reconfigures the pipeline with a new configuration.
     *
     * @param newConfig the new configuration
     * @throws IllegalArgumentException if newConfig is null
     */
    public void reconfigure(CompilerConfig newConfig) {
        if (newConfig == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        transformer.reconfigure(newConfig);
        generator.reconfigure(newConfig);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        try {
            parser.close();
        } catch (Exception e) {
            // Log and continue
        }
        try {
            transformer.close();
        } catch (Exception e) {
            // Log and continue
        }
        try {
            generator.close();
        } catch (Exception e) {
            // Log and continue
        }

        closed = true;
    }

    /**
     * Checks if the pipeline has been closed.
     *
     * @return true if the pipeline is closed, false otherwise
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Checks that the pipeline has not been closed.
     *
     * @throws IllegalStateException if the pipeline is closed
     */
    private void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("CompilationPipeline is closed");
        }
    }
}
