package banking.project3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Controls JavaFX-related functions to manage transactions for RU Bank
 * Contains separate functionality for opening, closing, depositing into,
 * and withdrawing from, an account
 * Contains further functionality for printing, printing with fees,
 * updating balances, and loading accounts from a .txt file
 * @author Nicholas Yim, Zain Zulfiqar
 */

public class TransactionManagerController {
    @FXML
    private TextField firstName, lastName, openDeposit, firstNameDW, lastNameDW, depositOrWithdraw;
    @FXML
    private DatePicker dob, dobDW;
    @FXML
    private RadioButton checking, collegeChecking, savings, moneyMarket,
            checkingDW, collegeCheckingDW, savingsDW, moneyMarketDW, loyal;
    @FXML
    private Button open, close, withdraw, deposit, loadAccounts;
    @FXML
    private ToggleGroup accountTypeGroup, accountTypeGroupDW;
    @FXML
    private ToggleGroup campus;
    @FXML
    private VBox campusGroupContainer, loyalContainer;
    @FXML
    private TextArea openCloseOutput, depositWithdrawOutput, databaseOutput;
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
    private static final String LOYAL = "1";
    private static final int ACCOUNT_TYPE_PART = 0;
    private static final int FNAME_PART = 1;
    private static final int LNAME_PART = 2;
    private static final int DOB_PART = 3;
    private static final int INITIAL_DEPOSIT_PART = 4;
    private static final int LOYALTY_PART = 5;
    private static final int CAMPUS_CODE_PART = 5;
    private static final int DATE_COMPONENTS_LENGTH = 3;
    private static final int YEAR_PART = 2;
    private static final int MONTH_PART = 0;
    private static final int DAY_PART = 1;


    /**
     * Loads accounts to account database from .txt file
     */
    @FXML
    protected void loadAccountsFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Accounts File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(loadAccounts.getScene().getWindow());

        if (selectedFile != null) {
            processFile(selectedFile);
        }
    }

    /**
     * Processes .txt file from GUI using Scanner
     * @param file as File
     */
    private void processFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line == null || line.trim().isEmpty()) {
                    break;
                }
                String[] parts = line.split(",");
                createAccountFromParts(parts);
            }
            databaseOutput.setText("Accounts loaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates corresponding accounts from lines within the .txt file
     * @param parts as String[]
     */
    private void createAccountFromParts(String[] parts) {
        String accountType = parts[ACCOUNT_TYPE_PART];
        String fName = parts[FNAME_PART];
        String lName = parts[LNAME_PART];
        Date dob = parseDate(parts[DOB_PART]);
        double initialDeposit = Double.parseDouble(parts[INITIAL_DEPOSIT_PART]);

        switch (accountType) {
            case "S" -> {
                String loyal = parts[LOYALTY_PART];
                openSavingsLoaded(fName, lName, dob, initialDeposit, loyal);
            }
            case "CC" -> {
                String campusCode = parts[CAMPUS_CODE_PART];
                openCollegeCheckingLoaded(fName, lName, dob, initialDeposit, campusCode);
            }
            case "C" -> openChecking(fName, lName, dob, initialDeposit);
            case "MM" -> openMoneyMarket(fName, lName, dob, initialDeposit);
            default -> System.out.println("Unknown account type: " + accountType);
        }
    }

    /**
     * Handles "Open Account" button interaction to open an account
     * @param event as ActionEvent
     */
    @FXML
    protected void handleOpen(ActionEvent event) {
        RadioButton selectedRadioButton = (RadioButton) accountTypeGroup.getSelectedToggle();
        String accountType = selectedRadioButton.getText();
        if(checkFieldsOpen() && isValidInitialDeposit() &&
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

    /**
     * Handles "Close Account" button interaction to close an account
     * @param event as ActionEvent
     */
    @FXML
    protected void handleClose(ActionEvent event){
        RadioButton selectedRadioButton = (RadioButton) accountTypeGroup.getSelectedToggle();
        String accountType = selectedRadioButton.getText();
        if(checkFieldsClose() && isValidInitialDeposit() &&
                ageCheck(accountDob, accountType)){
            switch (accountType) {
                case "College" -> closeChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
                case "College Checking" -> closeCollegeChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
                case "Savings" -> closeSavings(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
                case "Money Market" -> closeMoneyMarket(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
            }
        }
    }

    /**
     * Handles "Deposit" button interaction to deposit amount into an account
     * @param event as ActionEvent
     */
    @FXML
    protected void handleDeposit(ActionEvent event) {
        RadioButton selectedRadioButton = (RadioButton)accountTypeGroupDW.getSelectedToggle();
        String accountType = selectedRadioButton.getText();
        if (checkFieldsDepositWithdraw() && isValidDeposit()) {
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

    /**
     * Handles "Withdraw" button interaction to withdraw an amount
     * from an account
     * @param event as ActionEvent
     */
    @FXML
    protected void handleWithdraw(ActionEvent event) {
        RadioButton selectedRadioButton = (RadioButton)accountTypeGroupDW.getSelectedToggle();
        String accountType = selectedRadioButton.getText();
        if (checkFieldsDepositWithdraw() && isValidWithdraw()) {
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

    /**
     * Handles "Print Accounts" button interaction to display accounts
     * sorted by account type and profile
     * @param event as ActionEvent
     */
    @FXML
    protected void handlePrint(ActionEvent event){
        String textToDisplay = "";
        if (!(accountDatabase.isEmpty())) {
            textToDisplay += "*Accounts sorted by account type and profile.\n";
            textToDisplay += accountDatabase.accountDatabaseToString();
            textToDisplay += ("*end of list.\n");
        } else textToDisplay += "Account Database is empty!";

        databaseOutput.setText(textToDisplay);
    }

    /**
     * Handles "Print With Fees" button interaction to display accounts
     * sorted with fees included
     * @param event as ActionEvent
     */
    @FXML
    protected void handlePrintWithFees(ActionEvent event){
        String textToDisplay = "";
        if (!(accountDatabase.isEmpty())) {
            textToDisplay += "*Accounts sorted by account type and profile.\n";
            textToDisplay += accountDatabase.accountDatabaseFeesToString();
            textToDisplay += ("*end of list.\n");
        } else textToDisplay += "Account Database is empty!";

        databaseOutput.setText(textToDisplay);
    }

    /**
     * Handles "Update Balances" button interaction to apply the monthly
     * interests and fees and display the updated balances
     * @param event as ActionEvent
     */
    @FXML
    protected void handleUpdateBalances(ActionEvent event){
        String textToDisplay = "";
        if (!(accountDatabase.isEmpty())) {
            textToDisplay += "*Accounts sorted by account type and profile.\n";
            textToDisplay += accountDatabase.accountDatabaseUBToString();
            textToDisplay += ("*end of list.\n");
        } else textToDisplay += "Account Database is empty!";

        databaseOutput.setText(textToDisplay);
    }

    /**
     * Opens Checking account by calling general open account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param initialDeposit as double
     */
    private void openChecking(String fName, String lName, Date dob, double initialDeposit) {
        Checking newChecking = new Checking(new Profile(fName, lName, dob),
                initialDeposit);
        openAccount(fName, lName, dob, newChecking, "C");
    }

    /**
     * Opens College Checking account by calling general open account method
     * Checks if there is missing data and if the campus code is valid
     * Prints corresponding error message if any of the checks fail
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param initialDeposit as double
     */
    private void openCollegeChecking(String fName, String lName, Date dob, double initialDeposit) {
        Campus campusEnum = null;
        try {
            RadioButton selectedCampus = (RadioButton) campus.getSelectedToggle();
            String campusString = selectedCampus.getText();
            String campusCode = convertToCode(campusString);
            campusEnum = Campus.fromCode(campusCode);
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

    /**
     * Altered openCollegeChecking method to handle specific case of opening
     * from a College Checking account from a loaded .txt file
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param initialDeposit as double
     * @param campusCode as String
     */
    private void openCollegeCheckingLoaded(String fName, String lName, Date dob, double initialDeposit, String campusCode) {
        Campus campusEnum = null;
        try {
            campusEnum = Campus.fromCode(campusCode);
            CollegeChecking newCollegeChecking = new CollegeChecking(new
                    Profile(fName, lName, dob), initialDeposit, campusEnum);
            openAccount(fName, lName, dob, newCollegeChecking, "CC");
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Data");
            alert.setHeaderText("Please enter campus code");
            alert.showAndWait();
        }
    }

    /**
     * Opens Savings account by calling general open account method
     * Checks if there is missing data
     * Prints error message if there is missing data
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param initialDeposit as double
     */
    private void openSavings(String fName, String lName, Date dob, double initialDeposit) {
        Savings newSavings = new Savings(new Profile(fName, lName, dob),
                initialDeposit);
        if(loyal.isSelected()){
            newSavings.isLoyal = true;
        }
        openAccount(fName, lName, dob, newSavings, "S");
    }

    /**
     * Altered openSavings method to handle specific case of
     * opening a Savings account from a loaded .txt file
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param initialDeposit as double
     * @param loyalty as String
     */
    public void openSavingsLoaded(String fName, String lName, Date dob, double initialDeposit, String loyalty){
        Savings newSavings = new Savings(new Profile(fName, lName, dob),
                initialDeposit);
        if(loyalty.equals(LOYAL)){
            newSavings.isLoyal = true;
        }
        openAccount(fName, lName, dob, newSavings, "S");
    }

    /**
     * Opens Money Market account by calling general open account method
     * Checks if initial deposit it at least 2000
     * Prints error message if initial deposit is less than 2000
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param initialDeposit as double
     */
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

    /**
     * General open account method to handle opening an account given
     * identifying parameters
     * Calls 'open' method and checks if account is already in the database
     * Displays message indicating if account is opened or if it is already
     * in the database
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param account as Account
     * @param accountType as String
     */
    @FXML
    private void openAccount(String fName, String lName, Date dob,
                             Account account, String accountType) {
        if (accountDatabase.open(account)) {
            openCloseOutput.setText(fName + " " + lName + " " +
                    dob.dateString() + "(" + accountType + ") opened.");

        } else {
            openCloseOutput.setText(fName + " " + lName + " " + dob.dateString()
                    + "(" + accountType + ") is already in the database.");
        }
    }

    /**
     * Closes Checking account by calling general close account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     */
    protected void closeChecking(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        Checking accountToClose = new Checking(profileToClose, ZERO_QUANTITY);
        closeAccount(fName, lName, dob, accountToClose, "C");
    }

    /**
     * Closes College Checking account by calling general close account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     */
    protected void closeCollegeChecking(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        CollegeChecking accountToClose = new CollegeChecking(profileToClose, ZERO_QUANTITY, null);
        closeAccount(fName, lName, dob, accountToClose, "CC");
    }

    /**
     * Closes Savings account by calling general close account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     */
    protected void closeSavings(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        Savings accountToClose = new Savings(profileToClose, ZERO_QUANTITY);
        closeAccount(fName, lName, dob, accountToClose, "S");
    }

    /**
     * Closes Money Market account by calling general close account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     */
    protected void closeMoneyMarket(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        MoneyMarket accountToClose = new MoneyMarket(profileToClose, ZERO_QUANTITY, true);
        closeAccount(fName, lName, dob, accountToClose, "MM");
    }

    /**
     * General close account method to handle closing an account given
     * identifying parameters
     * Calls 'close' method and checks if account is in database
     * Prints message indicating if account has been closed or if is
     * not in the database
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param account as Account
     * @param accountType as String
     */
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

    /**
     * Deposits value into Checking account by calling general deposit
     * account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param deposit as double
     */
    private void depositChecking(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        Checking accountToDeposit = new Checking(profileToDeposit, deposit);
        depositAccount(fName, lName, dob, accountToDeposit, "C");
    }

    /**
     * Deposits value into College Checking account by calling general
     * deposit account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param deposit as double
     */
    private void depositCollegeChecking(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        CollegeChecking accountToDeposit = new CollegeChecking(profileToDeposit, deposit, null);
        depositAccount(fName, lName, dob , accountToDeposit,"CC");
    }

    /**
     * Deposits value into Savings account by calling general deposit
     * account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param deposit as double
     */
    private void depositSavings(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        Savings accountToDeposit = new Savings(profileToDeposit, deposit);
        depositAccount(fName, lName, dob, accountToDeposit, "S");
    }

    /**
     * Deposits value into Money Market account by calling general deposit
     * account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param deposit as double
     */
    private void depositMoneyMarket(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        MoneyMarket accountToDeposit = new MoneyMarket(profileToDeposit, deposit, true);
        depositAccount(fName, lName, dob, accountToDeposit, "MM");
    }

    /**
     * General deposit account to handle depositing value into an account
     * given identifying parameters
     * Checks if account is in account database and calls 'deposit' method
     * Displays message indicating if deposit is successful or if the account
     * is not in the database
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param account as Account
     * @param accountType as String
     */
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

    /**
     * Withdraws value from Checking account by calling general withdraw
     * account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param withdraw as double
     */
    private void withdrawChecking(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        Checking accountToWithdraw = new Checking(profileToWithdraw, withdraw);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "C");
    }

    /**
     * Withdraws value from College Checking account by calling general
     * withdraw account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param withdraw as double
     */
    private void withdrawCollegeChecking(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        CollegeChecking accountToWithdraw = new CollegeChecking(profileToWithdraw, withdraw, null);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "CC");
    }

    /**
     * Withdraws value from Savings account by calling general withdraw
     * account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param withdraw as double
     */
    private void withdrawSavings(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        Savings accountToWithdraw = new Savings(profileToWithdraw, withdraw);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "S");
    }

    /**
     * Withdraws value from Money Market account by calling general withdraw
     * account method
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param withdraw as double
     */
    private void withdrawMoneyMarket(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        MoneyMarket accountToWithdraw = new MoneyMarket(profileToWithdraw, withdraw, true);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "MM");
    }

    /**
     * General withdraw account method to handle withdrawing value from an
     * account given identifying parameters
     * Calls 'withdraw' method and performs two checks (insufficient fund
     * and not in database) then performs the withdrawal action if account and
     * withdraw value are valid
     * Displays message indicating a specific error or indicating if withdrawal
     * is successful
     * @param fName as String
     * @param lName as String
     * @param dob as Date
     * @param account as Account
     * @param withdraw as double
     * @param accountType as String
     */
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
    }

    /**
     * Shows error alert window with specified title and header
     * @param title as String
     * @param header as String
     * @return false
     */
    private boolean showAlert(String title, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
        return false;
    }

    /**
     * Checks validity of specific text field entry
     * @param textField as TextField
     * @return true if textField is null or empty, false otherwise
     */
    private boolean checkTextField(TextField textField) {
        return textField == null || textField.getText().isEmpty();
    }

    /**
     * Checks validity of DatePicker field entry
     * @param datePicker as DatePicker
     * @return true if datePicker is null or its value is null, false otherwise
     */
    private boolean checkDateField(DatePicker datePicker) {
        return datePicker == null || datePicker.getValue() == null;
    }

    /**
     * Populates fields using the text inputs and date picker value
     * Fields: first name, last name, deposit, date
     * @param fNameField as TextField
     * @param lNameField as lNameField
     * @param depositField as depositField
     * @param dobPicker as DatePicker
     */
    private void populateFields(TextField fNameField, TextField lNameField,
                                TextField depositField, DatePicker dobPicker) {
        fields[FNAME_INPUT] = fNameField.getText();
        fields[LNAME_INPUT] = lNameField.getText();
        fields[DEPOSIT_INPUT] = depositField.getText();
        initialDeposit = Double.parseDouble(fields[DEPOSIT_INPUT]);
        amount = initialDeposit;
        accountDob = new Date(dobPicker.getValue().getYear(),
                dobPicker.getValue().getMonthValue(),
                dobPicker.getValue().getDayOfMonth());
    }

    /**
     * Checks validity of corresponding fields for opening an account by
     * calling specific check and populate methods
     * Displays error alert indicating a specific error or no alert if
     * checks pass
     * @return false if error alert is shown, true otherwise
     */
    @FXML
    protected boolean checkFieldsOpen(){
        if (checkTextField(firstName) || checkTextField(lastName) ||
                checkTextField(openDeposit) || checkDateField(dob)) {
            return showAlert("Missing Data", "Please fill all values.");
        }
        try {
            populateFields(firstName, lastName, openDeposit, dob);
        } catch(NullPointerException e) {
            return showAlert("Missing Data", "Please fill all values.");
        } catch (NumberFormatException e) {
            return showAlert("Invalid Amount", "Not a valid amount.");
        }
        return true;
    }

    /**
     * Checks validity of corresponding fields for closing an account by
     * calling specific check and populate methods
     * Displays error alert indicating a specific error or no alert if
     * checks pass
     * @return false if error alert is shown, true otherwise
     */
    @FXML
    protected boolean checkFieldsClose(){
        if (checkTextField(firstName) ||
                checkTextField(lastName) || checkDateField(dob)) {
            return showAlert("Missing Data", "Please fill all values.");
        }
        try {
            populateFields(firstName, lastName, openDeposit, dob);
        } catch(NullPointerException e) {
            return showAlert("Missing Data", "Please fill all values.");
        }
        return true;
    }

    /**
     * Checks validity of corresponding fields for depositing into or
     * withdrawing from an account by calling specific check and populate methods
     * Displays error alert indicating a specific error or no alert if
     * checks pass
     * @return false if error alert is shown, true otherwise
     */
    @FXML
    protected boolean checkFieldsDepositWithdraw(){
        if (checkTextField(firstNameDW) || checkTextField(lastNameDW) ||
                checkTextField(depositOrWithdraw) || checkDateField(dobDW)) {
            return showAlert("Missing Data", "Please fill all values.");
        }
        try {
            populateFields(firstNameDW, lastNameDW, depositOrWithdraw, dobDW);
        } catch(NullPointerException e) {
            return showAlert("Missing Data", "Please fill all values.");
        } catch (NumberFormatException e) {
            return showAlert("Invalid Amount", "Not a valid amount.");
        }
        return true;
    }

    /**
     * Toggles Loyal and Campus container availabilities under specified conditions
     * Enables Campus container when College Checking is selected, and disables otherwise
     * Enables Loyal container when Savings is selected, and disables otherwise
     * Selects Loyal status when Money Market is selected, and disables otherwise, since
     * Money Market is loyal by default
     */
    @FXML
    protected void toggleLoyalCampus(){
        if(collegeChecking.isSelected()){
           campusGroupContainer.setDisable(false);
        }else {
            campusGroupContainer.setDisable(true);
        }
        if(savings.isSelected()){
            loyalContainer.setDisable(false);
        } else {
            loyalContainer.setDisable(true);
        }
        if(moneyMarket.isSelected()){
            loyal.setSelected(true);
        }
        else {
            loyal.setSelected(false);
        }
    }

    /**
     * Checks is initial deposit is valid
     * @return false if initial deposit is less than or equal to 0, true otherwise
     */
    private boolean isValidInitialDeposit(){
        if (initialDeposit <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Initial Deposit cannot be 0 or negative.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Checks if withdraw amount is valid
     * @return false if withdraw amount is less than or equal to 0, true otherwise
     */
    private boolean isValidWithdraw() {
        if (amount <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Withdraw - amount cannot be 0 or negative.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Checks is deposit amount is valid
     * @return if deposit amount is less than or equal to 0, true otherwise
     */
    private boolean isValidDeposit() {
        if (amount <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Deposit - amount cannot be 0 or negative.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Checks if user has valid date of birth (age) to open an account
     * User must be at least 16 years old to open any account and must be
     * under 24 years old to open a College Checking account
     * Prints error message if date of birth is invalid
     * @param date as Date
     * @param accountType as String
     * @return true if valid DOB, false if invalid DOB
     */
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

    /**
     * Calculates age of user using Calendar class
     * @param currentDate as Date
     * @param ageDate as Date
     * @return age as int
     */
    private int calculateAge(Date currentDate, Date ageDate) {
        int age = currentDate.getYear() - ageDate.getYear();

        if (currentDate.getMonth() < ageDate.getMonth() ||
                (currentDate.getMonth() == ageDate.getMonth()
                        && currentDate.getDay() < ageDate.getDay())) {
            age--;
        }
        return age;
    }

    /**
     * Converts the string form of the campus into the int form of the campus code
     * @param campusString as String
     * @return corresponding campus code as int
     */
    private String convertToCode(String campusString) {
        switch (campusString) {
            case "New Brunswick" -> {
                return "0";
            }
            case "Newark" -> {
                return "1";
            }
            case "Camden" -> {
                return "2";
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Parses date string from command line and converts to Date object
     * @param dateString date of event as String
     * @return created date as Date object
     */
    private Date parseDate(String dateString) {
        String[] dateComponents = dateString.split("/");
        if (dateComponents.length == DATE_COMPONENTS_LENGTH) {
            int year = Integer.parseInt(dateComponents[YEAR_PART]);
            int month = Integer.parseInt(dateComponents[MONTH_PART]);
            int day = Integer.parseInt(dateComponents[DAY_PART]);
            return new Date(year, month, day);
        }
        return null;
    }

}
