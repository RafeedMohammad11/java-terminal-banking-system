package service;

import db.AccountDAO;
import db.BranchDAO;
import db.DatabaseConnection;
import exception.AccountNotFoundException;
import exception.DatabaseException;
import exception.DuplicateAccountException;
import exception.InSufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import java.sql.SQLException;
import java.util.List;

public class BankServiceImpl implements BankService {
    private final AccountDAO accountDAO;
    private final BranchDAO branchDAO = new BranchDAO();

    public BankServiceImpl(AccountDAO accountDAO) {
        if (accountDAO == null) {
            throw new IllegalArgumentException("AccountDAO cannot be null");
        }
        this.accountDAO = accountDAO;
    }

    @Override
    public void createAccount(Account account) throws DuplicateAccountException {
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        }

        String accountNumber = account.getAccountNumber();
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number must not be empty");
        }

        try {
            if (accountDAO.getAccountByNumber(accountNumber) != null) {
                throw new DuplicateAccountException(accountNumber);
            }
            accountDAO.insertAccount(account);
        } catch (DuplicateAccountException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create account: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAccount(String accountNumber) throws AccountNotFoundException {
        try {
            Account account = findAccount(accountNumber);
            if (account.getBalance() != 0) {
                throw new IllegalStateException(
                        "Cannot close account with non-zero balance (BDT "
                                + String.format("%.2f", account.getBalance()) + "). "
                                + "Withdraw or transfer funds first.");
            }

            DatabaseConnection.beginTransaction();
            try {
                accountDAO.deleteTransactionsForAccount(accountNumber);
                accountDAO.deleteAccount(accountNumber);
                DatabaseConnection.commit();
            } catch (Exception e) {
                DatabaseConnection.rollback();
                throw e;
            }
        } catch (AccountNotFoundException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete account: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateAccount(String accountNumber, String newHolderName, String newEmail, String newPhone, String newNid, String newAddress)
            throws AccountNotFoundException {
        try {
            if (accountDAO.getAccountByNumber(accountNumber) == null) {
                throw new AccountNotFoundException(accountNumber);
            }

            DatabaseConnection.beginTransaction();
            try {
                accountDAO.updateAccountDetails(
                        accountNumber, newHolderName, newEmail, newPhone, newNid, newAddress);
                accountDAO.logTransaction(accountNumber, "UPDATED", 0);
                DatabaseConnection.commit();
            } catch (Exception e) {
                DatabaseConnection.rollback();
                throw e;
            }
        } catch (AccountNotFoundException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update account: " + e.getMessage(), e);
        }
    }

    @Override
    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        try {
            Account account = accountDAO.getAccountByNumber(accountNumber);
            if (account == null) {
                throw new AccountNotFoundException(accountNumber);
            }
            return account;
        } catch (AccountNotFoundException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find account: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        try {
            return accountDAO.getAllAccounts();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load accounts: " + e.getMessage(), e);
        }
    }

    @Override
    public void deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        try {
            DatabaseConnection.beginTransaction();
            try {
                Account account = findAccount(accountNumber);
                account.deposit(amount);

                accountDAO.updateAccountState(account);
                accountDAO.logTransaction(accountNumber, "DEPOSIT", amount);
                DatabaseConnection.commit();
            } catch (Exception e) {
                DatabaseConnection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to deposit: " + e.getMessage(), e);
        }
    }

    @Override
    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InSufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        try {
            DatabaseConnection.beginTransaction();
            try {
                Account account = findAccount(accountNumber);
                account.withdraw(amount);

                accountDAO.updateAccountState(account);
                accountDAO.logTransaction(accountNumber, "WITHDRAW", amount);
                DatabaseConnection.commit();
            } catch (Exception e) {
                DatabaseConnection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to withdraw: " + e.getMessage(), e);
        }
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InSufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        try {
            DatabaseConnection.beginTransaction();
            try {
                Account fromAccount = findAccount(fromAccountNumber);
                Account toAccount = findAccount(toAccountNumber);
                fromAccount.withdraw(amount);
                toAccount.deposit(amount);
                accountDAO.updateAccountState(fromAccount);
                accountDAO.updateAccountState(toAccount);
                accountDAO.logTransaction(fromAccountNumber, "TRANSFER_OUT", amount);
                accountDAO.logTransaction(toAccountNumber, "TRANSFER_IN", amount);
                DatabaseConnection.commit();
            } catch (Exception e) {
                DatabaseConnection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to transfer: " + e.getMessage(), e);
        }
    }

    @Override
    public void shiftBranch(String accountNumber, String newBranch) throws AccountNotFoundException {
        if (newBranch == null || newBranch.isBlank()) {
            throw new IllegalArgumentException("Please select a destination branch.");
        }

        String trimmedBranch = newBranch.trim();

        try {
            Account account = findAccount(accountNumber);
            String currentBranch = account.getBranch() == null ? "" : account.getBranch().trim();

            if (trimmedBranch.equalsIgnoreCase(currentBranch)) {
                throw new IllegalStateException("Account is already at branch: " +
                        (currentBranch.isEmpty() ? "Not assigned" : currentBranch));
            }

            if (!branchDAO.branchExists(trimmedBranch)) {
                throw new IllegalArgumentException("Branch '" + trimmedBranch + "' does not exist. "
                        + "Add it from Manage Branches first.");
            }

            DatabaseConnection.beginTransaction();
            try {
                accountDAO.updateBranch(accountNumber, trimmedBranch);
                accountDAO.logTransaction(accountNumber, "SHIFT_BRANCH", 0);
                DatabaseConnection.commit();
            } catch (Exception e) {
                DatabaseConnection.rollback();
                throw e;
            }
        } catch (AccountNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to shift branch: " + e.getMessage(), e);
        }
    }

    @Override
    public void addBranch(String branchName) {
        if (branchName == null || branchName.isBlank()) {
            throw new IllegalArgumentException("Branch name cannot be empty.");
        }
        String trimmed = branchName.trim();
        try {
            if (branchDAO.branchExists(trimmed)) {
                throw new IllegalArgumentException("Branch '" + trimmed + "' already exists.");
            }
            branchDAO.addBranch(trimmed);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add branch: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeBranch(String branchName) {
        if (branchName == null || branchName.isBlank()) {
            throw new IllegalArgumentException("Branch name cannot be empty.");
        }
        try {
            int inUse = branchDAO.countAccountsInBranch(branchName.trim());
            if (inUse > 0) {
                throw new IllegalStateException(
                        "Cannot remove branch '" + branchName + "'. "
                                + inUse + " account(s) are still assigned to it. "
                                + "Transfer them first using Shift Branch.");
            }
            branchDAO.removeBranch(branchName.trim());
        } catch (IllegalStateException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to remove branch: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateBranchName(String oldName, String newName) {
        try {
            branchDAO.updateBranchName(oldName, newName);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update branch: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getAllBranches() {
        try {
            return branchDAO.getAllBranches();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get branches: " + e.getMessage(), e);
        }
    }
}
