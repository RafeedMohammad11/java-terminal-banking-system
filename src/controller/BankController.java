package controller;

import exception.AccountNotFoundException;
import exception.DuplicateAccountException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;
import service.BankService;

import java.util.Scanner;

public class BankController {
    private final BankService bankService;
    private final Scanner scanner;

    public BankController(BankService bankService) {
        this.bankService = bankService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            bankService.loadFromFile();
        } catch (Exception e) {
            System.out.println("Warning: unable to load saved accounts at startup: " + e.getMessage());
        }

        while (true) {
            System.out.println("\n=== Banking System Menu ===");
            System.out.println("1. Create Savings Account");
            System.out.println("2. Create Current Account");
            System.out.println("3. List Accounts");
            System.out.println("4. Save accounts to data.csv");
            System.out.println("5. Load accounts from data.csv");
            System.out.println("6. Update Account");
            System.out.println("7. Deposit");
            System.out.println("8. Withdraw");
            System.out.println("9. Transfer");
            System.out.println("10. Exit");
            System.out.print("Select an option: ");

            String input = scanner.nextLine().trim();
            int option;
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (option) {
                case 1 -> createSavingsAccount();
                case 2 -> createCurrentAccount();
                case 3 -> listAccounts();
                case 4 -> saveAccounts();
                case 5 -> loadAccounts();
                case 6 -> updateAccount();
                case 7 -> deposit();
                case 8 -> withdraw();
                case 9 -> transfer();
                case 10 -> {
                    System.out.println("Exiting the application.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Unknown option. Please choose again.");
            }
        }
    }

    private void createSavingsAccount() {
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();
            
            try {
                bankService.findAccount(accountNumber);
                System.out.println("Error: Duplicate account exists with account number: " + accountNumber);
                return;
            } catch (AccountNotFoundException e) {
                // Account does not exist, safe to proceed
            }
            System.out.print("Enter holder name: ");
            String holderName = scanner.nextLine().trim();
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter initial balance: ");
            double balance = Double.parseDouble(scanner.nextLine().trim());

            if (email.isBlank() || phone.isBlank()) {
                System.out.println("Email and phone are required for savings accounts.");
                return;
            }

            SavingsAccount account = new SavingsAccount(accountNumber, holderName, email, phone, balance);
            bankService.createAccount(account);
            System.out.println("Savings account created successfully.");
        } catch (DuplicateAccountException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid balance value. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Failed to create savings account: " + e.getMessage());
        }
    }

    private void createCurrentAccount() {
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();
            
            try {
                bankService.findAccount(accountNumber);
                System.out.println("Error: Duplicate account exists with account number: " + accountNumber);
                return;
            } catch (AccountNotFoundException e) {
                // Account does not exist, safe to proceed
            }
            System.out.print("Enter holder name: ");
            String holderName = scanner.nextLine().trim();
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter initial balance: ");
            double balance = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Enter overdraft limit: ");
            double overdraftLimit = Double.parseDouble(scanner.nextLine().trim());

            CurrentAccount account = new CurrentAccount(accountNumber, holderName, email, phone, balance,
                    overdraftLimit);
            bankService.createAccount(account);
            System.out.println("Current account created successfully.");
        } catch (DuplicateAccountException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric value. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Failed to create current account: " + e.getMessage());
        }
    }

    private void listAccounts() {
        var accounts = bankService.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------------------------");
        System.out.printf(
                "| %-12s | %-20s | %-25s | %-15s | %-10s |%n",
                "Account No", "Holder Name", "Email", "Phone", "Balance");
        System.out.println("---------------------------------------------------------------------------------------------------");


        for (Account account : accounts) {
            account.displayInfo();
        }
    }

    private void saveAccounts() {
        try {
            bankService.saveToFile();
        } catch (Exception e) {
            System.out.println("Failed to save accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAccounts() {
        try {
            bankService.loadFromFile();
        } catch (Exception e) {
            System.out.println("Failed to load accounts: " + e.getMessage());
        }
    }

    private void updateAccount() {
        try {
            System.out.print("Enter account number to update: ");
            String accountNumber = scanner.nextLine().trim();
            System.out.print("Enter new holder name: ");
            String holderName = scanner.nextLine().trim();
            System.out.print("Enter new email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter new phone: ");
            String phone = scanner.nextLine().trim();

            bankService.updateAccount(accountNumber, holderName, email, phone);
            System.out.println("Account updated successfully.");
        } catch (Exception e) {
            System.out.println("Failed to update account: " + e.getMessage());
        }
    }

    private void deposit() {
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();
            System.out.print("Enter amount to deposit: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            bankService.deposit(accountNumber, amount);
            System.out.println("Deposit successful.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Failed to deposit: " + e.getMessage());
        }
    }

    private void withdraw() {
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();
            System.out.print("Enter amount to withdraw: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            bankService.withdraw(accountNumber, amount);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Failed to withdraw: " + e.getMessage());
        }
    }

    private void transfer() {
        try {
            System.out.print("Enter source account number: ");
            String fromAccount = scanner.nextLine().trim();
            System.out.print("Enter destination account number: ");
            String toAccount = scanner.nextLine().trim();
            System.out.print("Enter amount to transfer: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            bankService.transfer(fromAccount, toAccount, amount);
            System.out.println("Transfer successful.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Failed to transfer: " + e.getMessage());
        }
    }
}
