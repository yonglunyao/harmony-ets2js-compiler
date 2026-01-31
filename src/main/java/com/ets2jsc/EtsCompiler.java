package com.ets2jsc;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.generator.JsWriter;
import com.ets2jsc.generator.SourceMapGenerator;
import com.ets2jsc.parser.AstBuilder;
import com.ets2jsc.transformer.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Main compiler for ETS to JS compilation.
 * Orchestrates the entire compilation process including parsing,
 * transformation, code generation, and output writing.
 */
public class EtsCompiler {

    private final CompilerConfig config;
    private final List<AstTransformer> transformers;
    private final CodeGenerator codeGenerator;
    private final JsWriter jsWriter;

    /**
     * Creates a new ETS compiler with the given configuration.
     *
     * @param config the compiler configuration
     */
    public EtsCompiler(CompilerConfig config) {
        this.config = config;
        this.transformers = new ArrayList<>();
        this.codeGenerator = new CodeGenerator(config);
        this.jsWriter = new JsWriter();

        initializeTransformers();
    }

    /**
     * Initializes the transformation pipeline.
     */
    private void initializeTransformers() {
        // Add transformers in order
        transformers.add(new DecoratorTransformer(config.isPartialUpdateMode()));
        transformers.add(new BuildMethodTransformer(config.isPartialUpdateMode()));
        transformers.add(new ComponentTransformer());
    }

    /**
     * Compiles a single ETS source file to JavaScript.
     *
     * @param sourcePath path to the ETS source file
     * @param outputPath path to the output JavaScript file
     * @throws CompilationException if compilation fails
     */
    public void compile(Path sourcePath, Path outputPath) throws CompilationException {
        try {
            // Normalize paths
            sourcePath = sourcePath.normalize();
            outputPath = outputPath.normalize();

            // Step 1: Read source file
            String sourceCode = Files.readString(sourcePath);

            // Step 2: Parse source to AST
            AstBuilder astBuilder = new AstBuilder();
            SourceFile sourceFile = astBuilder.build(sourcePath.toString(), sourceCode);

            // Step 3: Transform AST
            AstNode transformedAst = transformAst(sourceFile);

            // Step 4: Generate JavaScript code
            String jsCode = generateCode(transformedAst);

            // Step 5: Write output
            if (config.isGenerateSourceMap()) {
                String sourceMap = generateSourceMap(sourceFile);
                Path sourceMapPath = Path.of(outputPath + ".map");
                jsWriter.writeWithSourceMap(outputPath, jsCode, sourceMapPath.getFileName().toString());
                jsWriter.write(sourceMapPath, sourceMap);
            } else {
                jsWriter.write(outputPath, jsCode);
            }

        } catch (IOException e) {
            throw new CompilationException("Failed to compile file: " + sourcePath, e);
        }
    }

    /**
     * Compiles multiple ETS source files (sequential).
     *
     * @param sourceFiles list of source file paths
     * @param outputDir output directory
     * @throws CompilationException if compilation fails
     */
    public void compileBatch(List<Path> sourceFiles, Path outputDir) throws CompilationException {
        for (Path sourceFile : sourceFiles) {
            String fileName = sourceFile.getFileName().toString();
            String outputName = fileName.replace(".ets", ".js").replace(".ts", ".js");
            Path outputPath = outputDir.resolve(outputName);

            compile(sourceFile, outputPath);
        }
    }

    /**
     * Compiles multiple ETS source files in parallel.
     * 使用多线程并行编译多个 ETS 文件
     *
     * @param sourceFiles list of source file paths
     * @param outputDir output directory
     * @return compilation result with statistics
     */
    public CompilationResult compileBatchParallel(List<Path> sourceFiles, Path outputDir) {
        ParallelEtsCompiler parallelCompiler = new ParallelEtsCompiler(config, null);
        try {
            CompilationResult result = parallelCompiler.compileParallel(sourceFiles, outputDir);
            return result;
        } finally {
            parallelCompiler.shutdown();
        }
    }

    /**
     * Compiles multiple ETS source files in parallel with custom thread pool size.
     * 使用指定线程池大小并行编译多个 ETS 文件
     *
     * @param sourceFiles list of source file paths
     * @param outputDir output directory
     * @param threadPoolSize number of threads to use
     * @return compilation result with statistics
     */
    public CompilationResult compileBatchParallel(List<Path> sourceFiles, Path outputDir, int threadPoolSize) {
        ParallelEtsCompiler parallelCompiler = new ParallelEtsCompiler(config, threadPoolSize);
        try {
            CompilationResult result = parallelCompiler.compileParallel(sourceFiles, outputDir);
            return result;
        } finally {
            parallelCompiler.shutdown();
        }
    }

    /**
     * Transforms the AST using the registered transformers.
     * Recursively transforms nested nodes.
     */
    private AstNode transformAst(AstNode ast) {
        AstNode current = ast;

        // If this is a SourceFile, transform its statements
        if (current instanceof SourceFile) {
            SourceFile sourceFile = (SourceFile) current;

            for (int i = 0; i < sourceFile.getStatements().size(); i++) {
                AstNode stmt = sourceFile.getStatements().get(i);
                AstNode transformedStmt = transformNode(stmt);
                sourceFile.getStatements().set(i, transformedStmt);
            }
            return sourceFile;
        }

        return transformNode(current);
    }

    /**
     * Transforms a single AST node through all transformers.
     */
    private AstNode transformNode(AstNode node) {
        AstNode current = node;

        for (AstTransformer transformer : transformers) {
            if (transformer.canTransform(current)) {
                current = transformer.transform(current);
            }
        }

        return current;
    }

    /**
     * Generates JavaScript code from the transformed AST.
     */
    private String generateCode(AstNode ast) {
        if (ast instanceof SourceFile) {
            return codeGenerator.generate((SourceFile) ast);
        } else {
            return codeGenerator.generate(ast);
        }
    }

    /**
     * Generates source map for the compiled file.
     */
    private String generateSourceMap(SourceFile sourceFile) {
        SourceMapGenerator generator = new SourceMapGenerator();
        // In production, would track all mappings during transformation
        return generator.generate();
    }

    /**
     * Gets the compiler configuration.
     */
    public CompilerConfig getConfig() {
        return config;
    }

    /**
     * Custom exception for compilation errors.
     */
    public static class CompilationException extends Exception {
        public CompilationException(String message) {
            super(message);
        }

        public CompilationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
