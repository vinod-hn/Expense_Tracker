package com.expense.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expense entity representing a single expense record.
 */
public class Expense extends BaseEntity {
    private BigDecimal amount; // must be >0, scale <=2
    private String description; // <=120 chars
    private String category; // Food, Transport, Bills, Entertainment, Others
    private LocalDate expenseDate; // not future

    public Expense() {}

    public Expense(Integer id, BigDecimal amount, String description, String category, LocalDate expenseDate) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.expenseDate = expenseDate;
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", expenseDate=" + expenseDate +
                '}';
    }
}
