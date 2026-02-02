package com.ets2jsc.shared.exception;

/**
 * Exception thrown when AST conversion or transformation fails.
 * This indicates errors in converting TypeScript AST to our internal AST model.
 */
public class AstConversionException extends ParserException {

    /**
     * Constructs a new AST conversion exception with the specified detail message.
     *
     * @param message the detail message
     */
    public AstConversionException(String message) {
        super(message);
    }

    /**
     * Constructs a new AST conversion exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public AstConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
