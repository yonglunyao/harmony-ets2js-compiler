package com.ets2jsc.application.compile;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.domain.service.GeneratorService;
import com.ets2jsc.domain.service.ParserService;
import com.ets2jsc.domain.service.TransformerService;
import com.ets2jsc.infrastructure.factory.DefaultTransformerFactory;
import com.ets2jsc.infrastructure.factory.TransformerFactory;
import com.ets2jsc.infrastructure.generator.CodeGenerator;
import com.ets2jsc.infrastructure.generator.JsWriter;
import com.ets2jsc.infrastructure.parser.TypeScriptScriptParser;
import com.ets2jsc.shared.exception.CodeGenerationException;
import com.ets2jsc.shared.exception.CompilationException;
import com.ets2jsc.infrastructure.transformer.AstTransformer;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Factory for creating compilation pipelines.
 * <p>
 * This factory encapsulates the creation of properly configured
 * compilation pipelines with all necessary services.
 */
public class CompilationPipelineFactory {

    /**
     * Creates a new compilation pipeline with the given configuration.
     *
     * @param config the compiler configuration
     * @return a new compilation pipeline
     */
    public static CompilationPipeline createPipeline(CompilerConfig config) {
        ParserService parser = createParserService();
        TransformerService transformerService = createTransformerService(config);
        GeneratorService generatorService = createGeneratorService(config);

        return new CompilationPipeline(parser, transformerService, generatorService, config);
    }

    /**
     * Creates a parser service.
     *
     * @return a new parser service
     */
    private static ParserService createParserService() {
        TypeScriptScriptParser typeScriptParser = new TypeScriptScriptParser();

        return new ParserService() {
            @Override
            public SourceFile parseFile(Path sourcePath) throws com.ets2jsc.shared.exception.ParserException {
                try {
                    String sourceCode = Files.readString(sourcePath);
                    return typeScriptParser.parse(sourcePath.toString(), sourceCode);
                } catch (Exception e) {
                    throw new com.ets2jsc.shared.exception.ParserException("Failed to read file: " + sourcePath, e);
                }
            }

            @Override
            public SourceFile parseString(String fileName, String sourceCode) throws com.ets2jsc.shared.exception.ParserException {
                return typeScriptParser.parse(fileName, sourceCode);
            }

            @Override
            public boolean canParse(Path sourcePath) {
                if (sourcePath == null) {
                    return false;
                }
                String fileName = sourcePath.toString().toLowerCase();
                return fileName.endsWith(".ets")
                        || fileName.endsWith(".ts")
                        || fileName.endsWith(".tsx");
            }

            @Override
            public void close() {
                // Cleanup if needed
            }
        };
    }

    /**
     * Creates a transformer service.
     *
     * @param config the compiler configuration
     * @return a new transformer service
     */
    private static TransformerService createTransformerService(CompilerConfig config) {
        TransformerFactory transformerFactory = new DefaultTransformerFactory();
        var transformers = transformerFactory.createTransformers(config);

        return new TransformerService() {
            @Override
            public SourceFile transform(SourceFile sourceFile, CompilerConfig config) throws CompilationException {
                sourceFile.getStatements().replaceAll(statement -> {
                    AstNode current = statement;
                    for (var transformer : transformers) {
                        if (transformer.canTransform(current)) {
                            current = transformer.transform(current);
                        }
                    }
                    return current;
                });
                return sourceFile;
            }

            @Override
            public AstNode transformNode(AstNode node, CompilerConfig config) throws CompilationException {
                AstNode current = node;
                for (var transformer : transformers) {
                    if (transformer.canTransform(current)) {
                        current = transformer.transform(current);
                    }
                }
                return current;
            }

            @Override
            public boolean canTransform(AstNode node, CompilerConfig config) {
                return transformers.stream().anyMatch(t -> t.canTransform(node));
            }

            @Override
            public void reconfigure(CompilerConfig config) {
                // Reconfigure transformers if needed
            }

            @Override
            public void close() {
                // Cleanup if needed
            }
        };
    }

    /**
     * Creates a generator service.
     *
     * @param config the compiler configuration
     * @return a new generator service
     */
    private static GeneratorService createGeneratorService(CompilerConfig config) {
        CodeGenerator codeGenerator = new CodeGenerator(config);
        JsWriter jsWriter = new JsWriter();

        return new GeneratorService() {
            @Override
            public com.ets2jsc.domain.model.compilation.CompilationOutput generate(SourceFile sourceFile, CompilerConfig config)
                    throws CodeGenerationException {
                String jsCode = codeGenerator.generate(sourceFile);
                return new com.ets2jsc.domain.model.compilation.CompilationOutput(jsCode, null);
            }

            @Override
            public void generateToFile(SourceFile sourceFile, Path outputPath, CompilerConfig config)
                    throws CodeGenerationException {
                String jsCode = codeGenerator.generate(sourceFile);
                try {
                    jsWriter.write(outputPath, jsCode);
                } catch (Exception e) {
                    throw new CodeGenerationException("Failed to write to file: " + outputPath, e);
                }
            }

            @Override
            public void generateWithSourceMap(SourceFile sourceFile, Path outputPath, Path sourceMapPath,
                                             CompilerConfig config) throws CodeGenerationException {
                String jsCode = codeGenerator.generate(sourceFile);
                try {
                    jsWriter.writeWithSourceMap(outputPath, jsCode, sourceMapPath.getFileName().toString());
                } catch (Exception e) {
                    throw new CodeGenerationException("Failed to write files", e);
                }
            }

            @Override
            public void reconfigure(CompilerConfig config) {
                // Reconfigure if needed
            }

            @Override
            public void close() {
                // Cleanup if needed
            }
        };
    }
}
