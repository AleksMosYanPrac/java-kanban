package ru.yandex.practicum.kanban.http_api.handlers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.kanban.http_api.BaseHttpContext.HttpStatus.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

class HistoryPathEndpointsTest extends AbstractPathEndpointsTest {

    private static final String PATH = "/prioritized";

    @Test
    void shouldGETAndReturnHistoryOfViewedTasks() throws Exception {
        Task task = taskManager.createTask(getDatedTask());
        Subtask subtask = taskManager.createSubtask(getDatedSubtask());
        taskManager.getTaskById(task.getId());
        taskManager.getSubTaskById(subtask.getId());

        HttpResponse<String> response = httpClient.GET(PATH);

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(converter.convert(taskManager.getHistoryOfViewedTasks()), response.body());
    }

    @Test
    void shouldFailOnBadPath() throws Exception {

        HttpResponse<String> response = httpClient.GET(PATH + "/anyValue");

        assertEquals(BAD_REQUEST, getStatusFromCode(response.statusCode()));
    }
}