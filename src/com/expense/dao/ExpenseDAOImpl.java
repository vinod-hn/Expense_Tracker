package com.expense.dao;

import com.expense.exception.DAOException;
import com.expense.model.Expense;
import com.expense.util.DB;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ExpenseDAO using MySQL.
 */
public class ExpenseDAOImpl implements ExpenseDAO {

    private static final String INSERT_SQL = "INSERT INTO expenses(amount, description, category, expense_date) VALUES(?,?,?,?)";
    private static final String SELECT_ALL_SQL = "SELECT id, amount, description, category, expense_date, created_at FROM expenses ORDER BY expense_date DESC, id DESC";
    private static final String UPDATE_SQL = "UPDATE expenses SET amount=?, description=?, category=?, expense_date=? WHERE id=?";
    private static final String DELETE_SQL = "DELETE FROM expenses WHERE id=?";

    @Override
    public void insert(Expense e) throws DAOException {
        try (Connection conn = DB.get();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBigDecimal(1, e.getAmount());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getCategory());
            ps.setDate(4, Date.valueOf(e.getExpenseDate()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new DAOException("Failed to insert expense", ex);
        }
    }

    @Override
    public List<Expense> findAll() throws DAOException {
        List<Expense> list = new ArrayList<>();
        try (Connection conn = DB.get();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Expense e = new Expense();
                e.setId(rs.getInt("id"));
                e.setAmount(rs.getBigDecimal("amount"));
                e.setDescription(rs.getString("description"));
                e.setCategory(rs.getString("category"));
                Date d = rs.getDate("expense_date");
                if (d != null) e.setExpenseDate(d.toLocalDate());
                Timestamp ts = rs.getTimestamp("created_at");
                e.setCreatedAtFromTimestamp(ts);
                list.add(e);
            }
        } catch (SQLException ex) {
            throw new DAOException("Failed to fetch expenses", ex);
        }
        return list;
    }

    @Override
    public void update(Expense e) throws DAOException {
        try (Connection conn = DB.get();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setBigDecimal(1, e.getAmount());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getCategory());
            ps.setDate(4, Date.valueOf(e.getExpenseDate()));
            ps.setInt(5, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DAOException("Failed to update expense id=" + e.getId(), ex);
        }
    }

    @Override
    public void delete(int id) throws DAOException {
        try (Connection conn = DB.get();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DAOException("Failed to delete expense id=" + id, ex);
        }
    }
}
