package service;

import db.AccountDAO;
import exception.AccountNotFoundException;
import exception.DuplicateAccountException;
import exception.InSufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;

import java.sql.SQLException;
import java.util.List;

public class BankServiceImpl implements BankService {
    private final AccountDAO accountDAO = new AccountDAO();

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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create account: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAccount(String accountNumber) throws AccountNotFoundException {
        try {
            if (accountDAO.getAccountByNumber(accountNumber) == null) {
                throw new AccountNotFoundException(accountNumber);
            }
            accountDAO.deleteAccount(accountNumber);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete account: " + e.getMessage(), e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update account: " + e.getMessage(), e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        try {
            return accountDAO.getAllAccounts();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load accounts: " + e.getMessage(), e);
        }
    }

    @Override
    public void deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        try {
            Account account = findAccount(accountNumber);
            account.deposit(amount);
            accountDAO.updateBalance(accountNumber, account.getBalance());
            accountDAO.logTransaction(accountNumber, "DEPOSIT", amount);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to deposit: " + e.getMessage(), e);
        }
    }

    @Override
    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InSufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        try {
            Account account = findAccount(accountNumber);
            account.withdraw(amount);
            if (account instanceof CurrentAccount currentAccount) {
                accountDAO.updateBalanceAndOverdraft(accountNumber, currentAccount.getBalance(),
                        currentAccount.getOverDraftLimit());
            } else {
                accountDAO.updateBalance(accountNumber, account.getBalance());
            }
            accountDAO.logTransaction(accountNumber, "WITHDRAW", amount);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to withdraw: " + e.getMessage(), e);
        }
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InSufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        try {
            Account fromAccount = findAccount(fromAccountNumber);
            Account toAccount = findAccount(toAccountNumber);
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            if (fromAccount instanceof CurrentAccount currentAccount) {
                accountDAO.updateBalanceAndOverdraft(fromAccountNumber, currentAccount.getBalance(),
                        currentAccount.getOverDraftLimit());
            } else {
                accountDAO.updateBalance(fromAccountNumber, fromAccount.getBalance());
            }
            accountDAO.updateBalance(toAccountNumber, toAccount.getBalance());
            accountDAO.logTransaction(fromAccountNumber, "TRANSFER_OUT", amount);
            accountDAO.logTransaction(toAccountNumber, "TRANSFER_IN", amount);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to transfer: " + e.getMessage(), e);
        }
    }
}
