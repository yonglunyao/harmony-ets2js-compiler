package com.ets2jsc.exception;

/**
 * Exception thrown when the TypeScript parser initialization fails.
 * This typically indicates a problem with the Node.js environment,
 * missing parser scripts, or invalid configuration.
 */
public class ParserInitializationException extends ParserException {

    /**
     * Constructs a new parser initialization exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ParserInitializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new parser initialization exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ParserInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
