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
    public void withdraw(double amount) throws exception.InSufficientFundsException, exception.InvalidAmountException {
        if (amount <= 0) {
            throw new exception.InvalidAmountException(amount);
        }

        double availableFunds = getBalance() + getOverDraftLimit();

        if (availableFunds >= amount) {
            double newBalance = getBalance() - amount;
            updateBalance(newBalance);
            System.out.println("Successfully withdrew: BDT " + amount);
            System.out.println("New Balance: " + getBalance());
        } else {
            throw new exception.InSufficientFundsException(amount, availableFunds);
        }
    }
}
