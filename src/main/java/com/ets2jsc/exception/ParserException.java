package com.ets2jsc.exception;

/**
 * Base exception for parsing errors in the ETS compiler.
 * Thrown when parsing of ETS/TypeScript source code fails.
 */
public class ParserException extends RuntimeException {

    /**
     * Constructs a new parser exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * Constructs a new parser exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
