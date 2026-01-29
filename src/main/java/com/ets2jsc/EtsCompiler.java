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
 * Main entry point for the ETS to JS compiler.
 * Orchestrates the entire compilation process.
 */
public class EtsCompiler {

    private final CompilerConfig config;
    private final List<AstTransformer> transformers;
    private final CodeGenerator codeGenerator;
    private final JsWriter jsWriter;

    public EtsCompiler(CompilerConfig config) {
        this.config = config;
        this.transformers = new ArrayList<>();
        this.codeGenerator = new CodeGenerator();
        this.jsWriter = new JsWriter();

        // Initialize transformers
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
     * Compiles multiple ETS source files.
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
     * Transforms the AST using the registered transformers.
     */
    private AstNode transformAst(AstNode ast) {
        AstNode current = ast;

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

    /**
     * Main method for command-line usage.
     */
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.err.println("Usage: EtsCompiler <input-file> <output-file>");
                System.err.println("   or: EtsCompiler <input-dir> <output-dir> --batch");
                System.exit(1);
            }

            // Create default configuration
            CompilerConfig config = CompilerConfig.createDefault();

            // Create compiler
            EtsCompiler compiler = new EtsCompiler(config);

            // Check if batch mode
            if (args.length > 2 && "--batch".equals(args[2])) {
                // Batch compilation
                Path inputDir = Path.of(args[0]);
                Path outputDir = Path.of(args[1]);

                List<Path> sourceFiles = findSourceFiles(inputDir);
                compiler.compileBatch(sourceFiles, outputDir);

                System.out.println("Compiled " + sourceFiles.size() + " files to " + outputDir);
            } else {
                // Single file compilation
                Path inputFile = Path.of(args[0]);
                Path outputFile = Path.of(args[1]);

                compiler.compile(inputFile, outputFile);

                System.out.println("Compiled " + inputFile + " to " + outputFile);
            }

        } catch (Exception e) {
            System.err.println("Compilation failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Finds all ETS/TypeScript source files in a directory.
     */
    private static List<Path> findSourceFiles(Path dir) throws IOException {
        List<Path> sourceFiles = new ArrayList<>();

        if (Files.isDirectory(dir)) {
            Files.walk(dir)
                .filter(path -> path.toString().endsWith(".ets") ||
                               path.toString().endsWith(".ts"))
                .forEach(sourceFiles::add);
        }

        return sourceFiles;
    }
}
