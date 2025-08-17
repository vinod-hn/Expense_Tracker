package com.expense;

import com.expense.gui.MainFrame;

import javax.swing.*;

/**
 * Application entry point.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
