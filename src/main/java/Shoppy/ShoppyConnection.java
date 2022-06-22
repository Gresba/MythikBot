package Shoppy;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ShoppyConnection {
    private static final String ShoppyAPIKey;

    private static final HttpClient client;

    private static final String domainURL;

    static {
        ShoppyAPIKey = System.getenv("SHOPPY_API_KEY");
        client = HttpClient.newHttpClient();
        domainURL = "https://shoppy.gg/api/v1/";
    }

    /**
     * Gets a Shoppy order with the order id passed in
     *
     * @param iD The order id of the order
     * @return A shoppy order
     * @throws IOException Using stream to access Shoppy data so must be caught
     * @throws InterruptedException Thread issues can occur so must be caught
     */
    public static ShoppyOrder getShoppyOrder(String iD) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Authorization", ShoppyAPIKey)
                .uri(URI.create(domainURL + "orders/" + iD))
                .build();
        Gson gson = new Gson();
        return gson.fromJson(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), ShoppyOrder.class);
    }
}
