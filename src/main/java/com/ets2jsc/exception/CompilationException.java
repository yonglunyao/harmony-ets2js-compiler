package com.ets2jsc.exception;

/**
 * Exception thrown when compilation fails.
 * Used for errors during ETS to JavaScript compilation process.
 */
public class CompilationException extends Exception {

    /**
     * Creates a new compilation exception with the specified message.
     *
     * @param message the error message
     */
    public CompilationException(String message) {
        super(message);
    }

    /**
     * Creates a new compilation exception with the specified message and cause.
     *
     * @param message the error message
     * @param cause the cause of the error
     */
    public CompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}
