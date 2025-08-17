package com.expense.dao;

import java.util.List;
import com.expense.model.Expense;
import com.expense.exception.DAOException;

/**
 * DAO interface defining CRUD operations for Expense.
 */
public interface ExpenseDAO {
    void insert(Expense e) throws DAOException;
    List<Expense> findAll() throws DAOException;
    void update(Expense e) throws DAOException;
    void delete(int id) throws DAOException;
}
