package ru.yandex.practicum.kanban.http_api.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.http_api.HttpTaskServer;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

public class SubtasksPathEndpointsTest {

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
    void shouldPostAndAddTask() throws Exception {
        String path = "/subtasks";
        int responseCode = 201;
        String taskDTOJson = SUBTASK_1.toJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .POST(HttpRequest.BodyPublishers.ofString(taskDTOJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals("Subtask 1", tasksFromManager.get(0).getTitle());
    }

    @Test
    void shouldPOSTAndUpdateTask() throws Exception {
        String path = "/subtasks";
        int responseCode = 201;
        Subtask task = taskManager.createSubtask(SUBTASK_1);
        TaskDTO updated = new TaskDTO(task.getId(), "Subtask", "Updated", "IN_PROGRESS");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .POST(HttpRequest.BodyPublishers.ofString(updated.toJson()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals("Updated", tasksFromManager.get(0).getDescription());
    }

    @Test
    void shouldPOSTAndFailOnTimeIntersection() throws Exception {
        String path = "/subtasks";
        int responseCode = 406;
        Subtask task = taskManager.createSubtask(getDatedSubtask());
        TaskDTO taskWithTimeIntersection = getDatedSubtask();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .POST(HttpRequest.BodyPublishers.ofString(taskWithTimeIntersection.toJson()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    void shouldPOSTAndFailOnCreateOrUpdateTask() throws IOException, InterruptedException {
        String path = "/subtasks";
        int responseCode = 500;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .POST(HttpRequest.BodyPublishers.ofString(new TaskDTO().toJson()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(responseCode, response.statusCode());
        assertTrue(tasksFromManager.isEmpty());
    }

    @Test
    void shouldGETAndFindTaskById() throws Exception {
        String path = "/subtasks";
        int responseCode = 200;
        Subtask task = taskManager.createSubtask(SUBTASK_1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path + "/" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    void shouldGETAndFailWhenCanNotFindTaskById() throws Exception {
        String path = "/subtasks";
        int responseCode = 404;
        Subtask task = taskManager.createSubtask(SUBTASK_1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path + "/" + 10))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    void shouldGETAndFindAllTasks() throws Exception {
        String path = "/subtasks";
        int responseCode = 200;
        Subtask task = taskManager.createSubtask(SUBTASK_1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    void shouldDELETEAndDeleteTaskById() throws Exception {
        String path = "/subtasks";
        int responseCode = 200;
        Subtask task = taskManager.createSubtask(SUBTASK_1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path + "/" + task.getId()))
                .DELETE()
                .build();

        List<Subtask> tasksBeforeDelete = taskManager.getAllSubtasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksAfterDelete = taskManager.getAllSubtasks();

        assertEquals(1, tasksBeforeDelete.size());
        assertEquals(responseCode, response.statusCode());
        assertEquals(0, tasksAfterDelete.size());
    }

    @Test
    void shouldDELETEAndFailWhenDeletedTaskNotFound() throws Exception {
        String path = "/subtasks";
        int responseCode = 404;
        Subtask task = taskManager.createSubtask(SUBTASK_1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(path + "/" + 10))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(responseCode, response.statusCode());
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    void shouldFailOnBadPath() throws Exception {
        String badPath = "/subtasks/anyNotDigitalValue";
        int responseCode = 400;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve(badPath))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(responseCode, response.statusCode());
    }
}