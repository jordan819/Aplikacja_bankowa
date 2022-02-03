package pl.pwsztar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.pwsztar.rest.connect.Database;

/**
 * Serwer aplikacji bankowej.
 * Obsluguje wszystkie zapytania od aplikacji desktopowej.
 */
@SpringBootApplication
public class Application {

	/**
	 * Nawiazuj polaczenie z baza SQL i uruchamia serwer.
	 *
	 * @param args args
	 */
	public static void main(String[] args) {
		Database.makeConnection();
		SpringApplication.run(Application.class, args);
	}

}
