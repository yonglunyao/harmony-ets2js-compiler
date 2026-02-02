package com.ets2jsc.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for copying resource files while preserving directory structure.
 * Handles copying of non-source files (images, configs, JSON, etc.) to output directory.
 */
public final class ResourceFileCopier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceFileCopier.class);

    // File extensions to exclude from resource copying (source files)
    private static final Set<String> SOURCE_EXTENSIONS = new HashSet<>();

    static {
        SOURCE_EXTENSIONS.add(".ets");
        SOURCE_EXTENSIONS.add(".ts");
        SOURCE_EXTENSIONS.add(".tsx");
        SOURCE_EXTENSIONS.add(".jsx");
        // Exclude map files as they are generated during compilation
        SOURCE_EXTENSIONS.add(".map");
    }

    // Directories to exclude from copying
    private static final Set<String> EXCLUDED_DIRECTORIES = new HashSet<>();

    static {
        EXCLUDED_DIRECTORIES.add("node_modules");
        EXCLUDED_DIRECTORIES.add(".git");
        EXCLUDED_DIRECTORIES.add(".idea");
        EXCLUDED_DIRECTORIES.add(".vscode");
        EXCLUDED_DIRECTORIES.add("dist");
        EXCLUDED_DIRECTORIES.add("build");
        EXCLUDED_DIRECTORIES.add("target");
        EXCLUDED_DIRECTORIES.add("__pycache__");
        EXCLUDED_DIRECTORIES.add(".hg");
        EXCLUDED_DIRECTORIES.add(".svn");
    }

    private ResourceFileCopier() {
        // Utility class - prevent instantiation
    }

    /**
     * Copies all non-source files from source directory to target directory.
     * Preserves the directory structure.
     *
     * @param sourceDir the source directory
     * @param targetDir the target directory
     * @param copiedCount count of copied files (output parameter)
     * @throws IOException if an I/O error occurs
     */
    public static void copyResourceFiles(Path sourceDir, Path targetDir, int[] copiedCount) throws IOException {
        if (!Files.isDirectory(sourceDir)) {
            throw new IOException("Source path is not a directory: " + sourceDir);
        }

        LOGGER.debug("Copying resource files from {} to {}", sourceDir, targetDir);

        Files.walkFileTree(sourceDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path relativePath = sourceDir.relativize(dir);
                        String dirName = dir.getFileName().toString();

                        // Skip excluded directories
                        if (EXCLUDED_DIRECTORIES.contains(dirName)) {
                            LOGGER.debug("Skipping excluded directory: {}", dir);
                            return FileVisitResult.SKIP_SUBTREE;
                        }

                        // Create corresponding directory in target
                        Path targetDirPath = targetDir.resolve(relativePath);
                        if (!Files.exists(targetDirPath)) {
                            Files.createDirectories(targetDirPath);
                        }

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        // Skip source files
                        if (isSourceFile(file)) {
                            return FileVisitResult.CONTINUE;
                        }

                        Path relativePath = sourceDir.relativize(file);
                        Path targetFile = targetDir.resolve(relativePath);

                        // Only copy if target doesn't exist or is older
                        if (shouldCopyFile(file, targetFile)) {
                            Files.copy(file, targetFile);
                            if (copiedCount != null && copiedCount.length > 0) {
                                copiedCount[0]++;
                            }
                            LOGGER.trace("Copied resource: {} -> {}", file, targetFile);
                        }

                        return FileVisitResult.CONTINUE;
                    }
                });

        LOGGER.info("Resource files copied to {}", targetDir);
    }

    /**
     * Copies all non-source files from source directory to target directory.
     *
     * @param sourceDir the source directory
     * @param targetDir the target directory
     * @return count of copied files
     * @throws IOException if an I/O error occurs
     */
    public static int copyResourceFiles(Path sourceDir, Path targetDir) throws IOException {
        int[] copiedCount = {0};
        copyResourceFiles(sourceDir, targetDir, copiedCount);
        return copiedCount[0];
    }

    /**
     * Checks if a file is a source file that should be compiled, not copied.
     *
     * @param file the file to check
     * @return true if the file is a source file
     */
    private static boolean isSourceFile(Path file) {
        String fileName = file.getFileName().toString();
        for (String ext : SOURCE_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a file should be copied based on existence and modification time.
     *
     * @param sourceFile the source file
     * @param targetFile the target file
     * @return true if the file should be copied
     * @throws IOException if an I/O error occurs
     */
    private static boolean shouldCopyFile(Path sourceFile, Path targetFile) throws IOException {
        if (!Files.exists(targetFile)) {
            return true;
        }

        // Copy if source is newer than target
        long sourceModTime = Files.getLastModifiedTime(sourceFile).toMillis();
        long targetModTime = Files.getLastModifiedTime(targetFile).toMillis();

        return sourceModTime > targetModTime;
    }

    /**
     * Adds a custom source file extension to exclude from resource copying.
     *
     * @param extension the file extension (e.g., ".custom")
     */
    public static void addSourceExtension(String extension) {
        String normalizedExt = extension.startsWith(".") ? extension : "." + extension;
        SOURCE_EXTENSIONS.add(normalizedExt);
    }

    /**
     * Adds a custom directory name to exclude from copying.
     *
     * @param dirName the directory name
     */
    public static void addExcludedDirectory(String dirName) {
        EXCLUDED_DIRECTORIES.add(dirName);
    }

    /**
     * Checks if a directory is excluded from copying.
     *
     * @param dirName the directory name
     * @return true if the directory is excluded
     */
    public static boolean isExcludedDirectory(String dirName) {
        return EXCLUDED_DIRECTORIES.contains(dirName);
    }

    /**
     * Checks if a file extension is treated as a source file.
     *
     * @param extension the file extension
     * @return true if the extension is a source file extension
     */
    public static boolean isSourceExtension(String extension) {
        String normalizedExt = extension.startsWith(".") ? extension : "." + extension;
        return SOURCE_EXTENSIONS.contains(normalizedExt);
    }
}
