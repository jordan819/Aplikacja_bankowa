package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import pl.pwsztar.Connect.Database;

import java.io.IOException;

public class TakeLoanController {


    @FXML
    private Label interestDisplay, infoDisplay;

    @FXML
    private TextField amount;

    @FXML
    private ChoiceBox lengthChoice;

    @FXML
    private Button takeLoan;

    double calculatedInterest;
    double multiplier;
    int duration;

    @FXML
    private void initialize() {
        interestDisplay.setText("Wysokość odsetek:\n" + 0 + " " + App.loggedCustomerAccount.getCurrency());
        lengthChoice.getItems().addAll("3 miesiące", "6 miesiący", "1 rok", "2 lata");
        lengthChoice.getSelectionModel().selectedIndexProperty().
                addListener((observableValue, number, number2) -> {

            switch (number2.intValue()) {
                case 0:
                    duration = 3;
                    multiplier = 0.10;
                    break;
                case 1:
                    duration = 6;
                    multiplier = 0.15;
                    break;
                case 2:
                    duration = 12;
                    multiplier = 0.20;
                    break;
                case 3:
                    duration = 24;
                    multiplier = 0.40;
                    break;
            }
            calculateInterest();

        });
        lengthChoice.getSelectionModel().selectFirst();

        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 15)
                amount.setText(oldValue);
            else if (newValue.length() > oldValue.length()) {
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
            calculateInterest();
        });
    }

    private void calculateInterest() {
        if(amount.getText().isBlank()) {
            interestDisplay.setText("Wysokość odsetek:\n" + 0 +
                    " " + App.loggedCustomerAccount.getCurrency());
        } else {
            calculatedInterest = Double.parseDouble(amount.getText()) * multiplier;
            String calculatedInterestAsString = String.format("%.2f ", calculatedInterest);
            interestDisplay.setText("Wysokość odsetek:\n" + calculatedInterestAsString +
                    " " + App.loggedCustomerAccount.getCurrency());
        }
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("accountManage");
    }

    @FXML
    private void takeLoan() {
        infoDisplay.setVisible(true);
        if(calculatedInterest != 0.0) {

            try {
                Database.createLoanInformation(App.loggedCustomerAccount.getAccountId(),
                        Double.parseDouble(amount.getText()) + calculatedInterest, duration);
                Database.updateAccountBalance(App.loggedCustomerAccount.getAccountId(), amount.getText());
            } catch (AccountNotFoundException e) {
                e.printStackTrace();
                infoDisplay.setTextFill(Paint.valueOf("red"));
                infoDisplay.setText("Wprowadź kwotę!");
                return;
            }

            infoDisplay.setTextFill(Paint.valueOf("green"));
            infoDisplay.setText("Kredyt został udzielony!");
            takeLoan.setDisable(true);
        } else {
            infoDisplay.setTextFill(Paint.valueOf("red"));
            infoDisplay.setText("Wprowadź kwotę!");
        }
    }
}
