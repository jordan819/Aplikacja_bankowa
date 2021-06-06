package pl.pwsztar.Connect;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Money {

    public static double fetchExchangeRate(String currency) {
        String sURL = "http://api.nbp.pl/api/exchangerates/rates/a/" + currency + "/?format=json"; //just a string
        double o = -1.0;
        try {
            URL url = new URL(sURL);
            URLConnection request = url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject jsonObject = root.getAsJsonObject(); //May be an array, may be an object.
            o = jsonObject.get("rates").getAsJsonArray().get(0).getAsJsonObject().get("mid").getAsDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return o;
    }

}
