package com.ets2jsc.compiler;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.SourceFile;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.exception.CompilationException;
import com.ets2jsc.generator.CodeGenerator;
import com.ets2jsc.generator.JsWriter;
import com.ets2jsc.generator.SourceMapGenerator;
import com.ets2jsc.parser.AstBuilder;
import com.ets2jsc.transformer.AstTransformer;
import com.ets2jsc.transformer.ComponentTransformer;
import com.ets2jsc.transformer.DecoratorTransformer;
import com.ets2jsc.transformer.BuildMethodTransformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Base compiler implementation containing common compilation logic.
 * Provides the core compilation pipeline that can be extended with different execution strategies.
 */
public abstract class BaseCompiler implements ICompiler {

    protected final CompilerConfig config;
    protected final List<AstTransformer> transformers;
    protected final CodeGenerator codeGenerator;
    protected final JsWriter jsWriter;

    /**
     * Creates a new base compiler with the given configuration.
     *
     * @param config the compiler configuration
     */
    protected BaseCompiler(CompilerConfig config) {
        this.config = config;
        this.transformers = new ArrayList<>();
        this.codeGenerator = new CodeGenerator(config);
        this.jsWriter = new JsWriter();

        initializeTransformers();
    }

    @Override
    public void compile(Path sourcePath, Path outputPath) throws CompilationException {
        try {
            // Normalize paths
            sourcePath = sourcePath.normalize();
            outputPath = outputPath.normalize();

            // Step 1: Read source file
            final String sourceCode = Files.readString(sourcePath);

            // Step 2: Parse source to AST
            final AstBuilder astBuilder = new AstBuilder();
            final SourceFile sourceFile = astBuilder.build(sourcePath.toString(), sourceCode);

            // Step 3: Transform AST
            final AstNode transformedAst = transformAst(sourceFile);

            // Step 4: Generate JavaScript code
            final String jsCode = generateCode(transformedAst);

            // Step 5: Write output
            if (config.isGenerateSourceMap()) {
                final String sourceMap = generateSourceMap(sourceFile);
                final Path sourceMapPath = Path.of(outputPath + ".map");
                jsWriter.writeWithSourceMap(outputPath, jsCode, sourceMapPath.getFileName().toString());
                jsWriter.write(sourceMapPath, sourceMap);
            } else {
                jsWriter.write(outputPath, jsCode);
            }

        } catch (IOException e) {
            throw new CompilationException("Failed to compile file: " + sourcePath, e);
        }
    }

    @Override
    public CompilationResult compileBatchWithStructure(List<Path> sourceFiles, Path baseDir, Path outputDir)
            throws CompilationException {
        final List<CompilationResult.FileResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (final Path sourceFile : sourceFiles) {
            try {
                // Calculate relative path from base directory
                final Path relativePath = baseDir.relativize(sourceFile);

                // Transform source file extension to .js
                final String relativePathStr = relativePath.toString();
                final String outputPathStr = relativePathStr
                        .replace(".ets", ".js")
                        .replace(".ts", ".js")
                        .replace(".tsx", ".js")
                        .replace(".jsx", ".js");

                final Path outputPath = outputDir.resolve(outputPathStr);

                // Create parent directories if needed
                final Path parentDir = outputPath.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }

                // Compile the file
                compile(sourceFile, outputPath);

                results.add(new CompilationResult.FileResult(sourceFile.toString(), outputPath.toString(), null, true));
                successCount++;

            } catch (Exception e) {
                results.add(new CompilationResult.FileResult(sourceFile.toString(), null, e.getMessage(), false));
                failureCount++;
            }
        }

        return new CompilationResult(results, sourceFiles.size(), successCount, failureCount, 0);
    }

    @Override
    public CompilationResult compileProject(Path sourceDir, Path outputDir, boolean copyResources)
            throws CompilationException {
        if (!Files.isDirectory(sourceDir)) {
            throw new CompilationException("Source path is not a directory: " + sourceDir);
        }

        try {
            // Normalize paths
            sourceDir = sourceDir.normalize();
            outputDir = outputDir.normalize();

            // Create output directory if it doesn't exist
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // Find all source files in the project
            final List<Path> sourceFiles = com.ets2jsc.util.SourceFileFinder.findSourceFiles(sourceDir);

            // Compile source files while preserving directory structure
            final CompilationResult compileResult;
            if (!sourceFiles.isEmpty()) {
                compileResult = compileBatchWithStructure(sourceFiles, sourceDir, outputDir);
            } else {
                // No source files to compile, create empty result
                compileResult = new CompilationResult();
            }

            // Copy resource files if requested
            int copiedResourceCount = 0;
            if (copyResources) {
                copiedResourceCount = com.ets2jsc.util.ResourceFileCopier.copyResourceFiles(sourceDir, outputDir);
            }

            return new CompilationResult(
                    compileResult.fileResults.values().stream().toList(),
                    compileResult.getTotalCount(),
                    compileResult.getSuccessCount(),
                    compileResult.getFailureCount(),
                    copiedResourceCount);

        } catch (IOException e) {
            throw new CompilationException("Failed to compile project: " + sourceDir, e);
        }
    }

    @Override
    public CompilerConfig getConfig() {
        return config;
    }

    /**
     * Initializes the transformation pipeline.
     * Subclasses can override to add custom transformers.
     */
    protected void initializeTransformers() {
        transformers.add(new DecoratorTransformer(config.isPartialUpdateMode()));
        transformers.add(new BuildMethodTransformer(config.isPartialUpdateMode()));
        transformers.add(new ComponentTransformer());
    }

    /**
     * Transforms the AST using the registered transformers.
     * Recursively transforms nested nodes.
     */
    protected AstNode transformAst(AstNode ast) {
        // If this is a SourceFile, transform its statements
        if (ast instanceof SourceFile sourceFile) {
            sourceFile.getStatements().replaceAll(this::transformNode);
            return sourceFile;
        }

        return transformNode(ast);
    }

    /**
     * Transforms a single AST node through all transformers.
     */
    protected AstNode transformNode(AstNode node) {
        AstNode current = node;

        for (final AstTransformer transformer : transformers) {
            if (transformer.canTransform(current)) {
                current = transformer.transform(current);
            }
        }

        return current;
    }

    /**
     * Generates JavaScript code from the transformed AST.
     */
    protected String generateCode(AstNode ast) {
        if (ast instanceof SourceFile) {
            return codeGenerator.generate((SourceFile) ast);
        } else {
            return codeGenerator.generate(ast);
        }
    }

    /**
     * Generates source map for the compiled file.
     */
    protected String generateSourceMap(SourceFile sourceFile) {
        final SourceMapGenerator generator = new SourceMapGenerator();
        // In production, would track all mappings during transformation
        return generator.generate();
    }
}
