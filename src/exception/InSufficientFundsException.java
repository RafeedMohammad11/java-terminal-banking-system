package exception;

public class InSufficientFundsException extends Exception {
    private double requestedAmount;
    private double availableBalance;

    public InSufficientFundsException(double requestedAmount, double availableBalance) {
        super("Insufficient funds. Request: " + requestedAmount + ", Available: " + availableBalance);
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    public double getRequestedAmount(){
        return requestedAmount;
    }

    public double getAvailableBalance(){
        return availableBalance;
    }



}
