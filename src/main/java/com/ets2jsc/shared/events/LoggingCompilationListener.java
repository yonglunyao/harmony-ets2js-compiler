package com.ets2jsc.shared.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compilation listener that logs all compilation events.
 * <p>
 * This listener provides detailed logging of the compilation process,
 * useful for debugging and monitoring.
 * </p>
 *
 * @since 1.0
 */
public class LoggingCompilationListener implements CompilationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCompilationListener.class);

    @Override
    public void onCompilationStart(CompilationEvent event) {
        LOGGER.info("=== Compilation Started ===");
        if (event.getTotalFiles() > 0) {
            LOGGER.info("Total files to process: {}", event.getTotalFiles());
        }
    }

    @Override
    public void onFileCompilationStart(CompilationEvent event) {
        if (event.getSourcePath() != null) {
            LOGGER.info("Compiling: {} ({}/{})",
                    event.getSourcePath(),
                    event.getFilesProcessed() + 1,
                    event.getTotalFiles());
        }
    }

    @Override
    public void onFileCompilationSuccess(CompilationEvent event) {
        if (event.getSourcePath() != null && event.getOutputPath() != null) {
            LOGGER.debug("Successfully compiled: {} -> {}",
                    event.getSourcePath(),
                    event.getOutputPath());
        }
    }

    @Override
    public void onFileCompilationFailure(CompilationEvent event) {
        LOGGER.error("Failed to compile: {}",
                event.getSourcePath() != null ? event.getSourcePath() : "unknown");
        if (event.getError() != null) {
            LOGGER.error("Error: {}", event.getError().getMessage());
        }
        if (event.getMessage() != null) {
            LOGGER.error("Details: {}", event.getMessage());
        }
    }

    @Override
    public void onCompilationComplete(CompilationEvent event) {
        LOGGER.info("=== Compilation Complete ===");
        if (event.getFilesProcessed() > 0) {
            LOGGER.info("Files processed: {}", event.getFilesProcessed());
        }
        if (event.getMessage() != null) {
            LOGGER.info("Result: {}", event.getMessage());
        }
    }

    @Override
    public void onCompilationFailure(CompilationEvent event) {
        LOGGER.error("=== Compilation Failed ===");
        if (event.getError() != null) {
            LOGGER.error("Error: {}", event.getError().getMessage());
        }
        if (event.getMessage() != null) {
            LOGGER.error("Details: {}", event.getMessage());
        }
    }
}
