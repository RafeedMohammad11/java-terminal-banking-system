package service;

import exception.AccountNotFoundException;
import exception.DuplicateAccountException;
import exception.InvalidAmountException;
import exception.InSufficientFundsException;
import java.util.List;
import model.Account;

public interface BankService {
    void createAccount(Account account) throws DuplicateAccountException;

    void deleteAccount(String accountNumber) throws AccountNotFoundException;

    void updateAccount(String accountNumber, String newHolderName, String newEmail, String newPhone)
            throws AccountNotFoundException;

    Account findAccount(String accountNumber) throws AccountNotFoundException;

    List<Account> getAllAccounts();

    void deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InSufficientFundsException;

    void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InSufficientFundsException;

    void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InSufficientFundsException;

    void saveToFile() throws Exception;

    void loadFromFile() throws Exception;
}
