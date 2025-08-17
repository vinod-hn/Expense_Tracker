package com.expense.exception;

/**
 * Thrown when user input violates validation constraints.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) { super(message); }
}
