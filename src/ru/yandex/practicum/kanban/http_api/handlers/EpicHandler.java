package ru.yandex.practicum.kanban.http_api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.http_api.Action;
import ru.yandex.practicum.kanban.http_api.BaseHttpHandler;
import ru.yandex.practicum.kanban.http_api.RequestConverter;
import ru.yandex.practicum.kanban.http_api.ResponseConverter;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.exceptions.PriorityManagerTimeIntersection;
import ru.yandex.practicum.kanban.service.managers.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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
        Action action = getAction(exchange);
        switch (action) {
            case GET_EPICS -> getAll(exchange, 200);
            case GET_EPIC_BY_ID -> getById(exchange, 200, 404);
            case POST_EPIC -> createEpic(exchange, 201, 406);
            case DELETE_EPIC -> delete(exchange, 200);
            case GET_EPIC_SUBTASKS -> getSubtasks(exchange, 200, 404);
            default -> super.writeBadRequest(exchange);
        }
    }

    private void createEpic(HttpExchange exchange, int code, int failCode) throws IOException {
        try {
            List<TaskDTO> data = super.parseToEpicDTO(exchange.getRequestBody());
            if (data.size() == 1) {
                super.writeSuccess(exchange, responseConverter.convert(taskManager.createEpic(data.getFirst())), code);
            } else if (data.size() > 1) {
                TaskDTO epic = data.getFirst();
                TaskDTO[] subtasks = data.subList(1, data.size()).toArray(new TaskDTO[0]);
                super.writeSuccess(exchange, responseConverter.convert(taskManager.createEpic(epic, subtasks)), code);
            }
        } catch (PriorityManagerTimeIntersection e) {
            super.writeFail(exchange, failCode);
        } catch (Exception e) {
            super.writeInternalServerError(exchange);
        }
    }

    private void getSubtasks(HttpExchange exchange, int code, int failCode) throws IOException {
        try {
            int id = super.parsePathToIntId(exchange.getRequestURI().getPath());
            super.writeSuccess(exchange, responseConverter.convert(taskManager.getSubtasksForEpic(id)), code);
        } catch (NoSuchElementException e) {
            super.writeFail(exchange, failCode);
        }
    }

    private void getById(HttpExchange exchange, int code, int failCode) throws IOException {
        try {
            int id = super.parsePathToIntId(exchange.getRequestURI().getPath());
            super.writeSuccess(exchange, responseConverter.convert(taskManager.getEpicById(id)), code);
        } catch (NoSuchElementException e) {
            super.writeFail(exchange, failCode);
        }
    }

    private void getAll(HttpExchange exchange, int code) throws IOException {
        super.writeSuccess(exchange, responseConverter.convert(taskManager.getAllEpics()), code);
    }

    private void delete(HttpExchange exchange, int code) throws IOException {
        try {
            int id = super.parsePathToIntId(exchange.getRequestURI().getPath());
            super.writeSuccess(exchange, responseConverter.convert(taskManager.deleteEpic(id)), code);
        } catch (NoSuchElementException e) {
            super.writeNotFound(exchange);
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