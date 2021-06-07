package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class PayLoanController {

    public Label loanDisplay;

    @FXML
    private void initialize() {
        loanDisplay.setText("Do spłacenia pozostało:\n" + App.loggedCustomerAccount.getLoan() + " " + App.loggedCustomerAccount.getCurrency());
    }

    public void goBack() throws IOException {
        App.setRoot("accountManage");
    }
}
