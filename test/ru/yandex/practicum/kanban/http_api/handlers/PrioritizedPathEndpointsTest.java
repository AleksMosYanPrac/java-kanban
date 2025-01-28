package ru.yandex.practicum.kanban.http_api.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.http_api.HttpTaskServer;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedPathEndpointsTest {
    private HttpTaskServer server;
    private TaskManager taskManager;

    private static int PORT = 8080;
    private HttpClient client = HttpClient.newHttpClient();
    private URI uri = URI.create("http://localhost:" + PORT);

    @BeforeEach
    void setUp() throws IOException {
        this.taskManager = Managers.getDefault();
        this.server = new HttpTaskServer(taskManager, PORT);
        server.start();
    }

    @AfterEach
    void setDown() {
        server.stop();
    }

    @Test
    void shouldGETAndPrioritizedTasks() throws Exception {
        String path = "/prioritized";
        int responseCode = 200;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(responseCode, response.statusCode());
    }

    @Test
    void shouldFailOnBadPath() throws Exception {
        String badPath = "/prioritized/anyValue";
        int responseCode = 400;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(badPath))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(responseCode, response.statusCode());
    }
}