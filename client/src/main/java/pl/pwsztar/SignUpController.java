package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Paint;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import pl.pwsztar.Connect.SendEmailTLS;

import java.io.IOException;

/**
 * Obsluguje logike okna odpowiedzialnego za rejestracje uzytkownika.
 * Sprawdza czy wszyskie pola formularza zostaly wypelnione,
 * email i haslo wpisane dwukrotnie sie zgadzaja, oraz czy haslo ma ponad 7 znakow.
 * Jezeli tak, sprawdza czy w bazie nie istnieje juz konto zalozone na podany adres email.
 * W przypadku, gdy jeden z warunkow nie zostanie spelniony, odpowiedni komunikat jest wyswietlany uzytkownikowi.
 */
public class SignUpController {

    @FXML
    private TextField firstName, lastName, email, emailRepeat;

    @FXML
    private PasswordField password, passwordRepeat;

    @FXML
    private Label errorDisplay;

    @FXML
    private void initialize() {
        disableAllSpaceBar();
    }


    @FXML
    private void goToSignIn() throws IOException {
        App.setRoot("signIn");
    }

    @FXML
    private void signUp() throws IOException {

        errorDisplay.setVisible(true);
        errorDisplay.setTextFill(Paint.valueOf("red"));

        if ( isInputBlank() ) {
            errorDisplay.setText("Uzupełnij wszystkie pola!");
        } else if ( emailsDifferent() ) {
            errorDisplay.setText("Adresy email nie mogą się różnić!");
        } else if ( emailIncorrect() ) {
            errorDisplay.setText("Adres email niepoprawny!");
        } else if ( password.getText().length() < 8 ) {
            errorDisplay.setText("Hasło musi składać się z conajmniej 8 znaków!");
        } else if( passwordsDifferent() ) {
            errorDisplay.setText("Hasła nie mogą się różnić!");
        } else {

            errorDisplay.setText("Przetwarzamy Twoje dane...\n Prosimy o cierpliwość.");

            final HttpClient client = HttpClientBuilder.create().build();
            final HttpPost request = new HttpPost("http://127.0.0.1:8080/bank/account/create/"
                    + firstName.getText() + "/"
                    + lastName.getText() + "/"
                    + email.getText() + "/"
                    + password.getText());
            int statusCode = client.execute(request).getStatusLine().getStatusCode();

            // zostało już założone konto na podany email
            if (statusCode == 409){
                errorDisplay.setVisible(true);
                errorDisplay.setText("Na taki adres email zostało już utworzone konto!");
                return;
            } else if (statusCode != 200){
                System.out.println("status code: " + statusCode);
                errorDisplay.setVisible(true);
                errorDisplay.setText("Wystąpił nieoczekiwany błąd");
                return;
            }


            errorDisplay.setTextFill(Paint.valueOf("green"));
            App.setRoot("registerVerification");
        }
    }

    private boolean isInputBlank() {
        return firstName.getText().isBlank() || lastName.getText().isBlank()
                || email.getText().isBlank() || password.getText().isBlank()
                || passwordRepeat.getText().isBlank() || emailRepeat.getText().isBlank();
    }

    private void disableAllSpaceBar() {
        disableSpaceBar(firstName);
        disableSpaceBar(lastName);
        disableSpaceBar(email);
        disableSpaceBar(password);
        disableSpaceBar(passwordRepeat);
        disableSpaceBar(emailRepeat);

    }

    private void disableSpaceBar(TextInputControl input) {
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 32)
                input.setText(oldValue);
            else {
                input.setText(newValue.replaceAll(" ", ""));
            }

        });
    }

    private boolean emailsDifferent() {
        return !email.getText().equals(emailRepeat.getText());
    }

    private boolean emailIncorrect() {
        return !SendEmailTLS.isEmailAddressValid(email.getText());
    }

    private boolean passwordsDifferent() {
        return !password.getText().equals(passwordRepeat.getText());
    }

}
