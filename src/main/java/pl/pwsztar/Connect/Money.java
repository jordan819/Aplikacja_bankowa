package pl.pwsztar.Connect;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public abstract class Money {

    public static double exchange(double amount, String fromCurrency, String toCurrency) {
        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        double result = amount*exchangeRate;
        result = result*100;
        result = (int) result;
        result = result /100;
        return result;
    }

    public static double getExchangeRate(String fromCurrency, String toCurrency) {
        double from = 1.0, to = 1.0;
        if (!fromCurrency.equals("PLN"))
            from = fetchExchangeRate(fromCurrency);

        if (!toCurrency.equals("PLN"))
            to = fetchExchangeRate(toCurrency);

        if (from == -1.0 || to == -1.0)
            return -1.0;

        return from/to;

    }

    private static double fetchExchangeRate(String currency) {
        String sURL = "http://api.nbp.pl/api/exchangerates/rates/a/" + currency + "/?format=json";
        double exchangeRate = -1.0;
        try {
            URL url = new URL(sURL);
            URLConnection request = url.openConnection();
            request.connect();


            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject jsonObject = root.getAsJsonObject(); //May be an array, may be an object.
            exchangeRate = jsonObject.get("rates").getAsJsonArray().get(0).getAsJsonObject().get("mid").getAsDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exchangeRate;
    }

}
