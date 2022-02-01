package pl.pwsztar.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pwsztar.AccountNotFoundException;
import pl.pwsztar.rest.connect.Account;
import pl.pwsztar.rest.connect.Database;
import pl.pwsztar.Connect.CustomerDto;

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

        try {
            Account account = Database.fetchAccount(id);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            LOGGER.info("Konto nie zostało znalezione");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
