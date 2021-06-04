package pl.pwsztar;


import java.io.IOException;

public class AccountManageController {
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
}
