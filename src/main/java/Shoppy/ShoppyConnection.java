package Shoppy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ShoppyConnection {
    private final String ShoppyAPIKey;

    private static HttpClient client;

    private static String domainURL;

    private static HttpRequest request;

    public ShoppyConnection() {
        ShoppyAPIKey = System.getenv("SHOPPY_API_KEY");
        client = HttpClient.newHttpClient();

        domainURL = "https://shoppy.gg/api/v1/";
    }

    public HttpResponse<String> getShoppyItem(String endPoint, String iD) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .GET()
                .header("Authorization", ShoppyAPIKey)
                .uri(URI.create(domainURL + endPoint + "/" + iD))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
