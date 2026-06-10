# Lab Test & Project Guideline: Banking System

This document covers the full architecture, Java OOP concepts, exception handling, file I/O, and step-by-step modification guides for the Banking System terminal application. It is designed to be a complete self-study and exam reference.

---

## 1. Core Concepts & Architecture

The project follows a simplified **Model-Service-Controller** architecture to separate concerns:

```
[Main Entry Point] ──► [BankController (UI / Console)]
                                  │
                                  ▼
                        [BankService (Interface)]
                                  │
                        [BankServiceImpl (Business Logic & Collections)]
                                  │
                        [Account / Subclasses (Domain Models)]
                                  │
                        [exception / util packages]
```

### Package Responsibilities

| Package     | Responsibility                                              |
|-------------|-------------------------------------------------------------|
| `model`     | Plain domain classes: `Account`, `SavingsAccount`, `CurrentAccount` |
| `service`   | Interface + implementation for all business operations      |
| `exception` | Custom checked exceptions extending `Exception`            |
| `util`      | File I/O helpers, input validation, console utilities       |
| `main`      | Entry point, wires everything together                      |

---

## 2. OOP Principles in Action

### 2.1 Abstraction

`Account` is declared `abstract` — you cannot instantiate it directly. It captures what is common to every account (number, holder, balance) while forcing subclasses to define their own withdrawal behaviour.

```java
public abstract class Account {
    private String accountNumber;
    private String holderName;
    private double balance;
    private String email;
    private String phone;

    public Account(String accountNumber, String holderName,
                   double initialBalance, String email, String phone) {
        this.accountNumber = accountNumber;
        this.holderName    = holderName;
        this.balance       = initialBalance;
        this.email         = email;
        this.phone         = phone;
    }

    // Subclasses MUST provide their own implementation
    public abstract void deposit(double amount) throws Exception;
    public abstract void withdraw(double amount) throws Exception;
    public abstract String getAccountType();
    public abstract void displayInfo();

    // Protected — only subclasses may mutate balance directly
    protected void updateBalance(double newBalance) {
        this.balance = newBalance;
    }

    // Public getters
    public String getAccountNumber() { return accountNumber; }
    public String getHolderName()    { return holderName; }
    public double getBalance()       { return balance; }
    public String getEmail()         { return email; }
    public String getPhone()         { return phone; }

    // Setters for mutable fields (accountNumber is intentionally absent)
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public void setEmail(String email)           { this.email = email; }
    public void setPhone(String phone)           { this.phone = phone; }
}
```

> **Why `accountNumber` has no setter:** it is the primary key. Changing it after creation would corrupt file storage and break lookups. Immutability here is a deliberate design choice.

---

### 2.2 Inheritance

`SavingsAccount` and `CurrentAccount` both extend `Account`. They inherit all common fields and add their own specialised state.

```
          Account  (abstract)
         /        \
SavingsAccount   CurrentAccount
  + interestRate   + overdraftLimit
```

Calling `super(...)` in the constructor is mandatory — it initialises the fields defined in the parent before the child adds its own.

```java
public class SavingsAccount extends Account {
    private double interestRate;

    // Minimal constructor — email/phone optional on creation
    public SavingsAccount(String accountNumber, String holderName, double initialBalance) {
        super(accountNumber, holderName, initialBalance, "", "");
        this.interestRate = 0.0;
    }

    // Full constructor
    public SavingsAccount(String accountNumber, String holderName,
                          double initialBalance, double interestRate,
                          String email, String phone) {
        super(accountNumber, holderName, initialBalance, email, phone);
        this.interestRate = interestRate;
    }
    // ...
}
```

---

### 2.3 Polymorphism

Polymorphism lets you write code against the **parent type** (`Account`) while the correct **subclass behaviour** runs at runtime.

```java
// Works for both SavingsAccount and CurrentAccount
for (Account account : accounts) {
    account.displayInfo();   // calls SavingsAccount.displayInfo() or CurrentAccount.displayInfo()
}
```

This is also why `BankService` operates on `Account` references, not on concrete subtypes. You can add a third account type later (`StudentAccount`) without changing the service layer at all.

---

### 2.4 Encapsulation

`balance` is `private` — only `updateBalance(double)` (protected) may change it, and only deposit/withdraw methods call that. This enforces the business rule that balance changes must pass through validation.

```java
// External code CANNOT do: account.balance = -9999;
// It MUST go through: account.withdraw(amount);
// Which validates, then calls: updateBalance(getBalance() - amount);
```

---

### 2.5 Interface & Polymorphic Service

`BankService` is an **interface** — a contract of what the service can do, with no implementation detail. `BankServiceImpl` provides the actual logic. `Main` (and `BankController`) only ever hold a `BankService` reference.

```java
BankService bankService = new BankServiceImpl();
// bankService could be swapped for a mock or a DB-backed version
// without changing a single line in BankController
```

Benefits for a lab exam:
- Demonstrates interface usage (a graded OOP concept)
- Makes the code testable
- `BankService` lists every operation as a method signature with precise `throws` declarations

---

## 3. Exception Handling

### 3.1 Exception Hierarchy

```
java.lang.Throwable
        │
java.lang.Exception  ◄── your custom checked exceptions live here
        │
        ├── AccountNotFoundException
        ├── DuplicateAccountException
        ├── InSufficientFundsException
        ├── InvalidAmountException
        └── OverdraftLimitExceededException

java.lang.RuntimeException  ◄── unchecked, no compiler enforcement
        │
        ├── IllegalArgumentException   (used for null / blank validation)
        └── NumberFormatException      (used when parsing user input)
```

### 3.2 Checked Exceptions (your custom ones)

The compiler **forces** callers to handle or declare these. Each carries the data needed to give the user a precise error message.

```java
// InSufficientFundsException.java
package exception;

public class InSufficientFundsException extends Exception {
    private final double requestedAmount;
    private final double availableBalance;

    public InSufficientFundsException(double requestedAmount, double availableBalance) {
        super("Insufficient funds. Requested: " + requestedAmount
              + ", Available: " + availableBalance);
        this.requestedAmount  = requestedAmount;
        this.availableBalance = availableBalance;
    }

    public double getRequestedAmount()  { return requestedAmount; }
    public double getAvailableBalance() { return availableBalance; }
}
```

```java
// AccountNotFoundException.java
package exception;

public class AccountNotFoundException extends Exception {
    private final String accountNumber;

    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() { return accountNumber; }
}
```

```java
// DuplicateAccountException.java
package exception;

public class DuplicateAccountException extends Exception {
    private final String accountNumber;

    public DuplicateAccountException(String accountNumber) {
        super("Account already exists: " + accountNumber);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() { return accountNumber; }
}
```

```java
// InvalidAmountException.java
package exception;

public class InvalidAmountException extends Exception {
    private final double amount;

    public InvalidAmountException(double amount) {
        super("Invalid amount: " + amount + ". Must be greater than zero.");
        this.amount = amount;
    }

    public double getAmount() { return amount; }
}
```

```java
// OverdraftLimitExceededException.java
package exception;

public class OverdraftLimitExceededException extends Exception {
    private final double requestedAmount;
    private final double overdraftLimit;

    public OverdraftLimitExceededException(double requestedAmount, double overdraftLimit) {
        super("Overdraft limit exceeded. Requested: " + requestedAmount
              + ", Limit: " + overdraftLimit);
        this.requestedAmount = requestedAmount;
        this.overdraftLimit  = overdraftLimit;
    }

    public double getRequestedAmount() { return requestedAmount; }
    public double getOverdraftLimit()  { return overdraftLimit; }
}
```

### 3.3 Unchecked Exceptions

Used for programmer errors and input format failures — situations where the program should loop and ask again, not propagate up a call stack.

```java
// In BankServiceImpl.createAccount()
if (account == null) {
    throw new IllegalArgumentException("Account must not be null");
}
if (accountNumber == null || accountNumber.isBlank()) {
    throw new IllegalArgumentException("Account number must not be empty");
}
```

```java
// In BankController — user typed "abc" for a balance
try {
    double balance = Double.parseDouble(scanner.nextLine().trim());
} catch (NumberFormatException e) {
    System.out.println("Please enter a valid number.");
    // loop continues
}
```

### 3.4 try-catch-finally Pattern

`finally` runs **regardless** of whether an exception was thrown or caught. Its primary purpose here is to guarantee resource cleanup (closing file streams).

```
try {
    // risky code — may throw
} catch (SpecificException e) {
    // handle a known failure
} catch (AnotherException e) {
    // handle another known failure
} finally {
    // ALWAYS runs — close files, scanners, connections here
}
```

**Three-layer responsibility model:**

| Layer        | Role of try-catch-finally                                     |
|--------------|---------------------------------------------------------------|
| `model`      | Throws custom exceptions only — no catching                  |
| `service`    | `finally` closes file resources; re-throws custom exceptions  |
| `controller` | Catches everything; shows user-friendly messages; `finally` closes `Scanner` |

**File I/O with finally:**

```java
public void saveToFile() throws Exception {
    BufferedWriter writer = null;
    try {
        writer = new BufferedWriter(new FileWriter("data.csv"));
        for (Account account : accounts) {
            writer.write(account.toString());
            writer.newLine();
        }
    } catch (IOException e) {
        System.out.println("Error saving: " + e.getMessage());
        throw e;                        // re-throw so caller knows it failed
    } finally {
        if (writer != null) {
            try { writer.close(); }     // always close, even on exception
            catch (IOException e) { System.out.println("Close error: " + e.getMessage()); }
        }
    }
}
```

**Multi-catch (Java 7+):** When two exceptions need identical handling, catch them together:

```java
} catch (AccountNotFoundException | InvalidAmountException e) {
    throw e;   // re-throw both the same way
}
```

---

## 4. File I/O

### 4.1 Writing — `saveToFile()`

Uses `StandardOpenOption.TRUNCATE_EXISTING` to overwrite the whole file on every save. This keeps the file consistent with the in-memory list.

```java
Path filePath = Paths.get("data.csv");
try (BufferedWriter writer = Files.newBufferedWriter(filePath,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE)) {

    writer.write("AccountType,AccountNumber,HolderName,Email,Phone,Balance,OverDraftLimit");
    writer.newLine();

    for (Account account : accounts) {
        writer.write(account.toCSV());
        writer.newLine();
    }
}
```

> **try-with-resources** (the `try (...)` syntax) automatically calls `writer.close()` when the block exits — cleaner than a manual `finally`. Use this for all file operations going forward.

### 4.2 Reading — `loadFromFile()`

```java
try (BufferedReader reader = Files.newBufferedReader(Paths.get("data.csv"))) {
    String line;
    int lineNumber = 0;
    while ((line = reader.readLine()) != null) {
        lineNumber++;
        if (lineNumber == 1) continue;   // skip header

        String[] fields = line.split(",", -1);
        if (fields.length < 6) {
            System.out.println("Skipping malformed line " + lineNumber);
            continue;
        }
        // parse and reconstruct Account objects
    }
} catch (FileNotFoundException e) {
    System.out.println("No data file found — starting fresh.");
} catch (IOException e) {
    throw new IOException("Failed to read data file", e);
}
```

### 4.3 Appending — `appendTransaction()`

Transaction logs should **never** be overwritten. Use `StandardOpenOption.APPEND`:

```java
private void appendTransaction(String log) {
    Path filePath = Paths.get("transactions.txt");
    try (BufferedWriter writer = Files.newBufferedWriter(filePath,
            StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
        writer.write(log);
        writer.newLine();
    } catch (IOException e) {
        System.out.println("Warning: Could not log transaction: " + e.getMessage());
    }
}
```

### 4.4 CSV Format

Each account's `toCSV()` method serialises it consistently so `loadFromFile()` can parse it back.

```
AccountType,AccountNumber,HolderName,Email,Phone,Balance,OverDraftLimit
SavingsAccount,ACC001,Rahim,,01711000000,15000.00,
CurrentAccount,ACC002,Karim,karim@mail.com,01811000000,50000.00,10000.00
```

---

## 5. The `BankService` Interface

```java
package service;

import exception.*;
import model.Account;
import java.util.List;

public interface BankService {

    // Account lifecycle
    void createAccount(Account account) throws DuplicateAccountException;
    void deleteAccount(String accountNumber) throws AccountNotFoundException;
    void updateAccount(String accountNumber, String newHolderName,
                       String newEmail, String newPhone) throws AccountNotFoundException;
    Account findAccount(String accountNumber) throws AccountNotFoundException;
    List<Account> getAllAccounts();

    // Transactions
    void deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException;

    void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException,
                   InSufficientFundsException, OverdraftLimitExceededException;

    void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException,
                   InSufficientFundsException, OverdraftLimitExceededException;

    // Persistence
    void saveToFile() throws Exception;
    void loadFromFile() throws Exception;

    // Reporting
    void printStatement(String accountNumber) throws AccountNotFoundException;
}
```

---

## 6. Advanced Java Concepts Used in This Project

### 6.1 `instanceof` and Downcasting

Used in `saveToFile()` to access subclass-specific fields like `overdraftLimit`:

```java
if (account instanceof CurrentAccount) {
    CurrentAccount ca = (CurrentAccount) account;
    // now we can call ca.getOverDraftLimit()
}
```

Java 16+ pattern matching removes the need for explicit casting:

```java
if (account instanceof CurrentAccount ca) {
    double limit = ca.getOverDraftLimit();   // ca is already cast
}
```

### 6.2 `final` Fields

Mark fields that should never be reassigned after construction as `final`. This is why `accountNumber` should ideally be declared `private final String accountNumber`:

```java
private final String accountNumber;   // set once in constructor, never again
```

### 6.3 Method Overriding vs Overloading

| Concept     | What changes           | Example                                      |
|-------------|------------------------|----------------------------------------------|
| Overriding  | Same signature, different class | `SavingsAccount.withdraw()` vs `CurrentAccount.withdraw()` |
| Overloading | Same name, different parameters | Two `SavingsAccount` constructors (3-param and 6-param) |

Always annotate overrides with `@Override` — the compiler will catch mistakes where you accidentally overloaded instead.

### 6.4 `ArrayList` and the `List` Interface

`accounts` is declared as `List<Account>` (interface type) but instantiated as `ArrayList<Account>` (concrete type). This is the same principle as `BankService` / `BankServiceImpl`:

```java
private final List<Account> accounts = new ArrayList<>();
```

Key `ArrayList` operations used in this project:

```java
accounts.add(account);          // add to end
accounts.remove(account);       // remove by reference
accounts.size();                // count
new ArrayList<>(accounts);      // defensive copy — prevents external mutation
```

### 6.5 Enhanced `for` Loop vs Iterator

```java
// Enhanced for (preferred for read-only iteration)
for (Account account : accounts) {
    account.displayInfo();
}

// Iterator (required when removing during iteration)
Iterator<Account> it = accounts.iterator();
while (it.hasNext()) {
    Account acc = it.next();
    if (acc.getAccountNumber().equals(targetNumber)) {
        it.remove();   // safe removal mid-iteration
    }
}
```

> Never call `accounts.remove()` inside an enhanced for loop — it throws `ConcurrentModificationException`.

### 6.6 `Stream` API (Java 8+)

Streams let you filter and transform collections in one readable line:

```java
// Find all savings accounts with balance over 10,000
List<Account> highBalance = accounts.stream()
    .filter(a -> a instanceof SavingsAccount)
    .filter(a -> a.getBalance() > 10000)
    .toList();

// Search by partial name match
List<Account> results = accounts.stream()
    .filter(a -> a.getHolderName().toLowerCase().contains(query.toLowerCase()))
    .toList();
```

### 6.7 `String.format()` for Clean Output

Avoid string concatenation for tabular output:

```java
System.out.printf("%-15s %-20s %12.2f%n",
    account.getAccountNumber(),
    account.getHolderName(),
    account.getBalance());
```

`%-15s` = left-aligned string in 15 chars. `%12.2f` = right-aligned float, 2 decimal places.

### 6.8 `Scanner` Input Discipline

Always use `nextLine()`, never `nextInt()` or `nextDouble()` directly — they leave a newline in the buffer that breaks the next `nextLine()` call.

```java
System.out.print("Enter amount: ");
double amount = Double.parseDouble(scanner.nextLine().trim());
```

This is why the entire input handling wraps `Double.parseDouble()` in a `try-catch NumberFormatException` — user input is always a string first.

### 6.9 `static` vs Instance Methods

| Modifier | Lives on           | When to use                                      |
|----------|--------------------|--------------------------------------------------|
| `static` | The class itself   | Utility helpers that don't need instance state (`InputValidator.isValidPhone()`) |
| instance | Each object        | Operations that read or change the object's own fields |

```java
// static helper — no instance needed
public class InputValidator {
    public static boolean isValidPhone(String phone) {
        return phone.matches("\\d{11}");
    }
}

// Usage without creating an object
InputValidator.isValidPhone("01711000000");
```

### 6.10 Access Modifiers Summary

| Modifier    | Accessible from                                |
|-------------|------------------------------------------------|
| `private`   | Only the same class                            |
| `protected` | Same class + subclasses + same package         |
| (default)   | Same package only                              |
| `public`    | Everywhere                                     |

In this project: `balance` is `private`, `updateBalance()` is `protected` (only subclasses may call it), service methods are `public`.

---

## 7. Step-by-Step Lab Modification Guide

### A. Add a Minimum Balance Constraint

Ensure a `SavingsAccount` balance never falls below BDT 500.

Open `src/model/SavingsAccount.java` and modify `withdraw`:

```java
private static final double MINIMUM_BALANCE = 500.0;

@Override
public void withdraw(double amount)
        throws InvalidAmountException, InSufficientFundsException {
    if (amount <= 0) throw new InvalidAmountException(amount);
    if (getBalance() - amount < MINIMUM_BALANCE) {
        throw new InSufficientFundsException(amount, getBalance() - MINIMUM_BALANCE);
    }
    updateBalance(getBalance() - amount);
}
```

---

### B. Add a New Account Type — `StudentAccount`

A `StudentAccount` has a maximum balance cap of BDT 50,000.

Create `src/model/StudentAccount.java`:

```java
package model;

import exception.InSufficientFundsException;
import exception.InvalidAmountException;

public class StudentAccount extends Account {
    private static final double MAX_BALANCE = 50_000.0;

    public StudentAccount(String accountNumber, String holderName,
                          String email, String phone, double initialBalance) {
        super(accountNumber, holderName, initialBalance, email, phone);
        if (initialBalance > MAX_BALANCE) {
            throw new IllegalArgumentException(
                "Student accounts cannot exceed BDT " + MAX_BALANCE);
        }
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        if (getBalance() + amount > MAX_BALANCE) {
            throw new IllegalArgumentException(
                "Deposit would exceed the BDT 50,000 balance limit.");
        }
        updateBalance(getBalance() + amount);
    }

    @Override
    public void withdraw(double amount)
            throws InvalidAmountException, InSufficientFundsException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        if (getBalance() < amount) {
            throw new InSufficientFundsException(amount, getBalance());
        }
        updateBalance(getBalance() - amount);
    }

    @Override
    public String getAccountType() { return "StudentAccount"; }

    @Override
    public void displayInfo() {
        System.out.println("[ Student Account ]");
        System.out.printf("  Number  : %s%n", getAccountNumber());
        System.out.printf("  Holder  : %s%n", getHolderName());
        System.out.printf("  Balance : BDT %.2f%n", getBalance());
        System.out.printf("  Max Cap : BDT %.2f%n", MAX_BALANCE);
    }
}
```

In `BankServiceImpl.loadFromFile()`, add the new type branch:

```java
if ("StudentAccount".equals(accountType)) {
    account = new StudentAccount(accountNumber, holderName, email, phone, balance);
}
```

---

### C. Phone and Email Validation

In `BankController`, validate before accepting input:

```java
// Phone — must be exactly 11 digits
String phone = readString("Enter phone: ", true, val -> {
    if (!val.matches("\\d{11}")) {
        throw new IllegalArgumentException("Phone must be exactly 11 digits.");
    }
});

// Email — must contain @ and a dot
String email = readString("Enter email: ", true, val -> {
    if (!val.contains("@") || !val.contains(".")) {
        throw new IllegalArgumentException("Invalid email format.");
    }
});
```

---

### D. Apply Monthly Maintenance Fees

Deduct BDT 100 from every `CurrentAccount`.

Add to `BankService.java`:

```java
void applyMaintenanceFees();
```

Implement in `BankServiceImpl.java`:

```java
@Override
public void applyMaintenanceFees() {
    for (Account account : accounts) {
        if (account instanceof CurrentAccount) {
            try {
                account.withdraw(100.0);
                System.out.println("Fee deducted from " + account.getAccountNumber());
            } catch (Exception e) {
                System.out.println("Could not deduct fee from "
                    + account.getAccountNumber() + ": " + e.getMessage());
            }
        }
    }
}
```

---

### E. Search Accounts by Holder Name

```java
public List<Account> searchByName(String query) {
    return accounts.stream()
        .filter(a -> a.getHolderName()
            .toLowerCase()
            .contains(query.toLowerCase()))
        .toList();
}
```

In `BankController`:

```java
private void searchAccounts() {
    System.out.print("Enter name to search: ");
    String query = scanner.nextLine().trim();
    List<Account> results = bankService.searchByName(query);
    if (results.isEmpty()) {
        System.out.println("No accounts found.");
    } else {
        results.forEach(Account::displayInfo);   // method reference
    }
}
```

---

### F. In-Memory Transaction History

Track the last N transactions per account without a file.

Add to `Account.java`:

```java
private final List<String> transactionLog = new ArrayList<>();

public void logTransaction(String detail) {
    transactionLog.add(detail);
}

public void printTransactions() {
    System.out.println("Transaction history — " + getAccountNumber());
    int start = Math.max(0, transactionLog.size() - 5);
    for (int i = start; i < transactionLog.size(); i++) {
        System.out.println("  " + transactionLog.get(i));
    }
}
```

Call `logTransaction(...)` inside `deposit` and `withdraw` in each subclass:

```java
logTransaction("DEPOSIT  BDT " + amount + " → Balance: " + getBalance());
logTransaction("WITHDRAW BDT " + amount + " → Balance: " + getBalance());
```

---

### G. Delete an Account

Already in `BankService` — here is the controller side:

```java
private void deleteAccount() {
    try {
        System.out.print("Enter account number to delete: ");
        String accNo = scanner.nextLine().trim();
        bankService.deleteAccount(accNo);
        bankService.saveToFile();
        System.out.println("Account deleted.");
    } catch (AccountNotFoundException e) {
        System.out.println("Error: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("Unexpected error: " + e.getMessage());
    }
}
```

---

### H. Update Account Information

```java
private void updateAccount() {
    try {
        System.out.print("Enter account number: ");
        String accNo = scanner.nextLine().trim();

        System.out.println("Leave blank to keep current value.");

        System.out.print("New holder name: ");
        String name = scanner.nextLine().trim();

        System.out.print("New email: ");
        String email = scanner.nextLine().trim();

        System.out.print("New phone: ");
        String phone = scanner.nextLine().trim();

        bankService.updateAccount(accNo, name, email, phone);
        bankService.saveToFile();

    } catch (AccountNotFoundException e) {
        System.out.println("Error: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("Unexpected error: " + e.getMessage());
    }
}
```

---

## 8. Quick Concept Reference for the Exam

| Concept                  | Where it appears in this project                          |
|--------------------------|-----------------------------------------------------------|
| Abstract class           | `Account`                                                 |
| Concrete subclass        | `SavingsAccount`, `CurrentAccount`                        |
| Interface                | `BankService`                                             |
| Polymorphism             | `account.displayInfo()` on a `List<Account>`              |
| Encapsulation            | `private balance` + `protected updateBalance()`           |
| Checked exception        | `AccountNotFoundException`, `InSufficientFundsException`  |
| Unchecked exception      | `IllegalArgumentException`, `NumberFormatException`       |
| try-catch-finally        | `saveToFile()`, `loadFromFile()`, `Main` scanner cleanup  |
| try-with-resources       | `saveToFile()` using `BufferedWriter`                     |
| Multi-catch              | `catch (AccountNotFoundException \| InvalidAmountException e)` |
| Method overriding        | `withdraw()` in `SavingsAccount` vs `CurrentAccount`      |
| Method overloading       | Multiple `SavingsAccount` constructors                    |
| `instanceof`             | `saveToFile()` checking for `CurrentAccount`              |
| `final` field            | `accountNumber` (should never change)                     |
| `static` method          | `InputValidator.isValidPhone()`                           |
| `ArrayList` + `List`     | `private final List<Account> accounts`                    |
| Enhanced for loop        | Iterating accounts to display or save                     |
| Stream + filter          | `searchByName()` in `BankServiceImpl`                     |
| Method reference         | `results.forEach(Account::displayInfo)`                   |
| `String.format` / printf | `displayInfo()` for aligned console output                |
| File write (overwrite)   | `saveToFile()` with `TRUNCATE_EXISTING`                   |
| File write (append)      | `appendTransaction()` with `APPEND`                       |
| File read                | `loadFromFile()` with `BufferedReader`                     |
| CSV parsing              | `line.split(",", -1)` to parse account rows               |
