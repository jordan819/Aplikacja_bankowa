package pl.pwsztar;

import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import pl.pwsztar.Connect.CustomerDto;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;

/**
 * Obsluguje logike okna odpowiedzialnego za logowanie sie uzytkownika.
 * Przyjmuje email i haslo, porownuje z danymi w bazie i loguje uzytkownika, lub odmawia dostepu.
 */
public class SignInController {

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    @FXML
    private Label infoDisplay;

    @FXML
    private Button signInBtn;

    @FXML
    private void initialize() {
        login.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 15)
                login.setText(oldValue);
            else if (!newValue.matches("[\\d]*")) {
                    login.setText(newValue.replaceAll("[^[\\d]]", ""));
                }

            signInBtn.setDisable((login.getText().length() < 7) || (password.getText().length() <= 7));

        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 28)
                password.setText(oldValue);

            signInBtn.setDisable((login.getText().length() < 3) || (password.getText().length() <= 7));
        });
    }

    @FXML
    private void SignIn() throws IOException {

        if (isInputBlank()) {
            infoDisplay.setVisible(true);
            infoDisplay.setText("Wprowadź dane!");
        } else {

            final HttpClient client = HttpClientBuilder.create().build();

            // sprawdzenie czy osoba logująca się to potencjalny pracownik (numer konta ma dokładnie 3 znaki)
            if (login.getText().length() == 3) {

                // zapytanie do serwera

                final HttpGet request = new HttpGet("http://127.0.0.1:8080/bank/loginEmployee/"
                                                    + login.getText() + "/" + password.getText());

                int statusCode = client.execute(request).getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    App.setRoot("employeePanel");
                } else if (statusCode == 403){
                    infoDisplay.setVisible(true);
                    infoDisplay.setText("Dane niepoprawne!");
                } else {
                    System.out.println("status code: " + statusCode);
                    infoDisplay.setVisible(true);
                    infoDisplay.setText("Wystąpił nieoczekiwany błąd");
                }

                return;

            }

            final HttpGet request = new HttpGet("http://127.0.0.1:8080/bank/loginCustomer/"
                                                    + login.getText() + "/" + password.getText());
            int statusCode = client.execute(request).getStatusLine().getStatusCode();

            if (statusCode == 403){
                infoDisplay.setVisible(true);
                infoDisplay.setText("Dane niepoprawne!");
                return;
            } else if (statusCode != 200){
                System.out.println("status code: " + statusCode);
                infoDisplay.setVisible(true);
                infoDisplay.setText("Wystąpił nieoczekiwany błąd");
                return;
            }

            final HttpResponse response = client.execute(request);  // Otrzymujemy odpowiedz od serwera.
            final HttpEntity entity = response.getEntity();

            final String json = EntityUtils.toString(entity);   // Na tym etapie odczytujemy JSON'a, ale jako String.

            // Wyswietlamy zawartosc JSON'a na standardowe wyjscie.
            System.out.println("Odczytano JSON'a:");
            System.out.println(json);

            // zamiana Stringa na obiekt CustomerDto
            final Gson gson = new Gson();
            final Type type = new TypeToken<CustomerDto>(){}.getType();
            App.loggedCustomer = gson.fromJson(json, type);
            App.setRoot("accountManage");

        }
    }

    @FXML
    private void goToSignUp() throws IOException {
        App.setRoot("signUp");
    }

    @FXML
    private void goToVerification() throws IOException {
        App.setRoot("registerVerification");
    }

    private boolean isInputBlank() {
        return login.getText().isBlank() || password.getText().isBlank();
    }

}
