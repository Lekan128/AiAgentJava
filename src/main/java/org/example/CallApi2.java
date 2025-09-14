package org.example;

import okhttp3.internal.http.HttpMethod;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import static java.net.URLEncoder.encode;


public class CallApi2 {
    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH
    }
    public static HttpResponse<String> call(String endpoint, String body, HttpMethod method, Map<String, String> queryParams, String... headers) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String fullUri = endpoint;
        if (queryParams != null && !queryParams.isEmpty()) {
            fullUri = getFullUriFromQueryParams(queryParams, fullUri);
        }
        System.out.println(fullUri);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(fullUri));
        if (headers.length >0) requestBuilder.headers(headers);

        switch (method) {
            case GET -> requestBuilder.GET();
            case POST -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
            case PUT -> requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body));
            case DELETE -> {
                // For DELETE with a body, use the method() builder.
                if (body != null && !body.isEmpty()) {
                    requestBuilder.method("DELETE", HttpRequest.BodyPublishers.ofString(body));
                } else {
                    requestBuilder.DELETE();
                }
            }
            case PATCH -> requestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofString(body));
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        HttpRequest request = requestBuilder.build();

        // Send the request and handle the response.
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String getFullUriFromQueryParams(Map<String, String> queryParams, String fullUri) {
        String queryString = queryParams.entrySet().stream()
                .map(
                        entry ->
                                encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + encode(entry.getValue(), StandardCharsets.UTF_8)
                )
                .collect(Collectors.joining("&"));
        fullUri += "?" + queryString;
        return fullUri.replace(" ", "%20");
    }
}
