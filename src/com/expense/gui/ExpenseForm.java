package com.expense.gui;

import com.expense.exception.DAOException;
import com.expense.exception.ValidationException;
import com.expense.model.Expense;
import com.expense.service.ExpenseService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Dialog for adding or editing an Expense.
 */
public class ExpenseForm extends JDialog {

    private final ExpenseService service;
    private Expense expense; // null = new

    private JTextField amountField = new JTextField();
    private JTextField descField = new JTextField();
    private JComboBox<String> categoryBox;
    private JTextField dateField = new JTextField(); // yyyy-MM-dd

    public ExpenseForm(Frame owner, ExpenseService service, Expense expense) {
        super(owner, true);
        this.service = service;
        this.expense = expense;
        setTitle(expense == null ? "Add Expense" : "Edit Expense" );
        categoryBox = new JComboBox<>(service.categories().toArray(new String[0]));
        buildUI();
        if (expense != null) populate();
        setSize(420,260);
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        panel.add(row("Amount (e.g. 12.50)", amountField));
        panel.add(row("Description", descField));
        panel.add(row("Category", categoryBox));
        panel.add(row("Date (yyyy-MM-dd)", dateField));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        buttons.add(save); buttons.add(cancel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(buttons);

        save.addActionListener(e -> onSave());
        cancel.addActionListener(e -> dispose());

        setContentPane(panel);
    }

    private JPanel row(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(5,0));
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(150,22));
        p.add(l, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return p;
    }

    private void populate() {
        amountField.setText(expense.getAmount().toPlainString());
        descField.setText(expense.getDescription());
        categoryBox.setSelectedItem(expense.getCategory());
        dateField.setText(expense.getExpenseDate().toString());
    }

    private void onSave() {
        try {
            if (expense == null) expense = new Expense();
            expense.setAmount(parseAmount(amountField.getText()));
            expense.setDescription(descField.getText());
            expense.setCategory((String) categoryBox.getSelectedItem());
            expense.setExpenseDate(parseDate(dateField.getText()));

            if (expense.getId() == null) {
                service.addExpense(expense);
                JOptionPane.showMessageDialog(this, "Expense added.");
            } else {
                service.updateExpense(expense);
                JOptionPane.showMessageDialog(this, "Expense updated.");
            }
            dispose();
        } catch (ValidationException | DAOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number/date format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BigDecimal parseAmount(String txt) { return new BigDecimal(txt.trim()); }
    private LocalDate parseDate(String txt) { return LocalDate.parse(txt.trim()); }
}
