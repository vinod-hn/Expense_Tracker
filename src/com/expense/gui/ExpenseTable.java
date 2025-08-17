package com.expense.gui;

import com.expense.exception.DAOException;
import com.expense.model.Expense;
import com.expense.service.ExpenseService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Window listing expenses with basic CRUD actions.
 */
public class ExpenseTable extends BaseFrame {

    private final ExpenseService service;
    private final ExpenseTableModel model = new ExpenseTableModel();
    private final JTable table = new JTable(model);
    private final JLabel totalLabel = new JLabel("Total: 0.00");
    private JComboBox<String> filterBox; // null = all

    public ExpenseTable(ExpenseService service) throws DAOException {
        super("Expenses");
        this.service = service;
        buildUI();
        load();
        setSize(760,400);
        centerOnScreen();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
    add(new JScrollPane(table), BorderLayout.CENTER);

    // Toolbar with actions and category filter
    JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(new AbstractAction("Refresh") { public void actionPerformed(ActionEvent e){ refresh(); }});
        tb.add(new AbstractAction("Edit Selected") { public void actionPerformed(ActionEvent e){ editSelected(); }});
        tb.add(new AbstractAction("Delete Selected") { public void actionPerformed(ActionEvent e){ deleteSelected(); }});
        tb.add(new AbstractAction("Add New") { public void actionPerformed(ActionEvent e){ addNew(); }});
        tb.add(new AbstractAction("Close") { public void actionPerformed(ActionEvent e){ dispose(); }});
    tb.addSeparator();
    filterBox = new JComboBox<>();
    filterBox.addItem("All Categories");
    service.categories().forEach(filterBox::addItem);
    filterBox.addActionListener(e -> applyFilter());
    tb.add(new JLabel("Filter:"));
    tb.add(filterBox);

        add(tb, BorderLayout.NORTH);

    // Status bar for total
    JPanel south = new JPanel(new BorderLayout());
    south.setBorder(BorderFactory.createEmptyBorder(2,8,2,8));
    south.add(totalLabel, BorderLayout.WEST);
    add(south, BorderLayout.SOUTH);
    }

    private void load() throws DAOException {
        model.setData(service.listExpenses());
        updateTotal();
    }

    private void refresh() { try { load(); applyFilter(); } catch (DAOException ex) { showError(ex);} }

    private void applyFilter() {
        if (filterBox == null) return; // not yet built
        String sel = (String) filterBox.getSelectedItem();
        if (sel == null || sel.startsWith("All")) { // reload full list already in model
            updateTotal();
            return;
        }
        // Filter in-memory and update model clone
        List<Expense> original = model.getData();
        List<Expense> filtered = new ArrayList<>();
        for (Expense e : original) {
            if (sel.equals(e.getCategory())) filtered.add(e);
        }
        model.setData(filtered);
        updateTotal();
    }

    private void updateTotal() {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (Expense e : model.getData()) {
            if (e.getAmount() != null) total = total.add(e.getAmount());
        }
        totalLabel.setText("Total: " + total.toPlainString());
    }

    private Expense getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return model.getAt(row);
    }

    private void editSelected() {
    Expense e = getSelected();
    if (e == null) { showInfo("Select a row first"); return; }
    ExpenseForm dlg = new ExpenseForm(this, service, e);
    dlg.setVisible(true);
    refresh();
    }

    private void addNew() {
    ExpenseForm dlg = new ExpenseForm(this, service, null);
    dlg.setVisible(true);
    refresh();
    }

    private void deleteSelected() {
        Expense e = getSelected();
        if (e == null) { showInfo("Select a row first"); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Delete selected expense?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                service.deleteExpense(e.getId());
                refresh();
            } catch (DAOException ex) { showError(ex);}        
        }
    }

    // Table model
    private static class ExpenseTableModel extends AbstractTableModel {
    private final String[] cols = {"ID","Amount","Description","Category","Date","Created"};
        private List<Expense> data = new ArrayList<>();

        public void setData(List<Expense> list) { this.data = list; fireTableDataChanged(); }
        public Expense getAt(int row) { return data.get(row); }
    public List<Expense> getData() { return data; }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            Expense e = data.get(rowIndex);
            switch (columnIndex) {
                case 0: return e.getId();
                case 1: return e.getAmount();
                case 2: return e.getDescription();
                case 3: return e.getCategory();
                case 4: return e.getExpenseDate();
                case 5: return e.getCreatedAt();
            }
            return null;
        }

        @Override public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return Integer.class;
            if (columnIndex == 1) return BigDecimal.class;
            return String.class;
        }
    }
}
