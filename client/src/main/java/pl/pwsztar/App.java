package pl.pwsztar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.pwsztar.Connect.Account;
import pl.pwsztar.Connect.CustomerDto;

import java.io.IOException;

/**
 * Aplikacja bankowa.
 * Umozliwia uzytkownikowi zalozenie konta bankowego i zarzadzanie nim.
 */
public class App extends Application {

    static CustomerDto loggedCustomer;
    static Account loggedCustomerAccount;

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("signIn"), 700, 500);
        stage.setScene(scene);
        stage.show();

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Uruchamia aplikacje
     * @param args args
     */
    public static void main(String[] args) {
        launch();
    }

}