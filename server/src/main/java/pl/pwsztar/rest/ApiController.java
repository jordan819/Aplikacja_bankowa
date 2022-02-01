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

}
