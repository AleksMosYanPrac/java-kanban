package ru.yandex.practicum.kanban.http_api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.managers.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public abstract class BaseHttpHandler {

    protected final TaskManager taskManager;
    protected final ResponseConverter responseConverter;
    protected final RequestConverter requestConverter;

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

    protected void writeSuccess(HttpExchange exchange, String body, int code) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(code, 0);
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    protected void writeFail(HttpExchange exchange, int failCode) throws IOException {
        exchange.sendResponseHeaders(failCode, 0);
        exchange.close();
    }

    protected void writeBadRequest(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, 0);
        exchange.close();
    }

    protected void writeNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    protected void writeInternalServerError(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, 0);
        exchange.close();
    }

    protected TaskDTO parseBodyToDTO(InputStream requestBody) throws IOException {
        return requestConverter.convertToObject(new String(requestBody.readAllBytes()));
    }

    protected List<TaskDTO> parseToEpicDTO(InputStream requestBody) throws IOException {
        return requestConverter.convertToList(new String(requestBody.readAllBytes()));
    }

    protected int parsePathToIntId(String path) {
        String[] pathValues = path.split("/");
        if(pathValues.length > 2){
            try {
                return Integer.parseInt(pathValues[2]);
            }catch (NumberFormatException e){
                return 0;
            }
        }
        return 0;
    }
}