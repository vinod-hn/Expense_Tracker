package com.expense.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple connection factory. Update credentials here if needed.
 */
public final class DB {
    private static final String DB_NAME = "expense_tracker";
    // connection parameters reused for base (no schema) and schema URL
    private static final String PARAMS = "?useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=5000&socketTimeout=5000";
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/" + PARAMS; // no schema
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME + PARAMS;
    private static final String USER = "root";
    private static final String PWD = "S1h2w3e@gowda"; // provided credentials
    private static volatile boolean schemaEnsured = false;

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    private DB() {}

    public static Connection get() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PWD);
        } catch (SQLException first) {
            if (!schemaEnsured && isUnknownDatabase(first)) {
                // Attempt to create schema + table then retry once
                try {
                    ensureSchema();
                    schemaEnsured = true;
                    return DriverManager.getConnection(URL, USER, PWD);
                } catch (SQLException createEx) {
                    createEx.addSuppressed(first);
                    throw createEx;
                }
            }
            // Fallback: try 127.0.0.1 if localhost resolution issue
            if (URL.contains("localhost")) {
                String alt = URL.replace("localhost", "127.0.0.1");
                try { return DriverManager.getConnection(alt, USER, PWD); }
                catch (SQLException second) { second.addSuppressed(first); throw second; }
            }
            throw first;
        }
    }

    private static boolean isUnknownDatabase(SQLException ex) {
        return ex.getErrorCode() == 1049 || (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unknown database"));
    }

    private static void ensureSchema() throws SQLException {
        try (Connection c = DriverManager.getConnection(BASE_URL, USER, PWD); var st = c.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        }
        // create table
        try (Connection c2 = DriverManager.getConnection(URL, USER, PWD); var st2 = c2.createStatement()) {
            st2.executeUpdate("CREATE TABLE IF NOT EXISTS expenses (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "amount DECIMAL(10,2) NOT NULL CHECK (amount>0)," +
                    "description VARCHAR(120) NOT NULL," +
                    "category VARCHAR(40) NOT NULL," +
                    "expense_date DATE NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
        }
    }

    /**
     * Attempts a lightweight connectivity check returning a human-readable result.
     */
    public static String ping() {
        long start = System.currentTimeMillis();
        try (Connection c = get(); var st = c.createStatement(); var rs = st.executeQuery("SELECT 1")) {
            rs.next();
            long ms = System.currentTimeMillis() - start;
            return "DB OK (" + c.getMetaData().getURL() + ", latency " + ms + " ms)";
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder("DB ERROR: ")
                    .append(ex.getClass().getSimpleName()).append(':').append(' ')
                    .append(ex.getMessage());
            if (ex.getCause() != null) sb.append(" | cause=").append(ex.getCause().getClass().getSimpleName()).append(": ").append(ex.getCause().getMessage());
            // Suppressed (fallback attempts)
            if (ex.getSuppressed().length > 0) {
                sb.append(" | suppressed=");
                for (Throwable t : ex.getSuppressed()) sb.append(t.getClass().getSimpleName()).append(' ');
            }
            return sb.toString();
        }
    }
}
