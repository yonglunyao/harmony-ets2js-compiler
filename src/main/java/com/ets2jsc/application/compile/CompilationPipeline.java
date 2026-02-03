package com.ets2jsc.application.compile;

import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.compilation.CompilationResult;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.domain.service.GeneratorService;
import com.ets2jsc.domain.service.ParserService;
import com.ets2jsc.domain.service.TransformerService;
import com.ets2jsc.shared.exception.CompilationException;

import java.nio.file.Path;
import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compilation pipeline that orchestrates compilation process.
 * <p>
 * This pipeline manages flow: Parse → Transform → Generate
 * and ensures proper resource management and error handling.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class CompilationPipeline implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompilationPipeline.class);

    @Getter
    private final ParserService parser;
    @Getter
    private final TransformerService transformer;
    @Getter
    private final GeneratorService generator;
    @Getter
    private final CompilerConfig config;
    @Getter
    private volatile boolean closed;

    /**
     * Creates a new compilation pipeline.
     *
     * @param parser  parser service
     * @param transformer transformer service
     * @param generator  generator service
     * @param config    compiler configuration
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
     * @param sourcePath  path to the source file
     * @param outputPath  path to the output file
     * @return compilation result
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
     * Reconfigures pipeline with a new configuration.
     *
     * @param newConfig  new configuration
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
     * Checks that the pipeline has not been closed.
     *
     * @throws IllegalStateException if pipeline is closed
     */
    private void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("CompilationPipeline is closed");
        }
    }
}
