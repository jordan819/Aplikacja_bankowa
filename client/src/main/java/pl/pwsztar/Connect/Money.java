package pl.pwsztar.Connect;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Umozliwia sprawdzenie pobieranego na biezaco kursu walut, a takze przewalutowanie.
 */
public abstract class Money {

    /**
     * Pozwala przewalutowac wybrana kwote.
     *
     * @param amount ilosc pieniedzy do przewalutowania
     * @param fromCurrency waluta wyjsciowa
     * @param toCurrency waluta docelowa
     * @return ilosc pieniedzy po przewalutowaniu
     */
    public static double exchange(double amount, String fromCurrency, String toCurrency) {
        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        double result = amount*exchangeRate;
        result = result*100;
        result = (int) result;
        result = result /100;
        return result;
    }

    /**
     * Pozwala pobrac aktualny kurs wymiany walut.
     *
     * @param fromCurrency waluta wyjsciowa
     * @param toCurrency waluta docelowa
     * @return aktualny kurs wymiany, -1 jezeli podana zostala nieistniejaca waluta
     */
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
        try {
            final HttpClient client = HttpClientBuilder.create().build();
            final HttpGet request = new HttpGet("http://127.0.0.1:8080/bank/exchange/" + currency);
            int statusCode = client.execute(request).getStatusLine().getStatusCode();

            /*
            if (statusCode == 200) {
                App.setRoot("employeePanel");
            } else if (statusCode == 403){
                infoDisplay.setVisible(true);
                infoDisplay.setText("Dane niepoprawne!");
            } else {
                System.out.println("status code: " + statusCode);
                infoDisplay.setVisible(true);
                infoDisplay.setText("Wystąpił nieoczekiwany błąd");
            }
            */

            final HttpResponse response = client.execute(request);  // Otrzymujemy odpowiedz od serwera.
            final HttpEntity entity = response.getEntity();

            final String json = EntityUtils.toString(entity);   // Na tym etapie odczytujemy JSON'a, ale jako String.

            // Wyswietlamy zawartosc JSON'a na standardowe wyjscie.
            System.out.println("Odczytano kurs walut: " + json);

            // zamiana Stringa na double
            final Gson gson = new Gson();
            final Type type = new TypeToken<Double>(){}.getType();
            return gson.fromJson(json, type);

        } catch (IOException e) {
            e.printStackTrace();
            return -1.0;
        }
    }



}
