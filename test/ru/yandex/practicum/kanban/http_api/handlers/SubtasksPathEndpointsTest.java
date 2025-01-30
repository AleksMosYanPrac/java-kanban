package ru.yandex.practicum.kanban.http_api.handlers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.TaskDTO;

import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.http_api.BaseHttpContext.HttpStatus.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

public class SubtasksPathEndpointsTest extends AbstractPathEndpointsTest {

    private static final String PATH = "/subtasks";

    @Test
    void shouldPOSTAndAddSubtask() throws Exception {
        String data = SUBTASK_1.toJson();

        HttpResponse<String> response = httpClient.POST(PATH, data);

        assertEquals(CREATED, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals(converter.convert(taskManager.getAllSubtasks().getFirst()), response.body());
    }

    @Test
    void shouldPOSTAndUpdateTask() throws Exception {
        Subtask task = taskManager.createSubtask(SUBTASK_1);
        String data = new TaskDTO(task.getId(), "Task", "Updated", "IN_PROGRESS").toJson();

        HttpResponse<String> response = httpClient.POST(PATH, data);

        assertEquals(CREATED, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals("Updated", taskManager.getAllSubtasks().getFirst().getDescription());
        assertEquals(converter.convert(taskManager.getAllSubtasks().getFirst()), response.body());
    }

    @Test
    void shouldPOSTAndFailOnTimeIntersection() throws Exception {
        taskManager.createSubtask(getDatedSubtask());
        String dataWithTimeIntersection = getDatedSubtask().toJson();

        HttpResponse<String> response = httpClient.POST(PATH, dataWithTimeIntersection);

        assertEquals(NOT_ACCEPTABLE, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllSubtasks().size());
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldPOSTAndFailOnCreateOrUpdateSubtask() throws Exception {
        String data = new TaskDTO().toJson();

        HttpResponse<String> response = httpClient.POST(PATH, data);

        assertEquals(INTERNAL_SERVER_ERROR, getStatusFromCode(response.statusCode()));
        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldGETAndFindSubtaskById() throws Exception {
        Subtask task = taskManager.createSubtask(SUBTASK_1);

        HttpResponse<String> response = httpClient.GET(PATH + "/" + task.getId());

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllSubtasks().size());
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
        taskManager.createSubtask(SUBTASK_1);

        HttpResponse<String> response = httpClient.GET(PATH);

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals(converter.convert(taskManager.getAllSubtasks()), response.body());
    }

    @Test
    void shouldDELETEAndDeleteTaskById() throws Exception {
        Subtask task = taskManager.createSubtask(SUBTASK_1);

        List<Subtask> tasksBeforeDelete = taskManager.getAllSubtasks();
        HttpResponse<String> response = httpClient.DELETE(PATH + "/" + task.getId());
        List<Subtask> tasksAfterDelete = taskManager.getAllSubtasks();

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