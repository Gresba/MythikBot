package Shoppy;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ShoppyConnection {
    private static final String ShoppyAPIKey;

    private static HttpClient client;

    private static String domainURL;

    private static HttpRequest request;

    static {
        ShoppyAPIKey = System.getenv("SHOPPY_API_KEY");
        client = HttpClient.newHttpClient();
        domainURL = "https://shoppy.gg/api/v1/";
    }

    public static ShoppyOrder getShoppyOrder(String iD) throws IOException, InterruptedException {
        try {
            request = HttpRequest.newBuilder()
                    .GET()
                    .header("Authorization", ShoppyAPIKey)
                    .uri(URI.create(domainURL + "orders/" + iD))
                    .build();
            Gson gson = new Gson();
            return gson.fromJson(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), ShoppyOrder.class);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return null;
        }
    }
}
