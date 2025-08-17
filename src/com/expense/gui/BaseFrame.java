package com.expense.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Base frame providing convenience utilities for all windows.
 */
public abstract class BaseFrame extends JFrame {

    public BaseFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    protected void centerOnScreen() {
        pack();
        setLocationRelativeTo(null);
    }

    protected void showError(Throwable t) {
        t.printStackTrace();
        JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    protected JPanel formRow(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(5,0));
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(110, 22));
        p.add(l, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }
}
