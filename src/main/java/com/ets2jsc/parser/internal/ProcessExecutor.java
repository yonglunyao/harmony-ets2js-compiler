package com.ets2jsc.parser.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Executes external processes for the TypeScript parser.
 * Handles Node.js process spawning and JAR resource extraction.
 * <p>
 * This class is responsible for:
 * <ul>
 *   <li>Finding and extracting the TypeScript parser script from the classpath</li>
 *   <li>Executing Node.js processes with the parser script</li>
 *   <li>Capturing process output and handling errors</li>
 * </ul>
 */
public class ProcessExecutor {

    private final String scriptPath;

    /**
     * Creates a new ProcessExecutor and locates the parser script.
     * The script is extracted from the JAR if running from a packaged application.
     */
    public ProcessExecutor() {
        this.scriptPath = locateParserScript();
    }

    /**
     * Creates a new ProcessExecutor with a specific script path.
     *
     * @param scriptPath the path to the parser script
     */
    public ProcessExecutor(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    /**
     * Executes the TypeScript parser with the given arguments.
     *
     * @param sourceFile the source file to parse
     * @param outputFile the output file for AST JSON
     * @return the process result containing exit code and output
     * @throws IOException if process execution fails
     * @throws InterruptedException if process is interrupted
     */
    public ProcessResult execute(Path sourceFile, Path outputFile)
            throws IOException, InterruptedException {
        List<String> command = buildCommand(sourceFile, outputFile);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        try {
            // Read output (for debugging)
            String output = readProcessOutput(process.getInputStream());
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException("TypeScript parser failed with exit code " + exitCode
                        + ":\n" + output);
            }

            return new ProcessResult(exitCode, output, "");
        } finally {
            // Ensure the process is destroyed to prevent resource leaks
            if (process != null) {
                process.destroyForcibly();
            }
        }
    }

    /**
     * Builds the command list for process execution.
     *
     * @param sourceFile the source file
     * @param outputFile the output file
     * @return the command list
     */
    private List<String> buildCommand(Path sourceFile, Path outputFile) {
        List<String> command = new ArrayList<>();
        command.add("node");
        command.add(validateScriptPath(scriptPath).toString());
        command.add(sourceFile.toAbsolutePath().toString());
        command.add(outputFile.toAbsolutePath().toString());
        return command;
    }

    /**
     * Validates the script path to ensure it is safe and exists.
     *
     * @param path the script path to validate
     * @return the validated normalized path
     * @throws SecurityException if the path is invalid
     */
    private Path validateScriptPath(String path) {
        if (path == null || path.isEmpty()) {
            throw new SecurityException("Script path cannot be null or empty");
        }

        Path scriptPath = Path.of(path).toAbsolutePath().normalize();

        // Validate the file exists and is a regular file
        if (!Files.exists(scriptPath)) {
            throw new SecurityException("Script path does not exist: " + path);
        }
        if (!Files.isRegularFile(scriptPath)) {
            throw new SecurityException("Script path is not a regular file: " + path);
        }

        // Validate the file extension
        String fileName = scriptPath.getFileName().toString();
        if (!fileName.endsWith(".js")) {
            throw new SecurityException("Script path must be a .js file: " + path);
        }

        return scriptPath;
    }

    /**
     * Reads process output into a string.
     *
     * @param inputStream the input stream
     * @return the output string
     * @throws IOException if reading fails
     */
    private String readProcessOutput(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Locates the TypeScript parser script in the classpath.
     * <p>
     * If running from a JAR, extracts the parser directory to a temp location.
     * If running from classpath on filesystem, uses the direct path.
     *
     * @return the absolute path to the parser script
     */
    private String locateParserScript() {
        try {
            // Try to use the classpath location directly
            java.net.URL scriptUrl = getClass().getClassLoader().getResource("typescript-parser/index.js");

            if (scriptUrl != null && "file".equals(scriptUrl.getProtocol())) {
                // Running from classpath on filesystem
                return new File(scriptUrl.getFile()).getAbsolutePath();
            } else {
                // Running from JAR - extract to temp directory
                return extractParserDirectory();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to locate TypeScript parser script", e);
        }
    }

    /**
     * Extracts the TypeScript parser directory from the JAR to a temp location.
     *
     * @return the absolute path to the extracted parser script
     * @throws Exception if extraction fails
     */
    private String extractParserDirectory() throws Exception {
        Path tempDir = Files.createTempDirectory("typescript-parser-");
        tempDir.toFile().deleteOnExit();

        // Extract all resources from typescript-parser directory
        String jarPath = getJarPath();
        extractResourceDirectory("typescript-parser/", tempDir, jarPath);

        return tempDir.resolve("index.js").toAbsolutePath().toString();
    }

    /**
     * Gets the path to the JAR file containing this class.
     *
     * @return the JAR file path
     * @throws Exception if getting the path fails
     */
    private String getJarPath() throws Exception {
        String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

        // Handle Windows paths with spaces
        if (jarPath.startsWith("/") && jarPath.contains(":")) {
            jarPath = jarPath.substring(1);
        }

        return jarPath;
    }

    /**
     * Extracts all files from a resource directory in the JAR to a temp directory.
     *
     * @param resourcePath the resource path in the JAR
     * @param targetDir the target directory
     * @param jarPath the path to the JAR file
     * @throws Exception if extraction fails
     */
    private void extractResourceDirectory(String resourcePath, Path targetDir, String jarPath) throws Exception {
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(resourcePath)) {
                    String relativePath = entryName.substring(resourcePath.length());
                    Path targetPath = targetDir.resolve(relativePath.replace('/', File.separatorChar));

                    if (entry.isDirectory()) {
                        Files.createDirectories(targetPath);
                    } else {
                        Files.createDirectories(targetPath.getParent());
                        try (InputStream is = jarFile.getInputStream(entry)) {
                            Files.copy(is, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the path to the parser script.
     *
     * @return the parser script path
     */
    public String getScriptPath() {
        return scriptPath;
    }

    /**
     * Result of a process execution.
     */
    public static class ProcessResult {
        private final int exitCode;
        private final String output;
        private final String errorOutput;

        public ProcessResult(int exitCode, String output, String errorOutput) {
            this.exitCode = exitCode;
            this.output = output;
            this.errorOutput = errorOutput;
        }

        public int getExitCode() {
            return exitCode;
        }

        public String getOutput() {
            return output;
        }

        public String getErrorOutput() {
            return errorOutput;
        }

        public boolean isSuccess() {
            return exitCode == 0;
        }
    }
}
