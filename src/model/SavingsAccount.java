package model;

public class SavingsAccount extends Account {

    private double interestRate;

    public SavingsAccount(String accountNumber, String holderName, String email, String phone, double initialBalance) {
        super(accountNumber, holderName, email, phone, initialBalance);
        this.interestRate = 0.0;
    }

    // Constructor used by DAO when loading from DB: (accNum, holderName, balance,
    // interestRate, email, phone)
    public SavingsAccount(String accountNumber, String holderName, double balance, double interestRate, String email,
            String phone) {
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
