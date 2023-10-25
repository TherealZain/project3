package banking.project3;
/**
 * Represents a Money Market account,
 * which is a type of Savings account.
 * This class extends the Savings class and
 * provides additional functionality
 * specific to Money Market accounts.
 *
 * @author Zain Zulfiqar, Nicholas Yim
 */
public class MoneyMarket extends Savings{

    private int withdrawal; //number of withdrawals
    private static final double INTEREST_RATE = 0.0450;
    private static final double LOYAL_INTEREST_RATE = 0.0475;
    public static final double MIN_BALANCE_FEE_WAIVED = 2000;
    private static final double MONTHLY_FEE = 25.0;
    private static final double WITHDRAWALS_OVER_MIN_FEE = 10.0;
    private static final int MIN_WITHDRAWALS_ALLOWED = 3;
    private static final int NUM_MONTHS = 12;
    private static final int THOUSANDTHS_MULTIPLIER = 1000;
    private static final int HUNDREDTHS_MULTIPLIER = 100;
    private static final double THOUSANDTHS = 5.0;
    private static final int GET_LAST_DIGIT_MODULUS = 10;

    /**
     * Constructor for MoneyMarket account.
     * @param holder   The profile of the account holder
     * @param balance  The initial balance
     * @param isLoyal  Whether the account holder is loyal
     */
    public MoneyMarket(Profile holder, double balance, boolean isLoyal) {
        super(holder, balance);
        isLoyal = true;
        this.isLoyal = isLoyal;
    }

    /**
     * Returns a string representation of the MoneyMarket account.
     * @return The string representation
     */
    @Override
    public String toString() {
        return String.format("%s::%s::withdrawal: %d",
                "Money Market",
                super.toString(),
                withdrawal);
    }

    /**
     * Generates a formatted string representation of the Money Market account,
     * including monthly fees and interest.
     * @return A string containing details of the Money Market account.
     */
    public String stringWithFees(){
        String feeStr = String.format("$%.2f", monthlyFee());
        String interestStr = String.format("$%.2f", monthlyInterest());
        String balanceStr = String.format("$%,.2f", balance);
        String loyalty = isLoyal ? "::is loyal" : "";
        return String.format("Money Market::Savings::%s %s %s::" +
                        "Balance %s%s::withdrawal: %d::fee %s::monthly interest %s",
                holder.getFname(), holder.getLname(), holder.getDob().dateString(),
                balanceStr, loyalty, withdrawal, feeStr, interestStr);
    }

    /**
     * Sets the number of withdrawals for this account.
     * @param withdrawal The number of withdrawals to set
     */
    public void setWithdrawal(int withdrawal) {
        this.withdrawal = withdrawal;
    }

    /**
     * Checks if this MoneyMarket account is equal to another object.
     * @param obj The object to compare with
     * @return true if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        if(!super.equals(obj)) return false;
        MoneyMarket mmAccount = (MoneyMarket) obj;
        return true;
    }

    /**
     * Checks if two MoneyMarket accounts are equal based on exact class.
     * @param obj The object to compare with.
     * @return true if both accounts have the same transactions, false otherwise.
     */
    @Override
    public boolean equalsForTransactions(Object obj){
        return super.equalsForTransactions(obj);
    }

    /**
     * Compares this MoneyMarket account with another Account object.
     * @param o The Account object to compare with.
     * @return -1, 0, or 1 based on the comparison.
     */
    @Override
    public int compareTo(Account o) {
        int superComparison= super.compareTo(o);
        if(superComparison != 0){
            return superComparison;
        }
        MoneyMarket mmAccount = (MoneyMarket) o;
        if(this.withdrawal < mmAccount.withdrawal){
            return -1;
        }
        if(this.withdrawal > mmAccount.withdrawal){
            return 1;
        }
        return 0;
    }

    /**
     * Calculates the monthly interest for this MoneyMarket account.
     * @return The monthly interest amount.
     */
    @Override
    public double monthlyInterest() {
        if(isLoyal){
            return balance*(LOYAL_INTEREST_RATE/NUM_MONTHS);
        }
        double interest = balance * (INTEREST_RATE/NUM_MONTHS);
        return customRound(interest);
    }

    /**
     * Custom round function to use for monthly interest
     * Rounds up when thousands place is greater than 5, rounds down otherwise
     * @param value as double
     * @return the rounded value as a double
     */
    private double customRound(double value) {
        int wholePart = (int) value;
        double fractionalPart = value - wholePart;
        int thousandthsPlace = (int) (fractionalPart *
                THOUSANDTHS_MULTIPLIER) % GET_LAST_DIGIT_MODULUS;

        if (thousandthsPlace == THOUSANDTHS) {
            return ((int) (value * HUNDREDTHS_MULTIPLIER)) /
                    (double) HUNDREDTHS_MULTIPLIER;
        } else {
            return Math.round(value * HUNDREDTHS_MULTIPLIER) /
                    (double) HUNDREDTHS_MULTIPLIER;
        }
    }

    /**
     * Calculates the monthly fee for this MoneyMarket account.
     * @return The monthly fee amount.
     */
    @Override
    public double monthlyFee() {
        if(withdrawal > MIN_WITHDRAWALS_ALLOWED){
            if(balance >= MIN_BALANCE_FEE_WAIVED){
                return WITHDRAWALS_OVER_MIN_FEE;
            }
            return MONTHLY_FEE + WITHDRAWALS_OVER_MIN_FEE;
        }
        if(balance >= MIN_BALANCE_FEE_WAIVED){
            return ZERO_FEE;
        }

        return MONTHLY_FEE;
    }

    /**
     * Increments the withdrawal count for this MoneyMarket account.
     */
    public void incrementWithdrawals(){
        withdrawal++;
    }
}
