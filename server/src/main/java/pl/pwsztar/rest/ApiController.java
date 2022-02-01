package pl.pwsztar.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pwsztar.AccountNotFoundException;
import pl.pwsztar.rest.connect.Account;
import pl.pwsztar.rest.connect.Database;
import pl.pwsztar.Connect.CustomerDto;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.List;

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

    @DeleteMapping(value = "account/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") String id) {
        LOGGER.info("Działa metoda deleteAccount z parametrem id: {}", id);
        Database.deactivateAccount(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
