package model;

public class SavingsAccount extends Account {


    public SavingsAccount(String accountNumber, String holderName, double initialBalance) {
        super(accountNumber, holderName, initialBalance);
    }

    @Override
    public void withdraw(double amount) {
        if(amount <= 0)
        {
            System.out.println("Withdrawal must be positive");
            return;
        }

        if(getBalance() >= amount)
        {
            double newBalance = getBalance() - amount;
            updateBalance(newBalance);
        }
    }
}
