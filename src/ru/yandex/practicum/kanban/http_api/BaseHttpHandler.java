package ru.yandex.practicum.kanban.http_api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.exceptions.PriorityManagerTimeIntersection;
import ru.yandex.practicum.kanban.service.managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.yandex.practicum.kanban.http_api.BaseHttpContext.*;
import static ru.yandex.practicum.kanban.http_api.BaseHttpContext.HttpStatus.*;

public abstract class BaseHttpHandler {

    @FunctionalInterface
    protected interface HttpSupplier<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    protected interface HttpFunction<T, R> {
        R get(T t) throws Exception;
    }

    protected TaskManager taskManager;
    protected ResponseConverter responseConverter;
    protected RequestConverter requestConverter;
    protected HttpExchange httpExchange;

    protected BaseHttpHandler(TaskManager taskManager,
                              ResponseConverter responseConverter,
                              RequestConverter requestConverter) {
        this.taskManager = taskManager;
        this.responseConverter = responseConverter;
        this.requestConverter = requestConverter;
    }

    public abstract String getPath();

    public abstract HttpHandler getHttpHandler();

    protected Action getAction(HttpExchange exchange) {
        return Arrays.stream(Action.values())
                .filter((action) ->
                        action.matchMethod(exchange.getRequestMethod()) && action.matchURI(exchange.getRequestURI()))
                .findFirst().orElse(Action.UNKNOWN);
    }

    protected void writeResponse(String body, HttpStatus status) throws IOException {
        try (OutputStream os = this.httpExchange.getResponseBody()) {
            this.httpExchange.sendResponseHeaders(status.getCode(), 0);
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        this.httpExchange.close();
    }

    protected void writeResponse(HttpStatus status) throws IOException {
        this.httpExchange.sendResponseHeaders(status.getCode(), 0);
        this.httpExchange.close();
    }

    protected void getElseNotAcceptable(HttpSupplier<TaskDTO> supplier,
                                        HttpFunction<TaskDTO, ? extends Task> function) throws IOException {
        try {
            TaskDTO taskDTO = supplier.get();
            writeResponse(responseConverter.convert(function.get(taskDTO)), CREATED);
        } catch (PriorityManagerTimeIntersection e) {
            writeResponse(NOT_ACCEPTABLE);
        } catch (Exception e) {
            writeResponse(INTERNAL_SERVER_ERROR);
        }
    }

    protected void complexElseNotAcceptable(HttpSupplier<List<TaskDTO>> supplier,
                                            HttpFunction<List<TaskDTO>, ? extends Task> function) throws IOException {
        try {
            List<TaskDTO> list = supplier.get();
            writeResponse(responseConverter.convert(function.get(list)), CREATED);
        } catch (PriorityManagerTimeIntersection e) {
            writeResponse(NOT_ACCEPTABLE);
        } catch (NoSuchElementException e) {
            writeResponse(BAD_REQUEST);
        } catch (Exception e) {
            writeResponse(INTERNAL_SERVER_ERROR);
        }
    }

    protected void getElseNotFound(HttpSupplier<? extends Task> supplier) throws IOException {
        try {
            writeResponse(responseConverter.convert(supplier.get()), OK);
        } catch (NoSuchElementException e) {
            writeResponse(NOT_FOUND);
        } catch (Exception e) {
            writeResponse(INTERNAL_SERVER_ERROR);
        }
    }

    protected void complexElseNotFound(HttpSupplier<Collection<? extends Task>> supplier) throws IOException {
        try {
            writeResponse(responseConverter.convert(supplier.get()), OK);
        } catch (NoSuchElementException e) {
            writeResponse(NOT_FOUND);
        } catch (Exception e) {
            writeResponse(INTERNAL_SERVER_ERROR);
        }
    }

    protected void get(HttpSupplier<Collection<? extends Task>> supplier) throws IOException {
        try {
            writeResponse(responseConverter.convert(supplier.get()), OK);
        } catch (Exception e) {
            writeResponse(INTERNAL_SERVER_ERROR);
        }
    }

    protected TaskDTO parseBodyToDTO() throws IOException {
        return requestConverter.convertToObject(new String(this.httpExchange.getRequestBody().readAllBytes()));
    }

    protected List<TaskDTO> parseToEpicDTO() throws IOException {
        return requestConverter.convertToList(new String(this.httpExchange.getRequestBody().readAllBytes()));
    }

    protected int parseId() {
        String path = this.httpExchange.getRequestURI().getPath();
        String[] pathValues = path.split("/");
        if (pathValues.length > 2) {
            try {
                return Integer.parseInt(pathValues[2]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}