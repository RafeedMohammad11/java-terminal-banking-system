package exception;

public class InvalidAmountException extends RuntimeException {
    private double amount;

    public InvalidAmountException(double amount)
    {
        super("Invalid amount: "+amount + ". Amount must be greater than zero");
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}
