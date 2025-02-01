package ru.yandex.practicum.kanban.http_api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.http_api.*;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.managers.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.yandex.practicum.kanban.http_api.BaseHttpContext.HttpStatus.*;

public final class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final String path;

    public EpicHandler(TaskManager taskManager,
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
            case GET_EPICS -> get(taskManager::getAllEpics);
            case GET_EPIC_BY_ID -> getElseNotFound(() -> taskManager.getEpicById(parseId()));
            case DELETE_EPIC -> getElseNotFound(() -> taskManager.deleteEpic(parseId()));
            case POST_EPIC -> complexElseNotAcceptable(
                    this::parseToEpicDTO,
                    (List<TaskDTO> list) -> {
                        if (list.size() == 1) {
                            return taskManager.createEpic(list.getFirst());
                        } else if (list.size() > 1) {
                            TaskDTO epic = list.getFirst();
                            TaskDTO[] subtasks = list.subList(1, list.size()).toArray(new TaskDTO[0]);
                            return taskManager.createEpic(epic, subtasks);
                        } else {
                            throw new NoSuchElementException("Empty request body");
                        }
                    }
            );
            case GET_EPIC_SUBTASKS -> complexElseNotFound(() -> taskManager.getSubtasksForEpic(parseId()));
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