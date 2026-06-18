package service;

import db.AccountDAO;
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
            if (accountDAO.getAccountByNumber(accountNumber) == null) {
                throw new AccountNotFoundException(accountNumber);
            }
            accountDAO.deleteAccount(accountNumber);
        } catch (AccountNotFoundException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete account: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateAccount(String accountNumber, String newHolderName, String newEmail, String newPhone)
            throws AccountNotFoundException {
        try {
            if (accountDAO.getAccountByNumber(accountNumber) == null) {
                throw new AccountNotFoundException(accountNumber);
            }
            accountDAO.updateAccountInfo(accountNumber, newHolderName, newEmail, newPhone);
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
}
