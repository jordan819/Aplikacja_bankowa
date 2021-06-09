package pl.pwsztar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import pl.pwsztar.Connect.Account;
import pl.pwsztar.Connect.CustomerDto;
import pl.pwsztar.Connect.Database;

import java.io.IOException;
import java.util.List;

/**
 * Obsluguje logike okna odpowiedzialnego za ekran powitalny po zalogowaniu.
 * Umozliwia nawigacje do wszystkich funkcjonalnosci konta.
 */
public class AccountManageController {

    @FXML
    private Label balanceDisplay;

    @FXML
    private Label infoDisplay;

    @FXML
    private void initialize() throws IOException {

        try {
            App.loggedCustomerAccount = setAccount(App.loggedCustomer);

            String balance = String.format("%.2f ", App.loggedCustomerAccount.getBalance());
            balance += App.loggedCustomerAccount.getCurrency();
            balanceDisplay.setText(balance);
        } catch (AccountNotFoundException e) {
            e.printStackTrace();
            App.setRoot("signIn");
        }
    }

    @FXML
    private void goToCurrencyConversion() throws IOException {
        App.setRoot("currencyConversion");
    }

    @FXML
    private void goToPayIn() throws IOException {
        App.setRoot("payIn");
    }

    @FXML
    private void goToPayOut() throws IOException {
        App.setRoot("payOut");
    }

    @FXML
    private void goToTransfer() throws IOException {
        App.setRoot("transfer");
    }

    @FXML
    private void goToDeactivate() throws IOException {
        App.setRoot("deactivate");
    }

    @FXML
    private void goToTakeLoan() throws IOException {
        if(App.loggedCustomerAccount.getLoan() == null)
            App.setRoot("takeLoan");
        else {
            infoDisplay.setVisible(true);
            infoDisplay.setTextFill(Paint.valueOf("red"));
            infoDisplay.setText("Niestety, najpierw musisz spłacić poprzednią pożyczkę!");
        }
    }

    @FXML
    private void goToPayLoan() throws IOException {
        if(App.loggedCustomerAccount.getLoan() != null)
            App.setRoot("payLoan");
        else {
            infoDisplay.setVisible(true);
            infoDisplay.setTextFill(Paint.valueOf("green"));
            infoDisplay.setText("Aktualnie nie masz żadnej pożyczki!");
        }
    }

    private Account setAccount(CustomerDto customer) throws AccountNotFoundException{
        List<Account> accounts = Database.fetchAccounts();
        for (Account account: accounts) {
            if (account.getAccountId().equals(customer.getIdAccount())) {
                return account;
            }
        }
        throw new AccountNotFoundException();
    }
}
