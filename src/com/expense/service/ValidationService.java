package com.expense.service;

import com.expense.exception.ValidationException;
import com.expense.model.Expense;

/**
 * Contract for validating Expense objects.
 */
public interface ValidationService {
    void validate(Expense e) throws ValidationException;
}
