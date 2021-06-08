package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import pl.pwsztar.Connect.CustomerDto;
import pl.pwsztar.Connect.Database;
import pl.pwsztar.Connect.SendEmailTLS;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RegisterVerificationController {
    public TextField emailInput;
    public TextField verificationCodeInput;
    public Label infoDisplay;

    private String email;
    private String verificationCode;

    List<CustomerDto> customers;

    @FXML
    private void initialize() throws IOException {
        try {
            customers = Database.fetchCustomers();
        } catch (SQLException e){
            e.printStackTrace();
            App.setRoot("signIn");
        }
    }

    public void goBack() throws IOException {
        App.setRoot("SignIn");
    }

    public void verifyAccount() {

        infoDisplay.setVisible(true);
        infoDisplay.setTextFill(Paint.valueOf("red"));

        if ( isInputBlank() ) {
            infoDisplay.setText("Uzupełnij wszystkie pola!");
        } else if ( emailIncorrect() ) {
            infoDisplay.setText("Adres email niepoprawny!");
        } else {
            email = emailInput.getText();
            verificationCode = verificationCodeInput.getText();
            infoDisplay.setTextFill(Paint.valueOf("green"));
            infoDisplay.setText("Przetwarzamy Twoje dane...\nProsimy o cierpliwość.");

            if ( validateCode() ) {
                infoDisplay.setText("Konto zostało aktywowane.\nMożesz się zalogować.");
                System.out.println("Konto zostało aktywowane");
            } else {
                infoDisplay.setTextFill(Paint.valueOf("red"));
                infoDisplay.setText("Nie udało się aktywować konta.\nSpróbuj ponownie później.");
                System.out.println("Nie udało się aktywować konta");
            }


        }

    }

    private boolean isInputBlank() {
        return emailInput.getText().isBlank() || verificationCodeInput.getText().isBlank();
    }

    private boolean emailIncorrect() {
        return !SendEmailTLS.isEmailAddressValid(emailInput.getText());
    }

    private boolean validateCode() {
        for (CustomerDto customer: customers) {
            if (customer.getEmail().equals(email)) {
                if (customer.getVerificationCode().equals(verificationCode)) {
                    Database.verifyCustomer(customer);
                    Database.updateAccountBalance(customer.getIdAccount(), "1000");
                    return true;
                } else
                    return false;
            }
        }
        return false;
    }
}
