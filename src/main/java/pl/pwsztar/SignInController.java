package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pl.pwsztar.Connect.CustomerDto;
import pl.pwsztar.Connect.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SignInController {


    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    @FXML
    private Label infoDisplay;

    @FXML
    private Button signInBtn;

    @FXML
    private void initialize() {
        login.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 15)
                login.setText(oldValue);
            else if (!newValue.matches("[\\d]*")) {
                    login.setText(newValue.replaceAll("[^[\\d]]", ""));
                }

            signInBtn.setDisable((login.getText().length() <= 7) || (password.getText().length() <= 7));

        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 28)
                password.setText(oldValue);

            signInBtn.setDisable((login.getText().length() <= 7) || (password.getText().length() <= 7));
        });
    }

    @FXML
    private void SignIn() throws IOException {

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

    @FXML
    private void goToSignUp() throws IOException {
        App.setRoot("signUp");
    }

    @FXML
    private void goToVerification() throws IOException {
        App.setRoot("registerVerification");
    }

    private boolean isInputBlank() {
        return login.getText().isBlank() || password.getText().isBlank();
    }

}
