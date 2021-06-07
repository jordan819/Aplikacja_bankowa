package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pl.pwsztar.Connect.Database;

import java.io.IOException;

public class PayInController {

    public TextField otherInput;
    public Button btn10;
    public Button btn20;
    public Button btn50;
    public Button btn100;
    public Button btn200;
    public Button btn500;
    public Button otherButton;

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
            if (!newValue.matches("\\d*")) {
                otherInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
            otherButton.disableProperty().set(!(Integer.parseInt(otherInput.getText())%10==0));
        });

    }

    public void payIn10() {
        payIn("10");
    }

    public void payIn20() {
        payIn("20");
    }

    public void payIn50() {
        payIn("10");
    }

    public void payIn100() {
        payIn("100");
    }

    public void payIn200() {
        payIn("200");
    }

    public void payIn500() {
        payIn("500");
    }

    public void payInOther() {
    }

    private void payIn(String value) {
        Database.updateAccountBalance(App.loggedCustomerAccount.getAccountId(), value);
        goBack();
    }

    public void goBack() {
        try {
            App.setRoot("accountManage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
