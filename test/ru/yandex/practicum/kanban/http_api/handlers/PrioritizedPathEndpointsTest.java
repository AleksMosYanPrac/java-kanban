package ru.yandex.practicum.kanban.http_api.handlers;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.kanban.http_api.BaseHttpContext.HttpStatus.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

class PrioritizedPathEndpointsTest extends AbstractPathEndpointsTest {

    private static final String PATH = "/prioritized";

    @Test
    void shouldGETAndPrioritizedTasks() throws Exception {
        taskManager.createTask(getDatedTask());
        taskManager.createSubtask(getDatedSubtask());

        HttpResponse<String> response = httpClient.GET(PATH);

        assertEquals(OK, getStatusFromCode(response.statusCode()));
        assertEquals(converter.convert(taskManager.getPrioritizedTasks()), response.body());
    }

    @Test
    void shouldFailOnBadPath() throws Exception {

        HttpResponse<String> response = httpClient.GET(PATH + "/anyValue");

        assertEquals(BAD_REQUEST, getStatusFromCode(response.statusCode()));
    }
}