package pl.pwsztar;

import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignInController {


    public TextField login;
    public PasswordField password;

    public void SignIn() {
    }

    public void goToSignUp() throws IOException {
        App.setRoot("signUp");
    }
}
