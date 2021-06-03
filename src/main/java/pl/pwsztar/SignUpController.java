package pl.pwsztar;

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

    public void signUp() {

    }

    public void goToSignIn() throws IOException {
        App.setRoot("signIn");
    }

    public void goToRegisterVerification() throws IOException {
        App.setRoot("registerVerification");
    }
}
