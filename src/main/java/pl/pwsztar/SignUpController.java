package pl.pwsztar;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignUpController {


    public TextField firstName;
    public TextField secondName;
    public TextField email;
    public PasswordField password;
    public PasswordField passwordRepeat;
    public TextField emailRepeat;
    public Label errorDisplay;

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
        if (isInputBlank()) {
            errorDisplay.setVisible(true);
            errorDisplay.setText("Uzupełnij wszystkie pola!");
            return false;
        }

        return true;
    }

    private boolean isInputBlank() {
        return firstName.getText().isBlank() || secondName.getText().isBlank()
                || email.getText().isBlank() || password.getText().isBlank()
                || passwordRepeat.getText().isBlank() || emailRepeat.getText().isBlank();
    }
}
