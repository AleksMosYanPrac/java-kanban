package ru.yandex.practicum.kanban.http_api;

import ru.yandex.practicum.kanban.http_api.handlers.*;
import ru.yandex.practicum.kanban.http_api.convertors.JsonConverterImpl;
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

    public enum HttpStatus {
        OK(200, "OK"),
        CREATED(201, "Created"),
        BAD_REQUEST(400, "Bad Request"),
        NOT_FOUND(404, "Not Found"),
        NOT_ACCEPTABLE(406, "Not acceptable"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        UNKNOWN(-1, "Unknown Status");

        private final int code;
        private final String description;

        HttpStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static HttpStatus getStatusFromCode(int code) {
            for (HttpStatus status : HttpStatus.values()) {
                if (status.getCode() == code) {
                    return status;
                }
            }
            return UNKNOWN;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}