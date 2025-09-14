package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
public class CallApi {
    public static HttpResponse<String> call(String endpoint, String apiKey) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                        "    \"contents\": [\n" +
                        "      {\n" +
                        "        \"parts\": [\n" +
                        "          {\n" +
                        "            \"text\": \"Explain how AI as a product works. Your response should only be a json in the format " + Util.convertToString(Response.class) + "\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }"))
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check the status code and print the body
        if (response.statusCode() == 200) {
            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
        } else {
            System.err.println("Request failed with status code: " + response.statusCode());
        }

        return response;
    }
}
