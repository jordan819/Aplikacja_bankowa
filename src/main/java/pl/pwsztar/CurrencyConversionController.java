package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import pl.pwsztar.Connect.Database;
import pl.pwsztar.Connect.Money;

import java.io.IOException;

public class CurrencyConversionController {

    @FXML
    private ChoiceBox choiceBox;

    @FXML
    private Label exchangeDisplay;

    private String currencyBefore, currencyAfter;
    private double balanceBefore, balanceAfter;

    @FXML
    private void initialize() {
        currencyBefore = App.loggedCustomerAccount.getCurrency();

        choiceBox.getItems().addAll("PLN", "USD", "GBP", "EUR");
        choiceBox.getItems().remove(App.loggedCustomerAccount.getCurrency());
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {

            currencyAfter = (String) choiceBox.getItems().get((Integer) number2);

            balanceBefore = App.loggedCustomerAccount.getBalance();
            balanceAfter = Money.exchange(balanceBefore, currencyBefore, currencyAfter);

            String result = String.format("%.2f %s -> %.2f %s",
                    balanceBefore, currencyBefore, balanceAfter, currencyAfter);

            exchangeDisplay.setText(result);

        });

        choiceBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void goToAccountManage() throws IOException {
        App.setRoot("accountManage");
    }

    @FXML
    private void convert() throws IOException {
        try {
            Database.updateAccountCurrency(App.loggedCustomerAccount.getAccountId(), currencyAfter, balanceAfter);
        } catch (AccountNotFoundException e) {
            e.printStackTrace();
            goToAccountManage();
        }
        goToAccountManage();
    }
}
