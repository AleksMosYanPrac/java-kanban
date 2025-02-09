package ru.yandex.practicum.kanban.http_api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestHttpClient {

    private final HttpClient client;
    private final URI serverURI;

    public TestHttpClient(URI uri) {
        this.client = HttpClient.newBuilder().build();
        this.serverURI = uri;
    }

    public HttpResponse<String> POST(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(serverURI.resolve(path))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> GET(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(serverURI.resolve(path))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> DELETE(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(serverURI.resolve(path))
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}