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


public class TransactionManagerController {
    @FXML
    private TextField firstName, lastName, openDeposit, firstNameDW, lastNameDW, depositOrWithdraw;
    @FXML
    private DatePicker dob, dobDW;
    @FXML
    private RadioButton checking, collegeChecking, savings, moneyMarket,
            checkingDW, collegeCheckingDW, savingsDW, moneyMarketDW;
    @FXML
    private Button open, close, withdraw, deposit, loadAccounts;
    @FXML
    private ToggleGroup accountTypeGroup, accountTypeGroupDW;
    @FXML
    private ToggleGroup campus;
    @FXML
    private VBox campusGroupContainer;
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

    private void createAccountFromParts(String[] parts) {
        String accountType = parts[ACCOUNT_TYPE_PART];
        String fName = parts[FNAME_PART];
        String lName = parts[LNAME_PART];
        Date dob = parseDate(parts[DOB_PART]);
        double initialDeposit = Double.parseDouble(parts[INITIAL_DEPOSIT_PART]);

        switch (accountType) {
            case "S" -> {
                // use LOYALTY_PART variable
                openSavings(fName, lName, dob, initialDeposit);
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
     * CONSIDER LOYALTY AS LAST INPUT?
     */
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

        } else {
            openCloseOutput.setText(fName + " " + lName + " " + dob.dateString()
                    + "(" + accountType + ") is already in the database.");
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
        CollegeChecking accountToWithdraw = new CollegeChecking(profileToWithdraw, withdraw, null);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "CC");
    }
    private void withdrawSavings(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        Savings accountToWithdraw = new Savings(profileToWithdraw, withdraw);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "S");
    }
    private void withdrawMoneyMarket(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        MoneyMarket accountToWithdraw = new MoneyMarket(profileToWithdraw, withdraw, true);
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
    }

    private boolean showAlert(String title, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
        return false;
    }

    private boolean checkTextField(TextField textField) {
        return textField == null || textField.getText().isEmpty();
    }

    private boolean checkDateField(DatePicker datePicker) {
        return datePicker == null || datePicker.getValue() == null;
    }

    private void populateFields(TextField fNameField, TextField lNameField, TextField depositField, DatePicker dobPicker) {
        fields[FNAME_INPUT] = fNameField.getText();
        fields[LNAME_INPUT] = lNameField.getText();
        fields[DEPOSIT_INPUT] = depositField.getText();
        initialDeposit = Double.parseDouble(fields[DEPOSIT_INPUT]);
        amount = initialDeposit;
        accountDob = new Date(dobPicker.getValue().getYear(),
                dobPicker.getValue().getMonthValue(), dobPicker.getValue().getDayOfMonth());
    }

    @FXML
    protected boolean checkFieldsOpen(){
        if (checkTextField(firstName) || checkTextField(lastName) || checkTextField(openDeposit) || checkDateField(dob)) {
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

    @FXML
    protected boolean checkFieldsClose(){
        if (checkTextField(firstName) || checkTextField(lastName) || checkDateField(dob)) {
            return showAlert("Missing Data", "Please fill all values.");
        }
        try {
            populateFields(firstName, lastName, openDeposit, dob);
        } catch(NullPointerException e) {
            return showAlert("Missing Data", "Please fill all values.");
        }
        return true;
    }

    @FXML
    protected boolean checkFieldsDepositWithdraw(){
        if (checkTextField(firstNameDW) || checkTextField(lastNameDW) || checkTextField(depositOrWithdraw) || checkDateField(dobDW)) {
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
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Initial Deposit cannot be 0 or negative.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private boolean isValidWithdraw() {
        if (amount <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Withdrawal - amount cannot be 0 or negative.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

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
