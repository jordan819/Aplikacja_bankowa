package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Paint;
import org.apache.commons.lang3.RandomStringUtils;
import pl.pwsztar.Connect.Customer;
import pl.pwsztar.Connect.CustomerDto;
import pl.pwsztar.Connect.Database;
import pl.pwsztar.Connect.SendEmailTLS;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpController {

    @FXML
    private TextField firstName, lastName, email, emailRepeat;

    @FXML
    private PasswordField password, passwordRepeat;

    @FXML
    private Label errorDisplay;

    private String code;

    @FXML
    private void initialize() {
        disableAllSpaceBar();
    }


    @FXML
    private void goToSignIn() throws IOException {
        App.setRoot("signIn");
    }

    @FXML
    private void signUp() throws IOException {

        errorDisplay.setVisible(true);
        errorDisplay.setTextFill(Paint.valueOf("red"));

        if ( isInputBlank() ) {
            errorDisplay.setText("Uzupełnij wszystkie pola!");
        } else if ( emailsDifferent() ) {
            errorDisplay.setText("Adresy email nie mogą się różnić!");
        } else if ( emailIncorrect() ) {
            errorDisplay.setText("Adres email niepoprawny!");
        } else if ( password.getText().length() < 8 ) {
            errorDisplay.setText("Hasło musi składać się z conajmniej 8 znaków!");
        } else if( passwordsDifferent() ) {
            errorDisplay.setText("Hasła nie mogą się różnić!");
        } else if ( isEmailUnique() ){
            errorDisplay.setTextFill(Paint.valueOf("green"));
            String code = generateVerificationCode();
            errorDisplay.setText("Przetwarzamy Twoje dane...\n Prosimy o cierpliwość.");
            addUser();
            String content = "Witaj, tu Twój bank.\n\nOto Twój kod weryfikacyjny: " + code;


            ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
            emailExecutor.execute(() -> SendEmailTLS.send(email.getText(), "Kod weryfikacyjny", content));
            emailExecutor.shutdown();

            App.setRoot("registerVerification");
        } else {
            errorDisplay.setText("Na taki adres email zostało już utworzone konto!");
        }

    }

    private boolean isInputBlank() {
        return firstName.getText().isBlank() || lastName.getText().isBlank()
                || email.getText().isBlank() || password.getText().isBlank()
                || passwordRepeat.getText().isBlank() || emailRepeat.getText().isBlank();
    }

    private void disableAllSpaceBar() {
        disableSpaceBar(firstName);
        disableSpaceBar(lastName);
        disableSpaceBar(email);
        disableSpaceBar(password);
        disableSpaceBar(passwordRepeat);
        disableSpaceBar(emailRepeat);

    }

    private void disableSpaceBar(TextInputControl input) {
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 32)
                input.setText(oldValue);
            else {
                input.setText(newValue.replaceAll(" ", ""));
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
        code = RandomStringUtils.random( 16, characters );

        return code;
    }

    private void addUser() {
        Customer customer = new Customer(firstName.getText(), lastName.getText(), email.getText(),
                password.getText(), "123456789", code, false);
        Database.addCustomer(customer);

        String accountNo = generateAccountNo();
        Database.setAccountNo(customer.getEmail(), accountNo);
    }

    private void addAccount(String accountNo, String customerId) {
        Database.addAccount(accountNo, customerId);
    }

    private String generateAccountNo() {
        String customerId = null;

        try {
            List<CustomerDto> customers = Database.fetchCustomers();

            if (customers == null)
                return  null;

            for (CustomerDto customer: customers) {
                if ( customer.getEmail().equals(email.getText()) ) {
                    customerId = customer.getIdCustomer();
                    break;
                }
            }

            if (customerId == null)
                return  null;

            int checksumAsInteger = Integer.parseInt(customerId) * Integer.parseInt(App.BANK_NO) % 100;
            String checksum = String.valueOf(checksumAsInteger);

            String accountNo = checksum + App.BANK_NO + customerId;

            addAccount(accountNo, customerId);

            return accountNo;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    private boolean isEmailUnique() {
        try {
            List<CustomerDto> customers = Database.fetchCustomers();
            for (CustomerDto customer: customers) {
                if (customer.getEmail().equals(email.getText()))
                    return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
