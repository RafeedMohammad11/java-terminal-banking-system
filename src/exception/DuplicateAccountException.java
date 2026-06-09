package exception;

public class DuplicateAccountException extends Exception {
    private final String accountNumber;

    public DuplicateAccountException(String accountNumber) {
        super("Duplicate account exists with account number: " + accountNumber);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
