package com.ets2jsc;

import com.ets2jsc.config.CompilerConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Test harness to compile ETS files using the EtsCompiler.
 */
public class CompilerRunner {

    public static void main(String[] args) {
        String sourceDir = "src/test/resources/fixtures/myutils/package";
        String outputDir = "target/compiled-output";

        try {
            // Create compiler configuration
            CompilerConfig config = CompilerConfig.createDefault();
            config.setSourcePath(sourceDir);
            config.setBuildPath(outputDir);
            config.setGenerateSourceMap(true);

            // Create compiler
            EtsCompiler compiler = new EtsCompiler(config);

            // Find all ETS files
            List<Path> etsFiles = findEtsFiles(sourceDir);
            System.out.println("Found " + etsFiles.size() + " ETS files to compile:");

            for (Path file : etsFiles) {
                System.out.println("  - " + file);
            }

            // Compile each file
            System.out.println("\nCompiling...");
            int successCount = 0;
            int failCount = 0;

            for (Path sourceFile : etsFiles) {
                try {
                    Path sourceDirPath = Paths.get(sourceDir).normalize();
                    Path normalizedSource = sourceFile.normalize();
                    String relativePath = sourceDirPath.relativize(normalizedSource).toString();

                    // Create output filename
                    String outputFileName = sourceFile.getFileName().toString().replace(".ets", ".js");
                    Path outputFile = Paths.get(outputDir, outputFileName);

                    System.out.println("Compiling: " + relativePath);
                    compiler.compile(sourceFile, outputFile);
                    System.out.println("  ✓ Output: " + outputFile);
                    successCount++;
                } catch (EtsCompiler.CompilationException e) {
                    System.err.println("  ✗ Failed: " + e.getMessage());
                    e.printStackTrace();
                    failCount++;
                }
            }

            // Summary
            System.out.println("\n=== Compilation Summary ===");
            System.out.println("Total files: " + etsFiles.size());
            System.out.println("Success: " + successCount);
            System.out.println("Failed: " + failCount);

            if (failCount == 0) {
                System.out.println("\n✓ All files compiled successfully!");
                System.out.println("Output directory: " + Paths.get(outputDir).toAbsolutePath());
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Find all .ets files in the given directory recursively.
     */
    private static List<Path> findEtsFiles(String sourceDir) throws IOException {
        List<Path> etsFiles = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(sourceDir))) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".ets"))
                 .forEach(etsFiles::add);
        }

        return etsFiles;
    }
}
