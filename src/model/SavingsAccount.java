package model;

public class SavingsAccount extends Account {

    public SavingsAccount(String accountNumber, String holderName, double initialBalance) {
        super(accountNumber, holderName, "", "", initialBalance);
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive");
            return;
        }

        if (getBalance() >= amount) {
            double newBalance = getBalance() - amount;
            updateBalance(newBalance);
            System.out.println("Successfully withdrew: BDT " + amount);
            System.out.println("New Balance: " + getBalance());
        } else {
            System.out.println("Transaction failed: Insufficient funds. Available balance: " + getBalance());
        }
    }
}
