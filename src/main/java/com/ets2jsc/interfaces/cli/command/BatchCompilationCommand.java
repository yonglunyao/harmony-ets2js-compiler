package com.ets2jsc.interfaces.cli.command;

import com.ets2jsc.compiler.CompilationResult;
import com.ets2jsc.compiler.ICompiler;
import com.ets2jsc.shared.exception.CompilationException;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command for compiling multiple source files to a single output directory.
 * <p>
 * This command encapsulates batch compilation where all source files
 * are compiled to the same output directory.
 * </p>
 *
 * @since 1.0
 */
public class BatchCompilationCommand implements CompilationCommand {

    private final ICompiler compiler;
    private final List<Path> sourceFiles;
    private final Path outputDir;

    /**
     * Creates a new batch compilation command.
     *
     * @param compiler the compiler to use
     * @param sourceFiles the list of source files to compile
     * @param outputDir the output directory
     * @throws IllegalArgumentException if any parameter is null or sourceFiles is empty
     */
    public BatchCompilationCommand(ICompiler compiler, List<Path> sourceFiles, Path outputDir) {
        if (compiler == null) {
            throw new IllegalArgumentException("Compiler cannot be null");
        }
        if (sourceFiles == null || sourceFiles.isEmpty()) {
            throw new IllegalArgumentException("Source files cannot be null or empty");
        }
        if (outputDir == null) {
            throw new IllegalArgumentException("Output directory cannot be null");
        }

        this.compiler = compiler;
        this.sourceFiles = List.copyOf(sourceFiles);
        this.outputDir = outputDir;
    }

    @Override
    public CompilationResult execute() throws CompilationException {
        return compiler.compileBatch(sourceFiles, outputDir);
    }

    @Override
    public String getDescription() {
        String fileList = sourceFiles.stream()
                .map(Path::toString)
                .collect(Collectors.joining(", "));
        return String.format("BatchCompilation: %d files -> %s", sourceFiles.size(), outputDir);
    }
}
