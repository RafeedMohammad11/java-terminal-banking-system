# Lab Test & Project Guideline: Banking System

This document outlines the core concepts of the Banking System application, explains its exception-handling mechanism, and provides step-by-step instructions for implementing potential modifications that an instructor might request during a lab test.

---

## 1. Core Concepts & Architecture

The project follows a simplified **Model-Service-Controller** architecture to separate concerns:

```
[Main Entry Point] ---> [BankController (UI/Console)]
                               |
                               v
                     [BankService (Interface)]
                               |
                     [BankServiceImpl (Business Logic & Collections)]
                               |
                     [Account / Subclasses (Domain Models)]
```

### OOP Principles in Action
*   **Abstraction (`Account.java`):** Defined as an `abstract class` representing generic accounts. It declares the abstract method `withdraw` which subclasses must implement.
*   **Inheritance (`SavingsAccount` & `CurrentAccount`):** Subclasses inherit common properties (holder name, email, balance) from `Account` and specialize behavior.
*   **Polymorphism:** The `listAccounts` method works on the type `Account` but invokes overwritten methods (like `displayInfo`) dynamically based on whether the runtime object is `SavingsAccount` or `CurrentAccount`.
*   **Encapsulation:** Properties like `balance` and `accountNumber` are `private`. They can only be accessed via public getters or protected setters (`updateBalance`), enforcing business rules.

---

## 2. Exceptions & Try-Catch Blocks

The application uses both **Checked** and **Unchecked (Runtime)** exceptions to ensure reliability and validate state:

```
                java.lang.Throwable
                        |
                java.lang.Exception
                /                 \
  [Checked Exceptions]        [RuntimeException] (Unchecked)
  - AccountNotFoundException           |
  - DuplicateAccountException  - IllegalArgumentException
  - InSufficientFundsException - NumberFormatException
  - InvalidAmountException
```

### Checked Exceptions
Exceptions that inherit directly from `java.lang.Exception` (e.g., `AccountNotFoundException`, `DuplicateAccountException`, `InSufficientFundsException`, and `InvalidAmountException`) are **checked**.
*   **Rule:** The Java compiler forces you to handle them using a `try-catch` block, or declare them in the method signature using the `throws` keyword.
*   **Example:**
    ```java
    public abstract void withdraw(double amount) throws InSufficientFundsException, InvalidAmountException;
    ```

### Unchecked Exceptions
Exceptions inheriting from `java.lang.RuntimeException` (like `IllegalArgumentException` and `NumberFormatException`) do not require mandatory compiler checks. 
*   **Usage in this app:** `NumberFormatException` is thrown when parsing invalid inputs (e.g., entering "abc" as a balance), and `IllegalArgumentException` is used in input validation to loop and ask the user for correct inputs immediately.

---

## 3. Step-by-Step Lab Modifications Guide

Below are instructions and code snippets for common requests your instructor might ask for.

---

### A. Add a Minimum Balance Constraint
*Constraint:* Ensure that a `SavingsAccount`'s balance never falls below BDT 500.

1.  Open `src/model/SavingsAccount.java`.
2.  Modify the `withdraw` method:
    ```diff
    -if (getBalance() >= amount) {
    +if (getBalance() - amount >= 500) {
         double newBalance = getBalance() - amount;
         updateBalance(newBalance);
         System.out.println("Successfully withdrew: BDT " + amount);
         System.out.println("New Balance: " + getBalance());
     } else {
    -    throw new exception.InSufficientFundsException(amount, getBalance());
    +    throw new exception.InSufficientFundsException(amount, getBalance() - 500);
     }
    ```

---

### B. Add a New Account Type (e.g., `StudentAccount`)
*Constraint:* A `StudentAccount` acts like a savings account but has a maximum balance limit of BDT 50,000.

1.  Create `src/model/StudentAccount.java`:
    ```java
    package model;

    public class StudentAccount extends Account {
        public StudentAccount(String accountNumber, String holderName, String email, String phone, double initialBalance) {
            super(accountNumber, holderName, email, phone, initialBalance);
            if (initialBalance > 50000) {
                throw new IllegalArgumentException("Student accounts cannot exceed BDT 50,000 balance limit.");
            }
        }

        @Override
        public void deposit(double amount) throws exception.InvalidAmountException {
            if (getBalance() + amount > 50000) {
                throw new IllegalArgumentException("Deposit failed. Balance limit of 50,000 BDT exceeded.");
            }
            super.deposit(amount);
        }

        @Override
        public void withdraw(double amount) throws exception.InSufficientFundsException, exception.InvalidAmountException {
            if (amount <= 0) throw new exception.InvalidAmountException(amount);
            if (getBalance() >= amount) {
                updateBalance(getBalance() - amount);
                System.out.println("Withdrew: BDT " + amount);
            } else {
                throw new exception.InSufficientFundsException(amount, getBalance());
            }
        }
    }
    ```
2.  In `src/service/BankServiceImpl.java`, modify `loadFromFile()` to support instantiation of `StudentAccount`:
    ```java
    if ("StudentAccount".equals(accountType)) {
        account = new StudentAccount(accountNumber, holderName, email, phone, balance);
    }
    ```
3.  In `src/controller/BankController.java`, add a menu option to trigger a `createStudentAccount()` method.

---

### C. Implement Phone or Email Format Validation
*Constraint:* Force the user to enter exactly 11 digits for a phone number or validate email addresses.

1.  Open `src/controller/BankController.java`.
2.  Modify the phone prompting logic using the lambda validator:
    ```java
    String phone = readString("Enter phone: ", true, val -> {
        if (!val.matches("\\d{11}")) {
            throw new IllegalArgumentException("Phone number must contain exactly 11 digits.");
        }
    });
    ```
3.  For email checking:
    ```java
    String email = readString("Enter email: ", true, val -> {
        if (!val.contains("@") || !val.contains(".")) {
            throw new IllegalArgumentException("Invalid email format (must contain '@' and '.').");
        }
    });
    ```

---

### D. Apply Monthly Maintenance Fees
*Constraint:* Deduct a fee (e.g. BDT 100) from all current accounts.

1.  In `src/service/BankService.java`, declare:
    ```java
    void applyMaintenanceFees();
    ```
2.  In `src/service/BankServiceImpl.java`, implement:
    ```java
    @Override
    public void applyMaintenanceFees() {
        for (Account account : accounts) {
            if (account instanceof CurrentAccount) {
                try {
                    // Try to withdraw fee; handles balance/overdraft automatically
                    account.withdraw(100.0); 
                } catch (Exception e) {
                    System.out.println("Failed to deduct fee from account " + account.getAccountNumber() + ": " + e.getMessage());
                }
            }
        }
    }
    ```
3.  Add an option to run this in `BankController.java`.

---

### E. Search Accounts by Holder Name
*Constraint:* Allow search by a partial name query.

1.  Add to `src/service/BankServiceImpl.java`:
    ```java
    public List<Account> searchByName(String name) {
        return accounts.stream()
            .filter(a -> a.getHolderName().toLowerCase().contains(name.toLowerCase()))
            .toList();
    }
    ```
2.  In `BankController.java`, add a search menu option:
    ```java
    private void searchAccounts() {
        try {
            String query = readString("Enter name query to search: ", true, null);
            var results = bankService.searchByName(query);
            if (results.isEmpty()) {
                System.out.println("No matching accounts found.");
            } else {
                for (Account account : results) {
                    account.displayInfo();
                }
            }
        } catch (OperationCancelledException e) {
            System.out.println("Operation cancelled.");
        }
    }
    ```

---

### F. Track Transaction History
*Constraint:* Record deposits and withdrawals, and allow printing the last 5 transactions.

1.  In `src/model/Account.java`, add a transaction log:
    ```java
    private final java.util.List<String> transactions = new java.util.ArrayList<>();

    public void logTransaction(String detail) {
        transactions.add(detail);
    }

    public void printTransactions() {
        System.out.println("Transaction History for " + accountNumber + ":");
        int start = Math.max(0, transactions.size() - 5);
        for (int i = start; i < transactions.size(); i++) {
            System.out.println("- " + transactions.get(i));
        }
    }
    ```
2.  Update `deposit` and `withdraw` methods in `SavingsAccount` and `CurrentAccount` to call:
    ```java
    logTransaction("Withdrew: BDT " + amount + " (Balance: " + getBalance() + ")");
    ```
