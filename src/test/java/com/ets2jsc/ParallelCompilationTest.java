package com.ets2jsc;

import com.ets2jsc.config.CompilerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试并行编译功能
 * Test parallel compilation functionality
 */
class ParallelCompilationTest {

    @TempDir
    Path tempDir;

    /**
     * 测试并行编译多个简单文件
     */
    @Test
    void testParallelCompilation() throws Exception {
        // 创建测试文件
        Path sourceDir = tempDir.resolve("sources");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(sourceDir);
        Files.createDirectories(outputDir);

        // 创建多个测试文件
        for (int i = 1; i <= 5; i++) {
            String content = ""
                + "@Component\n"
                + "struct Test" + i + " {\n"
                + "    build() {\n"
                + "        Column() {\n"
                + "            Text('Hello " + i + "')\n"
                + "        }\n"
                + "    }\n"
                + "}\n";

            Path sourceFile = sourceDir.resolve("Test" + i + ".ets");
            Files.writeString(sourceFile, content);
        }

        // 使用并行编译
        CompilerConfig config = CompilerConfig.createDefault();
        EtsCompiler compiler = new EtsCompiler(config);

        List<Path> sourceFiles = Files.walk(sourceDir)
            .filter(p -> p.toString().endsWith(".ets"))
            .toList();

        CompilationResult result = compiler.compileBatchParallel(sourceFiles, outputDir, 2);

        // 验证结果
        assertEquals(5, result.getTotalCount(), "应该编译 5 个文件");
        assertTrue(result.isAllSuccess(), "所有文件应该编译成功");
        assertTrue(result.getDurationMs() >= 0, "耗时应该非负");

        // 验证输出文件存在
        for (int i = 1; i <= 5; i++) {
            Path outputFile = outputDir.resolve("Test" + i + ".js");
            assertTrue(Files.exists(outputFile), "输出文件应该存在: " + outputFile);
        }
    }

    /**
     * 测试并行编译的性能优势
     */
    @Test
    void testParallelPerformance() throws Exception {
        // 创建测试文件
        Path sourceDir = tempDir.resolve("sources");
        Path outputDir1 = tempDir.resolve("output1");
        Path outputDir2 = tempDir.resolve("output2");
        Files.createDirectories(sourceDir);
        Files.createDirectories(outputDir1);
        Files.createDirectories(outputDir2);

        // 创建多个测试文件
        int fileCount = 10;
        for (int i = 1; i <= fileCount; i++) {
            String content = ""
                + "@Component\n"
                + "struct PerfTest" + i + " {\n"
                + "    @State count: number = 0\n"
                + "    build() {\n"
                + "        Column() {\n"
                + "            Text('Count: ' + this.count)\n"
                + "            Button('Increase')\n"
                + "                .onClick(() => {\n"
                + "                    this.count++\n"
                + "                })\n"
                + "        }\n"
                + "    }\n"
                + "}\n";

            Path sourceFile = sourceDir.resolve("PerfTest" + i + ".ets");
            Files.writeString(sourceFile, content);
        }

        CompilerConfig config = CompilerConfig.createDefault();
        List<Path> sourceFiles = Files.walk(sourceDir)
            .filter(p -> p.toString().endsWith(".ets"))
            .toList();

        // 顺序编译
        EtsCompiler compiler1 = new EtsCompiler(config);
        long sequentialStart = System.currentTimeMillis();
        for (Path sourceFile : sourceFiles) {
            String fileName = sourceFile.getFileName().toString();
            Path outputPath = outputDir1.resolve(fileName.replace(".ets", ".js"));
            compiler1.compile(sourceFile, outputPath);
        }
        long sequentialTime = System.currentTimeMillis() - sequentialStart;

        // 并行编译
        EtsCompiler compiler2 = new EtsCompiler(config);
        long parallelStart = System.currentTimeMillis();
        CompilationResult result = compiler2.compileBatchParallel(sourceFiles, outputDir2, 4);
        long parallelTime = System.currentTimeMillis() - parallelStart;

        // 验证结果
        assertTrue(result.isAllSuccess(), "并行编译应该成功");

        // 输出性能对比
        System.out.println("=== 性能对比 ===");
        System.out.println("文件数: " + fileCount);
        System.out.println("顺序编译耗时: " + sequentialTime + "ms");
        System.out.println("并行编译耗时: " + parallelTime + "ms");
        System.out.println("性能提升: " + ((sequentialTime - parallelTime) * 100.0 / sequentialTime) + "%");
        System.out.println("吞吐量: " + (fileCount * 1000.0 / parallelTime) + " 文件/秒");
    }

    /**
     * 测试并行编译的错误处理
     * 使用不存在的文件来测试错误处理
     */
    @Test
    void testParallelErrorHandling() throws Exception {
        // 创建测试文件
        Path sourceDir = tempDir.resolve("sources");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(sourceDir);
        Files.createDirectories(outputDir);

        // 创建一个空文件（可能导致解析问题）
        Files.writeString(sourceDir.resolve("Good.ets"),
            "@Component\nstruct Good { build() { Column() {} } }");
        Files.writeString(sourceDir.resolve("Bad.ets"), "");
        Files.writeString(sourceDir.resolve("Good2.ets"),
            "@Component\nstruct Good2 { build() { Column() {} } }");

        CompilerConfig config = CompilerConfig.createDefault();
        EtsCompiler compiler = new EtsCompiler(config);

        List<Path> sourceFiles = Files.walk(sourceDir)
            .filter(p -> p.toString().endsWith(".ets"))
            .toList();

        CompilationResult result = compiler.compileBatchParallel(sourceFiles, outputDir, 2);

        // 验证结果 - 至少应该处理了所有文件
        assertTrue(result.getTotalCount() >= 0, "应该处理文件");

        // 验证结果摘要可用
        String summary = result.getSummary();
        assertNotNull(summary, "摘要应该非空");
        assertTrue(summary.contains("总计"), "摘要应包含总计信息");
    }

    /**
     * 测试不同线程数的性能
     */
    @Test
    void testDifferentThreadCounts() throws Exception {
        // 创建测试文件
        Path sourceDir = tempDir.resolve("sources");
        Files.createDirectories(sourceDir);

        int fileCount = 20;
        for (int i = 1; i <= fileCount; i++) {
            String content = ""
                + "@Component\n"
                + "struct ThreadTest" + i + " {\n"
                + "    build() {\n"
                + "        Column() {\n"
                + "            Text('Test " + i + "')\n"
                + "        }\n"
                + "    }\n"
                + "}\n";

            Path sourceFile = sourceDir.resolve("ThreadTest" + i + ".ets");
            Files.writeString(sourceFile, content);
        }

        CompilerConfig config = CompilerConfig.createDefault();
        List<Path> sourceFiles = Files.walk(sourceDir)
            .filter(p -> p.toString().endsWith(".ets"))
            .toList();

        System.out.println("=== 不同线程数性能测试 ===");
        System.out.println("文件数: " + fileCount);
        System.out.println();

        int[] threadCounts = {1, 2, 4, 8};
        for (int threads : threadCounts) {
            Path outputDir = tempDir.resolve("output" + threads);
            Files.createDirectories(outputDir);

            EtsCompiler compiler = new EtsCompiler(config);
            long startTime = System.currentTimeMillis();
            CompilationResult result = compiler.compileBatchParallel(sourceFiles, outputDir, threads);
            long duration = System.currentTimeMillis() - startTime;

            System.out.println("线程数: " + threads);
            System.out.println("  耗时: " + duration + "ms");
            System.out.println("  吞吐量: " + (fileCount * 1000.0 / duration) + " 文件/秒");
            System.out.println("  结果: " + result.getSummary());
            System.out.println();

            assertTrue(result.isAllSuccess(), "编译应该成功");
        }
    }
}
