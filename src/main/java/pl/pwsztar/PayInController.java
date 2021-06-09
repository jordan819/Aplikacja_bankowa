package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pl.pwsztar.Connect.Database;

import java.io.IOException;

public class PayInController {

    @FXML
    private TextField otherInput;

    @FXML
    private Button btn10, btn20, btn50, btn100, btn200, btn500, otherButton;

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

    @FXML
    private void payIn10() {
        payIn("10");
    }

    @FXML
    private void payIn20() {
        payIn("20");
    }

    @FXML
    private void payIn50() {
        payIn("50");
    }

    @FXML
    private void payIn100() {
        payIn("100");
    }

    @FXML
    private void payIn200() {
        payIn("200");
    }

    @FXML
    private void payIn500() {
        payIn("500");
    }

    @FXML
    private void payInOther() {
        payIn(otherInput.getText());
    }

    private void payIn(String value) {
        Database.updateAccountBalance(App.loggedCustomerAccount.getAccountId(), value);
        goBack();
    }

    @FXML
    private void goBack() {
        try {
            App.setRoot("accountManage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
