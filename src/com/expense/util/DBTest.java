package com.expense.util;

/**
 * Standalone CLI test to verify database connectivity without launching GUI.
 */
public class DBTest {
    public static void main(String[] args) {
        System.out.println(DB.ping());
    }
}
