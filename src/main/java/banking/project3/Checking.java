package banking.project3;

/**
 *The Checking class extends the Account class and provides
 * specific implementations for a checking account.
 * @author Zain Zulfiqar, Nicholas Yim
 */
public class Checking extends Account {
    private static final double INTEREST_RATE = 0.01; // 1.0% annual interest rate
    private static final double MONTHLY_FEE = 12.0;
    private static final double MIN_BALANCE_FEE_WAIVED = 1000;
    private static final int NUM_MONTHS = 12;

    /**
     * Constructor to initialize the holder and balance for a Checking account.
     * @param holder  The profile of the account holder.
     * @param balance The initial balance of the account.
     */
    public Checking(Profile holder, double balance) {
        super(holder, balance);
    }


    /**
     * Calculates the monthly interest for the account.
     * @return The monthly interest amount.
     */
    @Override
    public double monthlyInterest() {
        return balance * (INTEREST_RATE / NUM_MONTHS);
    }

    /**
     * Calculates the monthly fee for the account.
     * @return The monthly fee amount.
     */
    @Override
    public double monthlyFee() {
        if (balance >= MIN_BALANCE_FEE_WAIVED) {
            return 0.0;
        }
        return MONTHLY_FEE;
    }

    /**
     * Returns a string representation of the Checking account.
     * @return A string representing the account.
     */
    @Override
    public String toString(){
        return String.format("%s::%s %s %s::Balance $%,.2f",
                getClass().getSimpleName(),
                holder.getFname(),
                holder.getLname(),
                holder.getDob().dateString(),
                balance);
    }

    /**
     * Returns a string representation of the Checking account with fees and interest.
     * @return A string representing the account with fees and interest.
     */
    public String stringWithFees(){
        String feeStr = String.format("$%.2f", monthlyFee());
        String interestStr = String.format("$%.2f", monthlyInterest());
        String balanceStr = String.format("$%,.2f", balance);
        return String.format("Checking::%s %s %s::Balance %s::fee %s::monthly interest %s",
                holder.getFname(), holder.getLname(), holder.getDob().dateString(),
                balanceStr, feeStr, interestStr);
    }


    /**
     * Checks equality based on the holder's profile.
     * @param obj The object to compare.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Checking checking = (Checking) obj;
        return checking.holder.equals(holder);
    }


    /**
     * Checks equality for transactions
     * @param obj The object to compare.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equalsForTransactions(Object obj){
        if (this == obj) return true;
        if (!(obj instanceof Checking)) return false;
        Checking checking = (Checking) obj;
        return checking.holder.equals(holder);
    }

    /**
     * Compares two Account objects.
     * @param o The Account object to compare.
     * @return An integer representing the comparison result.
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
        if (this.balance < o.balance) {
            return -1;
        }
        if (this.balance > o.balance) {
            return 1;
        }
        return 0;
    }
}
