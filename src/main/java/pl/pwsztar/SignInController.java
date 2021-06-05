package pl.pwsztar;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignInController {


    public TextField login;
    public PasswordField password;

    public void SignIn() throws IOException {
        App.setRoot("accountManage");
    }

    public void goToSignUp() throws IOException {
        App.setRoot("signUp");
    }

    public void goToVerification() throws IOException {
        App.setRoot("registerVerification");
    }
}
