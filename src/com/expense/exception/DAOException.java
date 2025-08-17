package com.expense.exception;

/**
 * Wraps SQL or persistence related errors.
 */
public class DAOException extends Exception {
    public DAOException(String message, Throwable cause) { super(message, cause); }
}
