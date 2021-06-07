package pl.pwsztar;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pl.pwsztar.Connect.Account;
import pl.pwsztar.Connect.CustomerDto;
import pl.pwsztar.Connect.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AccountManageController {

    public Label balanceDisplay;

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

    public void goToCurrencyConversion() throws IOException {
        App.setRoot("currencyConversion");
    }

    public void goToPayIn() throws IOException {
        App.setRoot("payIn");
    }

    public void goToPayOut() throws IOException {
        App.setRoot("payOut");
    }

    public void goToTransfer() throws IOException {
        App.setRoot("transfer");
    }

    public void goToDeactivate() throws IOException {
        App.setRoot("deactivate");
    }

    public void goToTakeLoan() throws IOException {
        App.setRoot("takeLoan");
    }

    public void goToPayLoan() throws IOException {
        App.setRoot("payLoan");
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
