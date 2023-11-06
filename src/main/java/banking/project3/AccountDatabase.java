package banking.project3;


/**
 * Manages an array-based database of various types of accounts.
 * The database can grow dynamically and supports basic operations like open, close, deposit, and withdraw.
 * @author Zain Zulfiqar, Nicholas Yim
 */
public class AccountDatabase {
    private static final int INCREMENT_AMOUNT = 4;
    private static final int INITIAL_CAPACITY = 4;
    private static final int NOT_FOUND = -1;
    private static final int STARTING_NUM_ACCT = 0;
    private static final int RESET_WITHDRAWAL = 0;
    private Account[] accounts; //list of various types of accounts



    private int numAcct; //number of accounts in the array

    /**
     * Initializes an empty account database with initial capacity.
     */
    public AccountDatabase(){
        accounts = new Account[INITIAL_CAPACITY];
        numAcct = STARTING_NUM_ACCT;
    }

    /**
     * Finds the index of a given account in the database.
     * @param account The account to find.
     * @return The index of the account, or NOT_FOUND if not found.
     */
    private int find(Account account) {
        int index = NOT_FOUND;
            for (int i = 0; i < numAcct; i++) {
                if (accounts[i].equals(account)) {
                    index = i;
                    break;
                }
            }
            return index;
        }


    /**
     * Increases the capacity of the account database by a fixed increment.
     */
    private void grow(){
        Account[] copy = new Account[numAcct + INCREMENT_AMOUNT];
        for(int i = 0; i < numAcct; i++){
            copy[i] = accounts[i];
        }
        accounts = copy;

    }

    /**
     * Checks if an account exists in the database.
     * @param account The account to check.
     * @return true if the account exists, false otherwise.
     */
    public boolean contains(Account account){
        return find(account) != NOT_FOUND;
    }

    /**
     * Checks if an Checking account exists in the database.
     * @param checking The checking account to check.
     * @return true if the account exists, false otherwise.
     */
    public boolean contains(Checking checking){
        int index = NOT_FOUND;
        for (int i = 0; i < numAcct; i++) {
            if (accounts[i].equalsForTransactions(checking)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Opens a new account and adds it to the database.
     * @param account The account to open.
     * @return true if the account was successfully opened, false otherwise.
     */
    public boolean open(Account account){
        if(account.getClass().equals(CollegeChecking.class)){
            if(contains((CollegeChecking) account)){
                return false;
            }
        } else if (account.getClass().equals(Checking.class)) {
            if (contains((Checking) account)) {
                return false;
            }
        } else if(contains(account)){
            return false;
        }
        if (numAcct >= accounts.length) {
            grow();
        }
        accounts[numAcct] = account;
        numAcct++;
        return true;
    }

    /**
     * Closes an existing account and removes it from the database.
     * @param account The account to close.
     * @return true if the account was successfully closed, false otherwise.
     */
    public boolean close(Account account){
        int removeIndex = find(account);
        if (removeIndex != NOT_FOUND) {
            for (int i = removeIndex; i < numAcct - 1; i++) {
                accounts[i] = accounts[i + 1];
            }
            numAcct--;
            accounts[numAcct] = null;
            return true;
        }
        return false;
    }

    /**
     * Withdraws an amount from an existing account.
     * @param account The account from which to withdraw.
     * @return true if the withdrawal was successful, false otherwise.
     */
    public boolean withdraw(Account account){
        int index = find(account);
        double withdrawAmt = account.balance;
        if (index == NOT_FOUND) {
            return false;
        }
        Account acct = accounts[index];
        account.balance = accounts[index].balance;
        if (acct.balance < withdrawAmt) {
            return false;
        }
        acct.balance -= withdrawAmt;
        if (acct instanceof MoneyMarket) {
            MoneyMarket mmAccount = (MoneyMarket) acct;
            mmAccount.incrementWithdrawals();
            if (mmAccount.balance < MoneyMarket.MIN_BALANCE_FEE_WAIVED) {
                mmAccount.isLoyal = false;
            }
            accounts[index] = mmAccount;
        }
        return true;
    }

    /**
     * Sorts the accounts array based on the account
     * type using the Selection Sort algorithm.
     */
    public void selectionSortAccountType() {
        int n = numAcct;

        for (int i = 0; i < n-1; i++) {
            int minIdx = i;
            for (int j = i+1; j < n; j++) {
                if (accounts[j].compareTo(accounts[minIdx]) < 0) {
                    minIdx = j;
                }
            }

            Account temp = accounts[minIdx];
            accounts[minIdx] = accounts[i];
            accounts[i] = temp;
        }
    }

    /**
     * Finds the index of an account that matches the given account for transactions.
     * @param account The account to find.
     * @return The index of the matching account, or NOT_FOUND if not found.
     */
    private int findForTransactions(Account account){
        int index = NOT_FOUND;
        for (int i = 0; i < numAcct; i++) {
            if (accounts[i].equals(account)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Deposits the balance into the account if it exists.
     * @param account The account to deposit into.
     */
    public void deposit(Account account) {
        int index = find(account);
        if (index == NOT_FOUND) {
            return;
        }
        accounts[index].balance += account.balance;
        Account acct = accounts[index];
        if (acct instanceof MoneyMarket) {
            MoneyMarket mmAccount = (MoneyMarket) acct;
            if (mmAccount.balance >= MoneyMarket.MIN_BALANCE_FEE_WAIVED) {
                mmAccount.isLoyal = true;
            }
            accounts[index] = mmAccount;
        }
    }

    /**
     * Checks if account is contained for transactions
     * @param account to be found
     * @return true if contained, false if not
     */
    public boolean containsForTransactions(Account account){
        return findForTransactions(account) != NOT_FOUND;
    }

    /**
     * Prints the sorted list of accounts.
     */
    public void printSorted(){
        selectionSortAccountType();
        for (int i = 0; i < numAcct; i++) {

        }
    }

    /**
     * Displays the list of accounts
     * @return text of accounts as String
     */
    public String accountDatabaseToString(){
        selectionSortAccountType();
        String text = "";
        for (int i = 0; i < numAcct; i++) {
           text += accounts[i].toString() + "\n";
        }
        return text;
    }

    /**
     * Displays the list of accounts along with their fees/interests
     * @return text of accounts as String
     */
    public String accountDatabaseFeesToString(){
        selectionSortAccountType();
        String text = "";
        for(int i = 0; i< numAcct; i++){
            text += accounts[i].stringWithFees() + "\n";
        }
        return text;
    }

    /**
     * Applies the monthly interest and fees to the accounts and displays
     * the updated balances
     * @return text of accounts as String
     */
    public String accountDatabaseUBToString(){
        selectionSortAccountType();
        String text = "";
        selectionSortAccountType();
        for(int i = 0; i < numAcct; i++) {
            accounts[i].balance += accounts[i].monthlyInterest();
            accounts[i].balance -= accounts[i].monthlyFee();
            if (accounts[i] instanceof MoneyMarket) {
                MoneyMarket mmAccount = (MoneyMarket) accounts[i];
                mmAccount.setWithdrawal(RESET_WITHDRAWAL);
                accounts[i] = mmAccount;
            }
            text += accounts[i].toString() + "\n";
        }
        return text;
    }

    /**
     * Prints the list of accounts along with their fees.
     */
    public void printFeesAndInterests(){ //calculate interests/fees
        selectionSortAccountType();
        for(int i = 0; i< numAcct; i++){
        }
    }

    /**
     * Applies the monthly interest and fees to the accounts and prints the updated balances.
     */
    public void printUpdatedBalances(){ //apply the interests/fees
        selectionSortAccountType();
        for(int i = 0; i < numAcct; i++){
            accounts[i].balance += accounts[i].monthlyInterest();
            accounts[i].balance -= accounts[i].monthlyFee();
            if(accounts[i] instanceof MoneyMarket){
                MoneyMarket mmAccount = (MoneyMarket) accounts[i];
                    mmAccount.setWithdrawal(RESET_WITHDRAWAL);
                    accounts[i] = mmAccount;
            }
        }
    }

    /**
     * Checks if the accounts array is empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty(){
        return numAcct == 0;
    }


    public int getNumAcct() {
        return numAcct;
    }
}
