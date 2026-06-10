package model;

public abstract class Account {
    private final String accountNumber;
    private String holderName;
    private String email;
    private String phone;
    private double balance;

    public Account(String accountNumber, String holderName, String email, String phone, double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.email = email;
        this.phone = phone;
        this.balance = balance;
    }

    public static void displayHeader() {
        System.out.println("---------------------------------------------------------------------------------------------------");
        System.out.printf(
                "| %-12s | %-20s | %-25s | %-15s | %-10s |%n",
                "Account No",
                "Holder Name",
                "Email",
                "Phone",
                "Balance"
        );
        System.out.println("---------------------------------------------------------------------------------------------------");
    }

    public void displayInfo() {
        System.out.printf(
                "| %-12s | %-20s | %-25s | %-15s | %10.2f |%n",
                accountNumber,
                holderName,
                email,
                phone,
                balance
        );
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) throws exception.InvalidAmountException {
        if (amount <= 0) {
            throw new exception.InvalidAmountException(amount);
        }
        this.balance += amount;
        System.out.println("Deposited: BDT. " + amount);
    }

    public abstract void withdraw(double amount)
            throws exception.InSufficientFundsException, exception.InvalidAmountException;

    protected void updateBalance(double newBalance) {
        this.balance = newBalance;
    }
}
