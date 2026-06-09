package model;

public class CurrentAccount extends Account {
    private final double overDraftLimit;

    public CurrentAccount(String accountNumber, String holderName, String email, String phone, double initialBalance,
            double overDraftLimit) {
        super(accountNumber, holderName, email, phone, initialBalance);
        this.overDraftLimit = overDraftLimit;
    }

    public double getOverDraftLimit() {
        return overDraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive");
            return;
        }

        double availableFunds = getBalance() + getOverDraftLimit();

        if (availableFunds >= amount) {
            double newBalance = getBalance() - amount;
            updateBalance(newBalance);
            System.out.println("Successfully withdrew: BDT " + amount);
            System.out.println("New Balance: " + getBalance());
        } else {
            System.out.println("Transaction failed: Insufficient funds. Overdraft limit exceeded");
        }
    }
}
