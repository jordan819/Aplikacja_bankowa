package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.robot.Robot;
import pl.pwsztar.Connect.CustomerDto;
import pl.pwsztar.Connect.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SignInController {


    public TextField login;
    public PasswordField password;
    public Label infoDisplay;

    @FXML
    private void initialize() {
        disableAllSpaceBar();
    }

    public void SignIn() throws IOException {

        if (isInputBlank()) {
            infoDisplay.setVisible(true);
            infoDisplay.setText("Wprowadź dane!");
        } else {

            try {
                List<CustomerDto> customers = Database.fetchCustomers();

                if (customers == null){
                    infoDisplay.setVisible(true);
                    infoDisplay.setText("Dane niepoprawne!");
                    return;
                }

                for (CustomerDto customer : customers) {
                    if (customer.getIdAccount().equals(login.getText())) {
                        if (customer.getPassword().equals(password.getText()) && customer.isVerified()) {
                            App.loggedCustomer = customer;
                            App.setRoot("accountManage");
                        } else
                            infoDisplay.setVisible(true);
                            infoDisplay.setText("Dane niepoprawne!");
                            return;
                    }
                }
                infoDisplay.setVisible(true);
                infoDisplay.setText("Dane niepoprawne!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void goToSignUp() throws IOException {
        App.setRoot("signUp");
    }

    public void goToVerification() throws IOException {
        App.setRoot("registerVerification");
    }

    private void disableAllSpaceBar() {
        disableSpaceBar(login);
        disableSpaceBar(password);
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

    private boolean isInputBlank() {
        return login.getText().isBlank() || password.getText().isBlank();
    }

}
