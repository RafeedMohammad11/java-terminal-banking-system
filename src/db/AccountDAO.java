package db;

import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // ── CREATE ──────────────────────────────────────────────
    public void insertAccount(Account account) throws SQLException {
        String sql = """
                    INSERT INTO accounts
                    (account_number, account_type, holder_name, email, phone,
                     balance, overdraft_limit, interest_rate)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountNumber());
            stmt.setString(2, account.getAccountType());
            stmt.setString(3, account.getHolderName());
            stmt.setString(4, account.getEmail());
            stmt.setString(5, account.getPhone());
            stmt.setDouble(6, account.getBalance());

            if (account instanceof CurrentAccount ca) {
                stmt.setDouble(7, ca.getOverDraftLimit());
                stmt.setDouble(8, 0);
            } else if (account instanceof SavingsAccount sa) {
                stmt.setDouble(7, 0);
                stmt.setDouble(8, sa.getInterestRate());
            } else {
                stmt.setDouble(7, 0);
                stmt.setDouble(8, 0);
            }

            stmt.executeUpdate();
        }
    }

    // ── READ ALL ─────────────────────────────────────────────
    public List<Account> getAllAccounts() throws SQLException {
        String sql = "SELECT * FROM accounts";
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                accounts.add(mapRowToAccount(rs));
            }
        }
        return accounts;
    }

    // ── READ ONE ─────────────────────────────────────────────
    public Account getAccountByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapRowToAccount(rs);
            }
        }
        return null;
    }

    // ── UPDATE BALANCE ────────────────────────────────────────
    /**
     * Unified method to update account state (balance and overdraft if applicable).
     * Handles both regular accounts and current accounts.
     */
    public void updateAccountState(Account account) throws SQLException {
        if (account instanceof CurrentAccount ca) {
            updateBalanceAndOverdraft(account.getAccountNumber(), account.getBalance(), ca.getOverDraftLimit());
        } else {
            updateBalance(account.getAccountNumber(), account.getBalance());
        }
    }

    public void updateBalance(String accountNumber, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        }
    }

    public void updateBalanceAndOverdraft(String accountNumber, double newBalance, double newOverdraft)
            throws SQLException {
        String sql = "UPDATE accounts SET balance = ?, overdraft_limit = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setDouble(2, newOverdraft);
            stmt.setString(3, accountNumber);
            stmt.executeUpdate();
        }
    }

    // ── UPDATE INFO ───────────────────────────────────────────
    public void updateAccountInfo(String accountNumber, String holderName,
            String email, String phone) throws SQLException {
        String sql = """
                    UPDATE accounts
                    SET holder_name = ?, email = ?, phone = ?
                    WHERE account_number = ?
                """;

        try (Connection conn = DatabaseConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, holderName);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, accountNumber);
            stmt.executeUpdate();
        }
    }

    // ── DELETE ────────────────────────────────────────────────
    public void deleteAccount(String accountNumber) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.executeUpdate();
        }
    }

    // ── LOG TRANSACTION ───────────────────────────────────────
    public void logTransaction(String accountNumber, String type, double amount)
            throws SQLException {
        String sql = """
                    INSERT INTO transactions (account_number, type, amount, timestamp)
                    VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            stmt.setString(4, LocalDateTime.now().toString());
            stmt.executeUpdate();
        }
    }

    // ── PRIVATE MAPPER ────────────────────────────────────────
    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        String type = rs.getString("account_type");
        String accNum = rs.getString("account_number");
        String name = rs.getString("holder_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        double balance = rs.getDouble("balance");

        return switch (type) {
            case "CurrentAccount" -> new CurrentAccount(
                    accNum, name, email, phone, balance,
                    rs.getDouble("overdraft_limit"));
            case "SavingsAccount" -> new SavingsAccount(
                    accNum, name, balance,
                    rs.getDouble("interest_rate"), email, phone);
            default -> throw new SQLException("Unknown account type: " + type);
        };
    }
}