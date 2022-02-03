package pl.pwsztar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Obsluguje logike okna odpowiedzialnego za udzielanie pozyczki klientowi.
 * Umozliwia mu podanie dowolnej kwoty pozyczki i wybranie jednej z kilku dostepnych dlugosci trwania pozyczki.
 * Na biezaco oblicza i przedstawia uzytkownikowi wysokosc odsetek.
 * Po zatwierdzeniu operacji, saldo uzytkownika oraz informacje o aktualnej pozyczce sa aktualizowane.
 */
public class TakeLoanController {


    @FXML
    private Label interestDisplay, infoDisplay;

    @FXML
    private TextField amount;

    @FXML
    private ChoiceBox<String> lengthChoice;

    @FXML
    private Button takeLoan;

    double calculatedInterest;
    double multiplier;
    int duration;

    @FXML
    private void initialize() {
        interestDisplay.setText("Wysokość odsetek:\n" + 0 + " " + App.loggedCustomerAccount.getCurrency());
        lengthChoice.getItems().addAll("3 miesiące", "6 miesiący", "1 rok", "2 lata");
        lengthChoice.getSelectionModel().selectedIndexProperty().
                addListener((observableValue, number, number2) -> {

            switch (number2.intValue()) {
                case 0:
                    duration = 3;
                    break;
                case 1:
                    duration = 6;
                    break;
                case 2:
                    duration = 12;
                    break;
                case 3:
                    duration = 24;
                    break;
            }
            // pobranie przelicznika odsetek z serwera
            final HttpClient client = HttpClientBuilder.create().build();
            final HttpGet request = new HttpGet("http://127.0.0.1:8080/bank/account/loan/getMultiplier/"
                    + duration);

            String json = "";

            try {
                final HttpResponse response = client.execute(request);  // Otrzymujemy odpowiedz od serwera.
                final HttpEntity entity = response.getEntity();

                json = EntityUtils.toString(entity);   // Na tym etapie odczytujemy JSON'a, ale jako String.
            } catch (IOException e) {
                e.printStackTrace();
            }


            // Wyswietlamy zawartosc JSON'a na standardowe wyjscie.
            System.out.println("Pobrano oprocentowanie z serwera: " + json);

            // zamiana Stringa na obiekt Account
            final Gson gson = new Gson();
            final Type type = new TypeToken<Double>(){}.getType();
            multiplier = gson.fromJson(json, type);

            calculateInterest();

        });
        lengthChoice.getSelectionModel().selectFirst();

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
            if (amount.getText().equals(".")) {
                amount.setText("");
            }
            calculateInterest();
        });
    }

    private void calculateInterest() {
        if(amount.getText().isBlank()) {
            interestDisplay.setText("Wysokość odsetek:\n" + 0 +
                    " " + App.loggedCustomerAccount.getCurrency());
        } else {
            calculatedInterest = Double.parseDouble(amount.getText()) * multiplier;
            String calculatedInterestAsString = String.format("%.2f ", calculatedInterest);
            interestDisplay.setText("Wysokość odsetek:\n" + calculatedInterestAsString +
                    " " + App.loggedCustomerAccount.getCurrency());
        }
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("accountManage");
    }

    @FXML
    private void takeLoan() {
        infoDisplay.setVisible(true);
        if(calculatedInterest != 0.0) {

            try {

                final HttpClient client = HttpClientBuilder.create().build();
                final HttpPut request = new HttpPut("http://127.0.0.1:8080/bank/account/loan/takeLoan/"
                        + App.loggedCustomerAccount.getAccountId() + "/" + amount.getText() + "/" + duration);
                client.execute(request);

            } catch (IOException e) {
                e.printStackTrace();
            }

            infoDisplay.setTextFill(Paint.valueOf("green"));
            infoDisplay.setText("Kredyt został udzielony!");
            takeLoan.setDisable(true);
        } else {
            infoDisplay.setTextFill(Paint.valueOf("red"));
            infoDisplay.setText("Wprowadź kwotę!");
        }
    }
}
