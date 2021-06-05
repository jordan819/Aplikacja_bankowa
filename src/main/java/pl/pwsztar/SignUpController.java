package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.scene.robot.Robot;
import org.apache.commons.lang3.RandomStringUtils;
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

        errorDisplay.setVisible(true);
        errorDisplay.setTextFill(Paint.valueOf("red"));

        if ( isInputBlank() ) {
            errorDisplay.setText("Uzupełnij wszystkie pola!");
        } else if ( emailsDifferent() ) {
            errorDisplay.setText("Adresy email nie mogą się różnić!");
        } else if ( emailIncorrect() ) {
            errorDisplay.setText("Adres email niepoprawny!");
        } else if ( passwordsDifferent() ) {
            errorDisplay.setText("Hasła nie mogą się różnić!");
        } else {
            errorDisplay.setTextFill(Paint.valueOf("green"));
            String code = generateVerificationCode();
            errorDisplay.setText("Przetwarzamy Twoje dane...\n Prosimy o cierpliwość.");
            new SendEmailTLS(email.getText(), code);
            App.setRoot("registerVerification");
        }

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

    private String generateVerificationCode() {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String code = RandomStringUtils.random( 16, characters );

        return code;
    }
}
