package ru.yandex.practicum.kanban.http_api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.http_api.*;
import ru.yandex.practicum.kanban.service.managers.TaskManager;

import java.io.IOException;

import static ru.yandex.practicum.kanban.http_api.BaseHttpContext.HttpStatus.*;

public final class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final String path;

    public PrioritizedHandler(TaskManager taskManager,
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
            case GET_PRIORITIZED -> get(taskManager::getPrioritizedTasks);
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