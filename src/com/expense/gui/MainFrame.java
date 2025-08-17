package com.expense.gui;

import com.expense.exception.DAOException;
import com.expense.service.ExpenseService;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window containing navigation buttons.
 */
public class MainFrame extends BaseFrame {

    private final ExpenseService service = new ExpenseService();

    public MainFrame() {
        super("Expense Tracker");
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        JPanel center = new JPanel(new GridLayout(0,1,10,10));

        JButton addBtn = new JButton("Add Expense");
        addBtn.addActionListener(e -> openAddForm());

        JButton viewBtn = new JButton("View Expenses");
        viewBtn.addActionListener(e -> openTable());

    JButton pingBtn = new JButton("Test DB Connection");
    pingBtn.addActionListener(e -> testDb());

        center.add(addBtn);
        center.add(viewBtn);
    center.add(pingBtn);

        add(new JLabel("Personal Expense Tracker", SwingConstants.CENTER), BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        setSize(320,200);
        centerOnScreen();
    }

    private void openAddForm() {
        new ExpenseForm(this, service, null).setVisible(true);
    }

    private void openTable() {
        try {
            new ExpenseTable(service).setVisible(true);
        } catch (DAOException ex) {
            showError(ex);
        }
    }

    private void testDb() {
        String result = com.expense.util.DB.ping();
        if (result.startsWith("DB OK")) {
            showInfo(result);
        } else {
            showError(new RuntimeException(result));
        }
    }
}
