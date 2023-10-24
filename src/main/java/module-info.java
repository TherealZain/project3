module banking.project3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens banking.project3 to javafx.fxml;
    exports banking.project3;
}