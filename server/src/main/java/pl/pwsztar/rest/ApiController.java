package pl.pwsztar.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pwsztar.AccountNotFoundException;

import pl.pwsztar.Connect.Customer;
import pl.pwsztar.rest.connect.Account;
import pl.pwsztar.rest.connect.Database;
import pl.pwsztar.Connect.CustomerDto;
import pl.pwsztar.rest.connect.Money;
import pl.pwsztar.rest.connect.SendEmailTLS;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/bank")
public class ApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    @GetMapping(value = "test")
    public ResponseEntity<Void> testServer() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "loginEmployee/{id}/{pass}")
    public ResponseEntity<Void> loginEmployee(@PathVariable("id") String id,
                                              @PathVariable("pass") String pass) {
        LOGGER.info("Działa metoda loginUser z parametrami id: {}, pass: {}", id, pass);
        if (Database.validateEmployee(id, pass)) {
            LOGGER.info("Dane logowania należą do pracownika");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.info("Dane logowania nie należą do żadnego pracownika");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "loginCustomer/{id}/{pass}")
    public ResponseEntity<CustomerDto> loginCustomer(@PathVariable("id") String id,
                                                     @PathVariable("pass") String pass) {
        LOGGER.info("Działa metoda loginCustomer z parametrami id: {}, pass: {}", id, pass);
        try {
            List<CustomerDto> customers = Database.fetchCustomers();

            if (customers == null){
                LOGGER.info("Nie znaleziono klienta o podanym id");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            for (CustomerDto customer : customers) {
                if (customer.getIdAccount().equals(id)) {
                    if (customer.getPassword().equals(pass) && customer.isVerified()) {
                        LOGGER.info("Dane logowania należą do klienta");
                        return new ResponseEntity<>(customer, HttpStatus.OK);
                    }
                    LOGGER.info("Hasło niepoprawne lub konto nieaktywne");
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LOGGER.info("Wystąpił nieoczekiwany błąd");
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "account/{id}")
    public ResponseEntity<Account> getAccountInfo(@PathVariable("id") String id) {
        LOGGER.info("Działa metoda getAccountInfo z parametrem id: {}", id);
        try {
            Account account = Database.fetchAccount(id);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            LOGGER.info("Konto nie zostało znalezione");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "exchange/{currency}")
    public ResponseEntity<Double> getExchangeRate(@PathVariable("currency") String currency) {
        LOGGER.info("Działa metoda getExchangeRate z parametrem currency: {}", currency);
        final String sURL = "http://api.nbp.pl/api/exchangerates/rates/a/" + currency + "/?format=json";
        double exchangeRate = -1.0;
        try {
            URL url = new URL(sURL);
            URLConnection request = url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonObject = root.getAsJsonObject();
            exchangeRate = jsonObject.get("rates").getAsJsonArray().get(0).
                    getAsJsonObject().get("mid").getAsDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Udostępniam kurs dla waluty: {}, wynoszący: {}", currency, exchangeRate);
        return new ResponseEntity<>(exchangeRate, HttpStatus.OK);
    }


    @GetMapping(value = "employee/table/headers")
    public ResponseEntity<List<String>> getHeadersForTable() {
        LOGGER.info("Działa metoda getHeadersForTable");
        try{
            List<String> names = Database.getColumnNames("customers");
            return new ResponseEntity<>(names, HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(value = "employee/table/content")
    public ResponseEntity<List<String[]>> getContentForTable() {
        LOGGER.info("Działa metoda getContentForTable");
        try {
            List<String[]> content = Database.getTableContent("customers");
            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "exchange/{id}/{currency}")
    public ResponseEntity<Void> changeAccountCurrency(@PathVariable("id") String id,
                                                      @PathVariable("currency") String currency) {
        LOGGER.info("Działa metoda changeAccountCurrency z parametrami id: {}, currency: {}", id, currency);
        try {
            Account account = getAccountInfo(id).getBody();
            assert account != null;
            double balanceAfter = Money.exchange(account.getBalance(), account.getCurrency(), currency);
            Database.updateAccountCurrency(id, currency, balanceAfter);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @PutMapping(value = "account/{id}/{status}")
    public ResponseEntity<Boolean> changeAccountStatus(@PathVariable("id") String id,
                                                       @PathVariable("status") boolean status){
        LOGGER.info("Działa metoda changeAccountCurrency z parametrami id: {}, status: {}", id, status);
        Database.changeAccountStatus(id, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "account/payInOut/{id}/{amount}")
    public ResponseEntity<Void> payMoneyInOut(@PathVariable("id") String id,
                                             @PathVariable("amount") String amount) {
        LOGGER.info("Działa metoda payMoneyInOut z parametrami id: {}, amount: {}", id, amount);
        Database.updateAccountBalance(id, amount);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "account/loan/getMultiplier/{duration}")
    public ResponseEntity<Double> getLoanMultiplier(@PathVariable("duration") int duration) {
        LOGGER.info("Działa metoda getLoanMultiplier z parametrem duration: {}", duration);
        double multiplier = 0;

        if (duration <= 3)
            multiplier = 0.1;
        else if (duration <= 6)
            multiplier = 0.15;
        else if (duration <= 12)
            multiplier = 0.2;
        else
            multiplier = 0.4;

        return new ResponseEntity<>(multiplier, HttpStatus.OK);

    }


    @PutMapping(value = "account/loan/takeLoan/{id}/{amount}/{duration}")
    public ResponseEntity<Void> takeLoan(@PathVariable("id") String id,
                                         @PathVariable("amount") String amount,
                                         @PathVariable("duration") int duration) {
        LOGGER.info("Działa metoda takeLoan z parametrami id: {}, amount: {}, duration: {}", id, amount, duration);
        try {
            double multiplier = getLoanMultiplier(duration).getBody();
            double calculatedInterest = Double.parseDouble(amount) * multiplier;
            Database.createLoanInformation(id, Double.parseDouble(amount) + calculatedInterest, duration);
            Database.updateAccountBalance(id, amount);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping(value = "account/transfer/{fromAccountId}/{toAccountId}/{amount}")
    public ResponseEntity<Void> transferMoney(@PathVariable("fromAccountId") String fromAccountId,
                                              @PathVariable("toAccountId") String toAccountId,
                                              @PathVariable("amount") String amount) throws AccountNotFoundException {
        LOGGER.info("Działa metoda transferMoney z parametrami fromAccountId: {}, toAccountId: {}, amount: {}",
                fromAccountId, toAccountId, amount);

        Account fromAccount = getAccountInfo(fromAccountId).getBody();
        Database.updateAccountBalance(fromAccountId, '-' + amount);
        assert fromAccount != null;
        Database.updateAccountBalance(toAccountId, amount, fromAccount.getCurrency());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PutMapping(value = "account/payLoan/{id}/{amount}")
    public ResponseEntity<Void> payLoan(@PathVariable("id") String id,
                                           @PathVariable("amount") String amount) {
        LOGGER.info("Działa metoda payLoan z parametrami id: {}, amount: {}", id, amount);
        try {
            Database.updateAccountBalance(id,"-" + amount);
            Database.updateLoanInformation(id, Double.parseDouble(amount));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "account/create/{name}/{surname}/{email}/{pass}")
    public ResponseEntity<Void> createAccount(@PathVariable("name") String name,
                                              @PathVariable("surname") String surname,
                                              @PathVariable("email") String email,
                                              @PathVariable("pass") String pass) {
        LOGGER.info("Działa metoda createAccount z parametrami name: {}, surname: {}, email: {}, pass: {}",
                name, surname, email, pass);

        final String BANK_NO = "1234";

        try {
            List<CustomerDto> customers = Database.fetchCustomers();
            assert customers != null;
            for (CustomerDto customer: customers) {
                if (customer.getEmail().equals(email)) {
                    // zostało już założone konto na podany email
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // generowanie kodu weryfikacyjnego
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String code = RandomStringUtils.random( 16, characters );

        LOGGER.info("Tworzenie konta użytkownika");
        Database.addCustomer(new Customer(name, surname, email, pass, "123456789", code, false));

        String accountNo;
        String customerId = null;

        try {
            List<CustomerDto> customers = Database.fetchCustomers();

            if (customers == null)
                return  null;

            for (CustomerDto customer: customers) {
                if ( customer.getEmail().equals(email) ) {
                    customerId = customer.getIdCustomer();
                    break;
                }
            }

            if (customerId == null)
                return  null;

            int checksumAsInteger = Integer.parseInt(customerId) * Integer.parseInt(BANK_NO) % 100;
            String checksum = String.valueOf(checksumAsInteger);

            accountNo = checksum + BANK_NO + customerId;

            Database.addAccount(accountNo, customerId);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        Database.setAccountNo(email, accountNo);

        // wysłanie maila z kodem weryfikacyjnym
        String content = "Witaj, tu Twój bank.\n\nOto Twój kod weryfikacyjny: " + code;
        ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
        emailExecutor.execute(() -> SendEmailTLS.send(email, "Kod weryfikacyjny", content));
        emailExecutor.shutdown();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "account/verify/{email}/{code}")
    public ResponseEntity<Void> verifyAccount(@PathVariable("email") String email,
                                              @PathVariable("code") String code) throws SQLException {
        LOGGER.info("Działa metoda verifyAccount z parametrami email: {}, code: {}", email, code);

        List<CustomerDto> customers = Database.fetchCustomers();
        for (CustomerDto customer: customers) {
            if (customer.getEmail().equals(email)) {
                if (!customer.isVerified()) {
                    if (customer.getVerificationCode().equals(code)) {
                        Database.verifyCustomer(customer);
                        Database.updateAccountBalance(customer.getIdAccount(), "1000");
                        CustomerDto customerDto = customer;

                        String content = "Weryfikacja Twojego konta przebiegła pomyślnie. " +
                                "Do zalogowania się wykorzystasz utworzone hasło, " +
                                "oraz numer Twojego rachunku: " + customerDto.getIdAccount();

                        ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
                        emailExecutor.execute(() -> SendEmailTLS.send(email, "Weryfikacja zakończona", content));
                        emailExecutor.shutdown();
                        return new ResponseEntity<>(HttpStatus.OK);
                    }
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);


    }

    // dezaktywowanie konta
    @DeleteMapping(value = "account/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") String id) {
        LOGGER.info("Działa metoda deleteAccount z parametrem id: {}", id);
        Database.deactivateAccount(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
