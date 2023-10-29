package banking.project3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.Calendar;


public class TransactionManagerController {
    @FXML
    private TextField firstName, lastName, openDeposit, firstNameDW, lastNameDW, depositOrWithdraw;
    @FXML
    private DatePicker dob, dobDW;
    @FXML
    private RadioButton checking, collegeChecking, savings, moneyMarket,
            checkingDW, collegeCheckingDW, savingsDW, moneyMarketDW;
    @FXML
    private Button open, close, withdraw, deposit;
    @FXML
    private ToggleGroup accountTypeGroup, accountTypeGroupDW;
    @FXML
    private ToggleGroup campus;
    @FXML
    private VBox campusGroupContainer;
    @FXML
    private TextArea openCloseOutput, depositWithdrawOutput;
    String[] fields = new String[FIELDS_FOR_OPEN_CLOSE];
    private AccountDatabase accountDatabase = new AccountDatabase();
    Date accountDob;
    double initialDeposit;
    double amount;
    private static final int FIELDS_FOR_OPEN_CLOSE = 6;
    private static final int FNAME_INPUT = 0;
    private static final int LNAME_INPUT = 1;
    private static final int DEPOSIT_INPUT = 2;
    private static final int ZERO_QUANTITY = 0;
    private static final double MIN_AGE_TO_TO_OPEN = 16;
    private static final double MAX_AGE_TO_OPEN_CC = 24;



    @FXML
    protected void handleOpen(ActionEvent event) {
        RadioButton selectedRadioButton = (RadioButton) accountTypeGroup.getSelectedToggle();
        String accountType = selectedRadioButton.getText();
        if(checkFields() && isValidInitialDeposit() &&
                ageCheck(accountDob, accountType)){
            switch (accountType) {

                case "Checking" -> openChecking(fields[FNAME_INPUT], fields[LNAME_INPUT],
                        accountDob, initialDeposit);
                case "College Checking" -> openCollegeChecking(fields[FNAME_INPUT], fields[LNAME_INPUT],
                        accountDob, initialDeposit);
                case "Savings" -> openSavings(fields[FNAME_INPUT], fields[LNAME_INPUT], accountDob,
                        initialDeposit);
                case "Money Market" -> openMoneyMarket(fields[FNAME_INPUT], fields[LNAME_INPUT],
                         accountDob, initialDeposit);
            }
        }
    }

    @FXML
    protected void handleClose(ActionEvent event){
        RadioButton selectedRadioButton = (RadioButton) accountTypeGroup.getSelectedToggle();
        String accountType = selectedRadioButton.getText();
        if(checkFields() && isValidInitialDeposit() &&
                ageCheck(accountDob, accountType)){
            switch (accountType) {
                case "C" -> closeChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
                case "CC" -> closeCollegeChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
                case "S" -> closeSavings(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
                case "MM" -> closeMoneyMarket(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
            }
        }
    }

    @FXML
    protected void handleDeposit(ActionEvent event) {
        String accountType = accountTypeGroupDW.getSelectedToggle().toString();
        if (checkFieldsWithdrawDeposit() && isValidDeposit()) {
            switch (accountType) {
                case "Checking" -> depositChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "College Checking" -> depositCollegeChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "Savings" -> depositSavings(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "Money Market" -> depositMoneyMarket(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
            }
        }
    }

    @FXML
    protected void handleWithdraw(ActionEvent event) {
        String accountType = accountTypeGroupDW.getSelectedToggle().toString();
        if (checkFieldsWithdrawDeposit() && isValidWithdraw()) {
            switch (accountType) {
                case "Checking" -> withdrawChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "College Checking" -> withdrawCollegeChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "Savings" -> withdrawSavings(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "Money Market" -> withdrawMoneyMarket(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
            }
        }
    }

    protected void closeChecking(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        Checking accountToClose = new Checking(profileToClose, ZERO_QUANTITY);
        closeAccount(fName, lName, dob, accountToClose, "C");
    }

    protected void closeCollegeChecking(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        CollegeChecking accountToClose = new CollegeChecking(profileToClose, ZERO_QUANTITY, null);
        closeAccount(fName, lName, dob, accountToClose, "CC");
    }

    protected void closeSavings(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        Savings accountToClose = new Savings(profileToClose, ZERO_QUANTITY);
        closeAccount(fName, lName, dob, accountToClose, "S");
    }

    protected void closeMoneyMarket(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        MoneyMarket accountToClose = new MoneyMarket(profileToClose, ZERO_QUANTITY, true);
        closeAccount(fName, lName, dob, accountToClose, "MM");
    }

    private void closeAccount(String fName, String lName, Date dob,
                              Account account, String accountType) {
        if (accountDatabase.close(account)) {
            openCloseOutput.setText(fName + " " + lName + " " + dob.dateString()
                    + "(" + accountType + ") has been closed.");
        } else {
            openCloseOutput.setText(fName + " " + lName + " " + dob.dateString()
                    + "(" + accountType + ") is not in the database.");
        }
    }

    private void openChecking(String fName, String lName, Date dob, double initialDeposit) {
        Checking newChecking = new Checking(new Profile(fName, lName, dob),
                initialDeposit);
        openAccount(fName, lName, dob, newChecking, "C");
    }
    private void openCollegeChecking(String fName, String lName, Date dob, double initialDeposit) {
        Campus campusEnum = null;
        try {
            RadioButton selectedCampus = (RadioButton) campus.getSelectedToggle();
            String campusString = selectedCampus.getText();
            campusEnum = Campus.fromCode(campusString);
            CollegeChecking newCollegeChecking = new CollegeChecking(new
                    Profile(fName, lName, dob), initialDeposit, campusEnum);
            openAccount(fName, lName, dob, newCollegeChecking, "CC");
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Data");
            alert.setHeaderText("Please select campus");
            alert.showAndWait();
        }


    }
    private void openSavings(String fName, String lName, Date dob, double initialDeposit) {
        Savings newSavings = new Savings(new Profile(fName, lName, dob),
                initialDeposit);
        openAccount(fName, lName, dob, newSavings, "S");
    }
    private void openMoneyMarket(String fName, String lName, Date dob, double initialDeposit) {
        if (initialDeposit < MoneyMarket.MIN_BALANCE_FEE_WAIVED) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please enter valid amount");
            alert.setHeaderText("Minimum of $2000 to open a Money Market account.");
            alert.showAndWait();
            return;
        }
        MoneyMarket newMoneyMarket = new MoneyMarket(new
                Profile(fName, lName, dob), initialDeposit, true);
        openAccount(fName, lName, dob, newMoneyMarket, "MM");
    }

    @FXML
    private void openAccount(String fName, String lName, Date dob,
                             Account account, String accountType) {
        if (accountDatabase.open(account)) {
            openCloseOutput.setText(fName + " " + lName + " " +
                    dob.dateString() + "(" + accountType + ") opened.");
            System.out.println("Ran");
        } else {
            openCloseOutput.setText(fName + " " + lName + " " + dob.dateString()
                    + "(" + accountType + ") is already in the database.");
        }
    }

    private void depositChecking(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        Checking accountToDeposit = new Checking(profileToDeposit, deposit);
        depositAccount(fName, lName, dob, accountToDeposit, "C");
    }
    private void depositCollegeChecking(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        CollegeChecking accountToDeposit = new CollegeChecking(profileToDeposit, deposit, null);
        depositAccount(fName, lName, dob , accountToDeposit,"CC");
    }
    private void depositSavings(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        Savings accountToDeposit = new Savings(profileToDeposit, deposit);
        depositAccount(fName, lName, dob, accountToDeposit, "S");
    }
    private void depositMoneyMarket(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        MoneyMarket accountToDeposit = new MoneyMarket(profileToDeposit, deposit, true);
        depositAccount(fName, lName, dob, accountToDeposit, "MM");
    }
    private void depositAccount(String fName, String lName, Date dob, Account
            account, String accountType) {
        if(accountDatabase.containsForTransactions(account)) {
            accountDatabase.deposit(account);
            depositWithdrawOutput.setText(fName + " " + lName + " " +
                    dob.dateString() + "(" + accountType + ") Deposit - " +
                    "balance updated.");
        } else
            depositWithdrawOutput.setText(fName + " " + lName + " " +
                    dob.dateString() + "(" + accountType + ") " +
                    "is not in the database.");

    }

    private void withdrawChecking(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        Checking accountToWithdraw = new Checking(profileToWithdraw, withdraw);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "C");
    }
    private void withdrawCollegeChecking(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        Checking accountToWithdraw = new Checking(profileToWithdraw, withdraw);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "CC");
    }
    private void withdrawSavings(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        Checking accountToWithdraw = new Checking(profileToWithdraw, withdraw);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "S");
    }
    private void withdrawMoneyMarket(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        Checking accountToWithdraw = new Checking(profileToWithdraw, withdraw);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "MM");
    }

    private void withdrawAccount(String fName, String lName, Date dob,
                                 Account account, double withdraw, String accountType) {
        if (!accountDatabase.withdraw(account)) {
            if (withdraw > account.balance) {
                depositWithdrawOutput.setText(fName + " " + lName + " " + dob.dateString()
                        + "(" + accountType + ") " + "Withdraw - " +
                        "insufficient fund.");
            }
            else {
                depositWithdrawOutput.setText(fName + " " + lName + " " + dob.dateString()
                        + "(" + accountType + ") is not in the database.");
            }
            return;
        }
        depositWithdrawOutput.setText(fName + " " + lName + " " + dob.dateString() + "("
                + accountType + ") Withdraw - balance updated.");
        System.out.println("Ran");
    }

    @FXML
    protected boolean checkFields(){
        if (firstName.getText() == null || firstName.getText().isEmpty() ||
                lastName.getText() == null || lastName.getText().isEmpty() ||
                openDeposit.getText() == null || openDeposit.getText().isEmpty() ||
                dob.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Data");
            alert.setHeaderText("Please fill all values");
            alert.showAndWait();
            return false;
        }
        try {
            fields[FNAME_INPUT] = firstName.getText();
            fields[LNAME_INPUT] = lastName.getText();
            fields[DEPOSIT_INPUT] = openDeposit.getText();
            initialDeposit = Double.parseDouble(fields[DEPOSIT_INPUT]);
            accountDob = new Date(dob.getValue().getYear(),
                    dob.getValue().getMonthValue(), dob.getValue().getDayOfMonth());
        }
        catch(NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Data");
            alert.setHeaderText("Please fill all values");
            alert.showAndWait();
            return false;
        }
        catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please enter valid amount");
            alert.setHeaderText("Invalid deposit for opening");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * ALTER THIS VVV
     * @return
     */
    @FXML
    protected boolean checkFieldsWithdrawDeposit(){
        if (firstNameDW.getText() == null || firstNameDW.getText().isEmpty() ||
                lastNameDW.getText() == null || lastNameDW.getText().isEmpty() ||
                depositOrWithdraw.getText() == null || depositOrWithdraw.getText().isEmpty()
                || dobDW.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Data");
            alert.setHeaderText("Please fill all values");
            alert.showAndWait();
            return false;
        }
        try {
            fields[FNAME_INPUT] = firstNameDW.getText();
            fields[LNAME_INPUT] = lastNameDW.getText();
            fields[DEPOSIT_INPUT] = depositOrWithdraw.getText();
            amount = Double.parseDouble(fields[DEPOSIT_INPUT]);
            accountDob = new Date(dobDW.getValue().getYear(),
                    dobDW.getValue().getMonthValue(), dobDW.getValue().getDayOfMonth());
        }
        catch(NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Data");
            alert.setHeaderText("Please fill all values");
            alert.showAndWait();
            return false;
        }
        catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please enter valid amount");
            alert.setHeaderText("Invalid deposit for opening");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    protected void toggleCampus(){
        if(collegeChecking.isSelected()){
           campusGroupContainer.setDisable(false);
        }else {
            campusGroupContainer.setDisable(true);
        }
    }

    private boolean isValidInitialDeposit(){
        if (initialDeposit <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please enter valid amount");
            alert.setHeaderText("Initial Deposit cannot be 0 or less than 0");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * CHANGE THIS
     * @return
     */
    private boolean isValidWithdraw() {
        try {
            amount = Double.parseDouble(depositOrWithdraw.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please enter a valid withdrawal amount");
            alert.setHeaderText("Withdraw must be a number");
            alert.showAndWait();
            return false;
        }
        if (amount <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Withdrawal amount cannot be 0 or negative.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private boolean isValidDeposit() {
        try {
            amount = Double.parseDouble(depositOrWithdraw.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please enter a valid deposit amount");
            alert.setHeaderText("Deposit must be a number");
            alert.showAndWait();
            return false;
        }
        if (amount <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Deposit amount cannot be 0 or negative.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private boolean ageCheck(Date date, String accountType){
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        currentMonth++;
        int currentDay = calendar.get(Calendar.DATE);
        Date currentDate = new Date(currentYear, currentMonth, currentDay);
        int age = calculateAge(currentDate, date);
        if (age < MIN_AGE_TO_TO_OPEN) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Date of Birth invalid");
            alert.setHeaderText("DOB invalid: " + date.dateString()
                    + " under 16.");
            alert.showAndWait();
            return false;
        }
        if ("CC".equals(accountType) && age >= MAX_AGE_TO_OPEN_CC) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Date of Birth invalid");
            alert.setHeaderText("DOB invalid for College Checking: " +
                    date.dateString() + " over 24.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private int calculateAge(Date currentDate, Date ageDate) {
        int age = currentDate.getYear() - ageDate.getYear();

        if (currentDate.getMonth() < ageDate.getMonth() ||
                (currentDate.getMonth() == ageDate.getMonth()
                        && currentDate.getDay() < ageDate.getDay())) {
            age--;
        }
        return age;
    }

}
