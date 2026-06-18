package model;

public class SavingsAccount extends Account {

    private double interestRate;

    public SavingsAccount(String accountNumber, String holderName, String email, String phone, double initialBalance) {
        super(accountNumber, holderName, email, phone, initialBalance);
        this.interestRate = 0.0;
    }

    // Constructor with hidden fields (NID, Address)
    public SavingsAccount(String accountNumber, String holderName, String email, String phone,
            double initialBalance, String nid, String address) {
        super(accountNumber, holderName, email, phone, initialBalance, nid, address);
        this.interestRate = 0.0;
    }

    // Constructor used by DAO when loading from DB with all fields
    public SavingsAccount(String accountNumber, String holderName, double balance, double interestRate,
            String email, String phone, String nid, String address) {
        super(accountNumber, holderName, email, phone, balance, nid, address);
        this.interestRate = interestRate;
    }

    // Legacy constructor used by DAO (backward compatible)
    public SavingsAccount(String accountNumber, String holderName, double balance, double interestRate,
            String email, String phone) {
        super(accountNumber, holderName, email, phone, balance);
        this.interestRate = interestRate;
    }

    @Override
    public void withdraw(double amount) throws exception.InSufficientFundsException, exception.InvalidAmountException {
        if (amount <= 0) {
            throw new exception.InvalidAmountException(amount);
        }

        if (getBalance() - amount >= 500) {
            double newBalance = getBalance() - amount;
            setBalance(newBalance);
            System.out.println("Successfully withdrew: BDT " + amount);
            System.out.println("New Balance: " + getBalance());
        } else {
            throw new exception.InSufficientFundsException(amount, getBalance());
        }
    }

    public double getInterestRate() {
        return interestRate;
    }
}
