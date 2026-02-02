package com.ets2jsc.compiler;

import com.ets2jsc.api.ICodeGenerator;
import com.ets2jsc.api.IModuleFactory;
import com.ets2jsc.api.IParser;
import com.ets2jsc.api.ITransformer;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.di.ModuleServiceProvider;
import com.ets2jsc.shared.exception.CompilationException;
import com.ets2jsc.factory.TransformerFactory;
import com.ets2jsc.factory.DefaultTransformerFactory;
import com.ets2jsc.transformer.AstTransformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Base compiler implementation containing common compilation logic.
 * Provides the core compilation pipeline that can be extended with different execution strategies.
 * <p>
 * This class now uses facade interfaces for modular architecture, enabling better testability
 * and separation of concerns.
 */
public abstract class BaseCompiler implements ICompiler {

    protected final CompilerConfig config;
    protected final IParser parser;
    protected final ITransformer transformer;
    protected final ICodeGenerator codeGenerator;
    protected final List<AstTransformer> transformers;
    protected final TransformerFactory transformerFactory;

    /**
     * Creates a new base compiler with the given configuration.
     *
     * @param config the compiler configuration
     */
    protected BaseCompiler(CompilerConfig config) {
        this(config, new DefaultTransformerFactory());
    }

    /**
     * Creates a new base compiler with the given configuration and transformer factory.
     *
     * @param config the compiler configuration
     * @param transformerFactory the factory for creating transformers
     */
    protected BaseCompiler(CompilerConfig config, TransformerFactory transformerFactory) {
        this.config = config;
        this.transformerFactory = transformerFactory;
        this.transformers = transformerFactory.createTransformers(config);

        // Create module facades using the service provider
        IModuleFactory moduleFactory = ModuleServiceProvider.getInstance().getModuleFactory();
        this.parser = moduleFactory.createParser();
        this.transformer = moduleFactory.createTransformer(config);
        this.codeGenerator = moduleFactory.createCodeGenerator(config);
    }

    @Override
    public void compile(Path sourcePath, Path outputPath) throws CompilationException {
        try {
            // Normalize paths
            sourcePath = sourcePath.normalize();
            outputPath = outputPath.normalize();

            // Step 1: Parse source to AST using parser facade
            final SourceFile sourceFile = parser.parseFile(sourcePath);

            // Step 2: Transform AST using transformer facade
            final SourceFile transformedSource = transformer.transform(sourceFile);

            // Step 3: Generate JavaScript code using code generator facade
            if (config.isGenerateSourceMap()) {
                final Path sourceMapPath = Path.of(outputPath + ".map");
                codeGenerator.generateWithSourceMap(transformedSource, outputPath, sourceMapPath);
            } else {
                codeGenerator.generateToFile(transformedSource, outputPath);
            }

        } catch (Exception e) {
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
            final List<Path> sourceFiles = com.ets2jsc.shared.util.SourceFileFinder.findSourceFiles(sourceDir);

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
                copiedResourceCount = com.ets2jsc.shared.util.ResourceFileCopier.copyResourceFiles(sourceDir, outputDir);
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

    @Override
    public void close() {
        // Clean up module resources
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
            codeGenerator.close();
        } catch (Exception e) {
            // Log and continue
        }
    }
}
