package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import pl.pwsztar.Connect.Money;

import java.io.IOException;

/**
 * Obsluguje logike okna odpowiedzialnego za przewalutowanie salda.
 * Uzytkownik wyniera z listy, na jaką walutę chce wymienić pieniadze.
 * Pobierane sa informacje o aktualnym kursie wymiany walut,
 * obliczony i wyswietlony zostaje stan konta po przewalutowaniu.
 * Po wcisnieciu przycisku, zmiany zostaja zapisane w bazie.
 */
public class CurrencyConversionController {

    @FXML
    private ChoiceBox<String> choiceBox;

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

            currencyAfter = choiceBox.getItems().get((Integer) number2);

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
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPut request = new HttpPut("http://127.0.0.1:8080/bank/exchange/"
                + App.loggedCustomerAccount.getAccountId() + "/" + currencyAfter);
        client.execute(request);
        goToAccountManage();
    }



}
