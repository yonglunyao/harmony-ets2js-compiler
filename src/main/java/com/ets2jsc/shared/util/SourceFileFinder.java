package com.ets2jsc.shared.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utility class for finding ETS/TypeScript source files in directories.
 * <p>
 * This class provides methods to discover source files with specific extensions
 * within a directory tree.
 */
public final class SourceFileFinder {

    // Default source file extensions
    private static final String EXT_ETS = ".ets";
    private static final String EXT_TS = ".ts";

    private SourceFileFinder() {
        // Utility class - prevent instantiation
    }

    /**
     * Finds all ETS/TypeScript source files in a directory.
     *
     * @param dir the directory to search
     * @return list of source file paths
     * @throws IOException if an I/O error occurs
     */
    public static List<Path> findSourceFiles(Path dir) throws IOException {
        return findSourceFiles(dir, SourceFileFinder::isSourceFile);
    }

    /**
     * Finds all files in a directory matching the given predicate.
     *
     * @param dir the directory to search
     * @param predicate the filter predicate
     * @return list of matching file paths
     * @throws IOException if an I/O error occurs
     */
    public static List<Path> findSourceFiles(Path dir, Predicate<Path> predicate) throws IOException {
        List<Path> sourceFiles = new ArrayList<>();

        if (Files.isDirectory(dir)) {
            try (Stream<Path> stream = Files.walk(dir)) {
                stream.filter(predicate)
                      .forEach(sourceFiles::add);
            }
        }

        return sourceFiles;
    }

    /**
     * Finds all ETS source files in a directory.
     *
     * @param dir the directory to search
     * @return list of ETS source file paths
     * @throws IOException if an I/O error occurs
     */
    public static List<Path> findEtsFiles(Path dir) throws IOException {
        return findSourceFiles(dir, SourceFileFinder::isEtsFile);
    }

    /**
     * Finds all TypeScript source files in a directory.
     *
     * @param dir the directory to search
     * @return list of TypeScript source file paths
     * @throws IOException if an I/O error occurs
     */
    public static List<Path> findTsFiles(Path dir) throws IOException {
        return findSourceFiles(dir, SourceFileFinder::isTsFile);
    }

    /**
     * Checks if a path is an ETS source file.
     *
     * @param path the path to check
     * @return true if the path ends with .ets
     */
    public static boolean isEtsFile(Path path) {
        return path.toString().endsWith(EXT_ETS);
    }

    /**
     * Checks if a path is a TypeScript source file.
     *
     * @param path the path to check
     * @return true if the path ends with .ts
     */
    public static boolean isTsFile(Path path) {
        return path.toString().endsWith(EXT_TS);
    }

    /**
     * Checks if a path is an ETS or TypeScript source file.
     *
     * @param path the path to check
     * @return true if the path ends with .ets or .ts
     */
    public static boolean isSourceFile(Path path) {
        String pathStr = path.toString();
        return pathStr.endsWith(EXT_ETS) || pathStr.endsWith(EXT_TS);
    }
}
