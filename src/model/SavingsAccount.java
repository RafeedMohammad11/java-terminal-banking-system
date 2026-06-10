package model;

public class SavingsAccount extends Account {

    public SavingsAccount(String accountNumber, String holderName, String email, String phone, double initialBalance) {
        super(accountNumber, holderName, email, phone, initialBalance);
    }

    @Override
    public void withdraw(double amount) throws exception.InSufficientFundsException, exception.InvalidAmountException {
        if (amount <= 0) {
            throw new exception.InvalidAmountException(amount);
        }

        if (getBalance() >= amount) {
            double newBalance = getBalance() - amount;
            updateBalance(newBalance);
            System.out.println("Successfully withdrew: BDT " + amount);
            System.out.println("New Balance: " + getBalance());
        } else {
            throw new exception.InSufficientFundsException(amount, getBalance());
        }
    }
}
