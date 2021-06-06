package pl.pwsztar;

import com.google.gson.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.pwsztar.Connect.CustomerDto;
import pl.pwsztar.Connect.Database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import com.google.gson.internal.LinkedTreeMap;
import pl.pwsztar.Connect.Money;

/**
 * JavaFX App
 */
public class App extends Application {

    //numer rozliczeniowy banku, wykorzystywany do utworzenia numeru konta klienta
    public static final String BANK_NO = "1234";

    static CustomerDto loggedCustomer;

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("signIn"), 700, 500);
        stage.setScene(scene);
        stage.show();

        new Database();

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