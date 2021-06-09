package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pwsztar.Connect.Database;

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
            Database.deactivateAccount(App.loggedCustomerAccount.getAccountId());
            try {
                App.setRoot("signIn");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
