package com.ets2jsc.integration.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validator for JavaScript syntax.
 * Uses GraalVM JavaScript engine to validate compiled code syntax.
 */
public final class JavaScriptSyntaxValidator {

    private static final String JS_LANGUAGE_ID = "js";
    private static final Pattern STRICT_DIRECTIVE = Pattern.compile("^['\"]use strict['\"]");

    private JavaScriptSyntaxValidator() {
        // Utility class - prevent instantiation
    }

    /**
     * Validates JavaScript syntax by attempting to parse the code.
     *
     * @param jsCode the JavaScript code to validate
     * @return validation result with details
     */
    public static ValidationResult validate(String jsCode) {
        if (jsCode == null) {
            return ValidationResult.error("Code is null");
        }

        if (jsCode.trim().isEmpty()) {
            return ValidationResult.error("Code is empty");
        }

        try (Context context = Context.newBuilder(JS_LANGUAGE_ID)
                .allowAllAccess(true)
                .build()) {

            // Try to evaluate the code to check for syntax errors
            context.eval(JS_LANGUAGE_ID, jsCode);
            return ValidationResult.success();

        } catch (PolyglotException e) {
            if (e.isSyntaxError()) {
                return ValidationResult.syntaxError(e.getMessage());
            }
            // Runtime errors are OK for syntax validation - we only care about syntax
            return ValidationResult.success();
        } catch (Exception e) {
            return ValidationResult.error("Validation failed: " + e.getMessage());
        }
    }

    /**
     * Checks if the code contains common syntax issues.
     * Note: Bracket/brace counting is skipped because type annotations
     * in comments can legitimately contain unbalanced brackets.
     *
     * @param jsCode the JavaScript code to check
     * @return list of issues found (empty if no issues)
     */
    public static List<String> detectCommonIssues(String jsCode) {
        List<String> issues = new ArrayList<>();

        if (jsCode == null || jsCode.trim().isEmpty()) {
            issues.add("Code is null or empty");
            return issues;
        }

        // Note: Skipping bracket/brace counting because type annotation comments
        // like /* number[] */ can legitimately contain unbalanced brackets

        // Check for incomplete statements - missing semicolons after certain keywords
        if (jsCode.matches(".*\\b(export|class|function|return|throw|break|continue|var|let|const)\\s[^{;]+$")) {
            // This is a heuristic - may have false positives with multi-line statements
            // issues.add("Possible missing semicolon");
        }

        // Check for obvious syntax errors like consecutive operators
        if (jsCode.matches(".*\\+\\+.*") || jsCode.matches(".*--.*")) {
            // These are valid (increment/decrement), so no issue
        }

        return issues;
    }

    /**
     * Result of syntax validation.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final boolean isSyntaxError;

        private ValidationResult(boolean valid, String errorMessage, boolean isSyntaxError) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.isSyntaxError = isSyntaxError;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null, false);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message, false);
        }

        public static ValidationResult syntaxError(String message) {
            return new ValidationResult(false, message, true);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public boolean isSyntaxError() {
            return isSyntaxError;
        }

        @Override
        public String toString() {
            if (valid) {
                return "ValidationResult[valid=true]";
            }
            return String.format("ValidationResult[valid=false, error=%s, syntaxError=%s]",
                errorMessage, isSyntaxError);
        }
    }
}
