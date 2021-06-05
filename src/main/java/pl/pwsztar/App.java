package pl.pwsztar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.pwsztar.Connect.Customer;
import pl.pwsztar.Connect.Database;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("signIn"), 700, 500);
        stage.setScene(scene);
        stage.show();

        /*
        Customer customer = new Customer("Patryk", "Zaucha", "mail@mail.pl",
                "1234", "123456789", "XYZ", false);
        new Database();
        Database.addCustomer(customer);
         */

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}