package pl.pwsztar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import pl.pwsztar.Connect.Account;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Obsluguje logike okna odpowiedzialnego za ekran powitalny po zalogowaniu.
 * Umozliwia nawigacje do wszystkich funkcjonalnosci konta.
 */
public class AccountManageController {

    @FXML
    private Label balanceDisplay;

    @FXML
    private Label infoDisplay;

    @FXML
    private void initialize() throws IOException {

        try {
            // pobranie danych o koncie zalogowanego użytkownika z serwera
            final HttpClient client = HttpClientBuilder.create().build();
            final HttpGet request = new HttpGet("http://127.0.0.1:8080/bank/account/"
                    + App.loggedCustomer.getIdAccount());

            int statusCode = client.execute(request).getStatusLine().getStatusCode();

            if (statusCode == 403){
                infoDisplay.setVisible(true);
                infoDisplay.setText("Dane niepoprawne!");
                throw new AccountNotFoundException();
            } else if (statusCode != 200){
                System.out.println("status code: " + statusCode);
                infoDisplay.setVisible(true);
                infoDisplay.setText("Wystąpił nieoczekiwany błąd");
                throw new AccountNotFoundException();
            }

            final HttpResponse response = client.execute(request);  // Otrzymujemy odpowiedz od serwera.
            final HttpEntity entity = response.getEntity();

            final String json = EntityUtils.toString(entity);   // Na tym etapie odczytujemy JSON'a, ale jako String.

            // Wyswietlamy zawartosc JSON'a na standardowe wyjscie.
            System.out.println("Odczytano JSON'a:");
            System.out.println(json);

            // zamiana Stringa na obiekt Account
            final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            final Type type = new TypeToken<Account>(){}.getType();

            // ustawienie pobranego konta użytkownika

            App.loggedCustomerAccount = gson.fromJson(json, type);
            String balance = String.format("%.2f ", App.loggedCustomerAccount.getBalance());
            balance += App.loggedCustomerAccount.getCurrency();
            balanceDisplay.setText(balance);
        } catch (AccountNotFoundException e) {
            e.printStackTrace();
            App.setRoot("signIn");
        }
    }

    @FXML
    private void goToCurrencyConversion() throws IOException {
        App.setRoot("currencyConversion");
    }

    @FXML
    private void goToPayIn() throws IOException {
        App.setRoot("payIn");
    }

    @FXML
    private void goToPayOut() throws IOException {
        App.setRoot("payOut");
    }

    @FXML
    private void goToTransfer() throws IOException {
        App.setRoot("transfer");
    }

    @FXML
    private void goToDeactivate() throws IOException {
        App.setRoot("deactivate");
    }

    @FXML
    private void goToTakeLoan() throws IOException {
        if(App.loggedCustomerAccount.getLoanDate() == null)
            App.setRoot("takeLoan");
        else {
            infoDisplay.setVisible(true);
            infoDisplay.setTextFill(Paint.valueOf("red"));
            infoDisplay.setText("Niestety, najpierw musisz spłacić poprzednią pożyczkę!");
        }
    }

    @FXML
    private void goToPayLoan() throws IOException {
        if(App.loggedCustomerAccount.getLoan() != null)
            App.setRoot("payLoan");
        else {
            infoDisplay.setVisible(true);
            infoDisplay.setTextFill(Paint.valueOf("green"));
            infoDisplay.setText("Aktualnie nie masz żadnej pożyczki!");
        }
    }

}
