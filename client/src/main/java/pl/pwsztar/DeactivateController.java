package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * Obsluguje logike okna odpowiedzialnego za dezaktywacje konta uzytkownika.
 * Po wprowadzeniu przez niego potwierdzenia, baza danych jest aktualizowana, a uzytkownik wylogowany.
 */
public class DeactivateController {

    @FXML
    private Label infoDisplay;

    @FXML
    private TextField textInput;

    private final String CODE = "Usuń moje konto";

    @FXML
    private void initialize() {
        infoDisplay.setText("Jeżeli dezaktywujesz swoje konto, nie będziesz mógł wykonywać " +
                "na nim żadnych operacji. Konsekwecji tej operacji nie można cofnąć. " +
                "Jeśli jesteś pewien, przepisz poniższy tekst:\n" + CODE);
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("accountManage");
    }

    @FXML
    private void deactivate() {
        if (textInput.getText().equals(CODE)) {
            try {
                final HttpClient client = HttpClientBuilder.create().build();
                final HttpDelete request = new HttpDelete("http://127.0.0.1:8080/bank/account/"
                        + App.loggedCustomerAccount.getAccountId());
                client.execute(request);  // Otrzymujemy odpowiedz od serwera.
                App.setRoot("signIn");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
