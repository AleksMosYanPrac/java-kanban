package ru.yandex.practicum.kanban.http_api.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.http_api.HttpTaskServer;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

public class EpicsPathEndpointsTest {

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
    void shouldPostAndAddEpic() throws Exception {
        String path = "/epics";
        int responseCode = 201;
        String taskDTOJson = EPIC_1.toJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .POST(HttpRequest.BodyPublishers.ofString(taskDTOJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals("Epic 1", tasksFromManager.get(0).getTitle());
    }

    @Test
    void shouldPOSTAndCreateComplexEpic() throws Exception {
        String path = "/epics";
        int responseCode = 201;
        String[] responseArray = {EPIC_1.toJson(), SUBTASK_1.toJson(), SUBTASK_2.toJson()};
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .POST(HttpRequest.BodyPublishers.ofString(Arrays.toString(responseArray)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromManager = taskManager.getAllEpics();
        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();

        assertEquals(responseCode, response.statusCode());
        assertEquals(1, tasksFromManager.size());
        assertEquals(2, subtasksFromManager.size());
    }

    @Test
    void shouldPOSTAndFailOnTimeIntersection() throws Exception {
        String path = "/epics";
        int responseCode = 406;
        Task task = taskManager.createTask(getDatedEpic());
        TaskDTO epicWithTimeIntersection = getDatedEpic();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .POST(HttpRequest.BodyPublishers.ofString(epicWithTimeIntersection.toJson()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(responseCode, response.statusCode());
    }

    @Test
    void shouldPOSTAndFailOnCreateEpic() throws IOException, InterruptedException {
        String path = "/epics";
        int responseCode = 500;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .POST(HttpRequest.BodyPublishers.ofString(new TaskDTO().toJson()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertEquals(responseCode, response.statusCode());
        assertTrue(tasksFromManager.isEmpty());
    }

    @Test
    void shouldGETAndFindEpicById() throws Exception {
        String path = "/epics";
        int responseCode = 200;
        Epic task = taskManager.createEpic(EPIC_1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path + "/" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    void shouldGETAndFailWhenCanNotFindEpicById() throws Exception {
        String path = "/epics";
        int responseCode = 404;
        Epic task = taskManager.createEpic(EPIC_1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path + "/" + 10))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    void shouldGETAndFindSubtasksForEpic() throws Exception {
        String path = "/epics";
        int responseCode = 200;
        Epic task = taskManager.createEpic(EPIC_1, SUBTASK_1, SUBTASK_2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path + "/" + task.getId() + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(responseCode, response.statusCode());
    }

    @Test
    void shouldGETAndFailWhenFindSubtasksForUnavailableEpic() throws Exception {
        String path = "/epics";
        int responseCode = 404;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path + "/" + 10 + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(responseCode, response.statusCode());
    }

    @Test
    void shouldGETAndFindAllEpics() throws Exception {
        String path = "/epics";
        int responseCode = 200;
        Epic task = taskManager.createEpic(EPIC_1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    void shouldDELETEAndDeleteTaskById() throws Exception {
        String path = "/epics";
        int responseCode = 200;
        Epic task = taskManager.createEpic(EPIC_1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path + "/" + task.getId()))
                .DELETE()
                .build();

        List<Epic> tasksBeforeDelete = taskManager.getAllEpics();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksAfterDelete = taskManager.getAllEpics();

        assertEquals(1, tasksBeforeDelete.size());
        assertEquals(responseCode, response.statusCode());
        assertEquals(0, tasksAfterDelete.size());
    }

    @Test
    void shouldDELETEAndFailWhenDeletedEpicNotFound() throws Exception {
        String path = "/epics";
        int responseCode = 404;
        Epic task = taskManager.createEpic(EPIC_1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path + "/" + 10))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    void shouldFailOnBadIdParameterPath() throws Exception {
        String badPath = "/epics/anyNotDigitalValue";
        int responseCode = 400;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(badPath))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(responseCode, response.statusCode());
    }

    @Test
    void shouldFailOnBadPath() throws Exception {
        String badPath = "/epics/1/NotSubtasks";
        int responseCode = 400;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(badPath))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(responseCode, response.statusCode());
    }
}