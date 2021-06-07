package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class TransferController {

    public TextField toAccountInput;
    public TextField amount;
    public Label infoDisplay;

    @FXML
    private void initialize() {
        toAccountInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                toAccountInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > oldValue.length()) {
                if (newValue.charAt(newValue.length() - 1) == '.') {
                    if (oldValue.contains("."))
                        amount.setText(oldValue);
                    else
                        amount.setText(newValue);
                } else if (!newValue.matches("[\\d]*")) {
                    amount.setText(newValue.replaceAll("[^[\\d.]]", ""));
                }
            }
            if (amount.getLength() > 3) {
                if (amount.getText().charAt(amount.getLength() - 4) == '.') {
                    amount.setText(oldValue);
                }
            }
            if (amount.getText().equals(".")) {
                amount.setText("");
            }
        });
    }

    public void makeTransfer() {
        infoDisplay.setVisible(false);
        if ( !inputBlank() ) {

        } else {
            infoDisplay.setVisible(true);
            infoDisplay.setText("Wprowadź dane!");
        }
    }

    private boolean inputBlank() {
        return toAccountInput.getText().isBlank() || amount.getText().isBlank();
    }

    public void goBack() throws IOException {
        App.setRoot("accountManage");
    }
}
