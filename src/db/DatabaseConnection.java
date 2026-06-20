package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:banking.db";
    private static volatile Connection instance = null;
    private static final Object LOCK = new Object();

    // Private constructor — no one can instantiate this class
    private DatabaseConnection() {
    }

    public static Connection getInstance() throws SQLException {
        // Double-checked locking pattern for thread safety
        if (instance == null || instance.isClosed()) {
            synchronized (LOCK) {
                if (instance == null || instance.isClosed()) {
                    instance = DriverManager.getConnection(URL);
                }
            }
        }
        return instance;
    }

    /**
     * Initializes database tables. Should be called once at application startup.
     */
    public static void initialize() throws SQLException {
        Connection conn = getInstance();
        initializeTables(conn);
    }

    /**
     * Begins a transaction on the connection.
     */
    public static void beginTransaction() throws SQLException {
        getInstance().setAutoCommit(false);
    }

    /**
     * Commits the current transaction.
     */
    public static void commit() throws SQLException {
        Connection conn = getInstance();
        if (!conn.getAutoCommit()) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }

    /**
     * Rolls back the current transaction in case of errors.
     */
    public static void rollback() {
        try {
            Connection conn = getInstance();
            if (!conn.getAutoCommit()) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error during rollback: " + e.getMessage());
        }
    }

    private static void initializeTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS accounts (
                        account_number  TEXT PRIMARY KEY,
                        account_type    TEXT NOT NULL,
                        holder_name     TEXT NOT NULL,
                        email           TEXT,
                        phone           TEXT,
                        balance         REAL NOT NULL DEFAULT 0,
                        overdraft_limit REAL DEFAULT 0,
                        interest_rate   REAL DEFAULT 0,
                        loan_limit      REAL DEFAULT 0,  
                        nid             TEXT DEFAULT '',
                        address         TEXT DEFAULT ''
                    );
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS transactions (
                        id             INTEGER PRIMARY KEY AUTOINCREMENT,
                        account_number TEXT NOT NULL,
                        type           TEXT NOT NULL,
                        amount         REAL NOT NULL,
                        timestamp      TEXT NOT NULL,
                        FOREIGN KEY (account_number) REFERENCES accounts(account_number)
                    );
                    """);

            ensureColumnExists(conn, "accounts", "nid", "TEXT DEFAULT ''");
            ensureColumnExists(conn, "accounts", "address", "TEXT DEFAULT ''");
            ensureColumnExists(conn, "accounts", "loan_limit", "REAL DEFAULT 0");
        }
    }

    /**
     * Ensures a column exists on older database files created before nid/address were added.
     */
    private static void ensureColumnExists(Connection conn, String tableName,
                                           String columnName, String columnDef) throws SQLException {
        if (columnExists(conn, tableName, columnName)) {
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDef);
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName)
            throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}