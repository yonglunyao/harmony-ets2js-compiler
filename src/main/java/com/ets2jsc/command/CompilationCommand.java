package com.ets2jsc.command;

import com.ets2jsc.compiler.CompilationResult;
import com.ets2jsc.exception.CompilationException;

import java.nio.file.Path;

/**
 * Interface for compilation commands following the Command pattern.
 * <p>
 * This interface encapsulates a compilation action as an object,
 * allowing for parameterization, queuing, and logging of compilation
 * operations. Different command types implement specific compilation
 * strategies.
 * </p>
 *
 * @since 1.0
 */
public interface CompilationCommand {

    /**
     * Executes the compilation command.
     *
     * @return the result of the compilation
     * @throws CompilationException if compilation fails
     */
    CompilationResult execute() throws CompilationException;

    /**
     * Gets the source path for this command.
     *
     * @return the source path, or null if not applicable
     */
    default Path getSourcePath() {
        return null;
    }

    /**
     * Gets the output path for this command.
     *
     * @return the output path, or null if not applicable
     */
    default Path getOutputPath() {
        return null;
    }

    /**
     * Returns a human-readable description of this command.
     *
     * @return the command description
     */
    default String getDescription() {
        return getClass().getSimpleName();
    }
}
