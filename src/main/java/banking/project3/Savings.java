package banking.project3;

/**
 * Represents a Savings account, which is a type of Account.
 * This class handles the specific features of a Savings account
 * such as interest rates and monthly fees.
 * @author Zain Zulfiqar, Nicholas Yim
 */
public class Savings extends Account{
    protected boolean isLoyal; //loyal customer status
    public static final double ZERO_FEE = 0.0;
    private static final double INTEREST_RATE = 0.04; // 1.0% annual interest rate
    private static final double LOYAL_INTEREST_RATE = 0.0425;
    private static final double MONTHLY_FEE = 25.0;
    private static final double MIN_BALANCE_FEE_WAIVED = 500;
    private static final int NUM_MONTHS = 12;

    /**
     * Constructs a new Savings account with the given profile and balance.
     * @param holder the profile of the account holder
     * @param balance the initial balance of the account
     */
    public Savings(Profile holder, double balance) {
        super(holder, balance);

    }

    /**
     * Returns a string representation of the Savings account.
     * @return a formatted string containing account details
     */
    @Override
    public String toString() {
        String loyalString = isLoyal ? "::is loyal" : "";
        return String.format("%s::%s %s %s::Balance $%,.2f%s",
                "Savings",
                holder.getFname(),
                holder.getLname(),
                holder.getDob().dateString(),
                balance,
                loyalString);
    }

    /**
     * Returns a string representation of the Savings account with fees and interest details.
     * @return a formatted string containing account details, fees, and interest
     */
    public String stringWithFees(){
        String feeStr = String.format("$%.2f", monthlyFee());
        String interestStr = String.format("$%.2f", monthlyInterest());
        String balanceStr = String.format("$%,.2f", balance);
        String loyalty = isLoyal ? "::is loyal" : "";
        return String.format("Savings::%s %s %s::Balance %s%s::fee %s::monthly interest %s",
                holder.getFname(), holder.getLname(), holder.getDob().dateString(),
                balanceStr, loyalty, feeStr, interestStr);
    }

    /**
     * Checks if this Savings account is equal to another object.
     * @param obj the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Savings savings = (Savings) obj;
        return savings.holder.equals(holder);
    }
    //&& savings.isLoyal == isLoyal

    /**
     * Checks if this Savings account is equal to another object for transaction purposes.
     * @param obj the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equalsForTransactions(Object obj){
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Savings savings = (Savings) obj;
        return savings.holder.equals(holder);
    }

    /**
     * Compares this Savings account to another Account object.
     * @param o the Account object to compare
     * @return an integer representing the comparison result
     */
    @Override
    public int compareTo(Account o) {
        int typeComparison = this.getClass().getSimpleName()
                .compareTo(o.getClass().getSimpleName());
        if(typeComparison > 0){
            return 1;
        }
        if(typeComparison < 0){
            return -1;
        }
        if(this.holder.compareTo(o.holder) > 0){
            return 1;
        }
        if(this.holder.compareTo(o.holder) < 0){
            return -1;
        }
        if(this.balance < o.balance) {
            return -1;
        }
        if (this.balance > o.balance) {
            return 1;
        }
        Savings savings = (Savings) o;
        return Boolean.compare(this.isLoyal, savings.isLoyal);
    }

    /**
     * Calculates and returns the monthly interest for this Savings account.
     * @return the monthly interest amount
     */
    @Override
    public double monthlyInterest() {
        if(isLoyal){
            return balance*(LOYAL_INTEREST_RATE/NUM_MONTHS);
        }
        return balance*(INTEREST_RATE/NUM_MONTHS);
    }

    /**
     * Calculates and returns the monthly fee for this Savings account.
     * @return the monthly fee amount
     */
    @Override
    public double monthlyFee() {
        if(balance >= MIN_BALANCE_FEE_WAIVED){
            return ZERO_FEE;
        }
        return MONTHLY_FEE;
    }

    /**
     * Sets the loyalty status of the account holder.
     * @param isLoyal the loyalty status to set
     */
    public void setIsLoyal(boolean isLoyal) {
        this.isLoyal = isLoyal;
    }

}
