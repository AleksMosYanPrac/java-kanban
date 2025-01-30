package ru.yandex.practicum.kanban.http_api.handlers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;

import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.http_api.BaseHttpContext.HttpStatus.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

public class TasksPathEndpointsTest extends AbstractPathEndpointsTest {

    private static final String PATH = "/tasks";

    @Test
    void shouldPOSTAndAddTask() throws Exception {
        String data = TASK_1.toJson();

        HttpResponse<String> response = httpClient.POST(PATH, data);

        assertEquals(CREATED, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(converter.convert(taskManager.getAllTasks().getFirst()), response.body());
    }

    @Test
    void shouldPOSTAndUpdateTask() throws Exception {
        Task task = taskManager.createTask(TASK_1);
        String data = new TaskDTO(task.getId(), "Task", "Updated", "IN_PROGRESS").toJson();

        HttpResponse<String> response = httpClient.POST(PATH, data);

        assertEquals(CREATED, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals("Updated", taskManager.getAllTasks().getFirst().getDescription());
        assertEquals(converter.convert(taskManager.getAllTasks().getFirst()), response.body());
    }

    @Test
    void shouldPOSTAndFailOnTimeIntersection() throws Exception {
        taskManager.createTask(getDatedTask());
        String dataWithTimeIntersection = getDatedTask().toJson();

        HttpResponse<String> response = httpClient.POST(PATH, dataWithTimeIntersection);

        assertEquals(NOT_ACCEPTABLE, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllTasks().size());
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldPOSTAndFailOnCreateOrUpdateTask() throws Exception {
        String data = new TaskDTO().toJson();

        HttpResponse<String> response = httpClient.POST(PATH, data);

        assertEquals(INTERNAL_SERVER_ERROR, getStatusFromCode(response.statusCode()));
        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldGETAndFindTaskById() throws Exception {
        Task task = taskManager.createTask(TASK_1);

        HttpResponse<String> response = httpClient.GET(PATH + "/" + task.getId());

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(converter.convert(task), response.body());
    }

    @Test
    void shouldGETAndFailWhenCanNotFindTaskById() throws Exception {

        HttpResponse<String> response = httpClient.GET(PATH + "/" + 10);

        assertEquals(NOT_FOUND, getStatusFromCode(response.statusCode()));
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldGETAndFindAllTasks() throws Exception {
        taskManager.createTask(TASK_1);

        HttpResponse<String> response = httpClient.GET(PATH);

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(converter.convert(taskManager.getAllTasks()), response.body());
    }

    @Test
    void shouldDELETEAndDeleteTaskById() throws Exception {
        Task task = taskManager.createTask(TASK_1);
        List<Task> tasksBeforeDelete = taskManager.getAllTasks();

        HttpResponse<String> response = httpClient.DELETE(PATH + "/" + task.getId());
        List<Task> tasksAfterDelete = taskManager.getAllTasks();

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(1, tasksBeforeDelete.size());
        assertEquals(0, tasksAfterDelete.size());
        assertEquals(converter.convert(task), response.body());
    }

    @Test
    void shouldDELETEAndFailWhenDeletedTaskNotFound() throws Exception {

        HttpResponse<String> response = httpClient.DELETE(PATH + "/" + 10);

        assertEquals(NOT_FOUND, getStatusFromCode(response.statusCode()));
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldFailOnBadIdParameterPath() throws Exception {

        HttpResponse<String> response = httpClient.GET(PATH + "/anyNotDigitalValue");

        assertEquals(BAD_REQUEST, getStatusFromCode(response.statusCode()));
    }
}