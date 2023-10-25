package banking.project3;


/**
 * Represents an abstract Account with basic functionalities.
 * This class serves as a base for different types of accounts.
 * @author Zain Zulfiqar, Nicholas Yim
 */
public abstract class Account implements Comparable<Account> {
    protected Profile holder;
    protected double balance;

    /**
     * Calculates the monthly interest for the account.
     * @return the monthly interest
     */
    public abstract double monthlyInterest();

    /**
     * Calculates the monthly fee for the account.
     * @return the monthly fee
     */
    public abstract double monthlyFee();

    /**
     * Constructs an Account with a holder profile and initial balance.
     * @param holder the profile of the account holder
     * @param balance the initial balance of the account
     */
    public Account(Profile holder, double balance) {
        this.holder = holder;
        this.balance = balance;
    }

    /**
     * Generates a string representation of the account with fees.
     * @return a string representation of the account
     */
    public abstract String stringWithFees();

    /**
     * Checks if accounts are equal to perform transactions
     * @param obj as Object
     * @return true if accounts are equal, false if they are not
     */
    public boolean equalsForTransactions(Object obj){
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;

        Account otherAccount = (Account) obj;
        return this.holder.equals(otherAccount.holder) && this.balance == otherAccount.balance;
    }


}

