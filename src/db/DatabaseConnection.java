package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "JDBC:sqlite:banking.db";
    private static Connection instance = null;

    // Private constructor — no one can instantiate this class
    private DatabaseConnection() {}

    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL);
            initializeTables(instance);
        }
        return instance;
    }

    private static void initializeTables(Connection conn) throws SQLException {
        String createAccounts = """
            CREATE TABLE IF NOT EXISTS accounts (
                account_number TEXT PRIMARY KEY,
                account_type   TEXT NOT NULL,
                holder_name    TEXT NOT NULL,
                email          TEXT,
                phone          TEXT,
                balance        REAL NOT NULL DEFAULT 0,
                overdraft_limit REAL DEFAULT 0,
                interest_rate   REAL DEFAULT 0
            );
        """;

        String createTransactions = """
            CREATE TABLE IF NOT EXISTS transactions (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                account_number TEXT NOT NULL,
                type        TEXT NOT NULL,
                amount      REAL NOT NULL,
                timestamp   TEXT NOT NULL,
                FOREIGN KEY (account_number) REFERENCES accounts(account_number)
            );
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createAccounts);
            stmt.execute(createTransactions);
        }
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