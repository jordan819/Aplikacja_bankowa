package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pwsztar.Connect.Database;

import java.io.IOException;

public class PayOutController {

    public TextField otherInput;
    public Button btn10;
    public Button btn20;
    public Button btn50;
    public Button btn100;
    public Button btn200;
    public Button btn500;
    public Button otherButton;
    public Label infoDisplay;

    @FXML
    private void initialize() {
        String currency = App.loggedCustomerAccount.getCurrency();
        btn10.setText("10 " + currency);
        btn20.setText("20 " + currency);
        btn50.setText("50 " + currency);
        btn100.setText("100 " + currency);
        btn200.setText("200 " + currency);
        btn500.setText("500 " + currency);

        otherInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 15)
                otherInput.setText(oldValue);
            else if (!newValue.matches("\\d*")) {
                otherInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (!otherInput.getText().equals("") && newValue.charAt(0) == '0')
                otherInput.setText(newValue.substring(1));
            if (!otherInput.getText().equals(""))
                otherButton.disableProperty().set(!(Long.parseLong(otherInput.getText())%10==0));
        });

    }

    public void payOut10() {
        payOut("10");
    }

    public void payOut20() {
        payOut("20");
    }

    public void payOut50() {
        payOut("50");
    }

    public void payOut100() {
        payOut("100");
    }

    public void payOut200() {
        payOut("200");
    }

    public void payOut500() {
        payOut("500");
    }

    public void payOutOther() {
        payOut(otherInput.getText());
    }

    private void payOut(String value) {
        if (balanceSufficient(Double.parseDouble(value))) {
            Database.updateAccountBalance(App.loggedCustomerAccount.getAccountId(), "-" + value);
            goBack();
        } else {
            infoDisplay.setVisible(true);
            infoDisplay.setText("Brak środków na koncie!");
        }
    }

    private boolean balanceSufficient(double amount) {
        return App.loggedCustomerAccount.getBalance() >= amount;
    }

    public void goBack() {
        try {
            App.setRoot("accountManage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
