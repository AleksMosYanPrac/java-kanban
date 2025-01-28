package ru.yandex.practicum.kanban.http_api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Arrays;

public class PathToActionMatcherTest {

    @Test
    void shouldMatchGetTasksActionFromPathAndMethod() {
        String requestURI = "/tasks";
        String method = "GET";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.GET_TASKS, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchGetTaskByIdActionFromPathAndMethod() {
        String requestURI = "/tasks/00";
        String method = "GET";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.GET_TASK_BY_ID, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchPostTaskActionFromPathAndMethod() {
        String requestURI = "/tasks";
        String method = "POST";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.POST_TASK, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchDeleteTaskActionFromPathAndMethod() {
        String requestURI = "/tasks/00";
        String method = "DELETE";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.DELETE_TASK, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchGetSubtasksActionFromPathAndMethod() {
        String requestURI = "/subtasks";
        String method = "GET";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.GET_SUBTASKS, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchGetSubtaskByIdActionFromPathAndMethod() {
        String requestURI = "/subtasks/00";
        String method = "GET";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.GET_SUBTASK_BY_ID, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchPostSubtaskActionFromPathAndMethod() {
        String requestURI = "/subtasks";
        String method = "POST";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.POST_SUBTASK, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchDeleteSubtaskActionFromPathAndMethod() {
        String requestURI = "/subtasks/00";
        String method = "DELETE";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.DELETE_SUBTASK, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchGetEpicsActionFromPathAndMethod() {
        String requestURI = "/epics";
        String method = "GET";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.GET_EPICS, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchGetEpicByIdActionFromPathAndMethod() {
        String requestURI = "/epics/00";
        String method = "GET";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.GET_EPIC_BY_ID, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchGetEpicSubtasksActionFromPathAndMethod() {
        String requestURI = "/epics/00/subtasks";
        String method = "GET";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.GET_EPIC_SUBTASKS, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchPostEpicActionFromPathAndMethod() {
        String requestURI = "/epics";
        String method = "POST";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.POST_EPIC, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchDeleteEpicActionFromPathAndMethod() {
        String requestURI = "/epics/00";
        String method = "DELETE";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.DELETE_EPIC, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchGetHistoryActionFromPathAndMethod() {
        String requestURI = "/history";
        String method = "GET";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.GET_HISTORY, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }

    @Test
    void shouldMatchGetPrioritizedActionFromPathAndMethod() {
        String requestURI = "/prioritized";
        String method = "GET";

        URI uri = URI.create(requestURI);

        Assertions.assertEquals(Action.GET_PRIORITIZED, Arrays.stream(Action.values())
                .filter(a -> a.matchMethod(method) && a.matchURI(uri))
                .findFirst()
                .orElse(Action.UNKNOWN));
    }
}