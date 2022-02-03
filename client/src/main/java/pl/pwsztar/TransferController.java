package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * Obsluguje logike okna odpowiedzialnego za wykonywanie przelewow.
 * Przyjmuje od uzytkownika wprowadzane z klawiatury kwote i numer konta odbiorcy.
 * Jesli dane sa poprawne, a stan konta pozwala na wykonywanie operacji,
 * salda kont nadawcy i odbiorcy sa aktualizowane.
 */
public class TransferController {

    @FXML
    private TextField toAccountInput, amount;

    @FXML
    private Label infoDisplay;

    @FXML
    private void initialize() {
        infoDisplay.setVisible(false);
        toAccountInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                toAccountInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > oldValue.length()) {
                if (newValue.charAt(newValue.length() - 1) == '.') {
                    if (oldValue.contains("."))
                        amount.setText(oldValue);
                    else
                        amount.setText(newValue);
                } else if (!newValue.matches("[\\d]*")) {
                    amount.setText(newValue.replaceAll("[^[\\d.]]", ""));
                }
            }
            if (!amount.getText().equals("") && newValue.charAt(0) == '0')
                amount.setText(newValue.substring(1));
            if (amount.getLength() > 3) {
                if (amount.getText().charAt(amount.getLength() - 4) == '.') {
                    amount.setText(oldValue);
                }
            }
            if (amount.getText().equals(".")) {
                amount.setText("");
            }
        });
    }

    @FXML
    private void makeTransfer() {
        infoDisplay.setVisible(true);

        if ( !inputBlank() ) {
            if (Double.parseDouble(amount.getText()) > App.loggedCustomerAccount.getBalance() ) {
                infoDisplay.setTextFill(Paint.valueOf("red"));
                infoDisplay.setText("Brak środków na koncie");
                return;
            }
            try {

                final HttpClient client = HttpClientBuilder.create().build();
                final HttpPut request = new HttpPut("http://127.0.0.1:8080/bank/account/transfer/"
                        + App.loggedCustomerAccount.getAccountId() + "/"
                        + toAccountInput.getText() + "/"
                        + amount.getText());

                client.execute(request);

                infoDisplay.setTextFill(Paint.valueOf("green"));
                infoDisplay.setText("Przelano pieniądze!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            infoDisplay.setTextFill(Paint.valueOf("red"));
            infoDisplay.setText("Wprowadź dane!");
        }
    }

    private boolean inputBlank() {
        return toAccountInput.getText().isBlank() || amount.getText().isBlank();
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("accountManage");
    }
}
