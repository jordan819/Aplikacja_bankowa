package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import pl.pwsztar.Connect.Database;

import java.io.IOException;

public class TransferController {

    public TextField toAccountInput;
    public TextField amount;
    public Label infoDisplay;

    @FXML
    private void initialize() {
        infoDisplay.setVisible(false);
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
            if (!amount.getText().equals("") && newValue.charAt(0) == '0')
                amount.setText(newValue.substring(1));
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
        infoDisplay.setVisible(true);
        if ( !inputBlank() ) {
            try {
                Database.updateAccountBalance(toAccountInput.getText(), amount.getText(),
                        App.loggedCustomerAccount.getCurrency());
                Database.updateAccountBalance(App.loggedCustomerAccount.getAccountId(), "-" + amount.getText());
                infoDisplay.setTextFill(Paint.valueOf("green"));
                infoDisplay.setText("Przelano pieniądze!");
            } catch (AccountNotFoundException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        } else {
            infoDisplay.setTextFill(Paint.valueOf("red"));
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
