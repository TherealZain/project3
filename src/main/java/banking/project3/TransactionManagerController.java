package banking.project3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.fxml.FXML;

public class TransactionManagerController {
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private DatePicker dob;
    @FXML
    private RadioButton checking;
    @FXML
    private RadioButton collegeChecking;
    @FXML
    private RadioButton savings;
    @FXML
    private RadioButton moneyMarket;
    @FXML
    private Button open;



    @FXML
    protected void openAccount(ActionEvent event) {
        try {
            String firstNameString = firstName.getText();
            String lastNameString = lastName.getText();
            Date accountDob = new Date(dob.getValue().getYear(),
                    dob.getValue().getMonthValue(), dob.getValue().getDayOfMonth());
        }
        catch(NumberFormatException e){
            System.out.println("error" + e);
        }

    }
}
