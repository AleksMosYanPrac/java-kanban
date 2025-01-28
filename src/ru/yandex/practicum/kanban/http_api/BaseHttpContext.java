package ru.yandex.practicum.kanban.http_api;

import ru.yandex.practicum.kanban.http_api.handlers.*;
import ru.yandex.practicum.kanban.http_api.impls.JsonConverterImpl;
import ru.yandex.practicum.kanban.service.managers.TaskManager;

import java.util.List;

public class BaseHttpContext {

    List<BaseHttpHandler> httpHandlers;
    RequestConverter requestConverter;
    ResponseConverter responseConverter;

    public BaseHttpContext(TaskManager taskManager) {
        JsonConverterImpl jsonConverter = new JsonConverterImpl(false);
        this.requestConverter = jsonConverter;
        this.responseConverter = jsonConverter;
        this.httpHandlers = List.of(
                new TaskHandler(taskManager, responseConverter, requestConverter, "/tasks"),
                new SubtaskHandler(taskManager, responseConverter, requestConverter, "/subtasks"),
                new EpicHandler(taskManager, responseConverter, requestConverter, "/epics"),
                new HistoryHandler(taskManager, responseConverter, requestConverter, "/history"),
                new PrioritizedHandler(taskManager, responseConverter, requestConverter, "/prioritized")
        );
    }

    public List<BaseHttpHandler> getHttpHandlers() {
        return httpHandlers;
    }
}