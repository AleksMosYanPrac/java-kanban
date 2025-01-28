package ru.yandex.practicum.kanban.http_api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.http_api.Action;
import ru.yandex.practicum.kanban.http_api.BaseHttpHandler;
import ru.yandex.practicum.kanban.http_api.RequestConverter;
import ru.yandex.practicum.kanban.http_api.ResponseConverter;
import ru.yandex.practicum.kanban.service.managers.TaskManager;

import java.io.IOException;

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
        Action action = getAction(exchange);
        switch (action) {
            case GET_PRIORITIZED -> getPrioritized(exchange, 200);
            default -> super.writeBadRequest(exchange);
        }
    }

    private void getPrioritized(HttpExchange exchange, int code) throws IOException {
        super.writeSuccess(exchange, responseConverter.convert(taskManager.getPrioritizedTasks()), code);
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