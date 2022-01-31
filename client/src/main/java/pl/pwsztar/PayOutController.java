package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pwsztar.Connect.Database;

import java.io.IOException;

/**
 * Obsluguje logike okna odpowiedzialnego za wyplacanie pieniedzy z konta.
 * Udostepnia mozliwosc wybrania 1 z 6 kwot, lub wpisania wlasnej.
 * Po nacisnieciu przycisku, sprawdza, czy saldo pozwala na wyplacenie wybranej kwoty.
 * Jezeli tak, jest ona wyplacana, w przeciwnym razie, wyswietlony zostaje komunikat o braku srodkow.
 */
public class PayOutController {

    @FXML
    private TextField otherInput;

    @FXML
    private Button btn10, btn20, btn50, btn100, btn200, btn500, otherButton;

    @FXML
    private Label infoDisplay;

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
    private void payOut10() {
        payOut("10");
    }

    @FXML
    private void payOut20() {
        payOut("20");
    }

    @FXML
    private void payOut50() {
        payOut("50");
    }

    @FXML
    private void payOut100() {
        payOut("100");
    }

    @FXML
    private void payOut200() {
        payOut("200");
    }

    @FXML
    private void payOut500() {
        payOut("500");
    }

    @FXML
    private void payOutOther() {
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

    @FXML
    private void goBack() {
        try {
            App.setRoot("accountManage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
