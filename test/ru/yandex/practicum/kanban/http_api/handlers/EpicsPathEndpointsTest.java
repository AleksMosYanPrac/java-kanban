package ru.yandex.practicum.kanban.http_api.handlers;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.TaskDTO;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.http_api.BaseHttpContext.HttpStatus.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

public class EpicsPathEndpointsTest extends AbstractPathEndpointsTest {

    private static final String PATH = "/epics";

    @Test
    void shouldPostAndAddEpic() throws Exception {
        String[] data = {TASK_1.toJson()};

        HttpResponse<String> response = httpClient.POST(PATH, Arrays.toString(data));

        assertEquals(CREATED, getStatusFromCode(response.statusCode()));
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(converter.convert(taskManager.getAllEpics().getFirst()), response.body());
    }

    @Test
    void shouldPOSTAndCreateComplexEpic() throws Exception {
        String[] dataArray = {EPIC_1.toJson(), SUBTASK_1.toJson(), SUBTASK_2.toJson()};

        HttpResponse<String> response = httpClient.POST(PATH, Arrays.toString(dataArray));
        List<Epic> tasksFromManager = taskManager.getAllEpics();
        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();

        assertEquals(CREATED, getStatusFromCode(response.statusCode()));
        assertEquals(1, tasksFromManager.size());
        assertEquals(2, subtasksFromManager.size());
        assertEquals(converter.convert(taskManager.getAllEpics().getFirst()), response.body());
    }

    @Test
    void shouldPOSTAndFailOnTimeIntersection() throws Exception {
        taskManager.createTask(getDatedEpic());
        TaskDTO epicWithTimeIntersection = getDatedEpic();

        HttpResponse<String> response = httpClient.POST(PATH, epicWithTimeIntersection.toJson());

        assertEquals(NOT_ACCEPTABLE, getStatusFromCode(response.statusCode()));
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldPOSTAndFailOnCreateEpic() throws Exception {

        HttpResponse<String> response = httpClient.POST(PATH, new TaskDTO().toJson());

        assertEquals(INTERNAL_SERVER_ERROR, getStatusFromCode(response.statusCode()));
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldGETAndFindEpicById() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1);

        HttpResponse<String> response = httpClient.GET(PATH + "/" + epic.getId());

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(converter.convert(epic), response.body());
    }

    @Test
    void shouldGETAndFailWhenCanNotFindEpicById() throws Exception {

        HttpResponse<String> response = httpClient.GET(PATH + "/" + 1000);

        assertEquals(NOT_FOUND, getStatusFromCode(response.statusCode()));
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldGETAndFindSubtasksForEpic() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1, SUBTASK_1, SUBTASK_2);

        HttpResponse<String> response = httpClient.GET(PATH + "/" + epic.getId() + "/subtasks");

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(converter.convert(epic.getSubtasks()), response.body());
    }

    @Test
    void shouldGETAndFailWhenFindSubtasksForUnavailableEpic() throws Exception {

        HttpResponse<String> response = httpClient.GET(PATH + "/" + 10 + "/subtasks");

        assertEquals(NOT_FOUND, getStatusFromCode(response.statusCode()));
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldGETAndFindAllEpics() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1);

        HttpResponse<String> response = httpClient.GET(PATH);

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(converter.convert(List.of(epic)), response.body());
    }

    @Test
    void shouldDELETEAndDeleteEpicById() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1);

        List<Epic> tasksBeforeDelete = taskManager.getAllEpics();
        HttpResponse<String> response = httpClient.DELETE(PATH + "/" + epic.getId());
        List<Epic> tasksAfterDelete = taskManager.getAllEpics();

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(1, tasksBeforeDelete.size());
        assertEquals(0, tasksAfterDelete.size());
        assertEquals(converter.convert(epic), response.body());
    }

    @Test
    void shouldDELETEAndFailWhenDeletedEpicNotFound() throws Exception {

        HttpResponse<String> response = httpClient.DELETE(PATH + "/" + 100);

        assertEquals(NOT_FOUND, getStatusFromCode(response.statusCode()));
        assertTrue(response.body().isEmpty());
    }

    @Test
    void shouldFailOnBadIdParameterPath() throws Exception {

        HttpResponse<String> response = httpClient.GET(PATH + "/anyNotDigitalValue");

        assertEquals(BAD_REQUEST, getStatusFromCode(response.statusCode()));
    }

    @Test
    void shouldFailOnBadPath() throws Exception {

        HttpResponse<String> response = httpClient.GET(PATH + "/10/NotSubtasks");

        assertEquals(BAD_REQUEST, getStatusFromCode(response.statusCode()));
    }
}