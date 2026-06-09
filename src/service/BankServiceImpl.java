package service;

import exception.AccountNotFoundException;
import exception.DuplicateAccountException;
import exception.InSufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BankServiceImpl implements BankService {
    private final List<Account> accounts = new ArrayList<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountException {
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        }

        String accountNumber = account.getAccountNumber();
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number must not be empty");
        }

        if (findAccountByNumber(accountNumber) != null) {
            throw new DuplicateAccountException(accountNumber);
        }

        accounts.add(account);
    }

    @Override
    public void deleteAccount(String accountNumber) throws AccountNotFoundException {
        Account account = findAccountByNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        accounts.remove(account);
    }

    @Override
    public void updateAccount(String accountNumber, String newHolderName, String newEmail, String newPhone)
            throws AccountNotFoundException {
        Account account = findAccountByNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        throw new UnsupportedOperationException("Account update is not implemented yet");
    }

    @Override
    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        Account account = findAccountByNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    @Override
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    @Override
    public void deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InSufficientFundsException {
        throw new UnsupportedOperationException("Deposit is not implemented yet");
    }

    @Override
    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InSufficientFundsException {
        throw new UnsupportedOperationException("Withdraw is not implemented yet");
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InSufficientFundsException {
        throw new UnsupportedOperationException("Transfer is not implemented yet");
    }

    @Override
    public void saveToFile() throws Exception {
        String filePath = "data.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("AccountType,AccountNumber,HolderName,Email,Phone,Balance,OverDraftLimit");
            writer.newLine();

            // Write each account
            for (Account account : accounts) {
                String accountType = account.getClass().getSimpleName();
                String accountNumber = account.getAccountNumber();
                String holderName = account.getHolderName();
                String email = account.getEmail() != null ? account.getEmail() : "";
                String phone = account.getPhone() != null ? account.getPhone() : "";
                double balance = account.getBalance();

                if (account instanceof CurrentAccount) {
                    CurrentAccount currentAccount = (CurrentAccount) account;
                    double overDraftLimit = currentAccount.getOverDraftLimit();
                    writer.write(String.format("%s,%s,%s,%s,%s,%.2f,%.2f",
                            accountType, accountNumber, holderName, email, phone, balance, overDraftLimit));
                } else {
                    writer.write(String.format("%s,%s,%s,%s,%s,%.2f,",
                            accountType, accountNumber, holderName, email, phone, balance));
                }
                writer.newLine();
            }
            System.out.println("Accounts saved to " + filePath);
        }
    }

    @Override
    public void loadFromFile() throws Exception {
        String filePath = "data.csv";
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No saved data file found: " + filePath);
            return;
        }

        accounts.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // Skip header
                if (lineNumber == 1) {
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 6) {
                    System.out.println("Invalid line format at line " + lineNumber);
                    continue;
                }

                String accountType = fields[0];
                String accountNumber = fields[1];
                String holderName = fields[2];
                String email = fields[3];
                String phone = fields[4];
                double balance = Double.parseDouble(fields[5]);

                Account account;
                if ("CurrentAccount".equals(accountType) && fields.length > 6 && !fields[6].isBlank()) {
                    double overDraftLimit = Double.parseDouble(fields[6]);
                    account = new CurrentAccount(accountNumber, holderName, email, phone, balance, overDraftLimit);
                } else {
                    account = new SavingsAccount(accountNumber, holderName, balance);
                    // Need to set email and phone if they exist
                    if (!email.isBlank() || !phone.isBlank()) {
                        account.setEmail(email);
                        account.setPhone(phone);
                    }
                }

                accounts.add(account);
            }
            System.out.println("Accounts loaded from " + filePath);
        }
    }

    private Account findAccountByNumber(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }

        for (Account account : accounts) {
            if (accountNumber.equals(account.getAccountNumber())) {
                return account;
            }
        }
        return null;
    }
}
