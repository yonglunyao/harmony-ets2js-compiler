package com.ets2jsc.exception;

/**
 * Exception thrown when code generation fails.
 * This indicates errors in generating JavaScript from AST.
 */
public class CodeGenerationException extends ParserException {

    /**
     * Constructs a new code generation exception with the specified detail message.
     *
     * @param message the detail message
     */
    public CodeGenerationException(String message) {
        super(message);
    }

    /**
     * Constructs a new code generation exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public CodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
