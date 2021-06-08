package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pwsztar.Connect.Database;

import java.io.IOException;

public class DeactivateController {

    public Label infoDisplay;
    public TextField textInput;

    private final String code = "Usuń moje konto";

    @FXML
    private void initialize() {
        infoDisplay.setText("Jeżeli dezaktywujesz swoje konto, nie będziesz mógł wykonywać " +
                "na nim żadnych operacji. Konsekwecji tej operacji nie można cofnąć. " +
                "Jeśli jesteś pewien, przepisz poniższy tekst:\n" + code);
    }

    public void goBack() throws IOException {
        App.setRoot("accountManage");
    }

    public void deactivate() {
        if (textInput.getText().equals(code)) {
            Database.deactivateAccount(App.loggedCustomerAccount.getAccountId());
            try {
                App.setRoot("signIn");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
