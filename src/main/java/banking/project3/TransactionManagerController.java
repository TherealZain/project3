package banking.project3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.Calendar;


public class TransactionManagerController {
    @FXML
    private TextField firstName, lastName, openDeposit;
    @FXML
    private DatePicker dob;
    @FXML
    private RadioButton checking, collegeChecking, savings, moneyMarket;
    @FXML
    private Button open, close;
    @FXML
    private ToggleGroup accountTypeGroup;
    @FXML
    private ToggleGroup campus;
    @FXML
    private VBox campusGroupContainer;
    @FXML
    private TextArea openCloseOutput;
    String[] fields = new String[FIELDS_FOR_OPEN_CLOSE];
    private AccountDatabase accountDatabase = new AccountDatabase();
    Date accountDob;
    double initialDeposit;
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
        if(checkFields() && isValidDeposit() &&
                ageCheck(accountDob, accountType)){
            switch (accountType) {

                case "Checking" -> openChecking(fields[FNAME_INPUT], fields[LNAME_INPUT],
                        accountDob, initialDeposit);
                case "College Checking" -> openCollegeChecking(fields[FNAME_INPUT], fields[LNAME_INPUT],
                        accountDob, initialDeposit);
                case "Savings" -> openSavings(fields[FNAME_INPUT], fields[LNAME_INPUT], accountDob,
                        initialDeposit);
                case "Money Market" -> openMoneyMarket(fields[FNAME_INPUT], fields[LNAME_INPUT]
                        , accountDob, initialDeposit);
            }
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

    @FXML
    protected void handleWithdraw(ActionEvent event) {
        String accountType = accountTypeGroup.getSelectedToggle().toString();
        if (checkFields()) {
            switch (accountType) {
                case "Checking" -> withdrawChecking(fields[FNAME_INPUT], fields[LNAME_INPUT], accountDob, initialDeposit);
                case "College Checking" -> withdrawCollegeChecking(fields[FNAME_INPUT], fields[LNAME_INPUT], accountDob);
                case "Savings" -> withdrawSavings(fields[FNAME_INPUT], fields[LNAME_INPUT], accountDob);
                case "Money Market" -> withdrawMoneyMarket(fields[FNAME_INPUT], fields[LNAME_INPUT], accountDob);
            }
        };
    }

    private void withdrawChecking(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        Checking accountToWithdraw = new Checking(profileToWithdraw, withdraw);
        withdrawAccount(fName, lName, dob, accountToWithdraw, withdraw, "C");
    }
    private void withdrawCollegeChecking(String fName, String lName, Date dob) {
        // your logic here
    }
    private void withdrawSavings(String fName, String lName, Date dob) {
        // your logic here
    }
    private void withdrawMoneyMarket(String fName, String lName, Date dob) {
        // your logic here
    }

    private void withdrawAccount(String fName, String lName, Date dob,
                                 Account account, double withdraw, String accountType) {
        if (!accountDatabase.withdraw(account)) {
            if (withdraw > account.balance) {
                System.out.println(fName + " " + lName + " " + dob.dateString()
                        + "(" + accountType + ") " + "Withdraw - " +
                        "insufficient fund.");
            }
            else {
                System.out.println(fName + " " + lName + " " + dob.dateString()
                        + "(" + accountType + ") is not in the database.");
            }
            return;
        }
        System.out.println(fName + " " + lName + " " + dob.dateString() + "("
                + accountType + ") Withdraw - balance updated.");
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
            fields[FNAME_INPUT] =firstName.getText();
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

    @FXML
    protected void toggleCampus(){
        if(collegeChecking.isSelected()){
           campusGroupContainer.setDisable(false);
        }else {
            campusGroupContainer.setDisable(true);
        }
    }

    private boolean isValidDeposit(){
        if (initialDeposit <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please enter valid amount");
            alert.setHeaderText("Initial Deposit cannot be 0 or less than 0");
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
