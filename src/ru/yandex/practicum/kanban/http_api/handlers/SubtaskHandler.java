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
import java.util.NoSuchElementException;

public final class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final String path;

    public SubtaskHandler(TaskManager taskManager,
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
            case GET_SUBTASKS -> getAll(exchange, 200);
            case GET_SUBTASK_BY_ID -> getById(exchange, 200, 404);
            case POST_SUBTASK -> createOrUpdate(exchange, 201, 406);
            case DELETE_SUBTASK -> delete(exchange, 200);
            default -> super.writeBadRequest(exchange);
        }
    }

    private void createOrUpdate(HttpExchange exchange, int code, int failCode) throws IOException {
        TaskDTO subtask = super.parseBodyToDTO(exchange.getRequestBody());
        try {
            if (subtask.getId() != 0) {
                super.writeSuccess(exchange, responseConverter.convert(taskManager.updateSubtask(subtask)), code);
            } else {
                super.writeSuccess(exchange, responseConverter.convert(taskManager.createSubtask(subtask)), code);
            }
        } catch (PriorityManagerTimeIntersection e) {
            super.writeFail(exchange, failCode);
        } catch (Exception e) {
            super.writeInternalServerError(exchange);
        }
    }

    private void getById(HttpExchange exchange, int code, int failCode) throws IOException {
        try {
            int id = super.parsePathToIntId(exchange.getRequestURI().getPath());
            super.writeSuccess(exchange, responseConverter.convert(taskManager.getSubTaskById(id)), code);
        } catch (NoSuchElementException e) {
            super.writeFail(exchange, failCode);
        }
    }

    private void getAll(HttpExchange exchange, int code) throws IOException {
        super.writeSuccess(exchange, responseConverter.convert(taskManager.getAllSubtasks()), code);
    }

    private void delete(HttpExchange exchange, int code) throws IOException {
        try {
            int id = super.parsePathToIntId(exchange.getRequestURI().getPath());
            super.writeSuccess(exchange, responseConverter.convert(taskManager.deleteSubtask(id)), code);
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