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
    private VBox campusGroupContainer;
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
        String accountType = accountTypeGroup.getSelectedToggle().toString();
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
        };
    }

    private void openMoneyMarket(String fName, String lName, Date dob, double initialDeposit) {

    }

    private void openSavings(String fName, String lName, Date dob, double initialDeposit) {
    }

    private void openCollegeChecking(String fName, String lName, Date dob, double initialDeposit) {
    }

    private void openChecking(String fName, String lName, Date dob, double initialDeposit) {
        Checking newChecking = new Checking(new Profile(fName, lName, dob),
                initialDeposit);
        openAccount(fName, lName, dob, newChecking, "C");
    }

    private void openAccount(String fName, String lName, Date dob,
                             Account account, String accountType) {
        if (accountDatabase.open(account)) {
            System.out.println(fName + " " + lName + " " +
                    dob.dateString() + "(" + accountType + ") opened.");
        } else {
            System.out.println(fName + " " + lName + " " + dob.dateString()
                    + "(" + accountType + ") is already in the database.");
        }
    }

    @FXML
    protected void handleWithdraw(ActionEvent event) {
        
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
