import exception.DuplicateAccountException;
import model.CurrentAccount;
import model.SavingsAccount;
import service.BankService;
import service.BankServiceImpl;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BankService bankService = new BankServiceImpl();
        Scanner scanner = new Scanner(System.in);

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
                case 1 -> createSavingsAccount(scanner, bankService);
                case 2 -> createCurrentAccount(scanner, bankService);
                case 3 -> listAccounts(bankService);
                case 4 -> saveAccounts(bankService);
                case 5 -> loadAccounts(bankService);
                case 6 -> updateAccount(scanner, bankService);
                case 7 -> deposit(scanner, bankService);
                case 8 -> withdraw(scanner, bankService);
                case 9 -> transfer(scanner, bankService);
                case 10 -> {
                    System.out.println("Exiting the application.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Unknown option. Please choose again.");
            }
        }
    }

    private static void createSavingsAccount(Scanner scanner, BankService bankService) {
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();
            System.out.print("Enter holder name: ");
            String holderName = scanner.nextLine().trim();
            System.out.print("Enter initial balance: ");
            double balance = Double.parseDouble(scanner.nextLine().trim());

            SavingsAccount account = new SavingsAccount(accountNumber, holderName, balance);
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

    private static void createCurrentAccount(Scanner scanner, BankService bankService) {
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();
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

    private static void listAccounts(BankService bankService) {
        var accounts = bankService.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.println("=== Accounts ===");
        for (var account : accounts) {
            account.displayInfo();
        }
    }

    private static void saveAccounts(BankService bankService) {
        try {
            bankService.saveToFile();
        } catch (Exception e) {
            System.out.println("Failed to save accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadAccounts(BankService bankService) {
        try {
            bankService.loadFromFile();
        } catch (Exception e) {
            System.out.println("Failed to load accounts: " + e.getMessage());
        }
    }

    private static void updateAccount(Scanner scanner, BankService bankService) {
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

    private static void deposit(Scanner scanner, BankService bankService) {
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

    private static void withdraw(Scanner scanner, BankService bankService) {
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

    private static void transfer(Scanner scanner, BankService bankService) {
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