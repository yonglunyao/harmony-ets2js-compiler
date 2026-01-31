package com.ets2jsc;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ClassDeclaration;
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
        this.codeGenerator = new CodeGenerator(config);
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

    /**
     * Main method for command-line usage.
     */
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                printUsage();
                System.exit(1);
            }

            // Create default configuration
            CompilerConfig config = CompilerConfig.createDefault();

            // Create compiler
            EtsCompiler compiler = new EtsCompiler(config);

            Path inputPath = Path.of(args[0]);
            Path outputPath = Path.of(args[1]);

            // Check if batch mode or parallel mode
            if (args.length > 2) {
                String mode = args[2];

                if ("--batch".equals(mode) || "--parallel".equals(mode)) {
                    // Batch/Parallel compilation
                    if (!Files.isDirectory(inputPath)) {
                        System.err.println("错误: " + inputPath + " 不是目录");
                        System.exit(1);
                    }

                    List<Path> sourceFiles = findSourceFiles(inputPath);

                    if (sourceFiles.isEmpty()) {
                        System.out.println("未找到 ETS/TS 文件");
                        System.exit(0);
                    }

                    System.out.println("找到 " + sourceFiles.size() + " 个文件");

                    if ("--parallel".equals(mode)) {
                        // Parallel compilation
                        int threads = Runtime.getRuntime().availableProcessors();
                        if (args.length > 3) {
                            try {
                                threads = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                System.err.println("无效的线程数: " + args[3]);
                                System.exit(1);
                            }
                        }

                        System.out.println("使用并行编译模式，线程数: " + threads);
                        long startTime = System.currentTimeMillis();

                        CompilationResult result = compiler.compileBatchParallel(sourceFiles, outputPath, threads);

                        long duration = System.currentTimeMillis() - startTime;

                        // Print results
                        System.out.println();
                        System.out.println("=== 编译结果 ===");
                        System.out.println(result.getSummary());
                        System.out.println("吞吐量: " + (result.getTotalCount() * 1000.0 / duration) + " 文件/秒");

                        if (!result.isAllSuccess()) {
                            System.out.println();
                            System.out.println("失败的文件:");
                            for (CompilationResult.FileResult failure : result.getFailures()) {
                                System.out.println("  - " + failure.getSourcePath());
                                System.out.println("    错误: " + failure.getMessage());
                            }
                            System.exit(1);
                        }
                    } else {
                        // Sequential batch compilation
                        long startTime = System.currentTimeMillis();
                        compiler.compileBatch(sourceFiles, outputPath);
                        long duration = System.currentTimeMillis() - startTime;

                        System.out.println("Compiled " + sourceFiles.size() + " files to " + outputPath);
                        System.out.println("耗时: " + duration + "ms");
                    }
                } else {
                    System.err.println("未知选项: " + mode);
                    printUsage();
                    System.exit(1);
                }
            } else {
                // Single file compilation
                compiler.compile(inputPath, outputPath);
                System.out.println("编译完成: " + inputPath + " -> " + outputPath);
            }

        } catch (Exception e) {
            System.err.println("编译失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Print usage information.
     */
    private static void printUsage() {
        System.err.println("用法: EtsCompiler <input> <output> [mode] [threads]");
        System.err.println();
        System.err.println("模式:");
        System.err.println("  (无)      - 编译单个文件");
        System.err.println("  --batch   - 批量编译目录（顺序）");
        System.err.println("  --parallel - 批量编译目录（并行）");
        System.err.println();
        System.err.println("示例:");
        System.err.println("  EtsCompiler src/App.ets build/App.js");
        System.err.println("  EtsCompiler src/main/ets build/dist --batch");
        System.err.println("  EtsCompiler src/main/ets build/dist --parallel");
        System.err.println("  EtsCompiler src/main/ets build/dist --parallel 8");
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
