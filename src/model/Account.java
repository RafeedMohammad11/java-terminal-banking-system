package model;

public abstract class Account {
    private final String accountNumber;
    private String holderName;
    private String email;
    private String phone;
    private double balance;

    // Hidden fields (not displayed in list views, but available in detail views)
    private String nid; // National ID
    private String address; // Physical address

    // Constructor without hidden fields (backward compatible)
    public Account(String accountNumber, String holderName, String email, String phone, double balance) {
        this(accountNumber, holderName, email, phone, balance, "", "");
    }

    // Constructor with hidden fields (used when creating new accounts)
    public Account(String accountNumber, String holderName, String email, String phone,
            double balance, String nid, String address) {
        if (balance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.email = email;
        this.phone = phone;
        this.balance = balance;
        this.nid = nid != null ? nid : "";
        this.address = address != null ? address : "";
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

    /**
     * Hidden field accessor - National ID
     */
    public String getNid() {
        return nid;
    }

    /**
     * Hidden field setter - National ID
     */
    public void setNid(String nid) {
        this.nid = nid != null ? nid : "";
    }

    /**
     * Hidden field accessor - Physical Address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Hidden field setter - Physical Address
     */
    public void setAddress(String address) {
        this.address = address != null ? address : "";
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
