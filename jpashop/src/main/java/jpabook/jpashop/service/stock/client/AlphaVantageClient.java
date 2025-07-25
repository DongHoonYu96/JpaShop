package jpabook.jpashop.service.stock.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class AlphaVantageClient {

    @Value("${api.alphavantage.key}")
    private String apiKey;

    private static final String BASE_URL = "https://www.alphavantage.co/query";

    public JsonObject getDailyTimeSeries(String symbol) {
        return callApi("TIME_SERIES_DAILY", symbol);
    }

    public JsonObject searchStocks(String keywords) {
        return callApi("SYMBOL_SEARCH", keywords);
    }

    private JsonObject callApi(String function, String symbolOrKeywords) {
        try {
            String urlString;
            if (function.equals("SYMBOL_SEARCH")) {
                urlString = String.format("%s?function=%s&keywords=%s&apikey=%s", BASE_URL, function, symbolOrKeywords, apiKey);
            } else {
                urlString = String.format("%s?function=%s&symbol=%s&apikey=%s", BASE_URL, function, symbolOrKeywords, apiKey);
            }

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            return JsonParser.parseString(content.toString()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
