package pl.pwsztar;

import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterVerificationController {
    public TextField code;
    public TextField verificationCode;

    public void goBack() throws IOException {
        App.setRoot("SignIn");
    }

    public void verifyAccount() {
    }
}
