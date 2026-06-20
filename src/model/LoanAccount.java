package model;

import exception.InSufficientFundsException;
import exception.InvalidAmountException;

public class LoanAccount extends Account {
    private double loanLimit;
    private double amountDue;

    public LoanAccount(String accountNumber, String holderName, String email, String phone, double amountDue,
                       double loanLimit) {
        super(accountNumber, holderName, email, phone, amountDue);
        if (loanLimit < 0) {
            throw new IllegalArgumentException("Loan limit cannot be negative");
        }
        if (amountDue < 0) {
            throw new IllegalArgumentException("Amount due cannot be negative");
        }
        if (amountDue > loanLimit) {
            throw new IllegalArgumentException("Amount due cannot exceed loan limit");
        }
        this.loanLimit = loanLimit;
        this.amountDue = amountDue;
    }

    public LoanAccount(String accountNumber, String holderName, String email, String phone,
                       double amountDue, double loanLimit, String nid, String address) {
        super(accountNumber, holderName, email, phone, amountDue, nid, address);
        if (loanLimit < 0) {
            throw new IllegalArgumentException("Loan limit cannot be negative");
        }
        if (amountDue < 0) {
            throw new IllegalArgumentException("Amount due cannot be negative");
        }
        if (amountDue > loanLimit) {
            throw new IllegalArgumentException("Amount due cannot exceed loan limit");
        }
        this.loanLimit = loanLimit;
        this.amountDue = amountDue;
    }

    /**
     * For loan accounts, balance stores amount due (persisted in the balance column).
     */
    @Override
    public double getBalance() {
        return amountDue;
    }

    public double getLoanLimit() {
        return loanLimit;
    }

    public double getAmountDue() {
        return amountDue;
    }

    /** Remaining credit the customer can still borrow. */
    public double getRemainingCredit() {
        return loanLimit - amountDue;
    }

    protected void setLoanLimit(double newLimit) {
        if (newLimit < 0) {
            throw new IllegalArgumentException("Loan limit cannot be negative");
        }
        if (amountDue > newLimit) {
            throw new IllegalArgumentException("Loan limit cannot be less than current amount due");
        }
        this.loanLimit = newLimit;
    }

    protected void setAmountDue(double newAmountDue) {
        if (newAmountDue < 0) {
            throw new IllegalArgumentException("Amount due cannot be negative");
        }
        if (newAmountDue > loanLimit) {
            throw new IllegalArgumentException("Amount due cannot exceed loan limit");
        }
        this.amountDue = newAmountDue;
        super.setBalance(newAmountDue);
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        setAmountDue(Math.max(0, amountDue - amount));
        System.out.println("Loan repayment: BDT " + amount);
        System.out.println("Remaining due: BDT " + getAmountDue());
    }

    @Override
    public void withdraw(double amount) throws InSufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        double remainingCredit = getRemainingCredit();
        if (amount > remainingCredit) {
            throw new InSufficientFundsException(amount, remainingCredit);
        }

        setAmountDue(amountDue + amount);
        System.out.println("Successfully withdrew: BDT " + amount);
        System.out.println("New due: BDT " + getAmountDue()
                + ", Remaining credit: BDT " + getRemainingCredit());
    }
}
