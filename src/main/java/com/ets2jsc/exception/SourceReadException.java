package com.ets2jsc.exception;

/**
 * Exception thrown when reading source files fails.
 * This indicates file I/O errors, file not found, or permission issues.
 */
public class SourceReadException extends ParserException {

    /**
     * Constructs a new source read exception with the specified detail message.
     *
     * @param message the detail message
     */
    public SourceReadException(String message) {
        super(message);
    }

    /**
     * Constructs a new source read exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public SourceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
