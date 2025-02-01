package ru.yandex.practicum.kanban.http_api;

import java.net.URI;

public enum Action {
    GET_TASKS("GET", "/tasks"),
    GET_TASK_BY_ID("GET", "/tasks/{id}"),
    POST_TASK("POST", "/tasks"),
    DELETE_TASK("DELETE", "/tasks/{id}"),

    GET_SUBTASKS("GET", "/subtasks"),
    GET_SUBTASK_BY_ID("GET", "/subtasks/{id}"),
    POST_SUBTASK("POST", "/subtasks"),
    DELETE_SUBTASK("DELETE", "/subtasks/{id}"),

    GET_EPICS("GET", "/epics"),
    GET_EPIC_BY_ID("GET", "/epics/{id}"),
    GET_EPIC_SUBTASKS("GET", "/epics/{id}/subtasks"),
    POST_EPIC("POST", "/epics"),
    DELETE_EPIC("DELETE", "/epics/{id}"),

    GET_HISTORY("GET", "/history"),
    GET_PRIORITIZED("GET", "/prioritized"),
    UNKNOWN("", "");

    private String method;
    private String path;

    Action(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public boolean matchMethod(String method) {
        return this.method.equals(method);
    }

    public boolean matchURI(URI requestURI) {
        String[] request = requestURI.getPath().split("/");
        String[] path = this.path.split("/");
        if (request.length == path.length) {
            if (path.length == 2) {
                return path[1].matches(request[1]);
            } else if (path.length == 3) {
                return request[2].matches("\\d+") && path[1].matches(request[1]);
            } else if (path.length == 4) {
                return request[2].matches("\\d+") && path[1].matches(request[1]) && path[3].matches(request[3]);
            }
        }
        return false;
    }
}