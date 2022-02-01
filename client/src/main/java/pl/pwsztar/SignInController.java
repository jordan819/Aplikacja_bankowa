package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import pl.pwsztar.Connect.CustomerDto;
import pl.pwsztar.Connect.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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

            // sprawdzenie czy osoba logująca się to potencjalny pracownik (numer konta ma dokładnie 3 znaki)
            if (login.getText().length() == 3) {

                // zapytanie do serwera
                final HttpClient client = HttpClientBuilder.create().build();
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

            try {
                List<CustomerDto> customers = Database.fetchCustomers();

                if (customers == null){
                    infoDisplay.setVisible(true);
                    infoDisplay.setText("Dane niepoprawne!");
                    return;
                }

                for (CustomerDto customer : customers) {
                    if (customer.getIdAccount().equals(login.getText())) {
                        if (customer.getPassword().equals(password.getText()) && customer.isVerified()) {
                            App.loggedCustomer = customer;
                            App.setRoot("accountManage");
                        } else
                            infoDisplay.setVisible(true);
                            infoDisplay.setText("Dane niepoprawne!");
                            return;
                    }
                }
                infoDisplay.setVisible(true);
                infoDisplay.setText("Dane niepoprawne!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
