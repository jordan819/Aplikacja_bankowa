package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import pl.pwsztar.Connect.Database;

import java.io.IOException;

public class PayLoanController {

    @FXML
    private Label loanDisplay;

    @FXML
    public Label infoDisplay;

    @FXML
    public TextField amount;

    @FXML
    public Button payInBtn;

    @FXML
    private void initialize() {
        String info = String.format("Do spłacenia pozostało:\n%.2f %s", App.loggedCustomerAccount.getLoan(), App.loggedCustomerAccount.getCurrency());
        loanDisplay.setText(info);

        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 15)
                amount.setText(oldValue);
            else if (newValue.length() > oldValue.length()) {
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
            payInBtn.setDisable(amount.getText().equals(""));
        });
    }

    public void goBack() throws IOException {
        App.setRoot("accountManage");
    }

    public void payIn() {
        infoDisplay.setVisible(true);
        if ( Double.parseDouble(amount.getText()) > App.loggedCustomerAccount.getBalance() ) {
            infoDisplay.setTextFill(Paint.valueOf("red"));
            infoDisplay.setText("Brak środków na koncie!");
        } else if ( Double.parseDouble(amount.getText()) > App.loggedCustomerAccount.getLoan() ){
            infoDisplay.setTextFill(Paint.valueOf("red"));
            infoDisplay.setText("Podałeś kwotę większą niż wartość Twojej pożyczki!");
        } else {
            try {
                Database.updateAccountBalance(App.loggedCustomerAccount.getAccountId(), "-" + amount.getText());
                Database.updateLoanInformation(App.loggedCustomerAccount.getAccountId(),
                        Double.parseDouble(amount.getText()));
                infoDisplay.setTextFill(Paint.valueOf("green"));
                infoDisplay.setText("Wpłata dokonana pomyślnie.");
            } catch (AccountNotFoundException e) {
                e.printStackTrace();
                infoDisplay.setTextFill(Paint.valueOf("red"));
                infoDisplay.setText("Wystąpił błąd!\nSpróbuj ponownie później.");
            }
        }
    }
}
