package com.expense.service;

import com.expense.dao.ExpenseDAO;
import com.expense.dao.ExpenseDAOImpl;
import com.expense.exception.DAOException;
import com.expense.exception.ValidationException;
import com.expense.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service layer handling validation + DAO delegation.
 */
public class ExpenseService implements ValidationService {

    private final ExpenseDAO dao = new ExpenseDAOImpl();
    private static final Set<String> CATEGORIES = new HashSet<>(Arrays.asList(
            "Food", "Transport", "Bills", "Entertainment", "Others"
    ));

    @Override
    public void validate(Expense e) throws ValidationException {
        if (e.getAmount() == null) {
            throw new ValidationException("Amount is required");
        }
        BigDecimal amt = e.getAmount();
        if (amt.signum() <= 0) {
            throw new ValidationException("Amount must be > 0");
        }
        if (amt.scale() > 2) {
            throw new ValidationException("Amount can have at most 2 decimals");
        }
        if (amt.compareTo(new BigDecimal("1000000")) > 0) {
            throw new ValidationException("Amount must be <= 1,000,000");
        }
        if (isBlank(e.getDescription())) {
            throw new ValidationException("Description is required");
        }
        if (e.getDescription().length() > 120) {
            throw new ValidationException("Description max length is 120");
        }
        if (isBlank(e.getCategory())) {
            throw new ValidationException("Category is required");
        }
        if (!CATEGORIES.contains(e.getCategory())) {
            throw new ValidationException("Category must be one of: " + CATEGORIES);
        }
        if (e.getExpenseDate() == null) {
            throw new ValidationException("Date is required");
        }
        LocalDate today = LocalDate.now();
        if (e.getExpenseDate().isAfter(today)) {
            throw new ValidationException("Date cannot be in the future");
        }
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    public void addExpense(Expense e) throws ValidationException, DAOException {
        validate(e);
        dao.insert(e);
    }

    public List<Expense> listExpenses() throws DAOException {
        return dao.findAll();
    }

    public void updateExpense(Expense e) throws ValidationException, DAOException {
        validate(e);
        dao.update(e);
    }

    public void deleteExpense(int id) throws DAOException {
        dao.delete(id);
    }

    public Set<String> categories() { return CATEGORIES; }
}
