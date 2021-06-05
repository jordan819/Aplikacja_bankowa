package pl.pwsztar;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.robot.Robot;
import pl.pwsztar.Connect.SendEmailTLS;

import java.io.IOException;

public class SignUpController {


    public TextField firstName;
    public TextField secondName;
    public TextField email;
    public PasswordField password;
    public PasswordField passwordRepeat;
    public TextField emailRepeat;
    public Label errorDisplay;

    @FXML
    private void initialize() {
        disableAllSpaceBar();
    }


    public void goToSignIn() throws IOException {
        App.setRoot("signIn");
    }

    public void signUp() throws IOException {

        boolean isValid = validateInput();

        if(isValid) {
            App.setRoot("registerVerification");
        }

    }

    private boolean validateInput() {
        if ( isInputBlank() ) {
            errorDisplay.setVisible(true);
            errorDisplay.setText("Uzupełnij wszystkie pola!");
            return false;
        } else if ( emailsDifferent() ) {
            errorDisplay.setVisible(true);
            errorDisplay.setText("Adresy email nie mogą się różnić!");
            return false;
        } else if ( emailIncorrect() ) {
            errorDisplay.setVisible(true);
            errorDisplay.setText("Adres email niepoprawny!");
            return false;
        } else if ( passwordsDifferent() ) {
            errorDisplay.setVisible(true);
            errorDisplay.setText("Hasła nie mogą się różnić!");
            return false;
        }

        return true;
    }

    private boolean isInputBlank() {
        return firstName.getText().isBlank() || secondName.getText().isBlank()
                || email.getText().isBlank() || password.getText().isBlank()
                || passwordRepeat.getText().isBlank() || emailRepeat.getText().isBlank();
    }

    private void disableAllSpaceBar() {
        disableSpaceBar(firstName);
        disableSpaceBar(secondName);
        disableSpaceBar(email);
        disableSpaceBar(password);
        disableSpaceBar(passwordRepeat);
        disableSpaceBar(emailRepeat);

    }

    private void disableSpaceBar(TextInputControl input) {
        input.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                Robot r = new Robot();
                r.keyPress(KeyCode.BACK_SPACE);
                r.keyRelease(KeyCode.BACK_SPACE);
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
