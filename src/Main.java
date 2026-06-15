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
            System.out.println("4. Exit");
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
                case 4 -> {
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
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter initial balance: ");
            double balance = Double.parseDouble(scanner.nextLine().trim());

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

}