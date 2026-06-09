package exception;

public class AccountNotFoundException extends Exception{
    private String accountNumber;

    public AccountNotFoundException(String accountNumber)
    {
        super("Account not found with account number: "+ accountNumber);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber(){
        return accountNumber;
    }
}
