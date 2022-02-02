package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import pl.pwsztar.Connect.SendEmailTLS;

import java.io.IOException;

/**
 * Obsluguje logike okna odpowiedzialnego za weryfikację konta uzytkownika.
 * Przyjmje adres email i kod wyslany mailem, jezeli dane sie zgadzaja,
 * a konto nie zostalo jeszcze aktywowane ani dezaktywowane, nastepuje aktywacja konta.
 */
public class RegisterVerificationController {

    @FXML
    private TextField emailInput, verificationCodeInput;

    @FXML
    private Label infoDisplay;

    @FXML
    private void goBack() throws IOException {
        App.setRoot("SignIn");
    }

    @FXML
    private void verifyAccount() {

        infoDisplay.setVisible(true);
        infoDisplay.setTextFill(Paint.valueOf("red"));

        if ( isInputBlank() ) {
            infoDisplay.setText("Uzupełnij wszystkie pola!");
        } else if ( emailIncorrect() ) {
            infoDisplay.setText("Adres email niepoprawny!");
        } else {
            infoDisplay.setTextFill(Paint.valueOf("green"));
            infoDisplay.setText("Przetwarzamy Twoje dane...\nProsimy o cierpliwość.");

            final HttpClient client = HttpClientBuilder.create().build();
            final HttpPut request = new HttpPut("http://127.0.0.1:8080/bank/account/verify/"
                    + emailInput.getText() + "/"
                    + verificationCodeInput.getText());

            try {
                int statusCode = client.execute(request).getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    infoDisplay.setTextFill(Paint.valueOf("green"));
                    infoDisplay.setText("Konto zostało aktywowane.\nWysłany został email z dalszymi instrukcjami.");
                } else if (statusCode == 403){
                    infoDisplay.setVisible(true);
                    infoDisplay.setText("Dane niepoprawne!");
                } else {
                    System.out.println("status code: " + statusCode);
                    infoDisplay.setVisible(true);
                    infoDisplay.setTextFill(Paint.valueOf("red"));
                    infoDisplay.setText("Wystąpił nieoczekiwany błąd");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isInputBlank() {
        return emailInput.getText().isBlank() || verificationCodeInput.getText().isBlank();
    }

    private boolean emailIncorrect() {
        return !SendEmailTLS.isEmailAddressValid(emailInput.getText());
    }

}
