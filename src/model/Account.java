package model;

public abstract class Account {
    private final String accountNumber;
    private final String holderName;
    private String email;
    private String phone;
    private double balance;

    public Account(String accountNumber, String holderName, String email, String phone, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.email = email;
        this.phone = phone;
        this.balance = balance;
    }

    public void displayInfo() {
        System.out.println("Account Number: " + accountNumber + " Balance: " + balance);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
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

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Deposited: BDT. " + amount);
        }
    }

    public abstract void withdraw(double amount);

    protected void updateBalance(double newBalance) {
        this.balance = newBalance;
    }
}
