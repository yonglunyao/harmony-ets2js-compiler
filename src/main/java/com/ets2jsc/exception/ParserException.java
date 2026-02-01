package com.ets2jsc.exception;

/**
 * Base exception for parsing errors in the ETS compiler.
 * Thrown when parsing of ETS/TypeScript source code fails.
 * <p>
 * Note: This exception extends RuntimeException rather than Exception
 * because it is used to wrap lower-level exceptions (IOException,
 * Process execution errors) that are not recoverable at the parser level.
 * The calling code can still catch and handle this exception specifically.
 * </p>
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
