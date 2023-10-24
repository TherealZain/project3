package banking.project3;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.fxml.FXML;

public class TransactionManagerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
