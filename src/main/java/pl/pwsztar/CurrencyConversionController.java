package pl.pwsztar;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import pl.pwsztar.Connect.Money;

import java.io.IOException;
import java.sql.SQLException;

public class CurrencyConversionController {

    public ChoiceBox choiceBox;
    public Label exchangeDisplay;

    private String currencyBefore;
    private String currencyAfter;

    @FXML
    private void initialize() {
        currencyBefore = App.loggedCustomerAccount.getCurrency();

        choiceBox.getItems().addAll("PLN", "USD", "GBP", "EUR");
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {

            currencyAfter = (String) choiceBox.getItems().get((Integer) number2);

            double balanceBefore = App.loggedCustomerAccount.getBalance();
            double balanceAfter = Money.exchange(balanceBefore, currencyBefore, currencyAfter);

            String result = String.format("%.2f %s -> %.2f %s",
                    balanceBefore, currencyBefore, balanceAfter, currencyAfter);

            exchangeDisplay.setText(result);

        });

        choiceBox.getSelectionModel().selectFirst();
    }

    public void goBack() throws IOException {
        App.setRoot("accountManage");
    }
}
