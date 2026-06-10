package model;

public class CurrentAccount extends Account {
    private double overDraftLimit;

    public CurrentAccount(String accountNumber, String holderName, String email, String phone, double initialBalance,
            double overDraftLimit) {
        super(accountNumber, holderName, email, phone, initialBalance);
        if (overDraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative");
        }
        this.overDraftLimit = overDraftLimit;
    }

    public double getOverDraftLimit() {
        return overDraftLimit;
    }

    @Override
    public void withdraw(double amount) throws exception.InSufficientFundsException, exception.InvalidAmountException {
        if (amount <= 0) {
            throw new exception.InvalidAmountException(amount);
        }

        double availableFunds = getBalance() + getOverDraftLimit();

        if (availableFunds >= amount) {
            if (getBalance() >= amount) {
                updateBalance(getBalance() - amount);
            } else {
                double remaining = amount - getBalance();
                updateBalance(0);
                this.overDraftLimit -= remaining;
            }
            System.out.println("Successfully withdrew: BDT " + amount);
            System.out.println("New Balance: " + getBalance() + ", Remaining Overdraft: " + getOverDraftLimit());
        } else {
            throw new exception.InSufficientFundsException(amount, availableFunds);
        }
    }
}
