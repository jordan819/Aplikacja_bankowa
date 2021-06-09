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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterVerificationController {

    @FXML
    private TextField emailInput, verificationCodeInput;

    @FXML
    private Label infoDisplay;

    private String email;
    private String verificationCode;

    List<CustomerDto> customers;
    CustomerDto customerDto;

    @FXML
    private void initialize() throws IOException {
        try {
            customers = Database.fetchCustomers();
        } catch (SQLException e){
            e.printStackTrace();
            App.setRoot("signIn");
        }
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("SignIn");
    }

    @FXML
    private void verifyAccount() {

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
                infoDisplay.setText("Konto zostało aktywowane.\nWysłany został email z dalszymi instrukcjami.");

                String content = "Weryfikacja Twojego konta przebiegła pomyślnie. " +
                        "Do zalogowania się wykorzystasz utworzone hasło, " +
                        "oraz numer Twojego rachunku: " + customerDto.getIdAccount();

                ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
                emailExecutor.execute(() -> SendEmailTLS.send(email, "Weryfikacja zakończona", content));
                emailExecutor.shutdown();


            } else {
                infoDisplay.setTextFill(Paint.valueOf("red"));
                infoDisplay.setText("Nie udało się aktywować konta.\nSpróbuj ponownie później.");
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
                if (!customer.isVerified()) {
                    if (customer.getVerificationCode().equals(verificationCode)) {
                        Database.verifyCustomer(customer);
                        Database.updateAccountBalance(customer.getIdAccount(), "1000");
                        customerDto = customer;
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }
}
