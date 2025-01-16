package ru.yandex.practicum.kanban.service.managers;

import org.junit.jupiter.api.Test;

public interface HistoryManagerTest {

    @Test
    void shouldAddTasksToViewedWhenGetTaskByIntegerId();

    @Test
    void shouldDeleteViewFromHistoryWhenUserDeleteTask();

    @Test
    void shouldDeleteViewFromHistoryWhenUserDeleteEpic();

    @Test
    void shouldDeleteViewFromHistoryWhenUserDeleteEpicWithSubtasks();
}