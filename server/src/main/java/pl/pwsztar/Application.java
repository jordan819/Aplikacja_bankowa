package pl.pwsztar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.pwsztar.rest.connect.Database;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		Database.makeConnection();
		SpringApplication.run(Application.class, args);
	}

}
