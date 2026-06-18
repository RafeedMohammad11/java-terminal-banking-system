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

    public String getAccountType() {
        return this.getClass().getSimpleName();
    }

    public void deposit(double amount) throws exception.InvalidAmountException {
        if (amount <= 0) {
            throw new exception.InvalidAmountException(amount);
        }
        setBalance(this.balance + amount);
        System.out.println("Deposited: BDT. " + amount);
    }

    public abstract void withdraw(double amount)
            throws exception.InSufficientFundsException, exception.InvalidAmountException;

    /**
     * Sets the account balance with validation.
     * Protected to allow subclasses to update balance during operations.
     */
    protected void setBalance(double newBalance) {
        if (newBalance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = newBalance;
    }
}
