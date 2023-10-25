package banking.project3;

/**
 * Represents a college checking account
 * with specific features like campus code.
 * This class extends the Checking class and
 * overrides some of its methods.
 * @author Zain Zulfiqar, Nicholas Yim
 */
public class CollegeChecking extends Checking{
    private Campus campus; //campus code

    /**
     * Constructs a CollegeChecking object with a profile holder, balance, and campus code.
     * @param holder the profile of the account holder
     * @param balance the initial balance
     * @param campus the campus code
     */
    public CollegeChecking(Profile holder, double balance, Campus campus) {
        super(holder, balance);
        this.campus = campus;
    }

    /**
     * Calculates the monthly fee for the college checking account.
     * @return 0.0 as there is no monthly fee for College Checking
     */
    @Override
    public double monthlyFee() {
        return Savings.ZERO_FEE; // No monthly fee for College Checking
    }

    /**
     * Generates a string representation of the CollegeChecking object.
     * @return a formatted string containing account details
     */
    @Override
    public String toString() {
        String balanceStr = String.format("$%,.2f", balance);

        return String.format("College Checking::%s %s %s::Balance %s::%s",
                holder.getFname(), holder.getLname(), holder.getDob().dateString(),
                balanceStr, campus.toString());
    }

    /**
     * Generates a string representation of the CollegeChecking object with fees.
     * @return a formatted string containing account details and fees
     */
    public String stringWithFees(){
        String feeStr = String.format("$%.2f", monthlyFee());
        String interestStr = String.format("$%.2f", monthlyInterest());
        String balanceStr = String.format("$%,.2f", balance);
        return String.format("College Checking::%s %s %s::Balance %s::%s::fee %s::monthly interest %s",
                holder.getFname(), holder.getLname(), holder.getDob().dateString(),
                balanceStr, campus, feeStr, interestStr);
    }

    /**
     * Checks if two CollegeChecking objects are equal.
     * @param obj the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        return super.equals(obj);
    }

    /**
     * Checks if two CollegeChecking objects are equal for transactions.
     * @param obj the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equalsForTransactions(Object obj){
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Checking) ) return false;
        return super.equalsForTransactions(obj);
    }

    /**
     * Compares two CollegeChecking objects for sorting.
     * @param o the object to compare with
     * @return an integer representing the comparison result
     */
    @Override
    public int compareTo(Account o){
        int superComparison = super.compareTo(o);
        if(superComparison != 0){
            return superComparison;
        }
        return this.campus.compareTo(((CollegeChecking) o).campus);
    }

}
