package db;

import model.Account;
import model.CurrentAccount;
import model.LoanAccount;
import model.SavingsAccount;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // ── CREATE ──────────────────────────────────────────────
    public void insertAccount(Account account) throws SQLException {
        // 1. Add 'loan_limit' to the columns list and add an extra '?' placeholder
        String sql = """
                INSERT INTO accounts
                (account_number, account_type, holder_name, email, phone,
                 balance, overdraft_limit, interest_rate, loan_limit, nid, address, branch)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountNumber());
            stmt.setString(2, account.getAccountType());
            stmt.setString(3, account.getHolderName());
            stmt.setString(4, account.getEmail());
            stmt.setString(5, account.getPhone());
            stmt.setDouble(6, account.getBalance());

            // 2. Set polymorphic fields (overdraft, interest, loan)
            if (account instanceof CurrentAccount ca) {
                stmt.setDouble(7, ca.getOverDraftLimit()); // overdraft_limit
                stmt.setDouble(8, 0);                      // interest_rate
                stmt.setDouble(9, 0);                      // loan_limit
            } else if (account instanceof SavingsAccount sa) {
                stmt.setDouble(7, 0);                      // overdraft_limit
                stmt.setDouble(8, sa.getInterestRate());   // interest_rate
                stmt.setDouble(9, 0);                      // loan_limit
            } else if (account instanceof LoanAccount la) {
                stmt.setDouble(7, 0);                      // overdraft_limit
                stmt.setDouble(8, 0);                      // interest_rate
                stmt.setDouble(9, la.getLoanLimit());      // loan_limit
            } else {
                stmt.setDouble(7, 0);
                stmt.setDouble(8, 0);
                stmt.setDouble(9, 0);
            }

            // 3. Set remaining fields at the end
            stmt.setString(10, account.getNid());
            stmt.setString(11, account.getAddress());
            stmt.setString(12, account.getBranch());

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
        } else if (account instanceof LoanAccount la) {
            updateLoanState(account.getAccountNumber(), la.getAmountDue(), la.getLoanLimit());
        } else {
            updateBalance(account.getAccountNumber(), account.getBalance());
        }
    }

    public void updateLoanState(String accountNumber, double amountDue, double loanLimit) throws SQLException {
        String sql = "UPDATE accounts SET balance = ?, loan_limit = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amountDue);
            stmt.setDouble(2, loanLimit);
            stmt.setString(3, accountNumber);
            stmt.executeUpdate();
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
    public void updateAccountDetails(String accountNumber, String holderName,
            String email, String phone, String nid, String address) throws SQLException {
        String sql = """
                    UPDATE accounts
                    SET holder_name = ?, email = ?, phone = ?, nid = ?, address = ?
                    WHERE account_number = ?
                """;

        try (Connection conn = DatabaseConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, holderName);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, nid != null ? nid : "");
            stmt.setString(5, address != null ? address : "");
            stmt.setString(6, accountNumber);
            stmt.executeUpdate();
        }
    }

    // ── UPDATE BRANCH ─────────────────────────────────────────
    public void updateBranch(String accountNumber, String newBranch) throws SQLException {
        String sql = "UPDATE accounts SET branch = ? WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newBranch != null ? newBranch : "");
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        }
    }

    // ── DELETE ────────────────────────────────────────────────
    public void deleteTransactionsForAccount(String accountNumber) throws SQLException {
        String sql = "DELETE FROM transactions WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.executeUpdate();
        }
    }

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
        String nid = rs.getString("nid");
        String address = rs.getString("address");

        String branch = rs.getString("branch");

        return switch (type) {
            case "CurrentAccount" -> new CurrentAccount(
                    accNum, name, email, phone, balance,
                    rs.getDouble("overdraft_limit"), nid, address, branch);
            case "SavingsAccount" -> new SavingsAccount(
                    accNum, name, balance,
                    rs.getDouble("interest_rate"), email, phone, nid, address, branch);
            // ADD THIS CASE:
            case "LoanAccount" -> new LoanAccount(
                    accNum, name, email, phone, balance,
                    rs.getDouble("loan_limit"), nid, address, branch);
            default -> throw new SQLException("Unknown account type: " + type);
        };
    }
}