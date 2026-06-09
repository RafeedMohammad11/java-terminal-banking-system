package service;
import com.sun.jdi.request.DuplicateRequestException;
import exception.*;
import java.util.List;
import model.Account;

public interface BankService {
    void createAccount(Account account) throws DuplicateRequestException;
    void deleteAccount(String accountNumber) throws AccountNotFoundException;
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
