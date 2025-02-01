package ru.yandex.practicum.kanban.http_api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.http_api.*;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.managers.TaskManager;

import java.io.IOException;

import static ru.yandex.practicum.kanban.http_api.BaseHttpContext.HttpStatus.*;

public final class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final String path;

    public TaskHandler(TaskManager taskManager,
                       ResponseConverter responseConverter,
                       RequestConverter requestConverter,
                       String path) {
        super(taskManager, responseConverter, requestConverter);
        this.path = path;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.httpExchange = exchange;
        Action action = getAction(exchange);
        switch (action) {
            case GET_TASKS -> get(taskManager::getAllTasks);
            case GET_TASK_BY_ID -> getElseNotFound(() -> taskManager.getTaskById(parseId()));
            case DELETE_TASK -> getElseNotFound(() -> taskManager.deleteTask(parseId()));
            case POST_TASK -> getElseNotAcceptable(
                    this::parseBodyToDTO,
                    (TaskDTO d) -> d.getId() != 0 ? taskManager.updateTask(d) : taskManager.createTask(d)
            );
            default -> super.writeResponse(BAD_REQUEST);
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public HttpHandler getHttpHandler() {
        return this;
    }
}