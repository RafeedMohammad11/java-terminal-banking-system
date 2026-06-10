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

    @FunctionalInterface
    private interface StringValidator {
        void validate(String val) throws IllegalArgumentException;
    }

    @FunctionalInterface
    private interface DoubleValidator {
        void validate(double val) throws IllegalArgumentException;
    }

    private static class OperationCancelledException extends Exception {
        public OperationCancelledException() {
            super("Operation cancelled by user.");
        }
    }

    public BankController(BankService bankService) {
        this.bankService = bankService;
        this.scanner = new Scanner(System.in);
    }

    private String readString(String prompt, boolean required, StringValidator validator) throws OperationCancelledException {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("cancel")) {
                throw new OperationCancelledException();
            }
            if (required && input.isBlank()) {
                System.out.println("This field is required. Type 'cancel' to abort.");
                continue;
            }
            try {
                if (validator != null) {
                    validator.validate(input);
                }
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + " Type 'cancel' to abort.");
            }
        }
    }

    private double readDouble(String prompt, DoubleValidator validator) throws OperationCancelledException {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("cancel")) {
                throw new OperationCancelledException();
            }
            try {
                double value = Double.parseDouble(input);
                if (validator != null) {
                    validator.validate(value);
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric value. Please enter a valid number. Type 'cancel' to abort.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + " Type 'cancel' to abort.");
            }
        }
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
            String accountNumber = readString("Enter account number: ", true, val -> {
                try {
                    bankService.findAccount(val);
                    throw new IllegalArgumentException("Duplicate account exists with account number: " + val);
                } catch (AccountNotFoundException e) {
                    // expected
                }
            });
            String holderName = readString("Enter holder name: ", true, null);
            String email = readString("Enter email: ", true, null);
            String phone = readString("Enter phone: ", true, null);
            double balance = readDouble("Enter initial balance: ", val -> {
                if (val < 0) throw new IllegalArgumentException("Initial balance cannot be negative");
            });

            SavingsAccount account = new SavingsAccount(accountNumber, holderName, email, phone, balance);
            bankService.createAccount(account);
            System.out.println("Savings account created successfully.");
        } catch (OperationCancelledException e) {
            System.out.println("Operation cancelled.");
        } catch (Exception e) {
            System.out.println("Failed to create savings account: " + e.getMessage());
        }
    }

    private void createCurrentAccount() {
        try {
            String accountNumber = readString("Enter account number: ", true, val -> {
                try {
                    bankService.findAccount(val);
                    throw new IllegalArgumentException("Duplicate account exists with account number: " + val);
                } catch (AccountNotFoundException e) {
                    // expected
                }
            });
            String holderName = readString("Enter holder name: ", true, null);
            String email = readString("Enter email: ", false, null);
            String phone = readString("Enter phone: ", false, null);
            double balance = readDouble("Enter initial balance: ", val -> {
                if (val < 0) throw new IllegalArgumentException("Initial balance cannot be negative");
            });
            double overdraftLimit = readDouble("Enter overdraft limit: ", val -> {
                if (val < 0) throw new IllegalArgumentException("Overdraft limit cannot be negative");
            });

            CurrentAccount account = new CurrentAccount(accountNumber, holderName, email, phone, balance,
                    overdraftLimit);
            bankService.createAccount(account);
            System.out.println("Current account created successfully.");
        } catch (OperationCancelledException e) {
            System.out.println("Operation cancelled.");
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
            String accountNumber = readString("Enter account number to update: ", true, val -> {
                try {
                    bankService.findAccount(val);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            });
            String holderName = readString("Enter new holder name: ", true, null);
            String email = readString("Enter new email: ", false, null);
            String phone = readString("Enter new phone: ", false, null);

            bankService.updateAccount(accountNumber, holderName, email, phone);
            System.out.println("Account updated successfully.");
        } catch (OperationCancelledException e) {
            System.out.println("Operation cancelled.");
        } catch (Exception e) {
            System.out.println("Failed to update account: " + e.getMessage());
        }
    }

    private void deposit() {
        try {
            String accountNumber = readString("Enter account number: ", true, val -> {
                try {
                    bankService.findAccount(val);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            });
            double amount = readDouble("Enter amount to deposit: ", val -> {
                if (val <= 0) throw new IllegalArgumentException("Amount must be greater than zero");
            });

            bankService.deposit(accountNumber, amount);
            System.out.println("Deposit successful.");
        } catch (OperationCancelledException e) {
            System.out.println("Operation cancelled.");
        } catch (Exception e) {
            System.out.println("Failed to deposit: " + e.getMessage());
        }
    }

    private void withdraw() {
        try {
            String accountNumber = readString("Enter account number: ", true, val -> {
                try {
                    bankService.findAccount(val);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            });
            double amount = readDouble("Enter amount to withdraw: ", val -> {
                if (val <= 0) {
                    throw new IllegalArgumentException("Amount must be greater than zero");
                }
                try {
                    Account account = bankService.findAccount(accountNumber);
                    double available = account.getBalance();
                    if (account instanceof CurrentAccount) {
                        available += ((CurrentAccount) account).getOverDraftLimit();
                    }
                    if (available < val) {
                        throw new IllegalArgumentException("Insufficient funds. Request: " + val + ", Available: " + available);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            });

            bankService.withdraw(accountNumber, amount);
        } catch (OperationCancelledException e) {
            System.out.println("Operation cancelled.");
        } catch (Exception e) {
            System.out.println("Failed to withdraw: " + e.getMessage());
        }
    }

    private void transfer() {
        try {
            String fromAccount = readString("Enter source account number: ", true, val -> {
                try {
                    bankService.findAccount(val);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            });
            String toAccount = readString("Enter destination account number: ", true, val -> {
                try {
                    bankService.findAccount(val);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
                if (val.equals(fromAccount)) {
                    throw new IllegalArgumentException("Source and destination accounts cannot be the same");
                }
            });
            double amount = readDouble("Enter amount to transfer: ", val -> {
                if (val <= 0) {
                    throw new IllegalArgumentException("Amount must be greater than zero");
                }
                try {
                    Account account = bankService.findAccount(fromAccount);
                    double available = account.getBalance();
                    if (account instanceof CurrentAccount) {
                        available += ((CurrentAccount) account).getOverDraftLimit();
                    }
                    if (available < val) {
                        throw new IllegalArgumentException("Insufficient funds. Request: " + val + ", Available: " + available);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            });

            bankService.transfer(fromAccount, toAccount, amount);
            System.out.println("Transfer successful.");
        } catch (OperationCancelledException e) {
            System.out.println("Operation cancelled.");
        } catch (Exception e) {
            System.out.println("Failed to transfer: " + e.getMessage());
        }
    }
}
